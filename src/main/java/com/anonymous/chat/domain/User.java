package com.anonymous.chat.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    // NEVER store raw passwords
    private String passwordHash;

    // User interests for future matching
    private List<String> interests;

    // ACTIVE, BLOCKED, DELETED
    private String status;

    private Instant createdAt;

    public User() {
        this.createdAt = Instant.now();
        this.status = "ACTIVE";
    }

    // -------- Getters & Setters --------

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public List<String> getInterests() {
        return interests;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
