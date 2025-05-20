import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Matiriel } from "../models";

@Injectable ({
  providedIn: 'root'
})
export class MatirielService{


    private apiUrl = 'http://backend.backend.svc.cluster.local:8081/api/v1/matiriel';

    constructor(private http :HttpClient){

    }

    getMatiriel(): Observable<Matiriel[]>  {
      // Assure-toi que 'responseType' est bien configuré
      return this.http.get<Matiriel[]>(`${this.apiUrl}/get`);
    }
    decreaseStock(ids: number[]): Observable<any> {
      return this.http.post(`${this.apiUrl}/decrease-stock`, ids);
    }
}
