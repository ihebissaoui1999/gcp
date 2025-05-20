import { Component, input, InputSignal, output, signal } from '@angular/core';

import { DatePipe } from '@angular/common';
import { ChatResponse, UserResponse } from '../../../services/models';
import { ChatService, UserService } from '../../../services/services';
import { KeycloakService } from '../../../utils/keycloak/keycloak.service';

@Component({
  selector: 'app-chat',
  imports: [DatePipe],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent {
  chats: InputSignal<ChatResponse[]> = input<ChatResponse[]>([]);
  //@Input chats  =signal<ChatResponse[]>([]);
  searchNewContact = false;
  contacts: Array<UserResponse> = [];
  chatSelected = output<ChatResponse>();
  selectedChat: ChatResponse | null = null;
  chatsList: ChatResponse[] = [];


  constructor(
    private chatService: ChatService,
    private userService: UserService,
    private keycloakService: KeycloakService
  ) {
  }

  searchContact() {
    this.userService.getAllUsers()
      .subscribe({
        next: (users) => {
          this.contacts = users;
          this.searchNewContact = true;
        }
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
        console.log("✅ Chat sélectionné :", this.selectedChat);
  
        this.searchNewContact = false;
        this.chatSelected.emit(chat);
      },
      error: (err) => {
        console.error("❌ Erreur lors de la création du chat :", err);
      }
    });
  }
  


  chatClicked(chat: ChatResponse) {
    this.chatSelected.emit(chat);
  }

  wrapMessage(lastMessage: string | undefined): string {
    if (lastMessage && lastMessage.length <= 20) {
      return lastMessage;
    }
    return lastMessage?.substring(0, 17) + '...';
  }
}