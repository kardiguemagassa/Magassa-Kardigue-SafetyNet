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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import java.util.List;

import static com.openclassrooms.safetynet.constant.dataBaseInMemory.DataBaseInMemoryWrapperConstant.*;

@Component
@Data
public class DataBaseInMemoryWrapper {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper;
    private ClassLoader classLoader;  // dynamic resource recharge

    private DataWrapper dataWrapper;
    private List<Person> persons = new ArrayList<>();
    private List<MedicalRecord> medicalRecords = new ArrayList<>();
    private List<FireStation> fireStations = new ArrayList<>();

    @Autowired
    public DataBaseInMemoryWrapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.classLoader = getClass().getClassLoader();
    }

    @PostConstruct
    public void loadData() {
        try {
            LOGGER.info(DataBaseInMemoryWrapperConstant.LOADING_JSON_DATA, DATA_JSON_PATH);

            try (InputStream inputStream = classLoader.getResourceAsStream(DATA_JSON_PATH)) {
                if (inputStream == null) {
                    throw new FileNotFoundException(DATA_JSON_PATH_NOT_FOUND);
                }
                dataWrapper = objectMapper.readValue(inputStream, new TypeReference<>() {});
            }

            if (dataWrapper != null) {
                persons = new ArrayList<>(dataWrapper.getPersons() != null ? dataWrapper.getPersons() : new ArrayList<>());
                medicalRecords = new ArrayList<>(dataWrapper.getMedicalrecords() != null ? dataWrapper.getMedicalrecords() : new ArrayList<>());
                fireStations = new ArrayList<>(dataWrapper.getFirestations() != null ? dataWrapper.getFirestations() : new ArrayList<>());

                LOGGER.info(DataBaseInMemoryWrapperConstant.DATA_LOADED_SUCCESSFULLY, persons.size(), fireStations.size(), medicalRecords.size());
            } else {
                LOGGER.warn(DATA_NOT_LOADED_SUCCESSFULLY);
            }

        } catch (IOException e) {
            throw new IllegalStateException(DATA_LOADED_ERROR + e.getMessage());
        }
    }
}
