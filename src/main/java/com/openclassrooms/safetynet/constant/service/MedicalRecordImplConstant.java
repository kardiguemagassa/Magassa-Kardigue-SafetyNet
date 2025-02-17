package com.openclassrooms.safetynet.constant.service;

public class MedicalRecordImplConstant {

    public static final String MEDICAL_RECORD_NOT_FOUND = "No medical records found in the repository.";
    public static final String MEDICAL_RECORD_ERROR = "MedicalRecordDTO cannot be null.";
    public static final String MEDICAL_RECORD_ERROR_UPDATING = "Updated medicalRecord data cannot be null.";

    public static final String MEDICAL_RECORD_ERROR_DELETING = "Invalid parameters: firstName or lastName is null/empty. Cannot perform deletion.";
    public static final String MEDICAL_RECORD_DELETING_SUCCESS = "Person {} {} deleted successfully via repository.";
    public static final String MEDICAL_RECORD_ERROR_DELETING_NOT_FOUND = "Medical {} {} not found for deletion in repository.";
}
