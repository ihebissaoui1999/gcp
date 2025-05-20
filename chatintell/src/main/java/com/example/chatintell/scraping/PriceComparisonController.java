package com.example.chatintell.scraping;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/comparison")
public class PriceComparisonController {
    PriceComparisonService priceComparisonService;
    @GetMapping("/api/best-prices")
    public Map<String, Product> getBestPrices() {
        return priceComparisonService.comparePrices();
    }
}
