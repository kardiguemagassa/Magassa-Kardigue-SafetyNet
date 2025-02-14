package com.openclassrooms.safetynet.convertorDTO;

import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.model.MedicalRecord;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordConvertorDTOTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordConvertorDTOTest.class);

    @InjectMocks
    private MedicalRecordConvertorDTO medicalRecordConvertorDTO;


    private MedicalRecord medicalRecord1;
    private MedicalRecord medicalRecord2;
    private MedicalRecordDTO medicalRecordDTO1;
    private MedicalRecordDTO medicalRecordDTO2;

    @BeforeEach
    void setUp() {

        medicalRecord1 = MedicalRecord.builder()
                .firstName("John")
                .lastName("Doe")
                .birthdate("01/01/1990")
                .medications(List.of("aznol:350mg","hydrapermazol:100mg"))
                .allergies(List.of("nillacilan"))
                .build();
        medicalRecord2 = MedicalRecord.builder()
                .firstName("Jane")
                .lastName("Doe")
                .birthdate("01/01/2000")
                .medications(List.of("aznol:350mg","hydrapermazol:100mg"))
                .allergies(List.of("nillacilan"))
                .build();

        medicalRecordDTO1 = MedicalRecordDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .birthdate("01/01/1990")
                .medications(List.of("aznol:350mg","hydrapermazol:100mg"))
                .allergies(List.of("nillacilan"))
                .build();
        medicalRecordDTO2 = MedicalRecordDTO.builder()
                .firstName("Jane")
                .lastName("Doe")
                .birthdate("01/01/2000")
                .medications(List.of("aznol:350mg","hydrapermazol:100mg"))
                .allergies(List.of("nillacilan"))
                .build();
    }

    @Test
    void shouldReturnConvertEntityToDto() {
        LOGGER.info("Start method: convertEntityToDto");
        MedicalRecordDTO medicalRecordDTO = medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1);

        // Then
        assertNotNull(medicalRecordDTO);
        assertEquals(medicalRecord1.getFirstName(), medicalRecordDTO.getFirstName());
        assertEquals(medicalRecord1.getLastName(), medicalRecordDTO.getLastName());
        LOGGER.info("End method: convertEntityToDto : Test passed");
    }

    @Test
    void shouldReturnConvertEntityToDtoNull () {
        LOGGER.info("Start method: convertEntityToDtoNull");
        assertNull(medicalRecordConvertorDTO.convertEntityToDto((MedicalRecord) null));
        LOGGER.info("End method: convertEntityToDtoNull : Test passed");
    }

    @Test
    void shouldReturnConvertDtoToEntity() {
        LOGGER.info("Start method: shouldReturnConvertDtoToEntity");
        MedicalRecord medicalRecord = medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTO1);

        assertNotNull(medicalRecord);
        assertEquals(medicalRecord1.getFirstName(), medicalRecord.getFirstName());
        assertEquals(medicalRecord1.getLastName(), medicalRecord.getLastName());
        LOGGER.info("End method: shouldReturnConvertDtoToEntity : Test passed");
    }

    @Test
    void shouldReturnConvertDtoToEntityNull() {
        LOGGER.info("Start method: shouldReturnConvertDtoToEntityNull");
        assertNull(medicalRecordConvertorDTO.convertDtoToEntity((MedicalRecordDTO) null));
        LOGGER.info("End method: shouldReturnConvertDtoToEntityNull : Test passed");
    }

    @Test
    void shouldReturnConvertEntityToDtoList() {
        LOGGER.info("Start method: shouldReturnConvertEntityToDtoList");
        List<MedicalRecord> medicalRecords = List.of(medicalRecord1, medicalRecord2);

        List<MedicalRecordDTO> medicalRecordDTOS = medicalRecordConvertorDTO.convertEntityToDto(medicalRecords);

        assertNotNull(medicalRecordDTOS);
        assertEquals(2, medicalRecordDTOS.size());
        assertEquals(medicalRecord1.getFirstName(), medicalRecordDTOS.get(0).getFirstName());
        assertEquals(medicalRecord2.getFirstName(), medicalRecordDTOS.get(1).getFirstName());
        LOGGER.info("End method: shouldReturnConvertEntityToDtoList : Test passed");
    }

    @Test
    void shouldReturnConvertEntityToDtoListIsEmpty() {
        LOGGER.info("Start method: shouldReturnConvertEntityToDtoListIsEmpty");
        assertTrue(medicalRecordConvertorDTO.convertEntityToDto(List.of()).isEmpty());
        LOGGER.info("End method: shouldReturnConvertEntityToDtoListIsEmpty : Test passed");
    }

    @Test
    void shouldReturnConvertDtoToEntityList() {
        LOGGER.info("Start method: shouldReturnConvertDtoToEntityList");
        List<MedicalRecordDTO> medicalRecordDTOS = List.of(medicalRecordDTO1, medicalRecordDTO2);
        List<MedicalRecord> medicalRecords = medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTOS);

        assertNotNull(medicalRecords);
        assertEquals(2, medicalRecords.size());
        assertEquals(medicalRecordDTO1.getFirstName(), medicalRecords.get(0).getFirstName());
        assertEquals(medicalRecordDTO2.getFirstName(), medicalRecords.get(1).getFirstName());
        LOGGER.info("End method: shouldReturnConvertDtoToEntityList : Test passed");
    }

    @Test
    void shouldReturnConvertDtoToEntityListIsEmpty() {
        LOGGER.info("Start method: shouldReturnConvertDtoToEntityListIsEmpty");
        assertTrue(medicalRecordConvertorDTO.convertDtoToEntity(List.of()).isEmpty());
        LOGGER.info("End method: shouldReturnConvertDtoToEntityListIsEmpty : Test passed");
    }
}
