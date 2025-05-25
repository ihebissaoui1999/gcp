import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Ticket } from "../models";

@Injectable ({
  providedIn: 'root'
})
export class ticketService {
  private apiUrl = '/api/v1/ticket';

  constructor(private http: HttpClient) {}

  addTicket(ticket :any ,iduser:string):Observable<any>{

    return this.http.post(`${this.apiUrl}/ajouter/${iduser}`, ticket);
  }

  getticketbyuser(iduser:string):Observable<any>{
    return this.http.get<any[]>(`${this.apiUrl}/get/${iduser}`)
  }
  findTicketsAssignedToUser(iduser : string):Observable<any>{
    return this.http.get<any[]>(`${this.apiUrl}/getticketuser/${iduser}`)
  }

  assignTicketsToUser(iduser:string,idTicket:number):Observable<any>{
    return this.http.get(`${this.apiUrl}/AcceptTicket/${iduser}/${idTicket}`)
  }
  completeTicket(idTicket:number,iduser:string):Observable<any>{
    return this.http.get(`${this.apiUrl}/completeTicket/${idTicket}/${iduser}`)
  }
getTickets(status: boolean): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/getaccep/${status}`);
  }

  getTicketsByUser(idUser: string): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/ticketget/${idUser}`);
  }
downloadTickets(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/export`, { responseType: 'blob' });
  }
  getTicketsAssignedTooneUser(idUser: string): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}/ticketss/${idUser}`);
  }
}
