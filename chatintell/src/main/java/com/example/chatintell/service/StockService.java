package com.example.chatintell.service;

import com.example.chatintell.entity.*;
import com.example.chatintell.gemini.GeminiService;
import com.example.chatintell.repository.MatirielRepository;
import com.example.chatintell.repository.StockRepository;
import com.example.chatintell.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Slf4j
public class StockService implements IserviceStock {
    StockRepository stockRepository;
    MatirielRepository matirielRepository;
     FileUploadService fileUpload;
UserRepository userRepository;

    @Override
    public Stock AddStock(String stockename, String stockdescription, StockType stockType, StockMatiriel stockMatiriel, MultipartFile imageFile, String idUser ) throws IOException {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID: " + idUser));

        Stock stock = new Stock();
        stock.setStockename(Stockename.valueOf(stockename));
        stock.setStockdescription(stockdescription);
        stock.setStockType(stockType);
        stock.setCreatedBy(idUser);
        stock.setStockMatiriel(stockMatiriel);
        String imageUrl = fileUpload.uploadFile(imageFile);
        stock.setImage(imageUrl);

        Stock newStock = stockRepository.save(stock);

        Matiriel matiriel = new Matiriel();
        matiriel.setMatirielname(stock.getStockename().name());
        matiriel.setMatirielDescription(stock.getStockdescription());
        matiriel.setImage(stock.getImage());
        matiriel.setMatrielstock(stock.getStockType().name());
        matiriel.setStock(newStock);
        matirielRepository.save(matiriel);

        return newStock;
    }

    }


