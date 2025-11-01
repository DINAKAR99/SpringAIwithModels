package com.telusko.SpringAIDemo;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import com.telusko.SpringAIDemo.service.VectorStoreConfig;

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

        String response = vectorStoreConfig.chat(message);

        return ResponseEntity.ok(response);
    }

}
