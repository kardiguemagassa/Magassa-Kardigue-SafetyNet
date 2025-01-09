package com.openclassrooms.safetynet.dataBaseInMemory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Data
//@AllArgsConstructor
public class DataBaseInMemoryWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseInMemoryWrapper.class);
    private static final String DATA_JSON_PATH = "/data/data.json";
    private final ObjectMapper objectMapper;

    private final List<Person> persons = new ArrayList<>();
    private final List<MedicalRecord> medicalRecords = new ArrayList<>();
    private final List<FireStation> fireStations = new ArrayList<>();

    public DataBaseInMemoryWrapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void loadData() {
        try {
            LOGGER.info("Loading data from file: {}", DATA_JSON_PATH);

            // Load data from JSON file
            DataWrapper dataWrapper = loadJson(DATA_JSON_PATH, new TypeReference<>() {});

            if (dataWrapper != null) {
                // Safely add data to lists
                persons.addAll(validateList(dataWrapper.getPersons()));
                medicalRecords.addAll(validateList(dataWrapper.getMedicalrecords()));
                fireStations.addAll(validateList(dataWrapper.getFirestations()));

                LOGGER.info("Data loaded successfully: {} persons, {} fire stations, {} medical records.",
                        persons.size(), fireStations.size(), medicalRecords.size());
            } else {
                LOGGER.warn("DataWrapper is null. No data was loaded.");
            }

        } catch (Exception e) {
            LOGGER.error("Error loading JSON data: {}", e.getMessage(), e);
        }
    }

    private <T> T loadJson(String path, TypeReference<T> typeReference) throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("JSON file not found: " + path);
            }
            return objectMapper.readValue(inputStream, typeReference);
        }
    }

    private <T> List<T> validateList(List<T> list) {
        return (list == null) ? Collections.emptyList() : list;
    }

    public List<Person> getPersons() {return persons;}
    public List<FireStation> getFireStations() {return fireStations;}
    public List<MedicalRecord> getMedicalRecords() {return medicalRecords;}

    // Internal class to map JSON structure
    @Data
    @NoArgsConstructor
    private static class DataWrapper {
        private List<Person> persons;
        private List<FireStation> firestations;
        private List<MedicalRecord> medicalrecords;

        public List<Person> getPersons() {return persons;}
        public List<FireStation> getFirestations() {return firestations;}
        public List<MedicalRecord> getMedicalrecords() {return medicalrecords;}
    }
}
