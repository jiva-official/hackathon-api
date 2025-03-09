package com.codesurge.hackathon.exception;


public class HackathonException extends RuntimeException {
    private final String errorCode;

    public HackathonException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}