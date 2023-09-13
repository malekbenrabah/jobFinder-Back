package com.example.springsecurity.auth;

public class DuplicateCompanyNameException extends RuntimeException {
    public DuplicateCompanyNameException(String message) {
        super(message);
    }
}
