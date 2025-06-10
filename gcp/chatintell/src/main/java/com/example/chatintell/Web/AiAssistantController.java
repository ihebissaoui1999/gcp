package com.example.chatintell.Web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class AiAssistantController {
    private Aiagentdev aiagentdev;
    @GetMapping("/askAgent")
    public Flux<String> chat(@RequestParam(defaultValue = "Bonjour") String question){
        return aiagentdev.chat(question);
    }
}




