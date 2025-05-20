import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Payment } from "../models/payment";

@Injectable ({
  providedIn: 'root'
})
export class paymentService {
  private apiUrl = 'http://backend.backend.svc.cluster.local:8081/api/v1/Payment';


  constructor(private http: HttpClient) {}

  getPaymentOrder():Observable<Payment[]>{
    return this.http.get<Payment[]>(`${this.apiUrl}/get`);
  }

  verifyPaymentOrder (paymentid: number ,iduser :string) : Observable<Payment> {
    return this.http.get(`${this.apiUrl}/addd/${paymentid}/${iduser}`);
  }

}
