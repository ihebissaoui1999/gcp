import { Component, OnInit } from '@angular/core';
import {ToastrModule, ToastrService} from 'ngx-toastr';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';
import { purchaseService } from '../../services/services/purchaserequest.service';
import { purchaseRequest } from '../../services/models/purchaseRequest';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SpinnerComponent } from '../../Chats/component/Shared/spinner/spinner.component';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { Notification } from '../../Chats/component/pages/main/models/notification';

@Component({
  selector: 'app-create-purchase-request',
  imports: [SidebarComponent,CommonModule,FormsModule,SpinnerComponent,ToastrModule],
  templateUrl: './create-purchase-request.component.html',
  styleUrl: './create-purchase-request.component.css'
})
export class CreatePurchaseRequestComponent implements OnInit {
  ticketid: number | null = null;
  errorMessage: string | null = null;
  purchaseRequest: purchaseRequest = {};
  notificationsSubscription :any;
  unreadNotificationsSubscription =0;
  notificationss :Array<Notification> =[];
  isLoading=false;
  iduser: string | null = localStorage.getItem('userId');
  socketClient: any = null;


  constructor(
    private service: purchaseService,
    private toastService: ToastrService,
    private keycloakService: KeycloakService
  ) {}

  ngOnInit(): void {
    this.initWebSocket();
    const ticketIdString = localStorage.getItem('ticketid');
    if (ticketIdString) {
      const parsed = parseInt(ticketIdString, 10);
      this.ticketid = isNaN(parsed) ? null : parsed;
    }
  }

  createPurchaseRequest(): void {
    if (!this.ticketid) {
      this.errorMessage = 'Ticket not selected';
      return;
    }
      if (!this.iduser) {
        this.errorMessage = 'Utilisateur non connecté';
        return;
      }
    this.isLoading = true;
    this.service.createPurchaseRequest(this.purchaseRequest, this.ticketid ,this.iduser).subscribe({
      next: (response) => {
        this.toastService.success('Purchase request created successfully!');
        console.log("reponse ",response);
        this.isLoading = false;
       
      },
      error: (error) => {
        this.toastService.error('Failed to create purchase request.');
        this.errorMessage = 'Error during request creation';
        console.error(error);
        this.isLoading = false;
       
      }
    });
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
