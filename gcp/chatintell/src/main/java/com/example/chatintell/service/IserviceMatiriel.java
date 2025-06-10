package com.example.chatintell.service;

import com.example.chatintell.entity.Matiriel;
import com.example.chatintell.entity.Stock;

import java.util.List;

public interface IserviceMatiriel {
    Matiriel ajoutMatiriel(Matiriel matiriel);
    List<Matiriel> getMatiriel();
    //public Boolean isMaterielInStock(String materielName, String stockName);

   // public String getMatiriel();
    List <Matiriel> selectedMateriel (List<Integer> matirielid);
    public void decreaseStock(List<Integer> ids);
}
