package com.dinakar.SpringAIDemo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.EmptyResultDataAccessException;
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
                You are a friendly chatbot  assistant in TGMIV website in Telangana for sand Bookings and all other queries.
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
    StringBuilder responseBuilder = new StringBuilder();

    // 🧠 1️⃣ Fetch available programming languages
    // if (messageContains(message, "programming", "language", "languages", "available")) {
    //     try {
    //         List<String> categories = jdbcTemplate.queryForList("SELECT cname FROM category", String.class);
    //         responseBuilder.append("💻 Programming languages available: ")
    //                 .append(String.join(", ", categories))
    //                 .append("\n\n");
    //     } catch (Exception e) {
    //         responseBuilder.append("❌ Couldn't fetch programming languages from DB.\n\n");
    //     }
    // }

    // 🚗 2️⃣ Vehicle owner details by mobile
    if (messageContains(message, "vehicle", "owner", "mobile", "number")) {
        String ownerDetails = fetchOwnerDetailsByMobile(message);
        responseBuilder.append(ownerDetails).append("\n\n");
    }
    if (messageContains(message, "sand", "booking", "status", "user")) {
        String ownerDetails = fetchBookingDetailsByMobile(message);
        responseBuilder.append(ownerDetails).append("\n\n");
    }

    // 🎯 3️⃣ Detect roll IDs (like R1023 or 12345)
    // if (message.matches(".*\\b(r\\d+|\\d{4,})\\b.*")) {
    //     try {
    //         String rollId = message.replaceAll(".*\\b(r\\d+|\\d{4,})\\b.*", "$1").toUpperCase();
    //         List<Map<String, Object>> exams = jdbcTemplate.queryForList(
    //                 "SELECT exam_name, exam_date, exam_time FROM student_exam WHERE roll_id = ?",
    //                 rollId);

    //         if (exams.isEmpty()) {
    //             responseBuilder.append("📋 No exams scheduled for roll ID: ").append(rollId).append("\n\n");
    //         } else {
    //             responseBuilder.append("📚 Exam schedule for roll ID ").append(rollId).append(":\n");
    //             for (Map<String, Object> exam : exams) {
    //                 responseBuilder.append("- ")
    //                         .append(exam.get("exam_name")).append(" on ")
    //                         .append(exam.get("exam_date")).append(" at ")
    //                         .append(exam.get("exam_time")).append("\n");
    //             }
    //             responseBuilder.append("\n");
    //         }

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         responseBuilder.append("⚠️ Error fetching exam schedule.\n\n");
    //     }
    // }

    // 🧩 Default fallback if nothing matched
    if (responseBuilder.length() == 0) {
        return "🤷‍♂️ I couldn't find any matching info in your message.";
    }

    return responseBuilder.toString().trim();
}

public String fetchBookingDetailsByMobile(String message) {
    // 🔍 Regex pattern to match exactly 13-digit numbers
      Pattern idPattern = Pattern.compile("\\b\\d{6}\\b");
    Matcher matcher = idPattern.matcher(message);

    if (matcher.find()) {
        String mobileNumber = matcher.group(0); 

        try {
            // 🧭 SQL query to fetch vehicle/owner details
            String sql = "SELECT sm.status_name ,customer_name,payment_amount FROM customer_details cd " + //
                                "LEFT JOIN status_mst sm ON sm.status_id = cd.status_id " + //
                                                                        " WHERE cd.customer_id =?";
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, Integer.parseInt(mobileNumber));

            // 🎯 Return formatted details
            return String.format(
                "sand Booking Details for %s:\n👤 Name: %s\n🚗 payment_amount: %s\n📅 status_name : %s\n📍 ",
                mobileNumber, 
                result.getOrDefault("customer_name", "N/A"),
                result.getOrDefault("payment_amount", "N/A"),
                result.getOrDefault("status_name", "N/A")
             );

        } catch (EmptyResultDataAccessException e) {
            return "❌ No records found for mobile number: " + mobileNumber;
        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ Something went wrong while fetching owner details.";
        }
    } else {
        return "Please provide a valid 6-digit booking id  in your message.";
    }
}
public String fetchOwnerDetailsByMobile(String message) {
    // 🔍 Regex pattern to match exactly 13-digit numbers
      Pattern idPattern = Pattern.compile("\\b\\d{13}\\b");
    Matcher matcher = idPattern.matcher(message);

    if (matcher.find()) {
        String mobileNumber = matcher.group(0); 

        try {
            // 🧭 SQL query to fetch vehicle/owner details
            String sql = "SELECT owner_name,address,bank_acc_holder_name FROM vehicle_owner_details vod  WHERE mobile_no = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, mobileNumber);

            // 🎯 Return formatted details
            return String.format(
                "📞 Owner Details for %s:\n👤 Name: %s\n🚗 address: %s\n📅 bank_acc_holder_name On: %s\n📍 ",
                mobileNumber,
                result.getOrDefault("owner_name", "N/A"),
                result.getOrDefault("address", "N/A"),
                result.getOrDefault("bank_acc_holder_name", "N/A")
             );

        } catch (EmptyResultDataAccessException e) {
            return "❌ No records found for mobile number: " + mobileNumber;
        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ Something went wrong while fetching owner details.";
        }
    } else {
        return "Please provide a valid 10-digit mobile number in your message.";
    }
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
