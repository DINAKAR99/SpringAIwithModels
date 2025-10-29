
package com.telusko.SpringAIDemo;
 import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/openai")
@CrossOrigin("*")
public class OpenAIController {

    private final OpenAiChatModel chatModel;
    private final Map<String, List<String>> userConversations = new HashMap<>();

    public OpenAIController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/{message}")
    public ResponseEntity<String> chat(@PathVariable String message) {
        userConversations.putIfAbsent("1", new ArrayList<>());
        List<String> history = userConversations.get("1");

        history.add("User: " + message);

        String prompt = String.join("\n", history) + "\nAI:";
        String response = chatModel.call(prompt);

        history.add("AI: " + response);

        return ResponseEntity.ok(response);
    }
}
