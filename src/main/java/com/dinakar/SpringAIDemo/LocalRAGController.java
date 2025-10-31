package com.dinakar.SpringAIDemo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag-lite")
@CrossOrigin("*")
public class LocalRAGController {

    private final OpenAiChatModel chatModel;
    private final JdbcTemplate jdbcTemplate;
    private final Map<String, String> docs = new HashMap<>();

    @Autowired
    public LocalRAGController(OpenAiChatModel chatModel, JdbcTemplate jdbcTemplate) throws IOException {
        this.chatModel = chatModel;
        this.jdbcTemplate = jdbcTemplate;
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

        String dbContext = getDbContext(message);
        String docContext = getBestDoc(message);

        String context = (dbContext.isEmpty()) ? docContext : (docContext + "\n\nDB Context:\n" + dbContext);

        String prompt = """
                You are a friendly chatbot assistant.
                Use the context below to answer naturally.

                Context:
                %s

                Question:
                %s
                """.formatted(context, message);

        String response = chatModel.call(prompt);
        return ResponseEntity.ok(response);
    }

    private String getBestDoc(String message) {
        return docs.entrySet().stream()
                .max(Comparator.comparingInt(e -> getKeywordScore(message, e.getValue())))
                .map(Map.Entry::getValue)
                .orElse("");
    }

    private int getKeywordScore(String question, String doc) {
        List<String> words = Arrays.asList(question.toLowerCase().split("\\s+"));
        return (int) words.stream().filter(doc.toLowerCase()::contains).count();
    }

    private String getDbContext(String message) {
        message = message.toLowerCase();

        // 🧠 1️⃣ Detect questions about available programming languages
        if (messageContains(message, "programming", "language", "languages", "available")) {
            try {
                List<String> categories = jdbcTemplate.queryForList("SELECT cname FROM category", String.class);
                return "Programming languages available in the store: " + String.join(", ", categories);
            } catch (Exception e) {
                return "Sorry, I couldn't fetch categories from the database.";
            }
        }

        // 🎯 2️⃣ Detect roll IDs using regex (like R1023, 12345, etc.)
        if (message.matches(".*\\b(r\\d+|\\d{4,})\\b.*")) {
            try {
                // Extract the roll ID
                String rollId = message.replaceAll(".*\\b(r\\d+|\\d{4,})\\b.*", "$1").toUpperCase();

                // Fetch the exam details
                List<Map<String, Object>> exams = jdbcTemplate.queryForList(
                        "SELECT exam_name, exam_date, exam_time FROM student_exam WHERE roll_id = ?",
                        rollId);

                if (exams.isEmpty()) {
                    return "No exams scheduled for roll ID: " + rollId;
                }

                StringBuilder sb = new StringBuilder("Exam schedule for roll ID " + rollId + ":\n");
                for (Map<String, Object> exam : exams) {
                    sb.append("- ")
                            .append(exam.get("exam_name"))
                            .append(" on ")
                            .append(exam.get("exam_date"))
                            .append(" at ")
                            .append(exam.get("exam_time"))
                            .append("\n");
                }
                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "Sorry, I couldn't fetch the exam schedule.";
            }
        }

        // 🧩 Default fallback
        return "";
    }

    private boolean isSimilar(String input, String keyword) {
        LevenshteinDistance distance = new LevenshteinDistance();
        int diff = distance.apply(input, keyword);
        int maxLen = Math.max(input.length(), keyword.length());
        double similarity = 1.0 - (double) diff / maxLen; // 0 = totally different, 1 = identical
        return similarity > 0.7; // tweak threshold
    }

    private boolean messageContains(String message, String... keywords) {
        String[] words = message.toLowerCase().split("\\s+");
        for (String word : words) {
            for (String keyword : keywords) {
                if (isSimilar(word, keyword))
                    return true;
            }
        }
        return false;
    }

}
