package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.FireStation;

import com.openclassrooms.safetynet.utils.CsvUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.stream.Collectors;

import static com.openclassrooms.safetynet.constant.FireStationRepositoryConstant.*;


@Component
@AllArgsConstructor
public class FireStationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(FireStationRepository.class);

    private final List<FireStation> fireStations = new ArrayList<>();
    private final DataBaseInMemoryWrapper dataBaseInMemoryWrapper;

    public List<FireStation> getFireStations() {

        try {
            List<FireStation> loadedFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (fireStations.isEmpty()) {
                LOGGER.warn(FIRE_STATION_List_EMPTY);

                if (loadedFireStations != null) {
                    saveFireStationToCsv(loadedFireStations);
                    fireStations.addAll(loadedFireStations);
                    LOGGER.info(FIRE_STATION_LOADED, loadedFireStations.size());
                    return List.of();
                } else {
                    LOGGER.warn(FIRE_STATION_NOT_FOUND);
                }
            }
            LOGGER.info(FIRE_STATION_LOADED_SUCCESS, fireStations.size());
            return fireStations;
        } catch (Exception e) {
            LOGGER.error(FIRE_STATION_ERROR_LOADING, e.getMessage(), e);
            return List.of();
        }
    }

    public List<FireStation> saveAll(List<FireStation> fireStationList) {

        if (fireStationList == null || fireStationList.isEmpty()) {
            LOGGER.warn(FIRE_STATION_ERROR_SAVING);
            return getFireStations();
        }

        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations != null) {
                allFireStations.addAll(fireStationList);
                saveFireStationToCsv(allFireStations);
            } else {
                LOGGER.warn(FIRE_STATION_SAVING_CSV );
            }

            LOGGER.info(FIRE_STATION_SAVING_DATA_BASE, fireStationList.size());
            return getFireStations();
        } catch (Exception e) {
            LOGGER.error(FIRE_STATION_ERROR_SAVING_DATA_BASE, e.getMessage(), e);
            return getFireStations();
        }
    }

    public FireStation save(FireStation fireStation) {

        if (fireStation == null) {
            LOGGER.warn(FIRE_STATION_ERROR);
            return null;
        }

        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations != null) {
                allFireStations.add(fireStation);
                saveFireStationToCsv(allFireStations);
            } else {
                LOGGER.warn(FIRE_STATION_ERROR_SAVING_CSV_FILE);
            }

            LOGGER.info(FIRE_STATION_SAVING_DATA_BASE_SUC, fireStation);
            return fireStation;
        } catch (Exception e) {
            LOGGER.error(FIRE_STATION_ERROR_SAVING_DATA_BASE_, e.getMessage(), e);
            return null;
        }
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
                save(updatedFireStation);
               saveFireStationToCsv(Collections.singletonList(updatedFireStation));
                return Optional.of(updatedFireStation);
            }

            if (!existingFireStation.getStation().equals(updatedFireStation.getStation())) {
                LOGGER.info(FIRE_STATION_ERROR_UPDATING_SUCCESS, updatedFireStation.getAddress());
                existingFireStation.setStation(updatedFireStation.getStation());
            }

            return Optional.of(existingFireStation);
        } catch (Exception e) {
            LOGGER.error(FIRE_STATION_ERROR_SAVING_UPDATING_SUCCESS, e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Boolean deleteByAddress(String address) {

        if (address == null || address.isBlank()) {
            LOGGER.warn(FIRE_STATION_ERROR_DELETING);
            return false;
        }
        saveFireStationToCsv(Collections.singletonList(findByAddress(address)));
        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations == null) {
                LOGGER.warn(FIRE_STATION_NOT_FOUND_DELETING);
                return false;
            }

            boolean isDeleted = allFireStations.removeIf(fireStation -> fireStation.getAddress().equalsIgnoreCase(address));
            if (isDeleted) {
                LOGGER.info(FIRE_STATION_DELETING_SUCCESS, address);
            } else {
                LOGGER.warn(FIRE_STATION_ERROR_DELETING_NOT_FOUND, address);
            }

            return isDeleted;
        } catch (Exception e) {
            LOGGER.error(FIRE_STATION_ERROR_DELETING_BY_ADDRESS, address, e.getMessage(), e);
            return false;
        }
    }

    // NEW ENDPOINT
    public List<String> findAddressesByStationNumber(int stationNumber) {

        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations == null) {
                LOGGER.warn(FIRE_STATION_ADDRESS_NUMBER_NOT_FOUND);
                return Collections.emptyList();
            }

            return allFireStations.stream()
                    .filter(station -> station.getStation() != null
                            && station.getStation().equals(String.valueOf(stationNumber)))
                    .map(FireStation::getAddress)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error(ERROR_SEARCHING_ADDRESS_NUMBER, stationNumber, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public List<String> findAddressesByStationNumbers(List<Integer> stationNumbers) {

        if (stationNumbers == null || stationNumbers.isEmpty()) {
            LOGGER.warn(FIRE_STATION_ERROR_SEARCHING_ADDRESSES_NUMBERS);
            return Collections.emptyList();
        }

        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations == null) {
                LOGGER.warn(FIRE_STATION_ADDRESSES_NUMBERS_NOT_FOUND);
                return Collections.emptyList();
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
            LOGGER.error(ERROR_SEARCHING_ADDRESSES_NUMBERS, e.getMessage(), e);
            return Collections.emptyList();
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
            LOGGER.error(FIRE_STATION_ERROR_FINDING_ADDRESS, address, e.getMessage(), e);
            return null;
        }
    }

    private void saveFireStationToCsv(List<FireStation> fireStationToSave) {

        CsvUtils.saveToCsv(FIRE_STATION_CSV_CONFIG_FILE, fireStationToSave);
    }
}
