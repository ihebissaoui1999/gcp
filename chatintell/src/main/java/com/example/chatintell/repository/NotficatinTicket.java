package com.example.chatintell.repository;

import com.example.chatintell.entity.TicketNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotficatinTicket extends JpaRepository<TicketNotification, Long> {

}
