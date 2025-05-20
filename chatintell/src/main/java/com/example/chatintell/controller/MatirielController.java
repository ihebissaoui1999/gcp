package com.example.chatintell.controller;

import com.example.chatintell.entity.Matiriel;
import com.example.chatintell.service.IserviceMatiriel;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/matiriel")
public class MatirielController {
    private IserviceMatiriel serviceMatiriel;

    @PostMapping("/add")
    public Matiriel add(@RequestBody Matiriel matiriel) {
        return serviceMatiriel.ajoutMatiriel(matiriel);
    }

    @GetMapping("/get")
    public List<Matiriel> get() {
        return serviceMatiriel.getMatiriel();
    }
    @PostMapping("/decrease-stock")
    public void decreaseStock(@RequestBody List<Integer> ids) {
        serviceMatiriel.decreaseStock(ids);
    }
}
