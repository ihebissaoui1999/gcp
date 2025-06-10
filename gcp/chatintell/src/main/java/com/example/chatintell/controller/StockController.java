package com.example.chatintell.controller;

import com.example.chatintell.entity.Stock;
import com.example.chatintell.entity.StockMatiriel;
import com.example.chatintell.entity.StockType;
import com.example.chatintell.service.IserviceStock;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/stock")
public class StockController {
   private IserviceStock stockservice;

    @PostMapping("/ajouter/{idUser}")
    public ResponseEntity<Stock> ajouterStock(
            @RequestParam("stockename") String stockename,
            @RequestParam("stockdescription") String stockdescription,
            @RequestParam("stockType") StockType stockType,
            @RequestParam("stockMatiriel") StockMatiriel stockMatiriel,
            @RequestParam("image") MultipartFile imageFile,
            @PathVariable("idUser") String idUser
    ) throws IOException {

        // appel du service métier
        Stock stock = stockservice.AddStock(stockename, stockdescription, stockType, stockMatiriel, imageFile,idUser);

        return ResponseEntity.ok(stock);
    }
}

