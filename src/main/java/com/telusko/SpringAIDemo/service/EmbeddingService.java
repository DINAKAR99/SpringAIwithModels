// package com.telusko.SpringAIDemo.service;

// import org.springframework.ai.openai.embedding.Embedding;
// import org.springframework.ai.openai.embedding.EmbeddingModel;
// import org.springframework.ai.openai.OpenAiClient;
// import org.springframework.stereotype.Service;

// import java.util.*;

// @Service
// public class EmbeddingService {

// private final OpenAiClient openAiClient; // Or Ollama embedding client
// private final Map<String, float[]> docEmbeddings = new HashMap<>();

// public EmbeddingService(OpenAiClient openAiClient) {
// this.openAiClient = openAiClient;
// }

// public void buildEmbeddings(List<String> docs) {
// for (String doc : docs) {
// Embedding embedding = openAiClient.embeddings()
// .create(doc, EmbeddingModel.TEXT_EMBEDDING_ADA_002);
// // Convert List<Double> to float[]
// float[] vector = embedding.getEmbedding()
// .stream()
// .mapToFloat(Double::floatValue)
// .toArray();
// docEmbeddings.put(doc, vector);
// }
// }

// public float[] getQueryEmbedding(String query) {
// Embedding embedding = openAiClient.embeddings()
// .create(query, EmbeddingModel.TEXT_EMBEDDING_ADA_002);
// return
// embedding.getEmbedding().stream().mapToFloat(Double::floatValue).toArray();
// }

// public Map<String, float[]> getDocEmbeddings() {
// return docEmbeddings;
// }
// }
