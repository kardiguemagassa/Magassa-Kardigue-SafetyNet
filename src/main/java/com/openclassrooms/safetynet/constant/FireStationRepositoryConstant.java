package com.openclassrooms.safetynet.constant;

public class FireStationRepositoryConstant {

    public static final String FIRE_STATION_CSV_CONFIG_FILE = "src/main/resources/csv/fireStations.csv";

    public static final String FIRE_STATION_List_EMPTY = "No fire stations found in the database.";
    public static final String FIRE_STATION_LOADED = "Successfully loaded {} fireStation.";
    public static final String FIRE_STATION_LOADED_SUCCESS = "Successfully retrieved {} fire stations.";
    public static final String FIRE_STATION_NOT_FOUND = "No fire found in DataBaseInMemoryWrapper.";
    public static final String FIRE_STATION_ERROR_LOADING = "Error retrieving fire stations: {}";

    public static final String FIRE_STATION_ERROR_SAVING = "Attempted to save an empty or null fire station list.";
    public static final String FIRE_STATION_SAVING_CSV = "Updated the database and saved to CSV with a total";
    public static final String FIRE_STATION_SAVING_DATA_BASE = "Successfully saved {} fire stations.";
    public static final String FIRE_STATION_ERROR_SAVING_DATA_BASE = "Error saving fire stations: {}";

    public static final String FIRE_STATION_ERROR = "Attempted to save a null fire station.";
    public static final String FIRE_STATION_ERROR_SAVING_CSV_FILE = "DataBaseInMemoryWrapper fire stations list is null. Creating a new list.";
    public static final String FIRE_STATION_SAVING_DATA_BASE_SUC = "Successfully saved fire station: {}";
    public static final String FIRE_STATION_ERROR_SAVING_DATA_BASE_ = "Error saving fire station: {}";

    public static final String FIRE_STATION_ERROR_UPDATING = "Attempted to update a null fire station.";
    public static final String FIRE_STATION_NOT_FOUND_UPDATING = "Fire station not found, adding a new one: {}";
    public static final String FIRE_STATION_ERROR_UPDATING_SUCCESS = "Updating fire station at address: {}";
    public static final String FIRE_STATION_ERROR_SAVING_UPDATING_SUCCESS = "Error updating fire station: {}";

    public static final String FIRE_STATION_ERROR_DELETING = "Attempted to delete a fire station with a null or blank address.";
    public static final String FIRE_STATION_NOT_FOUND_DELETING = "DataBaseInMemoryWrapper fire stations list is null. Cannot perform deletion.";
    public static final String FIRE_STATION_DELETING_SUCCESS = "Fire station at address {} deleted successfully.";
    public static final String FIRE_STATION_ERROR_DELETING_NOT_FOUND = "Fire station at address {} not found.";
    public static final String FIRE_STATION_ERROR_DELETING_BY_ADDRESS = "Error deleting fire station at address {}: {}";

    public static final String FIRE_STATION_ERROR_SEARCHING_ADDRESSES_NUMBERS = "Attempted to search with a null or empty station numbers list.";
    public static final String FIRE_STATION_ADDRESSES_NUMBERS_NOT_FOUND = "Nothing  int numbers found:";
    public static final String ERROR_SEARCHING_ADDRESSES_NUMBERS = "Error finding addresses by station numbers: {}";

    public static final String FIRE_STATION_ADDRESS_NUMBER_NOT_FOUND = "Nothing  station number found:";
    public static final String ERROR_SEARCHING_ADDRESS_NUMBER = "Error finding fire stations by station number {}: {}";

    public static final String FIRE_STATION_ERROR_SEARCHING_ADDRESS = "Attempted to find a fire station with a null or blank address.";
    public static final String FIRE_STATION_ERROR_NULL_ADDRESS = "DataBaseInMemoryWrapper fire stations list is null.";
    public static final String FIRE_STATION_ERROR_FINDING_ADDRESS = "Error finding fire station at address {}: {}";
}




