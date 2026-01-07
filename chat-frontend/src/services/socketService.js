let socket = null;

export function connectSocket(token, onMessage, onClose) {
  socket = new WebSocket(
    `ws://localhost:8080/ws?token=${token}`
  );

  socket.onopen = () => {
    console.log("WebSocket connected");
  };

  socket.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data);
      onMessage(data);
    } catch (e) {
      console.error("Invalid WS message", event.data);
    }
  };

  socket.onclose = () => {
    console.log("WebSocket disconnected");
    onClose();
  };

  socket.onerror = (err) => {
    console.error("WebSocket error", err);
  };
}

export function sendMessage(type, payload = {}) {
  if (!socket || socket.readyState !== WebSocket.OPEN) return;

  socket.send(
    JSON.stringify({
      type,
      payload,
    })
  );
}

export function disconnectSocket() {
  if (socket) {
    socket.close();
    socket = null;
  }
}
