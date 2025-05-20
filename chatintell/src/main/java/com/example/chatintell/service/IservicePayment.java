package com.example.chatintell.service;

import com.example.chatintell.entity.PaymentOrder;
import com.example.chatintell.entity.Stock;

import java.util.List;

public interface IservicePayment {
    public List<PaymentOrder> getPaymentOrder();
    //public PaymentOrder verifyPaymentOrder(Integer paymentid , Stock stock);
    public PaymentOrder verifyPaymentOrder(Integer paymentId,String idUser );
}
