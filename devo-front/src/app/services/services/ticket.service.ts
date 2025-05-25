import { HttpClient, HttpHeaders } from "@angular/common/http";  // <-- add HttpHeaders import
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Ticket } from "../models";

@Injectable ({
  providedIn: 'root'
})
export class ticketService {
  private apiUrl = '/api/v1/ticket';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('keycloak-token') || '';
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  addTicket(ticket: any, iduser: string): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/ajouter/${iduser}`,
      ticket,
      { headers: this.getAuthHeaders() }
    );
  }

  getticketbyuser(iduser: string): Observable<any> {
    return this.http.get<any[]>(
      `${this.apiUrl}/get/${iduser}`,
      { headers: this.getAuthHeaders() }
    );
  }

  findTicketsAssignedToUser(iduser: string): Observable<any> {
    return this.http.get<any[]>(
      `${this.apiUrl}/getticketuser/${iduser}`,
      { headers: this.getAuthHeaders() }
    );
  }

  assignTicketsToUser(iduser: string, idTicket: number): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/AcceptTicket/${iduser}/${idTicket}`,
      { headers: this.getAuthHeaders() }
    );
  }

  completeTicket(idTicket: number, iduser: string): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/completeTicket/${idTicket}/${iduser}`,
      { headers: this.getAuthHeaders() }
    );
  }

  getTickets(status: boolean): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(
      `${this.apiUrl}/getaccep/${status}`,
      { headers: this.getAuthHeaders() }
    );
  }

  getTicketsByUser(idUser: string): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(
      `${this.apiUrl}/ticketget/${idUser}`,
      { headers: this.getAuthHeaders() }
    );
  }

  downloadTickets(): Observable<Blob> {
    return this.http.get(
      `${this.apiUrl}/export`,
      { responseType: 'blob', headers: this.getAuthHeaders() }
    );
  }

  getTicketsAssignedTooneUser(idUser: string): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(
      `${this.apiUrl}/ticketss/${idUser}`,
      { headers: this.getAuthHeaders() }
    );
  }
}
