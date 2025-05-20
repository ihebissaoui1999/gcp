package com.example.chatintell.controller;

import com.example.chatintell.entity.Ticket;
import com.example.chatintell.service.IserviceTicket;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/ticket")
public class TicketController {
    IserviceTicket iserviceTicket;

    @PostMapping("/ajouter/{id}")
    public Ticket ajouterTicket(@RequestBody Ticket ticket ,@PathVariable("id") String id) {
        return iserviceTicket.ajouterTicket(ticket,id);
    }
    @PutMapping("/update/{idTicket}/{idUser}")
    public Ticket updateTicket(@RequestBody Ticket ticket, @PathVariable("idTicket") Integer idTicket,@PathVariable("idUser") String idUser){
        return iserviceTicket.updateTicket(ticket,idTicket,idUser);
    }
    @GetMapping("/get/{idUser}")
    public List<Ticket> getTickets(@PathVariable ("idUser") String idUser){
        return iserviceTicket.getTickets(idUser);
    }

    @GetMapping("getticketuser/{idUser}")
    public List<Ticket> findTicketsAssignedToUser(@PathVariable ("idUser") String idUser){
        return iserviceTicket.findTicketsAssignedToUser(idUser);
    }
    @GetMapping("/AcceptTicket/{idUser}/{idTicket}")
    public  Ticket assignTicketsToUser(@PathVariable ("idUser") String idUser,@PathVariable ("idTicket")  Integer idTicket){
        return iserviceTicket.assignTicketsToUser(idUser,idTicket);
    }

    @GetMapping("/completeTicket/{idTicket}/{idUser}")
    public  Ticket completeTicket(@PathVariable ("idTicket")  Integer idTicket,@PathVariable ("idUser") String idUser) {
        return iserviceTicket.completeTicket(idTicket, idUser);
    }

    @GetMapping("/getaccep/{accepeted}")
    public List<Ticket> getTickets( @PathVariable ("accepeted") Boolean accepeted){
        return iserviceTicket.getTickets(accepeted);
    }
    @GetMapping("/ticketget/{idUser}")
    public List<Ticket> getTicket(@PathVariable ("idUser") String idUser){
        return  iserviceTicket.getTicketsUS(idUser);
    }
    @GetMapping("/export")
    public ResponseEntity<String> exportTickets() {
        List<Ticket> tickets = iserviceTicket.getAllTickets();
        StringBuilder csv = new StringBuilder("ticketid,title,description,createdBy,accepted\n");
        for (Ticket ticket : tickets) {
            csv.append(ticket.getTicketid()).append(",")
                    .append("\"").append(ticket.getTitle().replace("\"", "\"\"")).append("\"").append(",")
                    .append("\"").append(ticket.getDescription().replace("\"", "\"\"")).append("\"").append(",")
                    .append(ticket.getCreatedBy()).append(",")
                    .append(ticket.getAccepted()).append("\n");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tickets.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv.toString());
    }
    @GetMapping("/ticketss/{idUser}")
    public List<Ticket> getTicketsAssignedTooneUser(@PathVariable ("idUser") String idUser){
        return iserviceTicket.getTicketsAssignedTooneUser(idUser);
    }
}
