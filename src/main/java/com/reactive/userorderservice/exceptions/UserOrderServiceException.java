package com.reactive.userorderservice.exceptions;

public class UserOrderServiceException extends RuntimeException {

    public UserOrderServiceException(String message) {
        super(message);
    }

    public UserOrderServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
