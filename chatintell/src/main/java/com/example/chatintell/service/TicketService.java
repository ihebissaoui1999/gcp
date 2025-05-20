package com.example.chatintell.service;


import com.example.chatintell.Notification.Notification;
import com.example.chatintell.Notification.NotificationService;
import com.example.chatintell.Notification.NotificationStatus;
import com.example.chatintell.entity.PriorityType;
import com.example.chatintell.entity.StatusType;
import com.example.chatintell.entity.Ticket;
import com.example.chatintell.entity.User;

import com.example.chatintell.gemini.GeminiService;
import com.example.chatintell.repository.TicketRepository;
import com.example.chatintell.repository.UserRepository;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TicketService implements IserviceTicket {
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final GeminiService geminiService;
    private JavaMailSender emailSender;
    private NotificationService notificationService;


    @Override
    @Transactional
    public Ticket ajouterTicket(Ticket ticket, String idUser) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID: " + idUser));
        ticket.setCreatedBy(idUser);
        ticket.setCreationdate(LocalDate.now());
        ticket.setStatusType(StatusType.New);
        ticket.setPriorityType(determinerPrioriteAvecIA(ticket.getDescription()));
        ticket.setAccepted(false);
        ticket = ticketRepository.save(ticket);
        final Ticket finalTicket = ticket;
        finalTicket.getUsers().add(user);
        userRepository.save(user);

        String categoryType = finalTicket.getCategoryType().name();
        List<User> usersWithSameRole = userRepository.findByRoles_Name(categoryType);

        usersWithSameRole.forEach(u -> u.getTickets().add(finalTicket));
        ticket.getUsers().addAll(usersWithSameRole);
        userRepository.saveAll(usersWithSameRole);
        ticketRepository.save(finalTicket);
        usersWithSameRole.forEach(assignedUser -> {
            System.out.println("eeeeeeeee Envoi notification à l'utilisateur : " + assignedUser.getUserid());


            notificationService.sendNotification(

                    assignedUser.getUserid(),
                    Notification.builder()
                            .notificationStatus(NotificationStatus.ADDED)
                            .content("Ticket '" + finalTicket.getTitle() + "' assigné par " + user.getFirstName())
                            .build()
            );
            System.out.println("Utilisateurs assignés :");
            usersWithSameRole.forEach(u -> System.out.println("ID: " + u.getUserid() + ", Nom: " + u.getFirstName()));

        });
        return finalTicket;
    }

    @Override
    public Ticket updateTicket(Ticket ticket, Integer idTicket, String idUser) {
        User user = userRepository.findById(idUser).get();
        Ticket ticket1 = ticketRepository.findById(idTicket).get();
        ticket1.setTitle(ticket.getTitle());
        ticket1.setDescription(ticket.getDescription());
        ticket1.setCategoryType(ticket.getCategoryType());
        ticket1.setStatusType(ticket.getStatusType());
        ticket1.setPriorityType(ticket.getPriorityType());
        ticket1.setLastmodifier(LocalDate.now());
        Ticket finalUpdatedTicket = ticketRepository.save(ticket1);
        String categoryType = finalUpdatedTicket.getCategoryType().name();
        List<User> usersWithSameRole = userRepository.findByRoles_Name(categoryType);
        usersWithSameRole.forEach(u -> u.getTickets().add(finalUpdatedTicket));
        ticket1.getUsers().add(user);
        userRepository.saveAll(usersWithSameRole);
        ticketRepository.save(finalUpdatedTicket);
        return ticket1;
    }

    @Override
    public List<Ticket> getTickets(String idUser) {
        List<Ticket> tickets = ticketRepository.findByCreatedBy(idUser).stream().toList();
        return tickets;
    }

    @Override
    public List<Ticket> findTicketsAssignedToUser(String idUser) {
        List<Ticket> tickets = ticketRepository.findTicketsAssignedToUser(idUser);
        return tickets;
    }

    @Override
    public Ticket assignTicketsToUser(String idUser, Integer idTicket) {
        Optional<Ticket> tickets = ticketRepository.findById(idTicket);
        Optional<User> userList = userRepository.findById(idUser);

        if (userList.isPresent() && tickets.isPresent()) {
            User user = userList.get();  // Iheb (RH) qui accepte le ticket
            Ticket ticket = tickets.get();

            // Vérifier que le ticket a bien un créateur
            Optional<User> ticketCreatorOpt = userRepository.findById(ticket.getCreatedBy());
            if (!ticketCreatorOpt.isPresent()) {
                throw new RuntimeException("Créateur du ticket introuvable !");
            }
            User ticketCreator = ticketCreatorOpt.get(); // Amin (créateur du ticket)

            // Assignation et mise à jour du ticket
            ticket.getUsers().clear();
            ticket.getUsers().add(user);
            ticket.setStatusType(StatusType.InProgress);
            ticket.setAccepted(true);
            Ticket updatedTicket = ticketRepository.save(ticket);
            notificationService.sendNotification(
                    ticket.getCreatedBy(),  // L'ID du créateur du ticket
                    Notification.builder()
                            .notificationStatus(NotificationStatus.ACCEPTED)  // Statut de la notification
                            .content("Ticket '" + updatedTicket.getTitle() + "' a été assigné à " + user.getEmail())  // Contenu
                            .build()
            );
            // Envoi du feedback par email au créateur du ticket (Amin)
            sendFeedbackEmailAsync(updatedTicket, ticketCreator, null);

            return updatedTicket;
        } else {
            if (!tickets.isPresent()) {
                System.err.println("Ticket non trouvé avec ID: " + idTicket);
            }
            if (!userList.isPresent()) {
                System.err.println("Utilisateur non trouvé avec ID: " + idUser);
            }
            return null;
        }
    }

    @Override
    public Ticket completeTicket(Integer idTicket, String idUser) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(idTicket);
        Optional<User> optionalUser = userRepository.findById(idUser);

        if (optionalTicket.isPresent() && optionalUser.isPresent()) {
            User user = optionalUser.get();
            Ticket ticket = optionalTicket.get();

            if (ticket.getAccepted() && ticket.getUsers().contains(user)) {
                ticket.setStatusType(StatusType.Completed);
                ticketRepository.save(ticket);

                // Envoi d'une notification au créateur du ticket
                notificationService.sendNotification(
                        ticket.getCreatedBy(),
                        Notification.builder()
                                .notificationStatus(NotificationStatus.COMPLETED) // Statut correct
                                .content("Votre ticket '" + ticket.getTitle() + "' a été complété.")
                                .build()
                );

                // Récupérer le créateur du ticket pour lui envoyer un e-mail
                User ticketCreator = userRepository.findById(ticket.getCreatedBy())
                        .orElseThrow(() -> new RuntimeException("Créateur du ticket introuvable"));

                // Envoi de l'e-mail de confirmation
                sendFeedbackEmailAsyncsuss(ticket,ticketCreator, null);

                return ticket;
            } else {
                throw new RuntimeException("Le ticket doit être accepté avant d'être complété.");
            }
        } else {
            if (!optionalTicket.isPresent()) {
                System.err.println("Ticket non trouvé avec ID: " + idTicket);
            }
            if (!optionalUser.isPresent()) {
                System.err.println("Utilisateur non trouvé avec ID: " + idUser);
            }
            return null;
        }
    }

    @Override
    public List<Ticket> getTickets(Boolean accepeted) {
         List<Ticket> t= ticketRepository.findAllByAccepted(accepeted);

        return t;
    }

    @Override
    public List<Ticket> getTicketsUS(String idUser) {
        List<Ticket> tt= ticketRepository.findTicketsByUserId(idUser);
        return tt;
    }
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> getTicketsAssignedTooneUser(String idUser) {
        return ticketRepository.findTicketsAssignedToOnlyOneUser(idUser);
    }


    public PriorityType determinerPrioriteAvecIA(String description) {
        String prompt = "Analyse cette description de ticket et attribue uniquement l'une des priorités suivantes : Low, Medium, High. "
                + "Ne retourne que Low, Medium ou High sans autre texte. "
                + "Description: " + description;

        String response = geminiService.sendMessageToGemini(prompt);

        System.out.println("Réponse de Gemini : " + response);

        response = response.toLowerCase().trim();

        if (response.equals("high") || description.toLowerCase().matches(".*(urgent|immédiatement|hors service|incident critique|cyberattaque|serveur principal).*")) {
            return PriorityType.High;
        } else if (response.equals("medium") || description.toLowerCase().matches(".*(bug|problème de connexion|accès refusé|lenteur|erreur récurrente|utilisateurs impactés).*")) {
            return PriorityType.Medium;
        } else if (response.equals("low") || description.toLowerCase().matches(".*(demande d'information|amélioration|maintenance planifiée|fonctionnalité optionnelle|question).*")) {
            return PriorityType.Low;
        } else {
            return PriorityType.Medium;
        }
    }

    @Async
    public void sendFeedbackEmailAsyncsuss(Ticket ticket, User acceptedBy, MultipartFile file) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Récupérer le créateur du ticket
            User ticketCreator = userRepository.findById(ticket.getCreatedBy())
                    .orElseThrow(() -> new RuntimeException("Créateur du ticket introuvable"));

            helper.setFrom("devotem@devoteam.com");

            // Envoyer l'email au créateur du ticket
            helper.setTo(ticketCreator.getEmail());
            helper.setSubject("Votre ticket a été complété avec succès : " + ticket.getTitle());

            // Contenu HTML de l'email
            String htmlContent = "<html>" +
                    "<body>" +
                    "<h3>Votre ticket a été complété avec succès</h3>" +
                    "<p>Votre ticket a été mis à jour avec les informations suivantes :</p>" +
                    "<ul>" +
                    "<li><strong>ID du ticket :</strong> " + ticket.getTicketid() + "</li>" +
                    "<li><strong>Titre :</strong> " + ticket.getTitle() + "</li>" +
                    "<li><strong>Description :</strong> " + ticket.getDescription() + "</li>" +
                    "<li><strong>Date de création :</strong> " + ticket.getCreationdate() + "</li>" +
                    "<li><strong>Dernière modification :</strong> " + ticket.getLastmodifier() + "</li>" +
                    "<li><strong>Catégorie :</strong> " + ticket.getCategoryType() + "</li>" +
                    "<li><strong>Statut :</strong> " + ticket.getStatusType() + "</li>" +
                    "<li><strong>Priorité :</strong> " + ticket.getPriorityType() + "</li>" +
                    "<li><strong>Accepté :</strong> " + (ticket.getAccepted() ? "Oui" : "Non") + "</li>" +
                    "<li><strong>Complété par :</strong> " + " " + " (" + acceptedBy.getEmail() + ")</li>" +
                    "</ul>" +
                    "<p>Vous pouvez discuter davantage sur ce ticket en suivant ce lien : <a href=\"http://localhost:4200/chats\">Consulter le chat</a></p>" +
                    "<p>Merci pour votre contribution.</p>" +
                    "<p>Cordialement,</p>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);

            // Vérification et ajout de l'attachement
            if (file != null && !file.isEmpty()) {
                helper.addAttachment(file.getOriginalFilename(), file);
            }

            // Envoi de l'email
            emailSender.send(message);
            System.out.println("Email de confirmation envoyé à " + ticketCreator.getEmail());

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email de confirmation : " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Async
    public void sendFeedbackEmailAsync(Ticket ticket, User acceptedBy, MultipartFile file) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            User ticketCreator = userRepository.findById(ticket.getCreatedBy())
                    .orElseThrow(() -> new RuntimeException("Créateur du ticket introuvable"));

            helper.setFrom("devotem@devoteam.com");

            helper.setTo("mohamed.issaoui@devoteam.com");
            helper.setSubject("Feedback sur votre ticket : " + ticket.getTitle());

            String htmlContent = "<html>" +
                    "<body>" +
                    "<h3>Feedback sur votre ticket</h3>" +
                    "<p>Votre ticket a été mis à jour avec les informations suivantes :</p>" +
                    "<ul>" +
                    "<li><strong>ID du ticket :</strong> " + ticket.getTicketid() + "</li>" +
                    "<li><strong>Titre :</strong> " + ticket.getTitle() + "</li>" +
                    "<li><strong>Description :</strong> " + ticket.getDescription() + "</li>" +
                    "<li><strong>Date de création :</strong> " + ticket.getCreationdate() + "</li>" +
                    "<li><strong>Dernière modification :</strong> " + ticket.getLastmodifier() + "</li>" +
                    "<li><strong>Catégorie :</strong> " + ticket.getCategoryType() + "</li>" +
                    "<li><strong>Statut :</strong> " + ticket.getStatusType() + "</li>" +
                    "<li><strong>Priorité :</strong> " + ticket.getPriorityType() + "</li>" +
                    "<li><strong>Accepté :</strong> " + (ticket.getAccepted() ? "Oui" : "Non") + "</li>" +
                    "</ul>" +
                    "<p>Vous pouvez discuter davantage sur ce ticket en suivant ce lien : <a href=\"http://localhost:4200/chats\">Consulter le chat</a></p>" +
                    "<p>Merci pour votre contribution.</p>" +
                    "<p>Cordialement,</p>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);

            if (file != null && !file.isEmpty()) {
                helper.addAttachment(file.getOriginalFilename(), file);
            }

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }




    }


