
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { Component, input, InputSignal, OnInit, output, signal } from '@angular/core';

import { CommonModule, DatePipe } from '@angular/common';
import { ChatResponse, MessageResponse, UserResponse } from '../../../services/models';
import { ChatService, UserService } from '../../../services/services';
import { KeycloakService } from '../../../utils/keycloak/keycloak.service';


import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { ToastrService } from 'ngx-toastr';
import { Notification } from '../../../Chats/component/pages/main/models/notification';
import { BaseChartDirective } from 'ng2-charts';
import {  ChartConfiguration, ChartData, ChartType,Chart, registerables } from 'chart.js';
import { ticketService } from '../../../services/services/ticket.service';
import { PdfViewerModule } from 'ng2-pdf-viewer';
Chart.register(...registerables);
@Component({
  selector: 'app-dashboard',
  imports: [RouterOutlet,RouterLink,RouterLinkActive,SidebarComponent,CommonModule,BaseChartDirective,PdfViewerModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

    iduser: string | null = localStorage.getItem('userId');
  messages: { sender: string, content: string }[] = [];
  userMessage: string = ''; 

  selectedChat: ChatResponse = {};
  chats: Array<ChatResponse> = [];
  chatMessages: Array<MessageResponse> = [];
  socketClient: any = null;
  messageContent: string = '';
  showEmojis = false;
  isLoading=false;
    errorMessage: string | null = null;

  private notificationSubscription: any;

  //@Input chats  =signal<ChatResponse[]>([]);
  searchNewContact = false;
  contacts: Array<UserResponse> = [];
  chatSelected = output<ChatResponse>();

  chatsList: ChatResponse[] = [];
barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: { legend: { display: true } },
    scales: { y: { beginAtZero: true } },
  };
  barChartType: ChartType = 'bar';
  barChartData: ChartData<'bar'> = {
    labels: ['Acceptés', 'Non Acceptés'],
    datasets: [{ data: [0, 0], label: 'Tickets', backgroundColor: ['#36A2EB', '#FF6384'] }],
  };

  pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: { legend: { display: true, position: 'top' } },
  };
  pieChartType: ChartType = 'pie';
  pieChartData: ChartData<'pie'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Tickets par Utilisateur',
        backgroundColor: ['#36A2EB', '#FF6384', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40', '#66CC66', '#FF6666'],
      },
    ],
  };

  pdfSrc: string = '/assets/ticketd accepet.pdf'; // Assuré que c'est un string

  userTicketChartData: ChartData<'bar'> = {
  labels: ['Tickets assignés'],
  datasets: [
    {
      data: [0], // Valeur initiale
      label: 'Utilisateur sélectionné',
      backgroundColor: ['#4CAF50'],
    },
  ],
};

userTicketChartOptions: ChartConfiguration['options'] = {
  responsive: true,
  plugins: {
    legend: { display: true, position: 'top' },
  },
  scales: {
    y: { beginAtZero: true },
  },
};

  constructor(
    private chatService: ChatService,
    private userService: UserService,
    private keycloakService: KeycloakService,
        private toastService :ToastrService,
        private ticketService :ticketService
  ) {
  }
    ngOnInit(): void {
    this.initWebSocket();
    this.getAllChats();
    this.loadTicketData();  
    this.loadUserTickets();
    
  }

downloadTickets(): void {
    this.ticketService.downloadTickets().subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'tickets.csv';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        this.toastService.success('Tickets téléchargés avec succès');
      },
      error: (err) => {
        console.error('Erreur lors du téléchargement des tickets:', err);
        this.toastService.error('Échec du téléchargement des tickets');
      },
    });
  }

  private loadTicketData(): void {
    this.ticketService.getTickets(true).subscribe({
      next: (acceptedTickets) => {
        console.log('Tickets Acceptés:', acceptedTickets);
        this.ticketService.getTickets(false).subscribe({
          next: (notAcceptedTickets) => {
            console.log('Tickets Non Acceptés:', notAcceptedTickets);
            this.barChartData.datasets[0].data = [acceptedTickets.length, notAcceptedTickets.length];
          },
          error: (err) => {
            console.error('Erreur lors de la récupération des tickets non acceptés:', err);
            this.toastService.error('Échec du chargement des tickets non acceptés');
          },
        });
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des tickets acceptés:', err);
        this.toastService.error('Échec du chargement des tickets acceptés');
      },
    });

    this.userService.getUsers().subscribe({
      next: (users) => {
        console.log('Utilisateurs:', users);
        // Filtrer les utilisateurs avec un id valide (non null et non undefined)
        const validUsers = users.filter((user): user is UserResponse & { id: string } => user.id != null);
        const userIds = validUsers.map((user) => user.id);
        const ticketRequests = userIds.map((id) => this.ticketService.getTicketsByUser(id).toPromise());

        Promise.all(ticketRequests)
          .then((ticketArrays) => {
            const userTicketCounts = validUsers.map((user, index) => ({
              userName: `${user.firstName || ''} `.trim() || user.id,
              id: user.id,
              count: ticketArrays[index]?.length || 0,
            }));
            console.log('Comptes de Tickets par Utilisateur:', userTicketCounts);
            this.pieChartData.labels = userTicketCounts.map((u) => u.userName);
            this.pieChartData.datasets[0].data = userTicketCounts.map((u) => u.count);
          })
          .catch((err) => {
            console.error('Erreur lors de la récupération des tickets des utilisateurs:', err);
            this.toastService.error('Échec du chargement des tickets des utilisateurs');
          });
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des utilisateurs:', err);
        this.toastService.error('Échec du chargement des utilisateurs');
      },
    });
  }
    selectContact(contact: UserResponse) {
      this.chatService.createChat({
        'sender-id': this.keycloakService.userId as string,
        'receiver-id': contact.id as string
      }).subscribe({
        next: async (res) => {
          console.log("📩 Réponse du backend après création du chat :", res);
    
          // Convertir la réponse en JSON si c'est un Blob
          let responseBody;
          if (res instanceof Blob) {
            responseBody = await res.text(); // Convertir en texte
            try {
              responseBody = JSON.parse(responseBody); // Parser en JSON
            } catch (error) {
              console.error("❌ Erreur : Impossible de parser la réponse JSON !", error);
              return;
            }
          } else {
            responseBody = res;
          }
    
          console.log("📝 Réponse après conversion :", responseBody);
    
          if (!responseBody.response) {
            console.error("❌ Erreur : L'ID du chat est manquant !");
            return;
          }
    
          const chat: ChatResponse = {
            id: responseBody.response,  // ID maintenant accessible
            name: contact.firstName + ' ' + contact.lastName,
            recipientOnline: contact.online,
            lastMessageTime: contact.lastSeen,
            senderId: this.keycloakService.userId,
            receiverId: contact.id
          };
    
          // Mettre à jour la liste des chats
          this.chatsList = [chat, ...this.chatsList];
    
          // Sélectionne le chat 
          this.selectedChat = chat;
          console.log("Chat sélectionné :", this.selectedChat);
    
          this.searchNewContact = false;
          this.chatSelected.emit(chat);
        },
        error: (err) => {
          console.error(" Erreur lors de la création du chat :", err);
        }
      });
    }


     private initWebSocket() {
        if (this.keycloakService.keycloak.tokenParsed?.sub) {
          console.log('User ID:', this.keycloakService.keycloak.tokenParsed.sub);
          let ws = new SockJS('http://localhost:8081/ws');
          this.socketClient = Stomp.over(ws);
      
          // Souscription au canal '/chat' pour recevoir des messages de chat
          const chatSubUrl = `/user/${this.keycloakService.keycloak.tokenParsed?.sub}/chat`;
          const notificationSubUrl = `/user/${this.keycloakService.keycloak.tokenParsed?.sub}/notifications`;
    
          this.socketClient.connect(
            { 'Authorization': 'Bearer ' + this.keycloakService.keycloak.token },
            () => {
              // Souscription au canal chat
              this.notificationSubscription = this.socketClient.subscribe(
                chatSubUrl,
                (message: any) => {
                  const notification: Notification = JSON.parse(message.body);
                  this.handleNotification(notification);
                },
                () => console.error('Error while connecting to WebSocket for chat')
              );
      
              // Souscription au canal '/notification' pour recevoir des notifications de type "notification"
              console.log('Subscribing to notifications channel at:', notificationSubUrl);
              
              this.socketClient.subscribe(
                notificationSubUrl,
                (message: any) => {
                  console.log('Received notification message:', message.body);
                  const notification: Notification = JSON.parse(message.body);
                 if(notification){
                  switch (notification.notificationStatus){
                    case 'ADDED':
                      this.toastService.info(notification.content,notification.notificationStatus);
                      break;
                      case 'ACCEPTED':
                      this.toastService.info(notification.content,notification.notificationStatus);
                      break;
                      case 'COMPLETED':
                        this.toastService.info(notification.content,notification.notificationStatus);
                        break;
    
                  }
                 }
                },
                () => console.error('Error while connecting to WebSocket for notifications')
              );
            }
          );
        }
      }
       private handleNotification(notification: Notification) {
    if (!notification) return;
  
    if (!Array.isArray(this.chats)) {
      console.error('Erreur: this.chats n\'est pas un tableau', this.chats);
      this.chats = []; // On réinitialise pour éviter l'erreur
    }
  
    if (this.selectedChat && this.selectedChat.id === notification.chatId) {
      switch (notification.type) {
        case 'MESSAGE':
        case 'IMAGE':
          const message: MessageResponse = {
            senderId: notification.senderId,
            receiverId: notification.receiverId,
            content: notification.content,
            type: notification.messageType,
            media: notification.media,
            createdAt: new Date().toString()
          };
          this.selectedChat.lastMessage = notification.type === 'IMAGE' ? 'Attachment' : notification.content;
          this.chatMessages.push(message);
          break;
        case 'SEEN':
          this.chatMessages.forEach(m => m.state = 'SEEN');
          break;
      }
    } else {
      const destChat = this.chats.find(c => c.id === notification.chatId);
      if (destChat && notification.type !== 'SEEN') {
        destChat.lastMessage = notification.type === 'IMAGE' ? 'Attachment' : notification.content;
        destChat.lastMessageTime = new Date().toString();
        destChat.unreadCount = (destChat.unreadCount || 0) + 1;
      } else if (notification.type === 'MESSAGE') {
        const newChat: ChatResponse = {
          id: notification.chatId,
          senderId: notification.senderId,
          receiverId: notification.receiverId,
          lastMessage: notification.content,
          name: notification.chatName,
          unreadCount: 1,
          lastMessageTime: new Date().toString()
        };
        this.chats.unshift(newChat);
      }
    }
  }
    private getAllChats() {
    this.chatService.getChatsByReceiver()
      .subscribe({
        next: (res) => {
          this.chats = res;
        }
      });
  }
  
  get fullName() {
    return this.keycloakService.keycloak?.tokenParsed?.['given_name'];
  }

 loadUserTickets() {
      if (!this.iduser) {
      this.errorMessage = 'Utilisateur non connecté';
      return;
    }
  this.ticketService.getTicketsByUser(this.iduser).subscribe({
    next: (tickets) => {
      const count = tickets.length;
      this.userTicketChartData = {
        labels: ['Tickets assignés'],
        datasets: [
          {
            label: 'Utilisateur sélectionné',
            data: [count],
            backgroundColor: '#4CAF50'
          }
        ]
      };
    },
    error: (err) => {
      console.error('Erreur lors du chargement des tickets de l’utilisateur', err);
    }
  });
}
  }


