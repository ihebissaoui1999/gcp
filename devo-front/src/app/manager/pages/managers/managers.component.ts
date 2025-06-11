import { ChangeDetectorRef, Component, NgZone, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { paymentService } from '../../../services/services/payment.service';
import { Payment } from '../../../services/models/payment';
import { CommonModule } from '@angular/common';
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { KeycloakService } from '../../../utils/keycloak/keycloak.service';
import { Notification } from '../../../Chats/component/pages/main/models/notification';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
@Component({
  selector: 'app-managers',
  imports: [SidebarComponent,CommonModule,ToastrModule],
  templateUrl: './managers.component.html',
  styleUrl: './managers.component.css'
})
export class ManagersComponent implements OnInit {

  payment :Payment [] =[];
  paymentt :Payment  ={};
  iduser: string | null = localStorage.getItem('userId');

notificationsSubscription :any;
  unreadNotificationsSubscription =0;
  notificationss :Array<Notification> =[];

  errorMessage: string | null = null;
  socketClient: any = null;


  constructor(private service :paymentService ,private toastService: ToastrService,private keycloakService: KeycloakService, private ngZone: NgZone, private crd : ChangeDetectorRef){
}
ngOnInit(): void {
  this.loadPayments();
  this.initWebSocket();

}

loadPayments(): void {
  this.service.getPaymentOrder().subscribe({
    next: (data) => {
      this.payment = data;
      this.crd.detectChanges();
    },
    error: (err) => {
      console.error('Erreur lors du chargement des paiements:', err);
      this.toastService.error('Erreur lors du chargement des paiements.');
    }
  });
}


verifyPayment(paymentid?: number): void {
  if (paymentid === undefined) {
    console.error('paymentId est undefined');
    this.toastService.error('ID de paiement non défini.');
    return;
  }
  if (!this.iduser) {
    this.errorMessage = 'Utilisateur non connecté';
    return;
  }

  this.service.verifyPaymentOrder(paymentid,this.iduser).subscribe({
    next: (updatedPayment) => {
      this.loadPayments(); // Reload payments to reflect updated status
      this.toastService.success('Paiement vérifié avec succès !');
    },
    error: (err) => {
      console.error('Erreur lors de la vérification du paiement:', err);
      this.toastService.error('Erreur lors de la vérification du paiement.');
    }
  });
}

  private initWebSocket() {
      if (this.keycloakService.keycloak.tokenParsed?.sub) {
        console.log('User ID:', this.keycloakService.keycloak.tokenParsed.sub);
        let ws = new SockJS('/ws');
        this.socketClient = Stomp.over(ws);

        const notificationSubUrl = `/user/${this.keycloakService.keycloak.tokenParsed?.sub}/notifications`;

        this.socketClient.connect(
          { 'Authorization': 'Bearer ' + this.keycloakService.keycloak.token },
          () => {
            this.socketClient.subscribe(
              notificationSubUrl,
              (message: any) => {
                this.ngZone.run(()=> {
                console.log('Received notification message:', message.body);
                const notification: Notification = JSON.parse(message.body);

                if (notification) {
                  switch (notification.notificationStatus) {
                    case 'ADDED':
                      this.loadPayments();
                      this.crd.detectChanges();
                      break;
                    case 'ACCEPTED':
                    case 'COMPLETED':
                      this.loadPayments();
                      this.toastService.info(notification.content, notification.notificationStatus);
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
