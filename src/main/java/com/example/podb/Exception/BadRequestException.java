package com.example.podb.Exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Getter
@Setter
public class BadRequestException extends RuntimeException {
    private String message;
    private String status;

    public BadRequestException(String message, HttpStatus status) {
        this.message = message;
        this.status = String.valueOf(status);
    }

}
