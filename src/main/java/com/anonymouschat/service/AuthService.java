package com.anonymouschat.service;

import com.anonymouschat.config.JwtUtil;
import com.anonymouschat.dto.LoginRequest;
import com.anonymouschat.dto.SignupRequest;
import com.anonymouschat.model.User;
import com.anonymouschat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public void signup(SignupRequest req) {
        User user = User.builder()
                .username(req.username())
                .email(req.email())
                .password(encoder.encode(req.password()))
                .gender(req.gender())
                .preferredGenders(req.preferredGenders())
                .interests(req.interests())
                .location(req.location())
                .status("OFFLINE")
                .build();

        userRepository.save(user);
    }

    public String login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(req.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getId());
    }
}
