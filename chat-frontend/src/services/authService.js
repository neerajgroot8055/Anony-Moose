const API_BASE = "http://localhost:8080";

export async function login(username, password) {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ username, password }),
  });

  if (!res.ok) {
    throw new Error("Invalid credentials");
  }

  const token = await res.text();
  return token;
}

export async function signup(username, email, password, interests) {
  const res = await fetch("http://localhost:8080/auth/signup", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      username,
      email,
      password,
      interests,
    }),
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || "Signup failed");
  }

  return true;
}

