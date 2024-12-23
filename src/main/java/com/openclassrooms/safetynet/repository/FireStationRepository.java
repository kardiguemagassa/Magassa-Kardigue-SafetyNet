package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.MedicalRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


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

    /**
     * Retourne la liste des FireStations depuis la base de données.
     *
     * @return Liste des FireStations ou une liste vide si une erreur survient.
     */
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

    /**
     * Enregistre une liste de FireStations dans la base de données.
     *
     * @param fireStationList Liste de FireStations à enregistrer.
     * @return La liste complète des FireStations après enregistrement.
     */
    public List<FireStation> saveAll(List<FireStation> fireStationList) {
        try {
            List<FireStation> fireStations = dataBaseInMemoryWrapper.getFireStations();
            if (fireStations == null) {
                LOGGER.error("Database wrapper returned a null fire station list.");
                return List.of();
            }

            fireStations.addAll(fireStationList);

            LOGGER.info("Successfully registered {} fire stations.", fireStationList.size());
            return fireStations;
        } catch (Exception e) {
            LOGGER.error("Error registering fire stations: {}", e.getMessage(), e);
            return List.of(); // Retourne une liste vide en cas d'erreur
        }
    }
}
