package com.openclassrooms.safetynet.exception.fireStation;

public class FireStationNotFoundException extends RuntimeException{

    public FireStationNotFoundException(String message) {
        super(message);
    }
}
