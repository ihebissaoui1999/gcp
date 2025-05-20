import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { ticketService } from '../../../services/services/ticket.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GetTicketComponent } from '../get-ticket/get-ticket.component';
import { FindTicketsAssignedToUserComponent } from "../find-tickets-assigned-to-user/find-tickets-assigned-to-user.component";
import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions } from '@fullcalendar/core/index.js';
import dayGridPlugin from '@fullcalendar/daygrid';
import {ToastrModule, ToastrService} from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BrowserModule } from '@angular/platform-browser';
import { KeycloakService } from '../../../utils/keycloak/keycloak.service';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { Notification } from '../../../Chats/component/pages/main/models/notification';

@Component({
  selector: 'app-addticket',
  imports: [SidebarComponent, CommonModule, FormsModule,FullCalendarModule,
     ToastrModule ],
  templateUrl: './addticket.component.html',
  styleUrl: './addticket.component.css'
})
export class AddticketComponent implements OnInit{


  socketClient: any = null;
  ticket: any = {};
  iduser: string | null = localStorage.getItem('userId');
  errorMessage: string | null = null;
  tickets: any[] = [];
  notificationsSubscription :any;
  unreadNotificationsSubscription =0;
  notificationss :Array<Notification> =[];


  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin], 
    initialView: 'dayGridMonth', 
    weekends: true, 
    events: [], 
    eventColor: '#e02e2e', 
    eventTextColor: '#e02e2e', 
    headerToolbar: {
      left: 'prev,next ',
      center: 'title',
      right: 'dayGridMonth,dayGridWeek'
    }
  };
  constructor(private service: ticketService ,private toastService :ToastrService,
 private keycloakService: KeycloakService,private cdr :ChangeDetectorRef

  ) {}
  ngOnInit(): void {
    this.getTicketbyuser();
    this.initWebSocket();}

  addTicket() {
    if (!this.iduser) {
      this.errorMessage = 'Utilisateur non connecté';
      return;
    }

    this.service.addTicket(this.ticket, this.iduser).subscribe(
      response => {
        console.log('Ticket ajouté avec succès', response);
        this.toastService.success("Ticket ajouté avec succès")
        this.ticket = {}; 
        this.cdr.detectChanges();
      },
      error => {
        console.error('Erreur ', error);
        this.errorMessage = 'Échec. Veuillez réessayer.';
        this.toastService.error(error.error.errorMessage,"Échec. Veuillez réessayer");

      }
    );
  }

  getTicketbyuser(){
    if (!this.iduser) {
      this.errorMessage = 'Utilisateur non connecté';
      return;
    }
    console.log('ID utilisateur:', this.iduser);
  
    this.service.getticketbyuser(this.iduser).subscribe(
      (response ) => {
        this.tickets = response ;
        console.log('Tickets reçus:', response );
        console.log(this.tickets); 
        this.cdr.detectChanges();
        this.calendarOptions.events = this.tickets.map(ticket => ({
          title: ticket.title,
          start: new Date(ticket.creationdate), // Assuming 'creationdate' is a valid date string
          description: ticket.description
        }));
      },
      (error) => {
        console.error('Erreur lors de la récupération des tickets:', error);
      }
    );
  }

  private initWebSocket() {
    if (this.keycloakService.keycloak.tokenParsed?.sub) {
      console.log('User ID:', this.keycloakService.keycloak.tokenParsed.sub);
      let ws = new SockJS('http://localhost:8081/ws');
      this.socketClient = Stomp.over(ws);
  
      const notificationSubUrl = `/user/${this.keycloakService.keycloak.tokenParsed?.sub}/notifications`;
  
      this.socketClient.connect(
        { 'Authorization': 'Bearer ' + this.keycloakService.keycloak.token },
        () => {
          this.socketClient.subscribe(
            notificationSubUrl,
            (message: any) => {
              console.log('Received notification message:', message.body);
              const notification: Notification = JSON.parse(message.body);
              
              if (notification) {
                this.notificationss.unshift(notification)
                switch (notification.notificationStatus) {
                  case 'ADDED':
                  case 'ACCEPTED':
                  case 'COMPLETED':
                    this.toastService.info(notification.content, notification.notificationStatus);
                    break;
                  default:
                    console.warn('Unknown notification status:', notification.notificationStatus);
                }
                this.unreadNotificationsSubscription ++;
              }
            },
            () => console.error('Error while connecting to WebSocket for notifications')
          );
        }
      );
    }
  }
  
}