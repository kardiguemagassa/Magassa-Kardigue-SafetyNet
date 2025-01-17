package com.openclassrooms.safetynet.constant.service;

public class MedicalRecordImplConstant {

    public static final String MEDICAL_RECORD_NOT_FOUND = "No medical records found in the repository.";
    public static final String ERROR_CONVERTING = "Error while retrieving and converting MedicalRecord: {}";
    public static final String MEDICAL_RECORD_UNEXPECT = "An error occurred while retrieving the MedicalRecord.";
    public static final String MEDICAL_RECORD_NOT_FOUND_MSG = "MedicalRecordNotFoundException: {}";

    public static final String MEDICAL_RECORD_ERROR_SAVING = "No medical records found in the provided list.";
    public static final String MEDICAL_RECORD_ERROR_SAVING_DATA_BASE = "System error while medical records in the repository: {}";
    public static final String MEDICAL_RECORD_ERROR_SAVING_REPO = "Unexpected error occurred while saving medicalRecord look repository: {}";

    public static final String MEDICAL_RECORD_ERROR = "MedicalRecordDTO cannot be null.";

    public static final String MEDICAL_RECORD_ERROR_UPDATING = "Updated medicalRecord data cannot be null.";
    public static final String MEDICAL_RECORD_ERROR_SAVING_UPDATING_SUCCESS = "Error updating medicalRecord: {}";

    public static final String MEDICAL_RECORD_ERROR_DELETING = "Invalid parameters: firstName or lastName is null/empty. Cannot perform deletion.";
    public static final String MEDICAL_RECORD_DELETING_SUCCESS = "Person {} {} deleted successfully via repository.";
    public static final String MEDICAL_RECORD_ERROR_DELETING_NOT_FOUND = "Medical {} {} not found for deletion in repository.";
    public static final String MEDICAL_RECORD_ERROR_DELETING_NOT_ = "Error while deleting medical {} {} via repository: {}";
}
