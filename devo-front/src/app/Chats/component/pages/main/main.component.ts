import { AfterViewChecked, ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { DatePipe } from '@angular/common';
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import {Notification} from './models/notification';
import {FormsModule} from '@angular/forms';
import {PickerComponent} from '@ctrl/ngx-emoji-mart';
import {EmojiData} from '@ctrl/ngx-emoji-mart/ngx-emoji';
import { ChatComponent } from '../../chat/chat.component';
import { ChatResponse, MessageRequest, MessageResponse } from '../../../../services/models';
import { ChatService, MessageService } from '../../../../services/services';
import { KeycloakService } from '../../../../utils/keycloak/keycloak.service';
import { GeminiService } from '../../../../services/services/gemini.service';
import { SpinnerComponent } from '../../Shared/spinner/spinner.component';
import { ToastrService } from 'ngx-toastr';
import { SidebarComponent } from '../../../../shared/components/sidebar/sidebar.component';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-main',
  imports: [ChatComponent, DatePipe, FormsModule, PickerComponent, SpinnerComponent,SidebarComponent],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent implements OnInit, OnDestroy, AfterViewChecked {
  messages: { sender: string, content: string }[] = [];
  userMessage: string = ''; 

  selectedChat: ChatResponse = {};
  chats: Array<ChatResponse> = [];
  chatMessages: Array<MessageResponse> = [];
  socketClient: any = null;
  messageContent: string = '';
  showEmojis = false;
  isLoading=false;
  @ViewChild('scrollableDiv') scrollableDiv!: ElementRef<HTMLDivElement>;
  private notificationSubscription: any;

  constructor(
    private chatService: ChatService,
    private messageService: MessageService,
    private keycloakService: KeycloakService,
    private geminiService: GeminiService,
    private cdr: ChangeDetectorRef,
    private toastService :ToastrService,
    private route: ActivatedRoute
  ) {
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  ngOnDestroy(): void {
    if (this.socketClient !== null) {
      this.socketClient.disconnect();
      this.notificationSubscription.unsubscribe();
      this.socketClient = null;
    }
  }

  ngOnInit(): void {
    this.initWebSocket();
    this.getAllChats();
     const chatId = this.route.snapshot.paramMap.get('chatId');
    if (chatId) {
      this.getAllChatMessages(chatId);
    } else {
      console.error("Aucun chatId fourni dans l'URL !");
    }
    
  }

  chatSelected(chatResponse: ChatResponse) {
    this.selectedChat = chatResponse;
    this.getAllChatMessages(chatResponse.id as string);
    this.setMessagesToSeen();
    this.selectedChat.unreadCount = 0;
  }

  isSelfMessage(message: MessageResponse): boolean {
    return message.senderId === this.keycloakService.userId;
  }

  sendMessage() {
    if (this.messageContent) {
      const messageRequest: MessageRequest = {
        chatId: this.selectedChat.id,
        senderId: this.getSenderId(),
        receiverId: this.getReceiverId(),
        content: this.messageContent,
        type: 'TEXT',
      };
      this.messageService.saveMessage({
        body: messageRequest
      }).subscribe({
        next: () => {
          const message: MessageResponse = {
            senderId: this.getSenderId(),
            receiverId: this.getReceiverId(),
            content: this.messageContent,
            type: 'TEXT',
            state: 'SENT',
            createdAt: new Date().toString()
          };
          this.selectedChat.lastMessage = this.messageContent;
          this.chatMessages.push(message);
          this.messageContent = '';
          this.showEmojis = false;
          console.log('Messages mis à jour:', this.chatMessages);
          this.cdr.detectChanges();
        }
      });
    }
  }
  getAIResponse() {
    if (!this.selectedChat || this.chatMessages.length === 0) {
      console.warn('Aucun chat sélectionné ou aucun message disponible.');
      return;
    }
    this.isLoading = true;
    console.log('ffffffffff');
    
    const lastUserMessage = this.chatMessages[this.chatMessages.length - 1]?.content || '';
    this.geminiService.getResponse(lastUserMessage).subscribe(
      (response: any) => {
        // Vérifier si la réponse contient des candidats et du texte
        const aiResponse = response?.candidates?.[0]?.content?.parts?.[0]?.text || 'Aucune réponse reçue.';
  
        // Création du message conforme à `MessageResponse`
        const aiMessage: MessageResponse = {
          id: Date.now().toString(), // ID temporaire
          content: aiResponse,
          senderId: 'AI_GEMINI',
          receiverId: this.selectedChat?.receiverId || '',
          type: 'TEXT',
          createdAt: new Date().toISOString(),
          media: [], // Compatible avec l'interface
          state: 'SENT' // État du message
        };
  
        this.chatMessages.push(aiMessage);
        this.isLoading = false;
      },
      error => {
        console.error('Erreur avec Gemini:', error);
        this.chatMessages.push({
          id: Date.now().toString(),
          content: 'Erreur lors de la réponse AI.',
          senderId: 'AI_GEMINI',
          receiverId: this.selectedChat?.receiverId || '',
          type: 'TEXT',
          createdAt: new Date().toISOString(),
          media: [],
          state: 'SENT'
        });
        this.isLoading=false;
      }
    );
  }
  
  
  
  
  
  keyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.sendMessage();
    }
  }

  onSelectEmojis(emojiSelected: any) {
    const emoji: EmojiData = emojiSelected.emoji;
    this.messageContent += emoji.native;
  }

  onClick() {
    this.setMessagesToSeen();
  }

  uploadMedia(target: EventTarget | null) {
    const file = this.extractFileFromTarget(target);
    if (file !== null) {
      const reader = new FileReader();
      reader.onload = () => {
        if (reader.result) {

          const mediaLines = reader.result.toString().split(',')[1];

          this.messageService.uploadMedia({
            'chat-id': this.selectedChat.id as string,
            body: {
              file: file
            }
          }).subscribe({
            next: () => {
              const message: MessageResponse = {
                senderId: this.getSenderId(),
                receiverId: this.getReceiverId(),
                content: 'Attachment',
                type: 'IMAGE',
                state: 'SENT',
                media: [mediaLines],
                createdAt: new Date().toString()
              };
              this.chatMessages.push(message);
            }
          });
        }
      }
      reader.readAsDataURL(file);
    }
  }

  logout() {
    this.keycloakService.logout();
  }

  userProfile() {
    this.keycloakService.accountManagement();
  }

  private setMessagesToSeen() {
    const payload = {
      'chat-id': this.selectedChat?.id as string
    };
  
    console.log(" Données envoyées au PATCH :", payload);
  
    if (!payload['chat-id']) {
      console.error(" Erreur : L'ID du chat est manquant !");
      return;
    }
  
    this.messageService.setMessageToSeen(payload).subscribe({
      next: () => console.log(" Message marqué comme vu"),
      error: (err) => console.error(" Erreur PATCH :", err)
    });
  }
  

  private getAllChats() {
    this.chatService.getChatsByReceiver()
      .subscribe({
        next: (res) => {
          this.chats = res;
        }
      });
  }

  private getAllChatMessages(chatId: string) {
    if (!chatId) {
      console.error(" Erreur : L'ID du chat est manquant !");
      return;
    }
  
    this.messageService.getAllMessages$Response({ 'chat-id': chatId }).subscribe({
      next: (response) => {
        console.log(" Réponse brute du serveur (Blob) :", response);
    
        // Vérifie que response.body est un Blob
        if (response.body instanceof Blob) {
          // Convertir le Blob en texte
          const reader = new FileReader();
          reader.onload = () => {
            try {
              // Parser la réponse en JSON
              const jsonResponse = JSON.parse(reader.result as string);
              console.log(" Réponse convertie en JSON :", jsonResponse);
    
              // Vérifie si la réponse est un tableau
              if (Array.isArray(jsonResponse)) {
                this.chatMessages = jsonResponse; // Assigner correctement un tableau de MessageResponse
              } else {
                console.error(" Erreur : La réponse n'est pas un tableau !");
              }
            } catch (error) {
              console.error(" Erreur lors du parsing du JSON :", error);
            }
          };
    
          reader.readAsText(response.body);  // Lire le Blob comme texte
        } else {
          console.error(" La réponse n'est pas un Blob !");
        }
      },
      error: (err) => {
        console.error(" Erreur lors de la récupération des messages :", err);
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
  

  private getSenderId(): string {
    if (this.selectedChat.senderId === this.keycloakService.userId) {
      return this.selectedChat.senderId as string;
    }
    return this.selectedChat.receiverId as string;
  }

  private getReceiverId(): string {
    if (this.selectedChat.senderId === this.keycloakService.userId) {
      return this.selectedChat.receiverId as string;
    }
    return this.selectedChat.senderId as string;
  }

  private scrollToBottom() {
    if (this.scrollableDiv) {
      const div = this.scrollableDiv.nativeElement;
      div.scrollTop = div.scrollHeight;
    }
  }

  private extractFileFromTarget(target: EventTarget | null): File | null {
    const htmlInputTarget = target as HTMLInputElement;
    if (target === null || htmlInputTarget.files === null) {
      return null;
    }
    return htmlInputTarget.files[0];
  }
}