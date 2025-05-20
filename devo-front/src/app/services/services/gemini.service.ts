import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class GeminiService {
  private apiUrl = 'http://35.201.225.147/api/v1/gemini/send-message'; // URL de ton backend

  constructor(private http: HttpClient) {}

  getResponse(message: string): Observable<any> {
    const token = localStorage.getItem('keycloak-token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
    const requestBody = { message }; // Structure conforme à ce que le backend attend
    // Envoi de la requête au backend, sans la clé API dans l'URL
    return this.http.post<any>(this.apiUrl, requestBody, { headers });
  }

}
