package com.project.orderservice.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CustomException extends Exception{
    String errorCode;
    int status;
    public CustomException(String message, String errorCode, int status){
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
