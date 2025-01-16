package com.openclassrooms.safetynet.dataBaseInMemory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.constant.dataBaseInMemory.DataBaseInMemoryWrapperConstant;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;

import jakarta.annotation.PostConstruct;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.openclassrooms.safetynet.constant.dataBaseInMemory.DataBaseInMemoryWrapperConstant.*;

@Component
@Data
public class DataBaseInMemoryWrapper {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private DataWrapper dataWrapper;

    private final ObjectMapper objectMapper;
    private final List<Person> persons = new ArrayList<>();
    private final List<MedicalRecord> medicalRecords = new ArrayList<>();
    private final List<FireStation> fireStations = new ArrayList<>();

    @PostConstruct
    public void loadData() {
        try {
            LOGGER.info(DataBaseInMemoryWrapperConstant.LOADING_JSON_DATA, DATA_JSON_PATH);

            // Load data from JSON file
            dataWrapper = loadJson(DATA_JSON_PATH, new TypeReference<>() {});

            if (dataWrapper != null) {
                // Safely add data to lists
                persons.addAll(validateList(dataWrapper.getPersons()));
                medicalRecords.addAll(validateList(dataWrapper.getMedicalrecords()));
                fireStations.addAll(validateList(dataWrapper.getFirestations()));

                LOGGER.info(DataBaseInMemoryWrapperConstant.DATA_LOADED_SUCCESSFULLY, persons.size(), fireStations.size(),
                        medicalRecords.size());

            } else {
                LOGGER.warn(DATA_NOT_LOADED_SUCCESSFULLY);
            }

        } catch (Exception e) {
            LOGGER.error(DataBaseInMemoryWrapperConstant.DATA_LOADED_ERROR, e.getMessage(), e);
        }
    }

    private <T> T loadJson(String path, TypeReference<T> typeReference) throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IllegalArgumentException(DATA_JSON_PATH_NOT_FOUND + path);
            }
            return objectMapper.readValue(inputStream, typeReference);
        }
    }

    private <T> List<T> validateList(List<T> list) {
        return (list == null) ? Collections.emptyList() : list;
    }
}
