package com.example.chatintell.dtoall;

import com.example.chatintell.entity.CategoryType;
import com.example.chatintell.entity.PriorityType;
import com.example.chatintell.entity.StatusType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketDto {
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
    private String createdBy;
    private Boolean accepted;
}
