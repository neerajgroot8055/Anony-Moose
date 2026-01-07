package com.anonymous.chat.dto;

import java.util.List;

public class SignupRequest {

    private String username;
    private String email;
    private String password;
    private List<String> interests;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }
}
