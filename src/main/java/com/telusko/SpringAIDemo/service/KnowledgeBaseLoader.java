package com.telusko.SpringAIDemo.service;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class KnowledgeBaseLoader {

    private final VectorStore vectorStore;

    public KnowledgeBaseLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    // @PostConstruct
    // public void loadDocs() {
    // vectorStore.add(List.of(
    // new Document("Spring Boot simplifies microservice development with embedded
    // servers.")));
    // System.out.println("✅ Knowledge base loaded into pgvector!");
    // }
}
