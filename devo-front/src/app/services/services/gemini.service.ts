import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GeminiService {
  private apiUrl = '/api/v1/gemini/send-message'; // URL de ton backend

  constructor(private http: HttpClient) {}

  getResponse(message: string): Observable<any> {
    const requestBody = { message }; // Structure conforme à ce que le backend attend

    // Envoi de la requête au backend, sans la clé API dans l'URL
    return this.http.post<any>(this.apiUrl, requestBody);
  }
}
