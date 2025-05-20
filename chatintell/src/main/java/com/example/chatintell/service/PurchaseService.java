package com.example.chatintell.service;

import com.example.chatintell.Notification.Notification;
import com.example.chatintell.Notification.NotificationService;
import com.example.chatintell.Notification.NotificationStatus;
import com.example.chatintell.entity.*;
import com.example.chatintell.gemini.GeminiService;
import com.example.chatintell.repository.PaymentRepository;
import com.example.chatintell.repository.PurchaseRepository;
import com.example.chatintell.repository.TicketRepository;
import com.example.chatintell.repository.UserRepository;
import com.example.chatintell.scraping.PriceComparisonService;
import com.example.chatintell.scraping.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService implements IservicePurchase {
    private final PurchaseRepository purchaseRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final PriceComparisonService priceComparisonService;
    private final NotificationService notificationService;

    private final GeminiService geminiService;
   private  final UserRepository userRepository;
    private static final int MAX_DESCRIPTION_LENGTH = 10000;
    @Override
    public PurchaseRequest createPurchaseRequest(PurchaseRequest purchaseRequest, Integer ticketid,String idUser) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketid);
        if (!ticket.isPresent()) {
            throw new RuntimeException("Ticket non trouvé");
        }
        Optional<User> userList = userRepository.findById(idUser);
        if (!userList.isPresent()) {
            throw new RuntimeException("userList non trouvé");
        }
        purchaseRequest.setCreatedBy(idUser);
        purchaseRequest.setTicket(ticket.get());
        purchaseRequest.setPurchasedate(LocalDate.now());
        purchaseRequest.setCreatedBy(purchaseRequest.getCreatedBy());
        Map<String, Product> allProducts = priceComparisonService.comparePrices();
        Map<String, ProductWithQuantity> selectedProducts = selectProductsWithAI(purchaseRequest.getDescription(), allProducts);
        String enhancedDescription = enhanceDescriptionWithProducts(purchaseRequest.getDescription(), selectedProducts, allProducts);
        if (enhancedDescription.length() > MAX_DESCRIPTION_LENGTH) {
            enhancedDescription = enhancedDescription.substring(0, MAX_DESCRIPTION_LENGTH - 3) + "...";
            log.warn("Description tronquée à {} caractères : {}", MAX_DESCRIPTION_LENGTH, enhancedDescription);
        }
        purchaseRequest.setDescription(enhancedDescription);
        purchaseRequest.setPurchaseStatus(PurchaseStatus.New);

        PaymentOrder paymentOrder = new PaymentOrder();
        Float totalAmount = calculateTotalAmount(selectedProducts);

        PurchaseRequest savedPurchase = purchaseRepository.save(purchaseRequest);

        paymentOrder.setDescription(enhancedDescription);
        paymentOrder.setPaymentdate(LocalDate.now());
        paymentOrder.setPaymentamount(totalAmount);
        paymentOrder.setPurchaserequests(Collections.singletonList(savedPurchase));

        savedPurchase.setPaymentorder(paymentRepository.save(paymentOrder));
        notificationService.sendNotification(
                purchaseRequest.getCreatedBy(),  // L'ID du créateur du ticket
                Notification.builder()
                        .notificationStatus(NotificationStatus.ADDED)  // Statut de la notification
                        .content("Purchase '" + purchaseRequest.getDescription() + "' a été ajouter  ")  // Contenu
                        .build()
        );
        return purchaseRepository.save(savedPurchase);
    }

    private static class ProductWithQuantity {
        Product product;
        int quantity;

        ProductWithQuantity(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    private Map<String, ProductWithQuantity> selectProductsWithAI(String description, Map<String, Product> allProducts) {
        log.info("Produits scrapés disponibles : {}", allProducts.keySet());
        String prompt = "Analyse cette description de demande d'achat et sélectionne les produits pertinents parmi la liste disponible. "
                + "Instructions strictes : "
                + "- Identifie chaque produit et sa quantité exacte telle que spécifiée dans la description (ex. '2 souris' signifie quantité 2). "
                + "- Indique le nom EXACT du produit tel qu'il apparaît dans la liste fournie, sans modification (même casse, espaces, ponctuation). "
                + "- Retourne chaque produit sur une nouvelle ligne au format 'quantité nom_exact_du_produit' (ex. '2 souris sans fil advance feel - noir'). "
                + "- Si un produit n’a pas de quantité spécifiée, utilise 1 comme défaut. "
                + "- Ne retourne QUE les lignes de produits, sans texte supplémentaire ni explication. "
                + "Description: " + description
                + "\nListe des produits disponibles: " + getAvailableProductsList(allProducts);

        String response = geminiService.sendMessageToGemini(prompt);
        log.info("Gemini response: {}", response);

        Map<String, ProductWithQuantity> selectedProducts = new HashMap<>();
        String productLines = extractTextFromGeminiResponse(response);
        log.info("Lignes extraites de Gemini : {}", productLines);
        if (productLines.isEmpty()) {
            log.warn("Aucun produit valide extrait de la réponse de Gemini");
            return selectedProducts;
        }

        String[] lines = productLines.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(" ", 2);
            int quantity = 1;
            String productName;

            if (parts.length == 2 && parts[0].matches("\\d+")) {
                quantity = Integer.parseInt(parts[0]);
                productName = parts[1].trim();
            } else {
                productName = line;
            }

            String normalizedProductName = productName.toLowerCase().trim();
            if (allProducts.containsKey(normalizedProductName)) {
                selectedProducts.put(productName, new ProductWithQuantity(allProducts.get(normalizedProductName), quantity));
                log.info("Produit trouvé avec correspondance exacte : {}", productName);
                continue;
            }

            normalizedProductName = normalizedProductName.replaceAll("\\s+", " ").trim();
            Product matchedProduct = null;
            for (String key : allProducts.keySet()) {
                String normalizedKey = key.toLowerCase().replaceAll("\\s+", " ").trim();
                if (normalizedKey.equals(normalizedProductName) ||
                        normalizedKey.contains(normalizedProductName) ||
                        normalizedProductName.contains(normalizedKey)) {
                    matchedProduct = allProducts.get(key);
                    selectedProducts.put(productName, new ProductWithQuantity(matchedProduct, quantity));
                    log.info("Produit trouvé avec correspondance approximative : {} -> {}", productName, key);
                    break;
                }
            }

            if (matchedProduct == null) {
                log.warn("Produit non trouvé dans les données scrapées : {}", productName);
                matchedProduct = findAlternativeProduct(productName, allProducts);
                if (matchedProduct != null) {
                    selectedProducts.put(productName, new ProductWithQuantity(matchedProduct, quantity));
                    log.info("Produit alternatif sélectionné : {} -> {}", productName, matchedProduct.getName());
                }
            }
        }

        return selectedProducts;
    }

    private Product findAlternativeProduct(String productName, Map<String, Product> allProducts) {
        String lowerProductName = productName.toLowerCase().trim();
        if (lowerProductName.contains("souris")) {
            return allProducts.values().stream()
                    .filter(p -> p.getName().toLowerCase().contains("souris") && !p.getName().toLowerCase().contains("en arrivage"))
                    .findFirst().orElse(null);
        } else if (lowerProductName.contains("écouteurs") || lowerProductName.contains("ecouteurs")) {
            return allProducts.values().stream()
                    .filter(p -> p.getName().toLowerCase().contains("ecouteurs"))
                    .findFirst().orElse(null);
        } else if (lowerProductName.contains("écran") || lowerProductName.contains("ecran")) {
            return allProducts.values().stream()
                    .filter(p -> p.getName().toLowerCase().contains("ecran"))
                    .findFirst().orElse(null);
        } else if (lowerProductName.contains("chaise")) {
            return allProducts.values().stream()
                    .filter(p -> p.getName().toLowerCase().contains("chaise"))
                    .findFirst().orElse(null);
        }
        return null;
    }

    private String getAvailableProductsList(Map<String, Product> products) {
        return products.values().stream()
                .map(Product::getName)
                .collect(Collectors.joining(", "));
    }

    private String enhanceDescriptionWithProducts(String originalDescription, Map<String, ProductWithQuantity> products, Map<String, Product> allProducts) {
        if (products.isEmpty()) {
            return originalDescription;
        }

        StringBuilder enhanced = new StringBuilder(originalDescription);
        enhanced.append("\nProduits sélectionnés:\n");

        for (ProductWithQuantity pq : products.values()) {
            Product product = pq.product;
            int quantity = pq.quantity;
            enhanced.append(String.format("- %d x %s (%.2f DT chacun, Total: %.2f DT) [%s]\n",
                    quantity, product.getName(), product.getPrice(), product.getPrice() * quantity, product.getSite()));
        }

        return enhanced.toString();
    }

    private Float calculateTotalAmount(Map<String, ProductWithQuantity> products) {
        return (float) products.values().stream()
                .mapToDouble(pq -> pq.product.getPrice() * pq.quantity)
                .sum();
    }

    private String extractTextFromGeminiResponse(String response) {
        // Recherche du début du texte
        int textStart = response.indexOf("\"text\": \"");
        if (textStart == -1) {
            log.warn("Champ 'text' non trouvé dans la réponse JSON de Gemini");
            return "";
        }
        textStart += 9; // Passe après "\"text\": \""

        // Recherche de la fin du texte (guillemet non échappé)
        int textEnd = textStart;
        boolean escaped = false;
        while (textEnd < response.length()) {
            char currentChar = response.charAt(textEnd);
            if (currentChar == '\\') {
                escaped = true;
                textEnd++;
                continue;
            }
            if (currentChar == '"' && !escaped) {
                break; // Fin du texte trouvée
            }
            escaped = false;
            textEnd++;
        }

        if (textEnd >= response.length()) {
            log.warn("Fin du champ 'text' non trouvée, extraction jusqu'à la fin de la réponse");
            textEnd = response.length();
        }

        String textContent = response.substring(textStart, textEnd)
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .trim();

        log.debug("Texte extrait : {}", textContent);
        return textContent;
    }
}