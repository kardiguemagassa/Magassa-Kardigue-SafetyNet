package com.openclassrooms.safetynet.constant.service;

public class FireStationImplConstant {

    public static final String FIRE_STATION_NOT_FOUND = "No fireStation found in the repository.";
    public static final String FIRE_STATION_ERROR_CONVERT = "Error while retrieving and converting fire: {}";
    public static final String FIRE_STATION_ERROR_RUN = "An unexpected Error occurred while processing fire station look repository: {}";


    public static final String FIRE_STATION_ERROR_SAVING = "Fire list cannot be null or empty.";
    public static final String FIRE_STATION_ERROR_SAVING_DATA_BASE = "System error while saving persons in the repository: {}";
    public static final String FIRE_STATION_ERROR_SAVING_REPO = "Unexpected error occurred while saving fireStations look repository:  {}";

    public static final String FIRE_STATION_ERROR = "fireStationDTO cannot be null.";
    public static final String FIRE_STATION_ERROR_SAVING_CSV_FILE = "DataBaseInMemoryWrapper fire stations list is null. Creating a new list.";
    public static final String FIRE_STATION_SAVING_DATA_BASE_SUC = "Successfully saved fire station: {}";
    public static final String FIRE_STATION_ERROR_SAVING_DATA_BASE_ = "Error saving fire station: {}";

    public static final String FIRE_STATION_ERROR_UPDATING = "Updated fireStation data cannot be null.";
    public static final String FIRE_STATION_NOT_FOUND_UPDATING = "Error updating fireStation: {}";
    public static final String FIRE_STATION_ERROR_UPDATING_SUCCESS = "Updating fire station at address: {}";
    public static final String FIRE_STATION_ERROR_SAVING_UPDATING_SUCCESS = "Error updating fire station: {}";

    public static final String FIRE_STATION_ERROR_DELETING = "Invalid parameters: address is null/empty. Cannot perform deletion.";
    public static final String FIRE_STATION_DELETING_SUCCESS = "Fire station at address {} deleted successfully.";
    public static final String FIRE_STATION_ERROR_DELETING_NOT_FOUND = "Fire station at address {} not found.";
    public static final String FIRE_STATION_ERROR_DELETING_BY_ADDRESS = "Error deleting fire station at address {}: {}";

}
