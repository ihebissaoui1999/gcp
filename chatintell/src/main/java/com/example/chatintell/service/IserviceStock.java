package com.example.chatintell.service;

import com.example.chatintell.entity.Stock;
import com.example.chatintell.entity.StockMatiriel;
import com.example.chatintell.entity.StockType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IserviceStock {
   // Stock AddStock(Stock stock , MultipartFile imageFile) throws IOException ;
   public Stock AddStock(String stockename, String stockdescription, StockType stockType, StockMatiriel stockMatiriel, MultipartFile imageFile , String idUser) throws IOException ;
}
