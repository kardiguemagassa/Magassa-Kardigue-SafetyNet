package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.FireStation;

import com.openclassrooms.safetynet.utils.CsvUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.stream.Collectors;

import static com.openclassrooms.safetynet.constant.repository.FireStationRepositoryConstant.*;


@Component
public class FireStationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(FireStationRepository.class);

    private final List<FireStation> fireStations = new ArrayList<>();
    private final DataBaseInMemoryWrapper dataBaseInMemoryWrapper;

    private boolean isLoading = false;

    @Autowired
    public FireStationRepository(DataBaseInMemoryWrapper dataBaseInMemoryWrapper) {
        this.dataBaseInMemoryWrapper = dataBaseInMemoryWrapper;
    }

    public List<FireStation> getFireStations() {

        try {
            if (fireStations.isEmpty() && !isLoading) {
                isLoading = true;
                LOGGER.info(FIRE_STATION_List_EMPTY);

                List<FireStation> loadedFireStations = dataBaseInMemoryWrapper.getFireStations();

                if (loadedFireStations != null && !loadedFireStations.isEmpty()) {

                    fireStations.addAll(loadedFireStations);
                    saveFireStationToCsv(loadedFireStations);
                    LOGGER.info(FIRE_STATION_LOADED, loadedFireStations.size());

                } /*else {
                    LOGGER.warn(FIRE_STATION_NOT_FOUND);
                }*/
                isLoading = false;
            }
            return new ArrayList<>(fireStations);
        } catch (Exception e) {
            isLoading = false;
            LOGGER.error(FIRE_STATION_ERROR_LOADING, e.getMessage());
            return Collections.emptyList();
        }
    }

    public FireStation save(FireStation fireStation) {

        if (fireStation == null) {
            LOGGER.warn(FIRE_STATION_ERROR);
            return null;
        }

        try {

            fireStations.add(fireStation);

            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();

            if (allFireStations != null) {
                allFireStations.add(fireStation);
                saveFireStationToCsv(allFireStations);
            }

            LOGGER.info(FIRE_STATION_SAVING_DATA_BASE_SUC, fireStation);

        } catch (Exception e) {
            LOGGER.error(FIRE_STATION_ERROR_SAVING_DATA_BASE_, e.getMessage());
        }
        return fireStation;
    }

    public Optional<FireStation> update(FireStation updatedFireStation) {

        if (updatedFireStation == null) {
            LOGGER.warn(FIRE_STATION_ERROR_UPDATING);
            return Optional.empty();
        }

        try {
            FireStation existingFireStation = findByAddress(updatedFireStation.getAddress());

            if (existingFireStation == null) {
                LOGGER.info(FIRE_STATION_NOT_FOUND_UPDATING, updatedFireStation);
                fireStations.add(updatedFireStation);
                dataBaseInMemoryWrapper.getFireStations().add(updatedFireStation);
                saveFireStationToCsv(fireStations);
                return Optional.of(updatedFireStation);
            }

            if (!existingFireStation.getStation().equals(updatedFireStation.getStation())) {
                LOGGER.info(FIRE_STATION_ERROR_UPDATING_SUCCESS, updatedFireStation.getAddress());
                existingFireStation.setStation(updatedFireStation.getStation());
                save(existingFireStation); // Sauvegarde la mise à jour
            }

            return Optional.of(existingFireStation);
        } catch (Exception e) {
            LOGGER.error(FIRE_STATION_ERROR_SAVING_UPDATING_SUCCESS, e.getMessage());
        }
        return Optional.empty();
    }

    public Boolean deleteByAddress(String address) {

        try {

            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();

            if (allFireStations == null || allFireStations.isEmpty()) {
                LOGGER.warn(FIRE_STATION_NOT_FOUND_DELETING);
                return false;
            }

            saveFireStationToCsv(allFireStations);

            boolean isDeleted = allFireStations.removeIf(fireStation -> fireStation.getAddress().equalsIgnoreCase(address));

            if (isDeleted) {
                LOGGER.info(FIRE_STATION_DELETING_SUCCESS, address);
            }

            return isDeleted;
        } catch (Exception e) {
            LOGGER.error(FIRE_STATION_ERROR_DELETING_BY_ADDRESS, address, e.getMessage());
            return false;
        }
    }

    // NEW ENDPOINT
    public List<String> findAddressesByStationNumber(int stationNumber) {

        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations == null) {
                LOGGER.warn(FIRE_STATION_ADDRESS_NUMBER_NOT_FOUND);
                return null;
            }

            return allFireStations.stream()
                    .filter(station -> station.getStation() != null
                            && station.getStation().equals(String.valueOf(stationNumber)))
                    .map(FireStation::getAddress)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error(ERROR_SEARCHING_ADDRESS_NUMBER, stationNumber, e.getMessage());
            return null;
        }
    }

    public List<String> findAddressesByStationNumbers(List<Integer> stationNumbers) {

        if (stationNumbers == null || stationNumbers.isEmpty()) {
            LOGGER.warn(FIRE_STATION_ERROR_SEARCHING_ADDRESSES_NUMBERS);
            return null;
        }

        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations == null) {
                LOGGER.warn(FIRE_STATION_ADDRESSES_NUMBERS_NOT_FOUND);
                return null;
            }

            Set<String> stationNumbersAsStrings = stationNumbers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet());

            return allFireStations.stream()
                    .filter(station -> station.getStation() != null
                            && stationNumbersAsStrings.contains(station.getStation()))
                    .map(FireStation::getAddress)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error(ERROR_SEARCHING_ADDRESSES_NUMBERS, e.getMessage());
            return null;
        }
    }

    public FireStation findByAddress(String address) {

        if (address == null || address.isBlank()) {
            LOGGER.warn(FIRE_STATION_ERROR_SEARCHING_ADDRESS);
            return null;
        }

        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations == null) {
                LOGGER.warn(FIRE_STATION_ERROR_NULL_ADDRESS);
                return null;
            }

            return allFireStations.stream()
                    .filter(station -> station.getAddress().equalsIgnoreCase(address))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            LOGGER.error(FIRE_STATION_ERROR_FINDING_ADDRESS, address, e.getMessage());
            return null;
        }
    }

    private void saveFireStationToCsv(List<FireStation> fireStationToSave) {

        CsvUtils.saveToCsv(FIRE_STATION_CSV_CONFIG_FILE, fireStationToSave);
    }
}
