package com.openclassrooms.safetynet.exception.person;

public class UsernameExistException extends Exception {
    public UsernameExistException(String message) {
        super(message);
    }
}
