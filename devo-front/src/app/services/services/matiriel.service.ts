import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Matiriel } from "../models";

@Injectable ({
  providedIn: 'root'
})
export class MatirielService{


    private apiUrl = '/api/v1/matiriel';

    constructor(private http :HttpClient){

    }

    getMatiriel(): Observable<Matiriel[]>  {
      const token = localStorage.getItem('keycloak-token');
      console.log('Using token:', token);
      const headers = new HttpHeaders({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      });
      // Assure-toi que 'responseType' est bien configuré
      return this.http.get<Matiriel[]>(`${this.apiUrl}/get`);
    }
    decreaseStock(ids: number[]): Observable<any> {
      return this.http.post(`${this.apiUrl}/decrease-stock`, ids);
    }
}
