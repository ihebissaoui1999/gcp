package com.example.chatintell.dtoall;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Matirieldto {
    private Integer matirielid;
    private String matirielname;
    private String matirielDescription;
    private String matrielstock;
    private String image;
}
