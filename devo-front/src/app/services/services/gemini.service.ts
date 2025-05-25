import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class GeminiService {
  private apiUrl = 'http://backend.backend.svc.cluster.local:8081/api/v1/gemini/send-message'; // URL de ton backend

  constructor(private http: HttpClient) {}

  getResponse(message: string): Observable<any> {
    const token = localStorage.getItem('keycloak-token');
    console.log('Using token:', token);
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
    const requestBody = { message }; // Structure conforme à ce que le backend attend
    // Envoi de la requête au backend, sans la clé API dans l'URL
    return this.http.post<any>(this.apiUrl, requestBody, { headers });
  }

}
