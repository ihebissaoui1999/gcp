package com.example.chatintell.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ticketNotification")
public class TicketNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tNotificationid;
    private String message;
    private Date senDate;

  @ManyToOne( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private Ticket ticketn;
}
