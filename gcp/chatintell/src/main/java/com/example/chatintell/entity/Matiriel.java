package com.example.chatintell.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "matiriel")
public class Matiriel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer matirielid;
    private String matirielname;
    private String matirielDescription;
    private String matrielstock;
    private String image;
    @ManyToOne
    private User user;
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Stock stock;


}
