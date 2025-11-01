package com.telusko.SpringAIDemo.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import com.telusko.SpringAIDemo.ChatResponseDTO;

@Configuration
public class VectorStoreConfig {

    private static final String OLLAMA_URL = "https://medha.cgg.gov.in/v1";
    private static final String API_KEY = "sk-uvYL8gRRQvIKiPcLhbWO6w";

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(OLLAMA_URL)
                .defaultHeader("Authorization", "Bearer " + API_KEY)
                .build();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(OLLAMA_URL)
                .defaultHeader("Authorization", "Bearer " + API_KEY)
                .build();
    }

    @Bean
    public OllamaApi ollamaApi(RestClient restClient, WebClient webClient) {
        return new OllamaApi(OLLAMA_URL,
                RestClient.builder(), // no circular dep now
                WebClient.builder());
    }

    @Bean
    public OllamaChatModel ollamaChatModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .build();
    }

    @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.create(ollamaChatModel);
    }

    public ChatResponseDTO chat(String prompt) {
        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt);

        Map<String, Object> body = Map.of(
                "model", "ollama/CGG-Assisted-Coder:latest",
                "messages", List.of(message));

        ChatResponseDTO response = restClient().post()
                .uri("/api/chat")
                .body(body)
                .retrieve()
                .body(ChatResponseDTO.class);

        System.out.println("🧠 Ollama said: " + response.getFirstMessageContent());
        return response;
    }

}
