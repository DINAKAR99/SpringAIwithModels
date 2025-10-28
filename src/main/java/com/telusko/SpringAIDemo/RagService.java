// package com.telusko.SpringAIDemo;

// import org.springframework.stereotype.Service;

// import java.util.*;

// @Service
// public class RagService {

// private Map<String, float[]> vectorStore = new HashMap<>();
// private EmbeddingModel embeddingModel = new EmbeddingModel();

// // Load docs into vector store
// public void loadDocuments(List<String> docs) {
// for (String doc : docs) {
// vectorStore.put(doc, embeddingModel.embed(doc));
// }
// }

// // Retrieve top K relevant docs
// public List<String> retrieveRelevant(String query, int topK) {
// float[] queryEmbedding = embeddingModel.embed(query);

// return vectorStore.entrySet().stream()
// .sorted((a, b) -> Float.compare(cosSim(b.getValue(), queryEmbedding),
// cosSim(a.getValue(), queryEmbedding)))
// .limit(topK)
// .map(Map.Entry::getKey)
// .toList();
// }

// private float cosSim(float[] a, float[] b) {
// float dot = 0f, normA = 0f, normB = 0f;
// for (int i = 0; i < a.length; i++) {
// dot += a[i] * b[i];
// normA += a[i] * a[i];
// normB += b[i] * b[i];
// }
// return dot / ((float) (Math.sqrt(normA) * Math.sqrt(normB)) + 1e-10f);
// }
// }
