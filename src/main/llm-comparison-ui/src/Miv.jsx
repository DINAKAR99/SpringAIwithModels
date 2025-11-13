import { useState, useCallback, useMemo } from "react";
import "./App.css";

function Miv() {
  const models = useMemo(
    () => [
      // { id: "ollama", name: "Chat GPT-4o", color: "#271715ff" },
      { id: "rag-lite", name: "Ollama local", color: "#E67E22" },
    ],
    []
  );

  const [messages, setMessages] = useState([]);
  const [prompt, setPrompt] = useState("");
  const [loadingModel, setLoadingModel] = useState(null);

  const fetchModelResponse = useCallback(async (model, prompt) => {
    try {
      const encodedPrompt = encodeURIComponent(prompt);
      const response = await fetch(
        `http://localhost:8081/api/${model}/${encodedPrompt}`
      );
      if (!response.ok) throw new Error(`Error: ${response.status}`);
      const data = await response.text();
      return data;
    } catch (error) {
      return `❌ ${error.message}`;
    }
  }, []);

  const handleSend = useCallback(async () => {
    if (!prompt.trim()) return;

    const userMessage = { sender: "user", text: prompt, timestamp: Date.now() };
    setMessages((prev) => [...prev, userMessage]);
    setPrompt("");

    for (const model of models) {
      setLoadingModel(model.id);

      const response = await fetchModelResponse(model.id, prompt);

      const botMessage = {
        sender: model.id,
        modelName: model.name,
        text: response,
        timestamp: Date.now(),
      };

      setMessages((prev) => [...prev, botMessage]);
    }

    setLoadingModel(null);
  }, [prompt, models, fetchModelResponse]);

  return (
    <div className="chat-container">
      <h1 className="chat-header"> Multi-Model Chat</h1>

      <div className="chat-window">
        {messages.map((msg, index) => {
          const model = models.find((m) => m.id === msg.sender);
          const isUser = msg.sender === "user";
          return (
            <div
              key={index}
              className={`chat-bubble ${isUser ? "user" : "bot"}`}
              style={{
                borderColor: model ? model.color : "#ccc",
                backgroundColor: isUser ? "#DCF8C6" : "#fff",
              }}
            >
              {!isUser && (
                <div
                  className="chat-model-name"
                  style={{ color: model?.color || "#555" }}
                >
                  {model?.name || "Unknown Model"}
                </div>
              )}
              <div className="chat-text">{msg.text}</div>
            </div>
          );
        })}
        {loadingModel && (
          <div className="loading">
            {models.find((m) => m.id === loadingModel)?.name} is thinking...
          </div>
        )}
      </div>

      <div className="chat-input-container">
        <textarea
          className="chat-input"
          placeholder="Type your message..."
          value={prompt}
          onChange={(e) => setPrompt(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter" && !e.shiftKey) {
              e.preventDefault();
              handleSend();
            }
          }}
        />
        <button className="chat-send-btn" onClick={handleSend}>
          🚀 Send
        </button>
      </div>
    </div>
  );
}

export default Miv;
