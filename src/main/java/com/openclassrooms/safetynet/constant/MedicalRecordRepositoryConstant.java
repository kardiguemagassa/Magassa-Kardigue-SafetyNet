package com.openclassrooms.safetynet.constant;

public class MedicalRecordRepositoryConstant {

    public static final String MEDICAL_RECORD_CSV_CONFIG_FILE = "src/main/resources/csv/medicalRecords.csv";

    public static final String MEDICAL_RECORD_List_EMPTY = "MedicalRecord list is empty, loading data...";
    public static final String MEDICAL_RECORD_LOADED = "Successfully loaded {} medicalRecords.";
    public static final String MEDICAL_RECORD_NOT_FOUND = "No medical found in DataBaseInMemoryWrapper.";
    public static final String MEDICAL_RECORD_ERROR_LOADING = "Error loading Json data: {}";

    public static final String MEDICAL_RECORD_ERROR_SAVING = "Attempted to save an empty or null medical list.";
    public static final String MEDICAL_RECORD_ERROR_SAVING_CSV = "Nothing  medical found in csv file:";
    public static final String MEDICAL_RECORD_SAVING_DATA_BASE = "Successfully registered {} medical in DataBaseInMemoryWrapper.";
    public static final String MEDICAL_RECORD_ERROR_SAVING_DATA_BASE = "Error registering medical: {}";

    public static final String MEDICAL_RECORD_ERROR = "Attempted to save a null medicalRecord.";
    public static final String MEDICAL_RECORD_ERROR_SAVING_CSV_FILE = "Nothing  medical found in csv file =:";
    public static final String MEDICAL_RECORD_SAVING_DATA_BASE_SUC = "Successfully registered medicalRecord: {}";
    public static final String MEDICAL_RECORD_ERROR_SAVING_DATA_BASE_ = "Error registering a medicalRecord {}";

    public static final String MEDICAL_RECORD_NOT_FOUND_BY_ALLERGIES = "Medical records list is null.";
    public static final String ERROR_MEDICAL_RECORD_NOT_FOUND_BY_ALLERGIES = "Error searching by medications and allergies: {}";

    public static final String MEDICAL_RECORD_ERROR_UPDATING = "Attempted to update a null medical record.";
    public static final String MEDICAL_RECORD_NOT_FOUND_UPDATING = "Medical record not found, creating new record.";
    public static final String MEDICAL_RECORD_ERROR_UPDATING_SUCCESS = "Medical record updated successfully.";
    public static final String MEDICAL_RECORD_ERROR_UPDATING_NULL_CSV = " Medical records list is null.";
    public static final String MEDICAL_RECORD_ERROR_SAVING_UPDATING_SUCCESS = "Error updating medical record: {}";

    public static final String MEDICAL_RECORD_ERROR_DELETING = "DataBaseInMemoryWrapper medical records list is null. Cannot perform deletion.";
    public static final String MEDICAL_RECORD_DELETING_SUCCESS = "Medical record {} {} deleted successfully.";
    public static final String MEDICAL_RECORD_ERROR_NOT_FOUND = "Medical record {} {} not found for deletion.";
    public static final String MEDICAL_RECORD_ERROR_DELETING_BY_FULL_NAME = "Error deleting medical record {} {}: {}";

    public static final String FULL_NAME_NOT_FOUND = "medical records list is null.";
    public static final String ERROR_SEARCHING_FULL_NAME = "Error finding medical record {} {}: {}";
}




