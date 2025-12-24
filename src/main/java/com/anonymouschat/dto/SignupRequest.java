package com.anonymouschat.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record SignupRequest(
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String password,
        String gender,
        List<String> preferredGenders,
        List<String> interests,
        String location
) {}
