package com.openclassrooms.safetynet.constant.service;

public class PersonImpConstant {
    public static final String PERSON_NOT_FOUND = "No persons found in the repository.";

    public static final String PERSON_ERROR_SAVING_DATA_BASE = "System error while saving persons in the repository: {}";
    public static final String PERSON_ERROR = "PersonDTO cannot be null.";

    public static final String PERSON_ERROR_UPDATING = "Updated person data cannot be null.";
    public static final String PERSON_NOT_FOUND_UPDATING = "Person not found for update.";

    public static final String PERSON_ERROR_DELETING = "First name or last name cannot be null or empty.";
    public static final String PERSON_ERROR_DELETING_NOT_FOUND = " Not found for deletion.";
}
