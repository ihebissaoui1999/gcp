package com.example.chatintell.Web;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

@AiService
public interface Aiagentdev {
    @SystemMessage("""
            You are a good assistant that can answer the user's question using provided tools.
            """)
    Flux<String> chat(String question);
}
