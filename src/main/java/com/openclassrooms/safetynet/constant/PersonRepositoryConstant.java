package com.openclassrooms.safetynet.constant;

public class PersonRepositoryConstant {

    public static final String PERSON_CSV_CONFIG_FILE = "src/main/resources/csv/persons.csv";

    public static final String PERSON_List_EMPTY = "Person list is empty, loading data...";
    public static final String PERSON_LOADED = "Successfully loaded {} persons.";
    public static final String PERSON_NOT_FOUND = "No persons found in DataBaseInMemoryWrapper.";
    public static final String PERSON_ERROR_LOADING = "Error loading Json data: {}";

    public static final String PERSON_ERROR_SAVING = "Attempted to save an empty or null person list";
    public static final String PERSON_SAVING_CSV = "Updated the database and saved to CSV with a total of {} persons.";
    public static final String PERSON_ERROR_SAVING_CSV = "Nothing  persons found in csv file:";
    public static final String PERSON_SAVING_DATA_BASE = "Successfully registered {} persons in DataBaseInMemoryWrapper.";
    public static final String PERSON_ERROR_SAVING_DATA_BASE = "Error registering people: {}";

    public static final String PERSON_ERROR = "Attempted to save a null person.";
    public static final String PERSON_ERROR_SAVING_CSV_FILE = "Nothing  persons found in csv file =:";
    public static final String PERSON_SAVING_DATA_BASE_SUC = "Successfully registered {} persons in DataBaseInMemoryWrapper. ";
    public static final String PERSON_ERROR_SAVING_DATA_BASE_ = "Error registering people: {} ";

    public static final String FULL_NAME_NOT_FOUND = "DataBaseInMemoryWrapper persons list is null.";
    public static final String ERROR_SEARCHING_FULL_NAME = "Error while searching for person by full name: {}";

    public static final String PERSON_ERROR_UPDATING = "Attempted to update a null person.";
    public static final String PERSON_NOT_FOUND_UPDATING = "Person not found, adding a new person: {} {}";
    public static final String PERSON_ERROR_UPDATING_SUCCESS = "Person updated successfully: {}";
    public static final String PERSON_ERROR_SAVING_UPDATING_SUCCESS = "Error updating person: {}";

    public static final String PERSON_ERROR_DELETING = "DataBaseInMemoryWrapper persons list is null. Cannot perform deletion.";
    public static final String PERSON_DELETING_SUCCESS = "Person {} {} deleted successfully.";
    public static final String PERSON_ERROR_DELETING_NOT_FOUND = "Person {} {} not found for deletion.";
    public static final String PERSON_ERROR_DELETING_BY_FULL_NAME = "Error while deleting person by full name: {}";

    public static final String PERSON_ERROR_SEARCHING_ADDRESSES = "Attempted to search with an empty or null addresses list.";
    public static final String PERSON_ADDRESSES_NOT_FOUND = "Nothing  addresses found:";
    public static final String ERROR_SEARCHING_ADDRESSES = "Error while searching for persons by addresses: {}";

    public static final String PERSON_ERROR_SEARCHING_ADDRESS = "Attempted to search with an empty or null address list. ";
    public static final String PERSON_ADDRESS_NOT_FOUND = "Nothing  address found: ";
    public static final String ERROR_SEARCHING_ADDRESS = "Error while searching for persons by address: {} ";

    public static final String LAST_NAME_NULL = "Attempted to search with a null or blank last name.";
    public static final String LAST_NAME_NOT_FOUND = "Nothing  lastname found:";
    public static final String LAST_NAME_ERROR_SEARCHING_ADDRESS = "Error while searching for persons by last name: {}";

    public static final String CITY_IS_NULL = "Attempted to search with a null or blank city.";
    public static final String CITY_NOT_FOUND = "Nothing  city found:";
    public static final String PERSON_TOTAL_IN_DATA_BASE = "Total persons in database: {}";
    public static final String FILTERED_CITY = "Filtered persons for city '{}': {}";
    public static final String CITY_ERROR_SEARCHING = "Error while searching for persons by city: {}";

    public static final String CALCULATED_AGE = "Cannot calculate age for a null birth date.";
    public static final String ERROR_CALCULATED_AGE = "Error while calculating age: {}";

    public static final String ERROR_INVALID_CSV = "Invalid CSV line: {}";
}




