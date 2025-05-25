import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable ({
  providedIn: 'root'
})
export class purchaseService {
  private apiUrl = 'http://backend.backend.svc.cluster.local:8081/api/v1/purchaseReq';


  constructor(private http: HttpClient) {}

  createPurchaseRequest(purchaseRequest :any ,ticketid:number ,iduser :string):Observable<any>{

    return this.http.post(`${this.apiUrl}/add/${ticketid}/${iduser}`, purchaseRequest);
  }
}
