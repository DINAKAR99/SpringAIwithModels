package com.dinakar.SpringAIDemo.DTO;

import java.util.List;

public class ChatResponseDTO {

    private String id;
    private long created;
    private String model;
    private String object;
    private List<Choice> choices;
    private Usage usage;

    // ✅ Nested classes
    public static class Choice {
        private String finish_reason;
        private int index;
        private Message message;

        public String getFinish_reason() {
            return finish_reason;
        }

        public void setFinish_reason(String finish_reason) {
            this.finish_reason = finish_reason;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    public static class Message {
        private String content;
        private String role;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class Usage {
        private int completion_tokens;
        private int prompt_tokens;
        private int total_tokens;

        public int getCompletion_tokens() {
            return completion_tokens;
        }

        public void setCompletion_tokens(int completion_tokens) {
            this.completion_tokens = completion_tokens;
        }

        public int getPrompt_tokens() {
            return prompt_tokens;
        }

        public void setPrompt_tokens(int prompt_tokens) {
            this.prompt_tokens = prompt_tokens;
        }

        public int getTotal_tokens() {
            return total_tokens;
        }

        public void setTotal_tokens(int total_tokens) {
            this.total_tokens = total_tokens;
        }
    }

    // ✅ Helper method
    public String getFirstMessageContent() {
        if (choices != null && !choices.isEmpty()) {
            Choice first = choices.get(0);
            if (first != null && first.getMessage() != null) {
                return first.getMessage().getContent();
            } else {
                System.err.println("⚠️ Message object is null in first choice!");
            }
        } else {
            System.err.println("⚠️ Choices list is empty or null!");
        }
        return null;
    }

    // ✅ Getters/Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }
}
