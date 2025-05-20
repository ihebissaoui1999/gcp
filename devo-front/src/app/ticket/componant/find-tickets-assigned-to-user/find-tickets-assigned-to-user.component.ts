import { ChangeDetectorRef, Component, NgZone, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ticketService } from '../../../services/services/ticket.service';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { CommonModule } from '@angular/common';
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { KeycloakService } from '../../../utils/keycloak/keycloak.service';
import { Notification } from '../../../Chats/component/pages/main/models/notification';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { Ticket } from '../../../services/models';
import { Router } from '@angular/router';
@Component({
  selector: 'app-find-tickets-assigned-to-user',
  imports: [CommonModule,SidebarComponent,ToastrModule],
  templateUrl: './find-tickets-assigned-to-user.component.html',
  styleUrl: './find-tickets-assigned-to-user.component.css'
})
export class FindTicketsAssignedToUserComponent implements OnInit ,OnChanges{
  tickets: any[] = [];
  iduser: string | null = localStorage.getItem('userId');
  errorMessage: string | null = null;
  ticket: any = {};
  idTicket : number = this.ticket.idTicket;
  selectedTicket: any = null; // Stocke le ticket sélectionné
  socketClient: any = null;
  notificationsSubscription :any;
  unreadNotificationsSubscription =0;
  notificationss :Array<Notification> =[];
  tticket : Ticket ={};


constructor(private service :ticketService,private toastService :ToastrService,private keycloakService: KeycloakService,private router: Router, private cdr :ChangeDetectorRef,private ngZone: NgZone){

}
  ngOnChanges(changes: SimpleChanges): void {
    this.findTicketsAssignedToUser();
console.log(this.findTicketsAssignedToUser())
  }
  

  ngOnInit(): void {
    this.findTicketsAssignedToUser();
    this.initWebSocket();

  }


  findTicketsAssignedToUser(){
    if (!this.iduser) {
      this.errorMessage = 'Utilisateur non connecté';
      return;
    }
    console.log('ID utilisateur:', this.iduser);
  
    this.service.findTicketsAssignedToUser(this.iduser).subscribe(
      (response ) => {
        this.tickets = response ;
        console.log('Tickets recived:', response );
        console.log(this.tickets); 
       // this.cdr.detectChanges();
      },
      (error) => {
        console.error('Erreur lors de la récupération des tickets:', error);
      }
    );
  }
  assignTicketsToUser(idTicket: number,ticket :any){
    if (!this.iduser) {
      this.errorMessage = 'Utilisateur non connecté';
      return;
    }
    if (!idTicket) {
      console.error('Erreur : ID du ticket est undefined');
      this.errorMessage = 'Erreur : ID du ticket invalide';
      return;
    }
    if (ticket.accepted) {
      return;
    }
    this.service.assignTicketsToUser(this.iduser,idTicket).subscribe(

      (response) => {
        console.log('Ticket accepté avec succès', response);
        console.log("etat accepter ",ticket.accepted);
        ticket.accepted = true;
        console.log("etat accepter ",ticket.accepted);
         this.toastService.success("Ticket accepté avec succès")
        this.findTicketsAssignedToUser();
      //  this.cdr.detectChanges();


      },
      (error) => {
        console.error('Erreur lors de l acceptation du ticket', error);
        this.errorMessage = 'Erreur lors de l acceptation du ticket';
        this.toastService.error(error.error.errorMessage,"Erreur lors de l acceptation du ticket")
      }
    );
  }
  completeTicket(idTicket: number, ticket: any) {
    if (!this.iduser) {
      this.errorMessage = 'Utilisateur non connecté';
      return;
    }
    if (!idTicket) {
      console.error('Erreur : ID du ticket est undefined');
      this.errorMessage = 'Erreur : ID du ticket invalide';
      return;
    }
    
    // Make the service call to complete the ticket
    this.service.completeTicket(idTicket, this.iduser).subscribe(
      (response) => {
        console.log('Ticket complété avec succès', response);
        ticket.statusType = 'COMPLETED'; 
        this.toastService.success("Ticket complété avec succès");
        this.findTicketsAssignedToUser(); // Refresh the ticket list
       // this.cdr.detectChanges();
      },
      (error) => {
        console.error('Erreur lors de la complétion du ticket', error);
        this.errorMessage = 'Erreur lors de la complétion du ticket';
        this.toastService.error(error.error.errorMessage, "Erreur lors de la complétion du ticket");
      }
    );
  }
  
  selectTicket(ticket: Ticket) {
    this.selectedTicket = ticket;
    localStorage.setItem('ticketid', ticket.ticketid?.toString() ?? '');
  }
  closeTicketDetails() {
    this.selectedTicket = null;
  }



  // Fonction pour demander un achat
  requestPurchase() {
    if (this.selectedTicket) {
      localStorage.setItem('ticketid', this.selectedTicket.ticketid.toString());
      this.router.navigate(['/Purchase']); 
    }
  }

private initWebSocket() {
    if (this.keycloakService.keycloak.tokenParsed?.sub) {
      console.log('User ID:', this.keycloakService.keycloak.tokenParsed.sub);
      let ws = new SockJS('http://localhost:8081/ws');
      this.socketClient = Stomp.over(ws);

      const notificationSubUrl = `/user/${this.keycloakService.keycloak.tokenParsed?.sub}/notifications`;

      this.socketClient.connect(
        { Authorization: 'Bearer ' + this.keycloakService.keycloak.token },
        () => {
          this.socketClient.subscribe(
            notificationSubUrl,
            (message: any) => {
              this.ngZone.run(() => {
                console.log('Received notification message:', message.body);
                const notification: Notification = JSON.parse(message.body);

                if (notification) {
                  switch (notification.notificationStatus) {
                    case 'ADDED':
                      this.toastService.info(notification.content, notification.notificationStatus);
                      this.findTicketsAssignedToUser(); // Refresh ticket list
                      break;
                    case 'ACCEPTED':
                    case 'COMPLETED':
                      this.toastService.info(notification.content, notification.notificationStatus);
                      this.findTicketsAssignedToUser(); // Refresh ticket list
                      break;
                    default:
                      console.warn('Unknown notification status:', notification.notificationStatus);
                  }
                }
              });
            },
            () => console.error('Error while connecting to WebSocket for notifications')
          );
        }
      );
    }
  }
}
