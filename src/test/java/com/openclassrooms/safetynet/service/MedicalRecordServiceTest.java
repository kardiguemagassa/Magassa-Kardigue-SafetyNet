package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.exception.MedicalRecordNotFoundException;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static com.openclassrooms.safetynet.constant.MedicalRecordConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordServiceTest.class);

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
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
    void shouldReturnGetMedicalRecords() {

        LOGGER.info("Start method: shouldReturnGetMedicalRecords");

        // Arrange
        when(medicalRecordRepository.getMedicalRecords()).thenReturn(List.of(medicalRecord1, medicalRecord2));
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord2)).thenReturn(medicalRecordDTO2);

        List<MedicalRecordDTO> medicalRecordDTOList = medicalRecordService.getMedicalRecords();

        // Assert
        assertNotNull(medicalRecordDTOList);

        assert(medicalRecordDTOList.size() == 2);
        assert(medicalRecordDTOList.get(0).getFirstName().equals(medicalRecordDTO1.getFirstName()));
        assert(medicalRecordDTOList.get(1).getFirstName().equals(medicalRecordDTO2.getFirstName()));


        verify(medicalRecordRepository,times(1)).getMedicalRecords();
        verify(medicalRecordConvertorDTO,times(1)).convertEntityToDto(medicalRecord1);
        verify(medicalRecordConvertorDTO,times(1)).convertEntityToDto(medicalRecord2);

        LOGGER.info("End method : shouldReturnGetMedicalRecords completed successfully");
    }

    @Test
    void shouldReturnGetMedicalRecordsNotFoundException () {
        LOGGER.info("Start method: shouldReturnGetMedicalRecords_NotFound");
        // Arrange
        when(medicalRecordRepository.getMedicalRecords()).thenReturn(null);

        // Act & Assert
        MedicalRecordNotFoundException exception = assertThrows(MedicalRecordNotFoundException.class, () -> medicalRecordService.getMedicalRecords());
        assertEquals("No medical records found in the repository.", exception.getMessage());

        verify(medicalRecordRepository, times(1)).getMedicalRecords();
        verifyNoInteractions(medicalRecordConvertorDTO);

        LOGGER.info("End method shouldReturnGetMedicalRecordsNotFoundException completed successfully");
    }

    @Test
    void shouldReturnSave() {

        LOGGER.info("Start method: shouldReturnSave");

        MedicalRecordDTO medicalRecordDTO = medicalRecordDTO1;
        MedicalRecord medicalRecordEntities = medicalRecord1;

        when(medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTO)).thenReturn(medicalRecordEntities);
        when(medicalRecordRepository.save(medicalRecordEntities)).thenReturn(medicalRecordEntities);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecordEntities)).thenReturn(medicalRecordDTO);

        MedicalRecordDTO result = medicalRecordService.save(medicalRecordDTO);

        assertNotNull(result);
        assert(result.getFirstName().equals(medicalRecordDTO1.getFirstName()));
        assert(result.getLastName().equals(medicalRecordDTO1.getLastName()));

        LOGGER.info("Verifying method ");
        verify(medicalRecordConvertorDTO, times(1)).convertDtoToEntity(medicalRecordDTO);
        verify(medicalRecordRepository, times(1)).save(medicalRecordEntities);
        verify(medicalRecordConvertorDTO, times(1)).convertEntityToDto(medicalRecordEntities);

        LOGGER.info("End method : shouldReturnSave completed successfully");
    }

    @Test
    void shouldReturnSaveException() {

        LOGGER.info("Start method: shouldReturnSaveException");

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.save(null));

        assertEquals(MEDICAL_RECORD_ERROR, exception1.getMessage());

        MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO();
        MedicalRecord medicalRecordEntity = new MedicalRecord();

        when(medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTO)).thenReturn(medicalRecordEntity);
        when(medicalRecordRepository.save(medicalRecordEntity))
                .thenThrow(new MedicalRecordNotFoundException(MEDICAL_RECORD_NOT_FOUND));


        RuntimeException exception2 = assertThrows(RuntimeException.class, () -> medicalRecordService.save(medicalRecordDTO));
        assertTrue(exception2.getMessage().contains(MEDICAL_RECORD_NOT_FOUND));
        LOGGER.info("Exception handling test passed");

        verify(medicalRecordConvertorDTO, times(1)).convertDtoToEntity(medicalRecordDTO);
        verify(medicalRecordRepository, times(1)).save(medicalRecordEntity);

        LOGGER.info("End method : shouldReturnSaveException completed successfully");
    }

    @Test
    void shouldReturnUpdate() {

        LOGGER.info("Start method: shouldReturnUpdate");

        // Arrange
        MedicalRecordDTO medicalRecordDTO = medicalRecordDTO1;
        MedicalRecord medicalRecordEntities = medicalRecord1;

        when(medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTO)).thenReturn(medicalRecordEntities);
        when(medicalRecordRepository.update(medicalRecordEntities)).thenReturn(Optional.of(medicalRecordEntities));
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecordEntities)).thenReturn(medicalRecordDTO);

        // Act
        Optional<MedicalRecordDTO> result = medicalRecordService.update(medicalRecordDTO);

        // Assert
        assertNotNull(result);
        assert(result.isPresent());
        assert(result.get().getFirstName().equals(medicalRecordDTO1.getFirstName()));
        assert(result.get().getLastName().equals(medicalRecordDTO1.getLastName()));


        verify(medicalRecordConvertorDTO, times(1)).convertDtoToEntity(medicalRecordDTO);
        verify(medicalRecordRepository, times(1)).update(medicalRecordEntities);
        verify(medicalRecordConvertorDTO, times(1)).convertEntityToDto(medicalRecordEntities);

        LOGGER.info("End method : shouldReturnUpdate completed successfully");
    }

    @Test
    void shouldReturnUpdateException() {

        LOGGER.info("Start method: shouldReturnUpdateException");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.update(null));
        assertEquals(MEDICAL_RECORD_ERROR_UPDATING, exception.getMessage());

        LOGGER.info("End method : shouldReturnUpdateException completed successfully");
    }

    @Test
    void shouldReturnDeleteByFullName() {

        LOGGER.info("Starting test: shouldReturnDeleteByFullName");

        // set up
        when(medicalRecordRepository.deleteByFullName(medicalRecord1.getFirstName(),medicalRecord1.getLastName())).thenReturn(true);

        LOGGER.info("Deleting medical record for {} {}", medicalRecord1.getFirstName(), medicalRecord1.getLastName());
        Boolean result = medicalRecordService.deleteByFullName(medicalRecord1.getFirstName(),medicalRecord1.getLastName());

        assertNotNull(result);
        assertTrue(result);

        LOGGER.info("Verifying ");
        verify(medicalRecordRepository,times(1)).deleteByFullName(medicalRecord1.getFirstName(),medicalRecord1.getLastName());
        verifyNoInteractions(medicalRecordConvertorDTO);

        LOGGER.info("End method shouldReturnDeleteByFullName completed successfully");
    }

    @Test
    void shouldReturnDeleteByFullNameException() {

        LOGGER.info("Start method: shouldReturnDeleteByFullNameException");

        // Given
        when(medicalRecordRepository.deleteByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName())).thenReturn(false);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.deleteByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName()));

        assertEquals(MEDICAL_RECORD_NOT_FOUND, exception.getMessage());
        LOGGER.info("End method : shouldReturnDeleteByFullNameException completed successfully");
    }



    @Test
    void shouldReturnDeleteByFullNameNull() {

        LOGGER.info("Start method: shouldReturnDeleteByFullNameNull");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                medicalRecordService.deleteByFullName(null, null));

        assertEquals(MEDICAL_RECORD_ERROR_DELETING, exception.getMessage());
        LOGGER.info("End method : shouldReturnDeleteByFullNameNull completed successfully");

    }
}
