package com.example.air_resume_mate.dto;

import lombok.Getter;

@Getter
public class JwtResponse {
    private final String accessToken;

    public JwtResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}