package com.example.chatintell.scraping;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PriceComparisonService {
@Autowired
    ScrapingService scrapingService;

    public Map<String, Product> comparePrices() {
        List<Product> allProducts = scrapingService.scrapeAllSites(); // Implement this to return List<Product>
        Map<String, Product> bestPrices = new HashMap<>();

        for (Product product : allProducts) {
            String productName = product.getName().toLowerCase(); // Normalize for comparison
            if (!bestPrices.containsKey(productName) || bestPrices.get(productName).getPrice() > product.getPrice()) {
                bestPrices.put(productName, product);
            }
        }
        return bestPrices;
    }
}