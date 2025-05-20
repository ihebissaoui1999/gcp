package com.example.chatintell.service;

import com.example.chatintell.entity.Matiriel;
import com.example.chatintell.entity.Stock;
import com.example.chatintell.entity.StockType;
import com.example.chatintell.repository.MatirielRepository;
import com.example.chatintell.repository.StockRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class MatirielService implements IserviceMatiriel {

    MatirielRepository matirielRepository;
    StockRepository stockRepository;

    @Override
    public Matiriel ajoutMatiriel(Matiriel matiriel) {
        Matiriel matiriel1 = matirielRepository.save(matiriel);
        Stock stock1 = new Stock();
        //stock1.setStockname(matiriel1.getMatirielname());
        stock1.setStockType(StockType.ENSTOCK);
        stockRepository.save(stock1);
        matiriel1.setStock(stock1);
        return matiriel1;
    }

    @Override

   public List<Matiriel> getMatiriel() {
        List<Matiriel> matiriel = matirielRepository.findAll();
        return matiriel;
    }
    public void decreaseStock(List<Integer> ids) {
        for (Integer id : ids) {
            Matiriel matiriel = matirielRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Material not found: " + id));
            if (matiriel.getStock().getStocknumbMatiriel() > 0) {
                var stock = matiriel.getStock();
                stock.setStocknumbMatiriel(stock.getStocknumbMatiriel() - 1);
                stockRepository.save(stock);
                matirielRepository.save(matiriel);
            }
        }
    }

    @Override
    public List<Matiriel> selectedMateriel(List<Integer> matirielid) {
        List<Matiriel> m = matirielRepository.findAllById(matirielid).stream().toList();

        for (Matiriel matiriel : m) {
            if(matiriel.getStock().getStocknumbMatiriel()< 1){
                matiriel.getStock().setStockType(StockType.CLEAR);
            }
            if (matiriel.getStock().getStocknumbMatiriel()> 1) {
                matiriel.getStock().setStockType(StockType.ENSTOCK);
            }
        }
        return List.of();
    }


}
