package com.openclassrooms.safetynet.constant.service;

import java.time.format.DateTimeFormatter;

public class ResidentInfoImplConstant {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static final String API_ADDRESS_NUMBER_NOT_FOUND = "No addresses number in getPersonsByStation method: {} ";
    public static final String API_ADDRESS_NOT_FOUND = "No residents found for addresses: {}";

    public static final String FIRE_STATION_LIST_NOT_FOUND = "No addresses found for the provided station numbers.";
    public static final String LAST_NAME_NOT_NULL = "Last name cannot be null or empty.";
    public static final String CITY_NOT_FOUND = "City cannot be null or empty.";
    public static final String PERSON_ERROR_EMAIL = "Unexpected error while retrieving community emails for city look repository {}: {}";

    public static final String RESIDENTS_INVALID = "Invalid resident type: {}, Unsupported resident type: ";
    public static final String MESSING_MEDICAL = "Missing medical record for {} {}";
    public static final String ERROR_REPOSITORIES = "Same thing wrong in repositories look personRepository or medicalRecordRepository: {}";
    public static final String MESSING_BIRTH_DATE = "Error parsing birthdate for {} {}: {}";

    public static final String PERSON_NOT_FOUND = "No person found for fullName:";
}
