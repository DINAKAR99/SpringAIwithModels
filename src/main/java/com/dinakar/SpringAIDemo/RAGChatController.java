package com.dinakar.SpringAIDemo;
// package com.telusko.SpringAIDemo;

// import org.springframework.ai.openai.OpenAiChatModel;
// import org.springframework.ai.vectorstore.SearchRequest;
// import org.springframework.ai.vectorstore.VectorStore;
// import org.springframework.ai.chat.prompt.Prompt;

// import java.util.List;

// import org.springframework.ai.chat.model.ChatResponse;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/rag")
// @CrossOrigin("*")
// public class RAGChatController {

// private final OpenAiChatModel chatModel;
// private final VectorStore vectorStore;

// public RAGChatController(OpenAiChatModel chatModel, VectorStore vectorStore)
// {
// this.chatModel = chatModel;
// this.vectorStore = vectorStore;
// }

// @GetMapping("/{message}")
// public String ask(@PathVariable String message) {
// List<?> results = vectorStore.similaritySearch(
// SearchRequest.builder()
// .query(message)
// .topK(3) // number of similar docs to fetch
// .build());
// String context = results.stream()
// .map(Object::toString)
// .reduce("", (a, b) -> a + "\n" + b);

// String promptText = """
// You are a helpful, friendly AI assistant.
// Use the context below to answer accurately and naturally.

// Context:
// %s

// Question:
// %s

// Answer:
// """.formatted(context, message);

// ChatResponse response = chatModel.call(new Prompt(promptText));
// return response.getResult().getOutput().getText();
// }
// }
