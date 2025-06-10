package com.example.chatintell.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "purchaserequest")
public class PurchaseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer purchaseid;
    private LocalDate purchasedate;
    private String createdBy;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    private PurchaseStatus purchaseStatus;
    @ManyToOne
    private PaymentOrder paymentorder;
    @ManyToOne
    private Ticket ticket;





}
