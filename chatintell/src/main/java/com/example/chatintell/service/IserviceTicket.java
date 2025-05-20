package com.example.chatintell.service;

import com.example.chatintell.entity.Ticket;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IserviceTicket {
    public Ticket ajouterTicket(Ticket ticket, String idUser);
    public Ticket updateTicket(Ticket ticket, Integer idTicket, String idUser);

    public List<Ticket> getTickets(String idUser);
    public List<Ticket> findTicketsAssignedToUser(String idUser);
    public Ticket assignTicketsToUser(String idUser, Integer idTicket);
    public Ticket completeTicket(Integer idTicket, String idUser);
  //  void sendFeedbackEmailAsync(Ticket ticket, MultipartFile file);
  //void ajouterFeedback(String idUser, Integer idTicket, MultipartFile file);

    public List<Ticket> getTickets(Boolean accepeted);
    public List<Ticket> getTicketsUS( String  idUser);
    public List<Ticket> getAllTickets();
    public List<Ticket> getTicketsAssignedTooneUser(String idUser);
}
