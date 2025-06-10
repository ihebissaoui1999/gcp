package com.example.chatintell.controller;

import com.example.chatintell.entity.PaymentOrder;
import com.example.chatintell.entity.PurchaseRequest;
import com.example.chatintell.service.IservicePurchase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/purchaseReq")
public class PurchaseReqController {
    IservicePurchase servicePurchase;

    @PostMapping("/add/{ticketid}/{idUser}")
    public PurchaseRequest createPurchaseRequest(@RequestBody PurchaseRequest purchaseRequest, @PathVariable ("ticketid") Integer ticketid, @PathVariable("idUser") String idUser){
        return servicePurchase.createPurchaseRequest(purchaseRequest, ticketid, idUser);
    }

}
