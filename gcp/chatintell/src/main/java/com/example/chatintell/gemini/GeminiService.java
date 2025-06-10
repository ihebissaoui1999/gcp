package com.example.chatintell.gemini;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://generativelanguage.googleapis.com").build();
    }

    public String sendMessageToGemini(String message) {
        String url = "/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
                "contents", Map.of(
                        "parts", new Object[]{
                                Map.of("text", message)
                        }
                )
        );

        return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Bloque pour obtenir la réponse immédiatement
    }
}

