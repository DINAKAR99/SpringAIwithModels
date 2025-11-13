import { useState, useCallback, useMemo, useEffect } from "react";
import "./App.css";
import { useRef } from "react";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import rehypeHighlight from "rehype-highlight";
import "highlight.js/styles/github-dark.css"; // for dark mode


function App() {
  const models = useMemo(
    () => [{ id: "rag-lite", name: "Ollama", color: "#919292ff" }],
    []
  );
const textareaRef = useRef(null);

  const [messages, setMessages] = useState([]);
  const [prompt, setPrompt] = useState("");
  const [loadingModel, setLoadingModel] = useState(null);
  const [theme, setTheme] = useState("dark");
  const [sidebarOpen, setSidebarOpen] = useState(true);

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
      const fullText = await fetchModelResponse(model.id, prompt);

      let streamed = "";
      setMessages((prev) => [
        ...prev,
        { sender: model.id, text: "", timestamp: Date.now(), streaming: true }
      ]);

      for (let i = 0; i < fullText.length; i++) {
        streamed += fullText[i];

        setMessages((prev) => {
          const updated = [...prev];
          updated[updated.length - 1] = {
            ...updated[updated.length - 1],
            text: streamed
          };
          return updated;
        });

        await new Promise((res) => setTimeout(res, 1)); // speed of typing
      }

    }

    setLoadingModel(null);
  }, [prompt, models, fetchModelResponse]);

  // auto-scroll like ChatGPT
  useEffect(() => {
    const box = document.querySelector(".messages");
    if (box) box.scrollTop = box.scrollHeight;
  }, [messages]);

  // toggle theme
  const toggleTheme = () => {
    setTheme((prev) => (prev === "light" ? "dark" : "light"));
  };

  return (
    <div className={`layout ${theme}`}>

      {/* Sidebar */}
      <aside className={`sidebar ${sidebarOpen ? "open" : "collapsed"}`}>
  <h2 className="logo">Optimus Prime 
    <button className="collaspse-btn" onClick={() => setSidebarOpen(!sidebarOpen)}>
      
    
  </button></h2> 

  <button className="new-chat">+ New Chat</button>

  {sidebarOpen && (
    <div className="model-list">
      {models.map((m) => (
        <div key={m.id} className="model-item">
          <span className="dot" style={{ background: m.color }}></span>
          {m.name}
        </div>
      ))}
    </div>
  )}

  <button className="theme-switch" onClick={toggleTheme}>
    {theme === "light" ? "🌙 Dark Mode" : "☀️ Light Mode"}
  </button>

   {/* Toggle button */}
  <button className="collapse-btn" style={{color:"black",margin:"21px"}} onClick={() => setSidebarOpen(!sidebarOpen)}>
    {sidebarOpen ? "➤" : "➤"}
  </button>
 
</aside>


      {/* Chat area */}
      <main className="chat-area">

        <div className="messages">
          {messages.map((msg, i) => {
            const model = models.find((m) => m.id === msg.sender);
            const isUser = msg.sender === "user";

            return (
              <div key={i} className={`msg-row ${isUser ? "user" : "bot"}`}>
                {!isUser && <div className="avatar bot-avatar"></div>}
                {isUser && <div className="avatar user-avatar"></div>}
                <div className="msg-bubble">
                  {!isUser && (
                    <div className="msg-model" style={{ color: model?.color }}>
                      {model?.name}
                    </div>
                  )}
                  <div className="msg-text">
                    <ReactMarkdown
                      remarkPlugins={[remarkGfm]}
                      rehypePlugins={[rehypeHighlight]}
                    >
                      {msg.text}
                    </ReactMarkdown>
                  </div>
                </div>
              </div>
            );
          })}

          {loadingModel && (
            <div className="msg-row bot">
              <div className="avatar bot-avatar"></div>
              <div className="thinking">Thinking...</div>
            </div>
          )}
        </div>

        {/* Floating input */}
        <div className="chat-input-wrapper">
  <div className="chat-input-bar">
    
    <button className="left-icon">+</button>

   <textarea
  ref={textareaRef}
  className="chat-text"
  placeholder="Ask anything"
  style={{display:"flex",alignItems:"center"}}
  value={prompt}
  onChange={(e) => {
    setPrompt(e.target.value);

    // Auto expand
    textareaRef.current.style.height = "auto";
    textareaRef.current.style.height = textareaRef.current.scrollHeight + "px";
  }}
  onKeyDown={(e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  }}
/>


    <div className="right-icons">

  <button className="icon-btn mic-btn">
    <svg width="18" height="18" viewBox="0 0 24 24" stroke="currentColor" fill="none" strokeWidth="2">
      <path d="M12 1C10.3 1 9 2.3 9 4V12C9 13.7 10.3 15 12 15C13.7 15 15 13.7 15 12V4C15 2.3 13.7 1 12 1Z"/>
      <path d="M5 9V11C5 14.9 8.1 18 12 18C15.9 18 19 14.9 19 11V9"/>
      <path d="M12 18V23"/>
      <path d="M8 23H16"/>
    </svg>
  </button>

  <button className="icon-btn waveform-btn">
    <svg width="18" height="18" viewBox="0 0 24 24" stroke="currentColor" fill="none" strokeWidth="2">
      <rect x="3" y="10" width="2" height="4" rx="1"/>
      <rect x="7" y="8" width="2" height="8" rx="1"/>
      <rect x="11" y="6" width="2" height="12" rx="1"/>
      <rect x="15" y="8" width="2" height="8" rx="1"/>
      <rect x="19" y="10" width="2" height="4" rx="1"/>
    </svg>
  </button>

  {/* SEND BUTTON */}
  {prompt.trim().length > 0 && (
  <button className="icon-btn send-btn">
  <svg width="18" height="18" viewBox="0 0 24 24" stroke="currentColor" fill="none" strokeWidth="2">
    <path d="M7 17L17 7" strokeLinecap="round" strokeLinejoin="round" />
    <path d="M7 7H17V17" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
</button>

  )}
  
</div>


  </div>
</div>



      </main>
    </div>
  );
}

export default App;
