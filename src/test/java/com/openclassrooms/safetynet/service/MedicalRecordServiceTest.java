package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordServiceTest.class);

    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private MedicalRecordConvertorDTO medicalRecordConvertorDTO;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private MedicalRecord medicalRecord1;
    private MedicalRecord medicalRecord2;
    private MedicalRecordDTO medicalRecordDTO1;
    private MedicalRecordDTO medicalRecordDTO2;

    @BeforeEach
    void setUp() {
        medicalRecord1 = new MedicalRecord("John", "Doe", "01/01/1990", null, null);
        medicalRecord2 = new MedicalRecord("Jane", "Doe", "01/01/2000", null, null);

        medicalRecordDTO1 = new MedicalRecordDTO("John", "Doe", "01/01/1990", null, null);
        medicalRecordDTO2 = new MedicalRecordDTO("Jane", "Doe", "01/01/2000", null, null);
    }

    @Test
    void testGetMedicalRecords_Success () {
        // Arrange
        when(medicalRecordRepository.getMedicalRecords()).thenReturn(List.of(medicalRecord1, medicalRecord2));
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord2)).thenReturn(medicalRecordDTO2);

        // Act
        List<MedicalRecordDTO> medicalRecordDTOList = medicalRecordService.getMedicalRecords();

        // Assert
        assertNotNull(medicalRecordDTOList);

        assert(medicalRecordDTOList.size() == 2);
        //assertEquals(2, medicalRecordDTOList.size());
        assert(medicalRecordDTOList.get(0).getFirstName().equals("John"));
        assert(medicalRecordDTOList.get(1).getFirstName().equals("Jane"));


        verify(medicalRecordRepository,times(1)).getMedicalRecords();
        verify(medicalRecordConvertorDTO,times(1)).convertEntityToDto(medicalRecord1);
        verify(medicalRecordConvertorDTO,times(1)).convertEntityToDto(medicalRecord2);
    }

    @Test
    void testGetMedicalRecords_NotFound () {
        // Arrange
        when(medicalRecordRepository.getMedicalRecords()).thenReturn(null);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> medicalRecordService.getMedicalRecords());
        assertEquals("404 NOT_FOUND \"No medical records found.\"", exception.getMessage());

        verify(medicalRecordRepository, times(1)).getMedicalRecords();
        verifyNoInteractions(medicalRecordConvertorDTO);
    }
}
