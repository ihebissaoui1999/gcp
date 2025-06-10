package com.example.chatintell.repository;

import com.example.chatintell.entity.Stock;
import com.example.chatintell.entity.StockType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    List<Stock> findByStockType(StockType stockType);
}
