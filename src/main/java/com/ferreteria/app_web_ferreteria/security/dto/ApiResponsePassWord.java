package com.ferreteria.app_web_ferreteria.security.dto;

import org.springframework.http.HttpStatus;

public class ApiResponsePassWord {

    private String message;
    private HttpStatus status;

    public ApiResponsePassWord(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
