package com.openclassrooms.safetynet.databaseInMemory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.constant.dataBaseInMemory.DataBaseInMemoryWrapperConstant;
import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.dataBaseInMemory.DataWrapper;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataBaseInMemoryWrapperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseInMemoryWrapperTest.class);


    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DataBaseInMemoryWrapper dataBaseInMemoryWrapper;

    private DataWrapper mockDataWrapper;

    @BeforeEach
    void setUp() throws Exception {
        mockDataWrapper = new DataWrapper();
        mockDataWrapper.setPersons(List.of(new Person()));
        mockDataWrapper.setMedicalrecords(List.of(new MedicalRecord()));
        mockDataWrapper.setFirestations(List.of(new FireStation()));

        InputStream mockInputStream = mock(InputStream.class);
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(mockDataWrapper);
    }

    @Test
    void shouldReturnLoadDataSuccess() {

        LOGGER.info("Start method : shouldReturnLoadData_Success");
        dataBaseInMemoryWrapper.loadData();

        assertNotNull(dataBaseInMemoryWrapper.getPersons());
        assertNotNull(dataBaseInMemoryWrapper.getMedicalRecords());
        assertNotNull(dataBaseInMemoryWrapper.getFireStations());

        assertEquals(1, dataBaseInMemoryWrapper.getPersons().size());
        assertEquals(1, dataBaseInMemoryWrapper.getMedicalRecords().size());
        assertEquals(1, dataBaseInMemoryWrapper.getFireStations().size());
        LOGGER.info("End method : shouldReturnLoadData_Success : Test passed");
    }
}
