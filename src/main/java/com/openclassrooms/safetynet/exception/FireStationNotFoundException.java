package com.openclassrooms.safetynet.exception;

public class FireStationNotFoundException extends RuntimeException{

    public FireStationNotFoundException(String message) {
        super(message);
    }
}
