package com.example.chatintell.repository;

import com.example.chatintell.entity.PurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<PurchaseRequest,Integer> {
    List<PurchaseRequest> findByCreatedBy(String createdBy);
}
