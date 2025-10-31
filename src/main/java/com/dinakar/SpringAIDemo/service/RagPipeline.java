package com.dinakar.SpringAIDemo.service;
// package com.telusko.SpringAIDemo.service;

// import org.springframework.ai.chat.client.ChatClient;
// import org.springframework.ai.chat.model.ChatResponse;
// import org.springframework.ai.ollama.OllamaChatModel;
// import org.springframework.stereotype.Service;

// import java.util.*;
// import java.util.stream.Collectors;

// @Service
// public class RagPipeline {

// private final RagService ragService;
// private final EmbeddingService embeddingService;
// private final ChatClient chatClient;

// public RagPipeline(RagService ragService, EmbeddingService embeddingService,
// OllamaChatModel chatModel) {
// this.ragService = ragService;
// this.embeddingService = embeddingService;
// this.chatClient = ChatClient.create(chatModel);
// }

// public List<String> retrieveRelevant(String query, int topK) {
// float[] qEmb = embeddingService.getQueryEmbedding(query);

// return embeddingService.getDocEmbeddings().entrySet().stream()
// .sorted((e1, e2) -> -Float.compare(
// cosineSimilarity(qEmb, e1.getValue()),
// cosineSimilarity(qEmb, e2.getValue())))
// .limit(topK)
// .map(Map.Entry::getKey)
// .collect(Collectors.toList());
// }

// private float cosineSimilarity(float[] a, float[] b) {
// float dot = 0f, normA = 0f, normB = 0f;
// for (int i = 0; i < a.length; i++) {
// dot += a[i] * b[i];
// normA += a[i] * a[i];
// normB += b[i] * b[i];
// }
// return dot / ((float) Math.sqrt(normA) * (float) Math.sqrt(normB));
// }

// public String query(String userMessage, int topK) {
// List<String> topDocs = retrieveRelevant(userMessage, topK);
// String context = String.join("\n", topDocs);
// String prompt = "You are a helpful assistant. Answer ONLY based on context
// below.\n" +
// "If answer not found, say: 'Sorry, I don’t have information.'\n\n" +
// "Context:\n" + context + "\n\nQuestion: " + userMessage;

// ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
// return chatResponse.getResult().getOutput().getText();
// }
// }
