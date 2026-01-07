import { useEffect, useState } from "react";
import {
  Container,
  Paper,
  Box,
  Typography,
  TextField,
  IconButton,
  Divider,
} from "@mui/material";
import SendIcon from "@mui/icons-material/Send";
import SkipNextIcon from "@mui/icons-material/SkipNext";
import LogoutIcon from "@mui/icons-material/Logout";

import {
  connectSocket,
  sendMessage,
  disconnectSocket,
} from "../services/socketService";
import { clearToken } from "../utils/storage";

function Chat({ token }) {
  const [status, setStatus] = useState("connecting");
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");

  useEffect(() => {
    connectSocket(token, handleMessage, handleClose);

    return () => {
      disconnectSocket();
    };
    // eslint-disable-next-line
  }, []);

  const handleMessage = (data) => {
    switch (data.type) {
      case "match":
        setStatus("matched");
        setMessages([
          { system: true, text: "Matched with a stranger" },
        ]);
        break;

      case "message":
        setMessages((prev) => [
          ...prev,
          { system: false, text: data.payload.text },
        ]);
        break;

      case "system":
        setMessages((prev) => [
          ...prev,
          { system: true, text: data.payload.text },
        ]);
        setStatus("waiting");
        break;

      default:
        break;
    }
  };

  const handleClose = () => {
    setStatus("disconnected");
  };

  const handleSend = () => {
    if (!input.trim()) return;

    sendMessage("message", { text: input });

    setMessages((prev) => [
      ...prev,
      { system: false, text: input },
    ]);

    setInput("");
  };

  const handleSkip = () => {
    sendMessage("skip");
    setMessages([]);
    setStatus("waiting");
  };

  const handleLogout = () => {
    disconnectSocket();
    clearToken();
    window.location.reload();
  };

  return (
    <Container maxWidth="sm" sx={{ mt: 4 }}>
      <Paper elevation={3} sx={{ p: 2 }}>
        {/* Header */}
        <Box
          display="flex"
          justifyContent="space-between"
          alignItems="center"
        >
          <Typography variant="h6">
            Anonymous Chat
          </Typography>

          <IconButton onClick={handleLogout}>
            <LogoutIcon />
          </IconButton>
        </Box>

        <Divider sx={{ my: 1 }} />

        {/* Status */}
        <Typography
          variant="body2"
          color="text.secondary"
          align="center"
          sx={{ mb: 1 }}
        >
          {status === "connecting" && "Connecting..."}
          {status === "waiting" && "Waiting for a stranger..."}
          {status === "matched" && "Stranger connected"}
          {status === "disconnected" && "Disconnected"}
        </Typography>

        {/* Messages */}
        <Box
          sx={{
            height: 300,
            overflowY: "auto",
            display: "flex",
            flexDirection: "column",
            gap: 1,
            mb: 2,
          }}
        >
          {messages.map((m, i) => (
            <Box
              key={i}
              alignSelf={m.system ? "center" : "flex-end"}
              sx={{
                bgcolor: m.system ? "grey.200" : "primary.main",
                color: m.system ? "black" : "white",
                px: 2,
                py: 1,
                borderRadius: 2,
                maxWidth: "80%",
              }}
            >
              <Typography variant="body2">
                {m.text}
              </Typography>
            </Box>
          ))}
        </Box>

        {/* Input */}
        {status === "matched" && (
          <Box display="flex" gap={1}>
            <TextField
              fullWidth
              size="small"
              placeholder="Type a message..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") handleSend();
              }}
            />

            <IconButton
              color="primary"
              onClick={handleSend}
            >
              <SendIcon />
            </IconButton>

            <IconButton
              color="warning"
              onClick={handleSkip}
            >
              <SkipNextIcon />
            </IconButton>
          </Box>
        )}
      </Paper>
    </Container>
  );
}

export default Chat;
