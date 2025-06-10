package com.example.chatintell.controller;

import com.example.chatintell.entity.PaymentOrder;
import com.example.chatintell.service.IservicePayment;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/Payment")
public class PaymentController {
    IservicePayment servicePayment;

    @GetMapping("/get")
    public List<PaymentOrder> getPaymentOrder(){
        return servicePayment.getPaymentOrder();
    }
    @GetMapping("/addd/{paymentId}/{idUser}")
    public PaymentOrder verifyPaymentOrder(@PathVariable ("paymentId") Integer paymentId,@PathVariable ("idUser") String idUser){
        return servicePayment.verifyPaymentOrder(paymentId,idUser);
    }



}
