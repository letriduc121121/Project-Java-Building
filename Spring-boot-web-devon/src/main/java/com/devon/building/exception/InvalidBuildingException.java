package com.devon.building.exception;

public class InvalidBuildingException extends RuntimeException {
    
    public InvalidBuildingException(String message) {
        super(message);
    }
    
    public InvalidBuildingException(String message, Throwable cause) {
        super(message, cause);
    }
}
