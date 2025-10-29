package com.telusko.SpringAIDemo.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VectorStoreConfig {

    // ✅ Explicitly use OpenAI Embedding Model

    // @Bean
    // public VectorStore vectorStore(JdbcTemplate jdbcTemplate,
    // OpenAiEmbeddingModel embeddingModel) {
    // return PgVectorStore.builder(jdbcTemplate, embeddingModel)
    // // Don’t auto-create or fill schema unless you’re ready
    // .initializeSchema(false)
    // // Just use default table + schema
    // .schemaName("public")
    // .vectorTableName("vector_store")
    // .build();
    // }
}
