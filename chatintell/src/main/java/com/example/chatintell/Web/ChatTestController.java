package com.example.chatintell.Web;


import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/Chatbot")
public class ChatTestController {

    private ChatLanguageModel chatLanguageModel;
    @GetMapping("/chatbot")
    public String chat(@RequestParam(defaultValue = "Bonjour") String question){
        return chatLanguageModel.chat(question);
    }
}
