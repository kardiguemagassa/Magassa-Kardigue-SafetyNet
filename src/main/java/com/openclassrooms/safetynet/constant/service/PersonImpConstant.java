package com.openclassrooms.safetynet.constant.service;

public class PersonImpConstant {
    public static final String PERSON_NOT_FOUND = "No persons found in the repository.";
    public static final String ERROR_CONVERTING = "Error while retrieving and converting persons: {}";
    public static final String PERSON_UNEXPECT = "An unexpected error occurred while processing persons.";
    public static final String PERSON_NOT_FOUND_MSG = "PersonNotFoundException: {}";

    public static final String PERSON_ERROR_SAVING = "Person list cannot be null or empty.";
    public static final String PERSON_ERROR_SAVING_DATA_BASE = "System error while saving persons in the repository: {}";
    public static final String PERSON_ERROR_SAVING_REPO = "Unexpected error occurred while saving look repository: {}";

    public static final String PERSON_ERROR = "PersonDTO cannot be null.";
    public static final String PERSON_ERROR_SAVING_C = "Error saving person: {}";
    public static final String PERSON_ERROR_SAVING_DATA_BASE_ = "An error occurred while saving look repository: ";

    public static final String PERSON_ERROR_UPDATING = "Updated person data cannot be null.";
    public static final String PERSON_NOT_FOUND_UPDATING = "Person not found for update.";
    public static final String PERSON_ERROR_UPDATING_SUCCESS = "An error occurred while updating the person look {} ";
    public static final String PERSON_ERROR_SAVING_UPDATING_SUCCESS = "Error updating person: {}";

    public static final String PERSON_ERROR_DELETING = "First name or last name cannot be null or empty.";
    public static final String PERSON_DELETING_SUCCESS = "Person {} {} deleted successfully.";
    public static final String PERSON_ERROR_DELETING_NOT_FOUND = " Not found for deletion.";
    public static final String PERSON_ERROR_DELETING_NOT_ = "Error while deleting person {} {}: {}";
    public static final String PERSON_ERROR_DELETING_BY_FULL_NAME = "Error while deleting person by full name: look repository: {}";

    public static final String EMAIL_NOT_FOUND = "No emails found for residents in city: {}";
    public static final String CITY_NOT_FOUND = "City cannot be null or empty.";
    public static final String RESIDENTS_NOT_FOUND = "No residents found in city: {}";
    public static final String PERSON_ERROR_EMAIL = "Unexpected error while retrieving community emails for city look repository {}: {}";
}
