import { useState } from "react";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Chat from "./pages/Chat";

function App() {
  const [token, setToken] = useState(null);
  const [showSignup, setShowSignup] = useState(false);

  if (!token) {
    return showSignup ? (
      <Signup onSwitchToLogin={() => setShowSignup(false)} />
    ) : (
      <Login
        onLogin={setToken}
        onSwitchToSignup={() => setShowSignup(true)}
      />
    );
  }

  return <Chat token={token} />;
}

export default App;
