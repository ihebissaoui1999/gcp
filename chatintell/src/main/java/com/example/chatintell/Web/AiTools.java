package com.example.chatintell.Web;

import com.example.chatintell.dtoall.Matirieldto;
import com.example.chatintell.dtoall.PaymentOrderDto;
import com.example.chatintell.dtoall.TicketDto;
import com.example.chatintell.entity.Matiriel;
import com.example.chatintell.entity.PaymentOrder;
import com.example.chatintell.entity.Ticket;
import com.example.chatintell.entity.User;
import com.example.chatintell.repository.*;
import com.example.chatintell.service.*;
import dev.langchain4j.agent.tool.Tool;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class AiTools {

    private final IserviceTicket iserviceTicket;
    IserviceMatiriel serviceMatiriel;
    IservicePurchase servicePurchase;
    IserviceTicket serviceTicket;
    IservicePayment servicePayment;
    IserviceStock serviceStock;
    TicketRepository ticketRepository;
    UserRepository userRepository;

    @Tool("Ajouter un ticket")
    public TicketDto ajouterTicket(Ticket ticket, String id) {
        Ticket result = iserviceTicket.ajouterTicket(ticket, id);
        return convertToDto(result);
    }
    @Tool("Obtenir tous les tickets")
    public List<TicketDto> getAllTickets() {
        List<Ticket> tickets = iserviceTicket.getAllTickets(); // récupération des entités
        return tickets.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Tool("Mettre à jour un ticket")
    public TicketDto updateTicket(Ticket ticket, Integer idTicket, String idUser) {
        Ticket result = iserviceTicket.updateTicket(ticket, idTicket, idUser);
        return convertToDto(result);
    }

    @Tool("Obtenir les tickets d'un utilisateur")
    public List<TicketDto> getTickets(String idUser) {
        return iserviceTicket.getTickets(idUser).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Tool("Trouver les tickets assignés à un utilisateur")
    public List<TicketDto> findTicketsAssignedToUser(String idUser) {
        return iserviceTicket.findTicketsAssignedToUser(idUser).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Tool("Assigner un ticket à un utilisateur")
    public TicketDto assignTicketsToUser(String idUser, Integer idTicket) {
        Ticket result = iserviceTicket.assignTicketsToUser(idUser, idTicket);
        return convertToDto(result);
    }

    @Tool("Compléter un ticket")
    public TicketDto completeTicket(Integer idTicket, String idUser) {
        Ticket result = iserviceTicket.completeTicket(idTicket, idUser);
        return convertToDto(result);
    }

    @Tool("Obtenir les tickets par statut accepted")
    public List<TicketDto> getTickets(Boolean accepted) {
        return iserviceTicket.getTickets(accepted).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Tool("Obtenir les tickets US d'un utilisateur")
    public List<TicketDto> getTicketsUS(String idUser) {
        return iserviceTicket.getTicketsUS(idUser).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Tool("Obtenir les tickets assignés à un seul utilisateur")
    public List<TicketDto> getTicketsAssignedTooneUser(String idUser) {
        User u = new User();
        //String firstName = u.getFirstName();
        return iserviceTicket.getTicketsAssignedTooneUser(idUser.replace(idUser,u.getFirstName())).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Tool("Exporter tous les tickets au format CSV (chaîne de texte)")
    public String exportTickets() {
        List<Ticket> tickets = iserviceTicket.getAllTickets();
        StringBuilder csv = new StringBuilder("ticketid,title,description,createdBy,accepted\n");
        for (Ticket ticket : tickets) {
            csv.append(ticket.getTicketid()).append(",")
                    .append("\"").append(ticket.getTitle().replace("\"", "\"\"")).append("\"").append(",")
                    .append("\"").append(ticket.getDescription().replace("\"", "\"\"")).append("\"").append(",")
                    .append(ticket.getCreatedBy()).append(",")
                    .append(ticket.getAccepted()).append("\n");
        }
        return csv.toString();
    }

    @Tool("Récupérer toutes les commandes de paiement")
    public List<PaymentOrderDto> getPaymentOrder() {
        List<PaymentOrder> p =  servicePayment.getPaymentOrder();
        return p.stream().map(this::convertToDtoop)
                .collect(Collectors.toList());
    }

    @Tool("Vérifier une commande de paiement par son ID et ID utilisateur")
    public PaymentOrder verifyPaymentOrder(Integer paymentId, String idUser) {
        return servicePayment.verifyPaymentOrder(paymentId, idUser);
    }
    @Tool("Récupérer la liste de tous les matériels")
    public List<Matirieldto> getAllMatiriel() {
        List<Matiriel> m =  serviceMatiriel.getMatiriel();
        return m.stream ()
                .map(this::convertToDtoo)
                .collect(Collectors.toList());
    }

    @Tool("Réduire le stock des matériels à partir d'une liste d'identifiants")
    public void decreaseStock(List<Integer> ids) {
        serviceMatiriel.decreaseStock(ids);
    }
    private TicketDto convertToDto(Ticket ticket) {
        TicketDto dto = new TicketDto();
        dto.setTicketid(ticket.getTicketid());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setCreatedBy(ticket.getCreatedBy());
        dto.setAccepted(ticket.getAccepted());
        return dto;
    }
    private Matirieldto convertToDtoo(Matiriel matiriel) {
        Matirieldto dto = new Matirieldto();
        dto.setMatirielid(matiriel.getMatirielid());
        dto.setMatirielname(matiriel.getMatirielname());
        dto.setMatirielDescription(matiriel.getMatirielDescription());
        dto.setMatrielstock(matiriel.getMatrielstock());

        return dto;
    }
    private PaymentOrderDto convertToDtoop(PaymentOrder paymentOrder) {
        PaymentOrderDto dto = new PaymentOrderDto();
        dto.setPaymentid(paymentOrder.getPaymentid());
        dto.setPaymentamount(paymentOrder.getPaymentamount());
        dto.setVerified(paymentOrder.getVerified());
        dto.setDescription(paymentOrder.getDescription());
        dto.setVerifiedBy(paymentOrder.getVerifiedBy());
        return dto;
    }

}
