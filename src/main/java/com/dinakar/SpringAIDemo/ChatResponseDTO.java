package com.telusko.SpringAIDemo;

import java.util.List;

public class ChatResponseDTO {

    private String id;
    private long created;
    private String model;
    private String object;
    private List<Choice> choices;
    private Usage usage;

    // getters and setters

    public static class Choice {
        private String finish_reason;
        private int index;
        private Message message;

        // getters and setters
    }

    public static class Message {
        private String content;
        private String role;

        // getters and setters
    }

    public static class Usage {
        private int completion_tokens;
        private int prompt_tokens;
        private int total_tokens;

        // getters and setters
    }

    // Optional: helper method
    public String getFirstMessageContent() {
        if (choices != null && !choices.isEmpty()) {
            return choices.get(0).getMessage().getContent();
        }
        return null;
    }

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
