package com.project.productservice.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Data
public class ResourceNotFoundException extends Exception{
    private String errorCode;
    public ResourceNotFoundException(String message, String errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
