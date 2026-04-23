package org.example.ex_05.service;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

