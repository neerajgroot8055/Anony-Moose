import { useState } from "react";
import {
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Box,
} from "@mui/material";
import PersonIcon from "@mui/icons-material/Person";
import LockIcon from "@mui/icons-material/Lock";

import { login } from "../services/authService";
import { saveToken } from "../utils/storage";

function Login({ onLogin, onSwitchToSignup }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const token = await login(username, password);
      saveToken(token);
      onLogin(token);
    } catch (err) {
      setError("Invalid username or password");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container
      maxWidth="sm"
      sx={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
      }}
    >
      <Paper
        elevation={4}
        sx={{
          width: "100%",
          p: 4,
          textAlign: "center",
        }}
      >
        {/* App Name */}
        <Typography
          variant="h3"
          fontWeight="bold"
          gutterBottom
        >
          ANONYMOOSE
        </Typography>

        <Typography
          variant="body2"
          color="text.secondary"
          sx={{ mb: 3 }}
        >
          Talk to strangers anonymously
        </Typography>

        <Box component="form" onSubmit={handleSubmit}>
          <TextField
            fullWidth
            margin="normal"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            InputProps={{
              startAdornment: <PersonIcon sx={{ mr: 1 }} />,
            }}
            required
          />

          <TextField
            fullWidth
            margin="normal"
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            InputProps={{
              startAdornment: <LockIcon sx={{ mr: 1 }} />,
            }}
            required
          />

          {error && (
            <Typography
              color="error"
              variant="body2"
              sx={{ mt: 1 }}
            >
              {error}
            </Typography>
          )}

          <Button
            fullWidth
            size="large"
            variant="contained"
            sx={{ mt: 3 }}
            type="submit"
            disabled={loading}
          >
            {loading ? "Logging in..." : "Login"}
          </Button>
        </Box>

        <Button
          variant="text"
          sx={{ mt: 2 }}
          onClick={onSwitchToSignup}
        >
          Don’t have an account? Sign up
        </Button>
      </Paper>
    </Container>
  );
}

export default Login;
