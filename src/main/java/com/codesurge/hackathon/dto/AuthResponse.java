package com.codesurge.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class AuthResponse {
    private String token;
    private String username;
    private String message;

    public AuthResponse(final String username, final String message) {
        this.username = username;
        this.message = message;
    }
}
