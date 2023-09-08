package com.example.podb.Exception;

public class AlreadyExistsException extends RuntimeException{
    private String message;
    public AlreadyExistsException(String message){
        this.message = message;
    }
}
