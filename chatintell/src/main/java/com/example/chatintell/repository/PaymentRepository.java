package com.example.chatintell.repository;

import com.example.chatintell.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentOrder,Integer> {
@Query("select p from PaymentOrder p order by p.paymentdate desc ")
    List<PaymentOrder> findAllByOrderByPaymentdate();
}
