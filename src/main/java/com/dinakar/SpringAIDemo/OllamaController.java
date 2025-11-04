package com.dinakar.SpringAIDemo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dinakar.SpringAIDemo.service.VectorStoreConfig;

@RestController
@RequestMapping("/api/ollama")
@CrossOrigin("*")
public class OllamaController {

    private ChatClient chatClient;

    @Autowired
    VectorStoreConfig vectorStoreConfig;

    public OllamaController(OllamaChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    @GetMapping("/{message}")
    public ResponseEntity<String> getAnswer(@PathVariable String message) {

        // ChatResponse chatResponse = chatClient
        // .prompt(message)
        // .call()
        // .chatResponse();

        // System.out.println(chatResponse.getMetadata().getModel());

        ChatResponseDTO response = vectorStoreConfig.chat(message);

        return ResponseEntity.ok(response.getChoices().get(0).getMessage().getContent());
    }

}
