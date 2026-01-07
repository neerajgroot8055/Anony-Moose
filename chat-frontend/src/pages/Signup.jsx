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
import EmailIcon from "@mui/icons-material/Email";
import LockIcon from "@mui/icons-material/Lock";

import { signup } from "../services/authService";

function Signup({ onSwitchToLogin }) {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [interests, setInterests] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      await signup(
        username,
        email,
        password,
        interests
          .split(",")
          .map((i) => i.trim())
          .filter(Boolean)
      );
      setSuccess(true);
    } catch (err) {
      setError("Signup failed");
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
          ANON CHAT
        </Typography>

        <Typography
          variant="body2"
          color="text.secondary"
          sx={{ mb: 3 }}
        >
          Create your anonymous identity
        </Typography>

        {success ? (
          <>
            <Typography sx={{ mb: 2 }}>
              Account created successfully 🎉
            </Typography>
            <Button
              variant="contained"
              onClick={onSwitchToLogin}
            >
              Go to Login
            </Button>
          </>
        ) : (
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
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              InputProps={{
                startAdornment: <EmailIcon sx={{ mr: 1 }} />,
              }}
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

            <TextField
              fullWidth
              margin="normal"
              placeholder="Interests (comma separated)"
              value={interests}
              onChange={(e) => setInterests(e.target.value)}
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
              {loading ? "Signing up..." : "Sign Up"}
            </Button>
          </Box>
        )}

        {!success && (
          <Button
            variant="text"
            sx={{ mt: 2 }}
            onClick={onSwitchToLogin}
          >
            Already have an account? Login
          </Button>
        )}
      </Paper>
    </Container>
  );
}

export default Signup;
