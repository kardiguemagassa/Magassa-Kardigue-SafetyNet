package com.openclassrooms.safetynet.exception.medicalRecord;

public class MedicalRecordNotFoundException extends RuntimeException {
    public MedicalRecordNotFoundException(String message) {
        super(message);
    }

    public MedicalRecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
