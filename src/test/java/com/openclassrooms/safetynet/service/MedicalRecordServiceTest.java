package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        medicalRecord1 = new MedicalRecord("John", "Doe", "01/01/1990",
                List.of("aznol:350mg","hydrapermazol:100mg"), List.of("nillacilan"));
        medicalRecord2 = new MedicalRecord("Jane", "Doe", "01/01/2000",
                List.of("aznol:350mg","hydrapermazol:100mg"), List.of("nillacilan"));

        medicalRecordDTO1 = new MedicalRecordDTO("John", "Doe", "01/01/1990",
                List.of("aznol:350mg","hydrapermazol:100mg"), List.of("nillacilan"));
        medicalRecordDTO2 = new MedicalRecordDTO("Jane", "Doe", "01/01/2000",
                List.of("aznol:350mg","hydrapermazol:100mg"), List.of("nillacilan"));
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

    @Test
    void testSaveAll_Success () {

        List<MedicalRecordDTO> medicalRecordDTOList = List.of(medicalRecordDTO1, medicalRecordDTO2);
        List<MedicalRecord> medicalRecordEntities = List.of(medicalRecord1, medicalRecord2);

        when(medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTOList)).thenReturn(medicalRecordEntities);
        when(medicalRecordRepository.saveAll(medicalRecordEntities)).thenReturn(medicalRecordEntities);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecordEntities)).thenReturn(medicalRecordDTOList);

        List<MedicalRecordDTO> result = medicalRecordService.saveAll(medicalRecordDTOList);

        assertNotNull(medicalRecordDTOList);
        assert(result.size() == 2);
        assert(result.get(0).getFirstName().equals("John"));
        assert(result.get(1).getFirstName().equals("Jane"));

    }

    @Test
    void testSaveAll_NullOrEmptyList() {
        // Test pour une liste nulle
        ResponseStatusException exception1 = assertThrows(ResponseStatusException.class, () -> {
            medicalRecordService.saveAll(null);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception1.getStatusCode());
        assertTrue(Objects.requireNonNull(exception1.getReason()).contains("No medical records were provided for saving."));
        // Test pour une liste vide
        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class, () -> {
            medicalRecordService.saveAll(List.of());
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception2.getStatusCode());
        assertTrue(Objects.requireNonNull(exception1.getReason()).contains("No medical records were provided for saving."));    }
/*
    @Test
    void testSaveAll_ExceptionThrownByRepository() {
        // Arrange
        List<MedicalRecordDTO> medicalRecordDTOList = List.of(medicalRecordDTO1, medicalRecordDTO2);
        List<MedicalRecord> medicalRecordEntities = List.of(medicalRecord1, medicalRecord2);

        when(medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTOList)).thenReturn(medicalRecordEntities);
        when(medicalRecordRepository.saveAll(medicalRecordEntities)).thenThrow(new RuntimeException("Something went wrong"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> medicalRecordService.saveAll(medicalRecordDTOList));

        // Verify correct exception details
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(Objects.requireNonNull(exception.getReason())
                .contains("An unexpected error occurred while saving medical records"));

        verify(medicalRecordConvertorDTO, times(1)).convertDtoToEntity(medicalRecordDTOList);
        verify(medicalRecordRepository, times(1)).saveAll(medicalRecordEntities);
    }
 */

    @Test
    void tesSave_Success() {
        MedicalRecordDTO medicalRecordDTO = medicalRecordDTO1;
        MedicalRecord medicalRecordEntities = medicalRecord1;

        when(medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTO)).thenReturn(medicalRecordEntities);
        when(medicalRecordRepository.save(medicalRecordEntities)).thenReturn(medicalRecordEntities);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecordEntities)).thenReturn(medicalRecordDTO);

        MedicalRecordDTO result = medicalRecordService.save(medicalRecordDTO);

        assertNotNull(result);
        assert(result.getFirstName().equals("John"));
        assert(result.getLastName().equals("Doe"));

        verify(medicalRecordConvertorDTO, times(1)).convertDtoToEntity(medicalRecordDTO);
        verify(medicalRecordRepository, times(1)).save(medicalRecordEntities);
        verify(medicalRecordConvertorDTO, times(1)).convertEntityToDto(medicalRecordEntities);
    }

    @Test
    void testUpdate_Success() {
        MedicalRecordDTO medicalRecordDTO = medicalRecordDTO1;
        MedicalRecord medicalRecordEntities = medicalRecord1;

        when(medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTO)).thenReturn(medicalRecordEntities);
        when(medicalRecordRepository.update(medicalRecordEntities)).thenReturn(Optional.of(medicalRecordEntities));
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecordEntities)).thenReturn(medicalRecordDTO);

        Optional<MedicalRecordDTO> result = medicalRecordService.update(medicalRecordDTO);

        assertNotNull(result);
        assert(result.isPresent());
        assert(result.get().getFirstName().equals("John"));
        assert(result.get().getLastName().equals("Doe"));

        verify(medicalRecordConvertorDTO, times(1)).convertDtoToEntity(medicalRecordDTO);
        verify(medicalRecordRepository, times(1)).update(medicalRecordEntities);
        verify(medicalRecordConvertorDTO, times(1)).convertEntityToDto(medicalRecordEntities);
    }

    @Test
    void testDelete_Success() {
        // set up
        when(medicalRecordRepository.deleteByFullName(medicalRecord1.getFirstName(),medicalRecord1.getLastName())).thenReturn(true);

        Boolean result = medicalRecordService.deleteByFullName(medicalRecord1.getFirstName(),medicalRecord1.getLastName());

        assertNotNull(result);
        assertTrue(result);

        verify(medicalRecordRepository,times(1)).deleteByFullName(medicalRecord1.getFirstName(),medicalRecord1.getLastName());
        verifyNoInteractions(medicalRecordConvertorDTO);
    }

}
