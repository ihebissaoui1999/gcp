package com.example.chatintell.repository;

import com.example.chatintell.entity.Matiriel;
import com.example.chatintell.entity.StockType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface MatirielRepository extends JpaRepository<Matiriel, Integer> {

   /* @Query("select m.matirielname ,s.stockType from Matiriel m join m.stock s")
    List<Matiriel> findAllMatirielAndStockType();*/

    //Matiriel findByStock_StockTypeAndMatiriel(StockType stockType, Matiriel matiriel);

    //Matiriel findByMatirielnameAndStock_StockType(String matirielname, StockType stockStockType);




}
