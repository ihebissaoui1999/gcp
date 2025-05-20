package com.example.chatintell.service;

import com.example.chatintell.entity.PaymentOrder;
import com.example.chatintell.entity.PurchaseRequest;

public interface IservicePurchase {
    PurchaseRequest createPurchaseRequest(PurchaseRequest purchaseRequest , Integer ticketid,String idUser);
}
