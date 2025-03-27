package com.codesurge.hackathon.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    public ErrorResponse(String message2, LocalDateTime now) {
        //TODO Auto-generated constructor stub
    }
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
}