import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AiService {
  private apiUrl = '/askAgent';

  constructor(private http: HttpClient) {}

  askAgent(question: string): Observable<HttpEvent<any>> {
    const url = `${this.apiUrl}?question=${encodeURIComponent(question)}`;
    return this.http.get(url, {
      responseType: 'text',
      observe: 'events',
      reportProgress: true
    });
  }
}
