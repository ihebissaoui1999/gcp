package com.example.chatintell.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "stock")
public class Stock implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer stockid;
    private String createdBy;
    private String stockdescription;
    private Integer stocknumbMatiriel;
    @Enumerated(EnumType.STRING)
    private Stockename  stockename;
    private String image;
    @Enumerated(EnumType.STRING)
    private  StockMatiriel stockMatiriel;
    @Enumerated(EnumType.STRING)
    private  StockType stockType;
    @OneToMany(mappedBy = "stock",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Matiriel> matiriels;

}
