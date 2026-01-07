package com.anonymous.chat.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.anonymous.chat.dto.LoginRequest ;
import com.anonymous.chat.domain.User;
import com.anonymous.chat.dto.SignupRequest;
import com.anonymous.chat.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.anonymous.chat.config.JwtUtil ;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    public void signup(SignupRequest request) {

        // 1. Validation
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        // 2. Uniqueness checks
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        if (request.getEmail() != null &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // 3. Hash password
        String hashedPassword =
                passwordEncoder.encode(request.getPassword());

        // 4. Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(hashedPassword);
        user.setInterests(request.getInterests());

        // 5. Save
        userRepository.save(user);
    }
    public String login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }


}