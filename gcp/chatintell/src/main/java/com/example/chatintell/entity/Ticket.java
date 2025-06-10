package com.example.chatintell.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ticketid;
    private String title;
    private String description;
    private LocalDate creationdate;
    private LocalDate lastmodifier;
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType ;
    @Enumerated(EnumType.STRING)
    private StatusType statusType;
    @Enumerated(EnumType.STRING)
    private PriorityType priorityType;
    private String file;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "ticketn")
    private List<TicketNotification> ticketNotifications;
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonIgnore
    private List<User> users=new ArrayList<>();

    @OneToMany(mappedBy = "ticket",cascade = CascadeType.ALL)
    private List<FeedBack> feedBacks;

    private String createdBy;
    private Boolean accepted;

    @OneToMany(mappedBy = "ticket",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PurchaseRequest> purchaseRequests;

}
