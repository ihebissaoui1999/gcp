package com.example.chatintell.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name ="paymentorde")
public class PaymentOrder implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentid;
    private LocalDate paymentdate;
    private Boolean verified;
    private String verifiedBy;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Float paymentamount;
    @OneToMany(mappedBy = "paymentorder")
    @JsonIgnore
    private List<PurchaseRequest> purchaserequests;
    @ManyToOne
    private Supplier supplier;

}
