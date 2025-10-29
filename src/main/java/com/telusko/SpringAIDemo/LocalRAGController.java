package com.telusko.SpringAIDemo;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rag-lite")
@CrossOrigin("*")
public class LocalRAGController {

    private final OpenAiChatModel chatModel;
    private final Map<String, String> docs = new HashMap<>();

    public LocalRAGController(OpenAiChatModel chatModel) throws IOException {
        this.chatModel = chatModel;

        // Load your local knowledge files at startup
        loadDocs();
    }

    private void loadDocs() throws IOException {
        List<String> files = List.of("about.txt", "faq.txt", "product_info.txt");
        for (String file : files) {
            var resource = new ClassPathResource("knowledgebase/" + file);
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            docs.put(file, content);
        }
    }

    @GetMapping("/{message}")
    public ResponseEntity<String> ask(@PathVariable String message) {
        // Find the doc most related to the question (basic keyword match)
        String bestDoc = docs.entrySet().stream()
                .max(Comparator.comparingInt(e -> getKeywordScore(
                        message, e.getValue())))
                .map(Map.Entry::getValue)
                .orElse("");

        String prompt = """
                You are a friendly chatbot.
                Use the context below to answer the user's question naturally.

                Context:
                %s

                Question:
                %s
                """.formatted(bestDoc, message);

        String response = chatModel.call(prompt);
        return ResponseEntity.ok(response);
    }

    private int getKeywordScore(String question, String doc) {
        List<String> words = Arrays.asList(question.toLowerCase().split("\\s+"));
        return (int) words.stream().filter(doc.toLowerCase()::contains).count();
    }
}
