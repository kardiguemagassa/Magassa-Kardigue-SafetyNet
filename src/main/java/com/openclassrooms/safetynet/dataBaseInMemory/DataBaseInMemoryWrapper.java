package com.openclassrooms.safetynet.dataBaseInMemory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.FireStationRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Component
@Data
@AllArgsConstructor
public class DataBaseInMemoryWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseInMemoryWrapper.class);

    private final List<Person> persons = new ArrayList<>();
    private final List<MedicalRecord> medicalRecords = new ArrayList<>();
    private final List<FireStation> fireStations = new ArrayList<>();

    @PostConstruct
    public void loadData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Load data from JSON file
            DataWrapper dataWrapper = loadJson("/data/data.json", objectMapper, new TypeReference<>() {});

            // Add data to lists
            persons.addAll(dataWrapper.getPersons());
            medicalRecords.addAll(dataWrapper.getMedicalrecords());
            fireStations.addAll(dataWrapper.getFirestations());

            LOGGER.info("Data loaded successfully !");

            /*
            // Display data in the console
            LOGGER.info("=== Displaying Loaded Data ===");
            LOGGER.info("Persons:");
            persons.stream().map(Person::toString).forEach(LOGGER::info);
            LOGGER.info("FireStations:");
            fireStations.stream().map(FireStation::toString).forEach(LOGGER::info);
            LOGGER.info("MedicalRecords:");
            medicalRecords.stream().map(MedicalRecord::toString).forEach(LOGGER::info);

             */

            LOGGER.info("{} people in charge.", persons.size());
            LOGGER.info("{} Fire stations loaded.", fireStations.size());
            LOGGER.info("{} medical records loaded.", medicalRecords.size());


        } catch (Exception e) {
            LOGGER.error("Error loading JSON data: {}", e.getMessage(), e);
        }
    }

    /**
     * Méthode générique pour charger un fichier JSON dans un objet Java.
     *
     * @param <T>           Le type d'objet à retourner.
     * @param path          Le chemin vers le fichier JSON.
     * @param objectMapper  L'instance d'ObjectMapper pour la désérialisation.
     * @param typeReference Le type attendu (ex : DataWrapper).
     * @return L'objet Java désérialisé à partir du fichier JSON.
     * @throws Exception Si une erreur se produit pendant la lecture du fichier JSON.
     */
    private <T> T loadJson(String path, ObjectMapper objectMapper, TypeReference<T> typeReference) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(path);

        if (inputStream == null) {
            throw new IllegalArgumentException("JSON file not found : " + path);
        }

        return objectMapper.readValue(inputStream, typeReference);
    }

    public List<Person> getPersons() {
        return persons;
    }

    public List<FireStation> getFireStations() {
        return fireStations;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    // Internal class to map JSON structure
    @Data
    @NoArgsConstructor
    private static class DataWrapper {
        private List<Person> persons;
        private List<FireStation> firestations;
        private List<MedicalRecord> medicalrecords;

        public List<Person> getPersons() {
            return persons;
        }

        public List<FireStation> getFirestations() {
            return firestations;
        }

        public List<MedicalRecord> getMedicalrecords() {
            return medicalrecords;
        }

        public void setPersons(List<Person> persons) {
            this.persons = persons;
        }
        public void setFirestations(List<FireStation> firestations) {
            this.firestations = firestations;
        }
        public void setMedicalrecords(List<MedicalRecord> medicalrecords) {
            this.medicalrecords = medicalrecords;
        }
    }
}
