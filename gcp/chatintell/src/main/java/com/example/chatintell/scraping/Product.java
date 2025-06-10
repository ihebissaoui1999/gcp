package com.example.chatintell.scraping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter

public class Product {

    private String name;
    private double price;
    private String site;
    private String url;
}
