package com.example.chatintell.dtoall;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentOrderDto {
    private Integer paymentid;
    private LocalDate paymentdate;
    private Boolean verified;
    private String verifiedBy;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Float paymentamount;
}
