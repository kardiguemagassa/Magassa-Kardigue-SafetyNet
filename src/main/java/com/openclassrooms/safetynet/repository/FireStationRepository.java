package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.FireStation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;


//@Data
@Component
@Repository
//@AllArgsConstructor
public class FireStationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(FireStationRepository.class);

    private final List<FireStation> fireStations = new ArrayList<>();

    private final DataBaseInMemoryWrapper dataBaseInMemoryWrapper;

    public FireStationRepository (DataBaseInMemoryWrapper dataBaseInMemoryWrapper) {
        this.dataBaseInMemoryWrapper = dataBaseInMemoryWrapper;
    }


    public List<FireStation> getFireStations() {
        try {
            List<FireStation> loadedFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (fireStations == null || fireStations.isEmpty()) {
                LOGGER.warn("No fire stations found in the database.");

                if (loadedFireStations != null) {
                    fireStations.addAll(loadedFireStations);
                    LOGGER.info("Successfully loaded {} medicalRecords.", loadedFireStations.size());
                    return List.of();
                } else {
                    LOGGER.warn("No persons found in DataBaseInMemoryWrapper.");
                }
            }
            LOGGER.info("Successfully retrieved {} fire stations.", fireStations.size());
            return fireStations;
        } catch (Exception e) {
            LOGGER.error("Error retrieving fire stations: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<FireStation> saveAll(List<FireStation> fireStationList) {
        if (fireStationList == null || fireStationList.isEmpty()) {
            LOGGER.warn("Attempted to save an empty or null fire station list.");
            return getFireStations(); // Retourne la liste actuelle sans modification
        }

        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations != null) {
                allFireStations.addAll(fireStationList); // Mise à jour du wrapper
            } else {
                LOGGER.warn("DataBaseInMemoryWrapper fire stations list is null. Creating a new list.");
                //dataBaseInMemoryWrapper.setFireStations(new ArrayList<>(fireStationList));
            }

            LOGGER.info("Successfully saved {} fire stations.", fireStationList.size());
            return getFireStations(); // Retourner l'état mis à jour
        } catch (Exception e) {
            LOGGER.error("Error saving fire stations: {}", e.getMessage(), e);
            return getFireStations();
        }
    }

    public FireStation save(FireStation fireStation) {
        if (fireStation == null) {
            LOGGER.warn("Attempted to save a null fire station.");
            return null;
        }

        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations != null) {
                allFireStations.add(fireStation); // Mise à jour du wrapper
            } else {
                LOGGER.warn("DataBaseInMemoryWrapper fire stations list is null. Creating a new list.");
                //dataBaseInMemoryWrapper.setFirestations(new ArrayList<>(List.of(fireStation)));
            }

            LOGGER.info("Successfully saved fire station: {}", fireStation);
            return fireStation;
        } catch (Exception e) {
            LOGGER.error("Error saving fire station: {}", e.getMessage(), e);
            return null;
        }
    }

    public Optional<FireStation> update(FireStation updatedFireStation) {
        if (updatedFireStation == null) {
            LOGGER.warn("Attempted to update a null fire station.");
            return Optional.empty();
        }

        try {
            FireStation existingFireStation = findByAddress(updatedFireStation.getAddress());
            if (existingFireStation == null) {
                LOGGER.info("Fire station not found, adding a new one: {}", updatedFireStation);
                save(updatedFireStation);
                return Optional.of(updatedFireStation);
            }

            if (!existingFireStation.getStation().equals(updatedFireStation.getStation())) {
                LOGGER.info("Updating fire station at address: {}", updatedFireStation.getAddress());
                existingFireStation.setStation(updatedFireStation.getStation());
            }

            return Optional.of(existingFireStation);
        } catch (Exception e) {
            LOGGER.error("Error updating fire station: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Boolean deleteByAddress(String address) {
        if (address == null || address.isBlank()) {
            LOGGER.warn("Attempted to delete a fire station with a null or blank address.");
            return false;
        }

        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations == null) {
                LOGGER.warn("DataBaseInMemoryWrapper fire stations list is null. Cannot perform deletion.");
                return false;
            }

            boolean isDeleted = allFireStations.removeIf(fireStation -> fireStation.getAddress().equalsIgnoreCase(address));
            if (isDeleted) {
                LOGGER.info("Fire station at address {} deleted successfully.", address);
            } else {
                LOGGER.warn("Fire station at address {} not found.", address);
            }

            return isDeleted;
        } catch (Exception e) {
            LOGGER.error("Error deleting fire station at address {}: {}", address, e.getMessage(), e);
            return false;
        }
    }

    // NEW ENDPOINT
    public List<String> findAddressesByStationNumber(int stationNumber) {
        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations == null) {
                LOGGER.warn("DataBaseInMemoryWrapper fire stations list is null.");
                return Collections.emptyList();
            }

            // Filtrer les stations par numéro et collecter uniquement les adresses
            return allFireStations.stream()
                    .filter(station -> station.getStation() != null
                            && station.getStation().equals(String.valueOf(stationNumber)))
                    .map(FireStation::getAddress) // Extraire uniquement les adresses
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Error finding fire stations by station number {}: {}", stationNumber, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public List<String> findAddressesByStationNumberSt(String stationNumber) {
        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations == null) {
                LOGGER.warn("DataBaseInMemoryWrapper fire stations list is null.");
                return Collections.emptyList();
            }

            // Filtrer les stations par numéro et collecter uniquement les adresses
            return allFireStations.stream()
                    .filter(station -> station.getStation() != null
                            && station.getStation().equals(String.valueOf(stationNumber)))
                    .map(FireStation::getAddress) // Extraire uniquement les adresses
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Error finding fire stations by station number {}: {}", stationNumber, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public List<String> findAddressesByStationNumbers(List<Integer> stationNumbers) {
        if (stationNumbers == null || stationNumbers.isEmpty()) {
            LOGGER.warn("Attempted to search with a null or empty station numbers list.");
            return Collections.emptyList();
        }

        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations == null) {
                LOGGER.warn("DataBaseInMemoryWrapper fire stations list is null.");
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
            LOGGER.error("Error finding addresses by station numbers: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public FireStation findByAddress(String address) {
        if (address == null || address.isBlank()) {
            LOGGER.warn("Attempted to find a fire station with a null or blank address.");
            return null;
        }

        try {
            List<FireStation> allFireStations = dataBaseInMemoryWrapper.getFireStations();
            if (allFireStations == null) {
                LOGGER.warn("DataBaseInMemoryWrapper fire stations list is null.");
                return null;
            }

            return allFireStations.stream()
                    .filter(station -> station.getAddress().equalsIgnoreCase(address))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            LOGGER.error("Error finding fire station at address {}: {}", address, e.getMessage(), e);
            return null;
        }
    }
}
