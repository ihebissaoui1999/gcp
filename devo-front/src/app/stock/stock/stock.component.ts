import { Component, OnInit } from '@angular/core';
import {  FormsModule } from '@angular/forms';
import { stockService } from '../../services/services/stock';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';
import {ToastrModule, ToastrService} from 'ngx-toastr';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';
import { Notification } from '../../Chats/component/pages/main/models/notification';


@Component({
  selector: 'app-stock',
  imports: [CommonModule,FormsModule,SidebarComponent,ToastrModule],
  templateUrl: './stock.component.html',
  styleUrl: './stock.component.css'
})
export class StockComponent implements OnInit{
  iduser: string | null = localStorage.getItem('userId');

  stock = {
    stockename: '',
    stockdescription: '',
    stockType: '',
    stockMatiriel: ''
  };

  selectedFile: File | null = null;
  errorMessage: string | null = null;
    notificationsSubscription :any;
    unreadNotificationsSubscription =0;
    notificationss :Array<Notification> =[];
    socketClient: any = null;


  constructor(private stockService: stockService,private toastService :ToastrService,
   private keycloakService: KeycloakService) {}
  ngOnInit(): void {
    this.initWebSocket();
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  onSubmit(): void {
    if (!this.iduser) {
      this.errorMessage = 'Utilisateur non connecté';
      return;
    }

    if (this.selectedFile) {
      const formData = new FormData();
      formData.append('stockename', this.stock.stockename);
      formData.append('stockdescription', this.stock.stockdescription);
      formData.append('stockType', this.stock.stockType);
      formData.append('stockMatiriel', this.stock.stockMatiriel);
      formData.append('image', this.selectedFile);

      this.stockService.addstock(formData, this.iduser).subscribe({
        next: res => console.log(' Stock ajouté :', res),
        error: err => console.error(' Erreur :', err)
      });
    } else {
      this.errorMessage = 'Veuillez sélectionner une image.';
    }
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