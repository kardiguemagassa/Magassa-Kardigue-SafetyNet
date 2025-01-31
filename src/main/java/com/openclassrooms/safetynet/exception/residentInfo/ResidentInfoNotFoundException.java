package com.openclassrooms.safetynet.exception.residentInfo;

public class ResidentInfoNotFoundException extends RuntimeException {

    public ResidentInfoNotFoundException(String message) {
        super(message);
    }

    public ResidentInfoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
