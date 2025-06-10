package com.example.chatintell.service;

import com.example.chatintell.Notification.Notification;
import com.example.chatintell.Notification.NotificationService;
import com.example.chatintell.Notification.NotificationStatus;
import com.example.chatintell.entity.*;
import com.example.chatintell.gemini.GeminiService;
import com.example.chatintell.repository.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentOrderService implements  IservicePayment{
    private final MatirielRepository matirielRepository;
    private final UserRepository userRepository;
    PaymentRepository paymentRepository;
    StockRepository stockRepository;
    GeminiService geminiService;
    private JavaMailSender emailSender;
    private NotificationService notificationService;
    PurchaseRepository purchaseRepository;


    @Override
    public List<PaymentOrder> getPaymentOrder() {
        List<PaymentOrder> paymentOrder = paymentRepository.findAllByOrderByPaymentdate();
        return paymentOrder;
    }

    @Override
    public PaymentOrder verifyPaymentOrder(Integer paymentid ,String idUser) {
        Optional<User> userList = userRepository.findById(idUser);
        if (!userList.isPresent()) {
            throw new RuntimeException("userList non trouvé");
        }
        PaymentOrder paymentOrder = paymentRepository.findById(paymentid).get();
        paymentOrder.setVerifiedBy(idUser);
        paymentOrder.setVerified(true);
        PaymentOrder updatedPaymentOrder = paymentRepository.save(paymentOrder);
        sendPaymentVerificationEmail(updatedPaymentOrder);
        PurchaseRequest purchaseRequest = purchaseRepository.findById(paymentid).get();
        notificationService.sendNotification(
                purchaseRequest.getCreatedBy(),  // L'ID du créateur du ittec
                Notification.builder()
                        .notificationStatus(NotificationStatus.ACCEPTED)  // Statut de la notification
                        .content("Payment '" + paymentOrder.getDescription() + "' a été verifier  ")  // Contenu
                        .build()
        );
        return updatedPaymentOrder;

    }
    @Async
    public void sendPaymentVerificationEmail(PaymentOrder paymentOrder) {
        try {

            // Récupérer le créateur du paiement (ou l'utilisateur lié au paiement)
            User paymentCreator = userRepository.findById(paymentOrder.getPurchaserequests().get(0).getTicket().getCreatedBy())
                    .orElseThrow(() -> new RuntimeException("Créateur du paiement introuvable"));

            // Création du message
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("managerrr@devoteam.com");

            // Destinataire de l'email
            helper.setTo("Hazem@devoteam.com");
            helper.setSubject("Votre paiement a été vérifié avec succès : " + paymentOrder.getPaymentid());

            // Contenu HTML de l'email
            String htmlContent = "<html>" +
                    "<body>" +
                    "<h3>Votre paiement a été vérifié avec succès</h3>" +
                    "<p>Votre paiement a été mis à jour avec les informations suivantes :</p>" +
                    "<ul>" +
                    "<li><strong>ID du paiement :</strong> " + paymentOrder.getPaymentid() + "</li>" +
                    "<li><strong>Montant :</strong> " + paymentOrder.getPaymentamount() + " DT</li>" +
                    "<li><strong>Date :</strong> " + paymentOrder.getPaymentdate() + "</li>" +
                    "<li><strong>Description :</strong> " + paymentOrder.getDescription() + "</li>" +
                    "<li><strong>Vérifié :</strong> Oui</li>" +
                    "</ul>" +
                    "<p>Merci pour votre patience et votre contribution.</p>" +
                    "<p>Cordialement,</p>" +
                    "<p>L'équipe Devoteam</p>" +
                    "</body>" +
                    "</html>";

            // Ajouter le texte HTML au message
            helper.setText(htmlContent, true);

            // Envoi de l'email
            emailSender.send(message);
            System.out.println("Email de confirmation envoyé à " + paymentCreator.getEmail());

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email de confirmation : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
