package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
@Component
public class MedicalRecordRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordRepository.class);

    // Liste des personnes (sera remplie après le chargement des données)
    private final List<MedicalRecord> medicalRecords = new ArrayList<>();
    private final DataBaseInMemoryWrapper dataBaseInMemoryWrapper;

    public MedicalRecordRepository (DataBaseInMemoryWrapper dataBaseInMemoryWrapper) {
        this.dataBaseInMemoryWrapper = dataBaseInMemoryWrapper;
    }

    public List<MedicalRecord> getMedicalRecords() {
        try {
            if (medicalRecords.isEmpty()) {
                LOGGER.info("MedicalRecord list is empty, loading data...");
                List<MedicalRecord> loadedMedicalRecords = dataBaseInMemoryWrapper.getMedicalRecords();
                if (loadedMedicalRecords != null) {
                    medicalRecords.addAll(loadedMedicalRecords);
                    LOGGER.info("Successfully loaded {} medicalRecords.", loadedMedicalRecords.size());
                } else {
                    LOGGER.warn("No persons found in DataBaseInMemoryWrapper.");
                }
            }
            return new ArrayList<>(medicalRecords);
        } catch (Exception e) {
            LOGGER.error("Error loading Json data: {}", e.getMessage(), e);
        }
        return List.of(); // Retourner une liste vide pour éviter une exception en aval
    }
}
