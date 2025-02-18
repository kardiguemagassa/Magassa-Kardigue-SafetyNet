package com.openclassrooms.safetynet.constant;

import java.time.format.DateTimeFormatter;

public class ResidentInfoConstant {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static final String API_ADDRESS_NUMBER_NOT_FOUND = "No addresses number in getPersonsByStation method: {} ";
    public static final String API_ADDRESS_NOT_FOUND = "No residents found for addresses: {}";

    public static final String FIRE_STATION_LIST_NOT_FOUND = "No addresses found for the provided station numbers.";
    public static final String LAST_NAME_NOT_NULL = "Last name cannot be null or empty.";
    public static final String CITY_NOT_FOUND = "City cannot be null or empty.";

    public static final String RESIDENTS_INVALID = "Invalid resident type: {}, Unsupported resident type: ";
    public static final String MESSING_MEDICAL = "Missing medical record for {} {}";
    public static final String PERSON_NOT_FOUND = "No person found for fullName:";
}
