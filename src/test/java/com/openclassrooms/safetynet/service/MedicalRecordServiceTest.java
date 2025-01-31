package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.exception.medicalRecord.MedicalRecordNotFoundException;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static com.openclassrooms.safetynet.constant.service.MedicalRecordImplConstant.MEDICAL_RECORD_ERROR;
import static com.openclassrooms.safetynet.constant.service.MedicalRecordImplConstant.MEDICAL_RECORD_NOT_FOUND;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
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

        LOGGER.info("Starting test: shouldReturnGetMedicalRecords");

        // Arrange
        when(medicalRecordRepository.getMedicalRecords()).thenReturn(List.of(medicalRecord1, medicalRecord2));
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord2)).thenReturn(medicalRecordDTO2);

        // Act
        LOGGER.info("Fetching medical records");
        List<MedicalRecordDTO> medicalRecordDTOList = medicalRecordService.getMedicalRecords();

        // Assert
        assertNotNull(medicalRecordDTOList);

        assert(medicalRecordDTOList.size() == 2);
        //assertEquals(2, medicalRecordDTOList.size());
        assert(medicalRecordDTOList.get(0).getFirstName().equals(medicalRecordDTO1.getFirstName()));
        assert(medicalRecordDTOList.get(1).getFirstName().equals(medicalRecordDTO2.getFirstName()));

        LOGGER.info("Verifying method interactions");
        verify(medicalRecordRepository,times(1)).getMedicalRecords();
        verify(medicalRecordConvertorDTO,times(1)).convertEntityToDto(medicalRecord1);
        verify(medicalRecordConvertorDTO,times(1)).convertEntityToDto(medicalRecord2);

        LOGGER.info("Test shouldReturnGetMedicalRecords completed successfully");
    }

    @Test
    void shouldReturnGetMedicalRecords_NotFound () {
        // Arrange
        when(medicalRecordRepository.getMedicalRecords()).thenReturn(null);

        // Act & Assert
        MedicalRecordNotFoundException exception = assertThrows(MedicalRecordNotFoundException.class, () -> medicalRecordService.getMedicalRecords());
        assertEquals("No medical records found in the repository.", exception.getMessage());

        verify(medicalRecordRepository, times(1)).getMedicalRecords();
        verifyNoInteractions(medicalRecordConvertorDTO);

        LOGGER.info("Test shouldReturnGetMedicalRecords_NotFound completed successfully");
    }

    @Test
    void shouldReturnSave() {

        LOGGER.info("Starting test: shouldReturnSave");

        MedicalRecordDTO medicalRecordDTO = medicalRecordDTO1;
        MedicalRecord medicalRecordEntities = medicalRecord1;

        when(medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTO)).thenReturn(medicalRecordEntities);
        when(medicalRecordRepository.save(medicalRecordEntities)).thenReturn(medicalRecordEntities);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecordEntities)).thenReturn(medicalRecordDTO);

        LOGGER.info("Saving a new medical record");
        MedicalRecordDTO result = medicalRecordService.save(medicalRecordDTO);

        assertNotNull(result);
        assert(result.getFirstName().equals(medicalRecordDTO1.getFirstName()));
        assert(result.getLastName().equals(medicalRecordDTO1.getLastName()));

        LOGGER.info("Verifying method ");
        verify(medicalRecordConvertorDTO, times(1)).convertDtoToEntity(medicalRecordDTO);
        verify(medicalRecordRepository, times(1)).save(medicalRecordEntities);
        verify(medicalRecordConvertorDTO, times(1)).convertEntityToDto(medicalRecordEntities);

        LOGGER.info("Test shouldReturnSave completed successfully");
    }

    @Test
    void shouldReturnSave_ExceptionThrownByRepository() {

        LOGGER.info("Starting test: shouldReturnSave_ExceptionThrownByRepository");

        // Test with null medicalRecordDTO
        LOGGER.debug("Testing with a null medicalRecordDTO");
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            medicalRecordService.save(null);
        });

        assertEquals(MEDICAL_RECORD_ERROR, exception1.getMessage());
        LOGGER.info("Null medicalRecordDTO test passed");

        MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO();
        MedicalRecord medicalRecordEntity = new MedicalRecord();

        //convert repository
        LOGGER.debug("Mocking DTO to Entity conversion");
        when(medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTO)).thenReturn(medicalRecordEntity);
        LOGGER.debug("Mocking repository to throw an exception when saving");
        when(medicalRecordRepository.save(medicalRecordEntity))
                .thenThrow(new MedicalRecordNotFoundException(MEDICAL_RECORD_NOT_FOUND));

        // Test RuntimeException
        LOGGER.info("Testing repository exception handling");
        RuntimeException exception2 = assertThrows(RuntimeException.class, () -> {medicalRecordService.save(medicalRecordDTO);});
        assertTrue(exception2.getMessage().contains(MEDICAL_RECORD_NOT_FOUND));
        LOGGER.info("Exception handling test passed");

        // Verify interactions
        LOGGER.debug("Verifying method interactions");
        verify(medicalRecordConvertorDTO, times(1)).convertDtoToEntity(medicalRecordDTO);
        verify(medicalRecordRepository, times(1)).save(medicalRecordEntity);

        LOGGER.info("Test shouldReturnSave_ExceptionThrownByRepository completed successfully");
    }

    @Test
    void shouldReturnUpdate() {

        LOGGER.info("Starting test: shouldReturnUpdate");

        // Arrange
        MedicalRecordDTO medicalRecordDTO = medicalRecordDTO1;
        MedicalRecord medicalRecordEntities = medicalRecord1;

        LOGGER.debug("Mocking conversion from DTO to Entity");
        when(medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTO)).thenReturn(medicalRecordEntities);
        when(medicalRecordRepository.update(medicalRecordEntities)).thenReturn(Optional.of(medicalRecordEntities));
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecordEntities)).thenReturn(medicalRecordDTO);

        // Act
        LOGGER.info("Calling medicalRecordService.update()");
        Optional<MedicalRecordDTO> result = medicalRecordService.update(medicalRecordDTO);

        // Assert
        assertNotNull(result);
        assert(result.isPresent());
        assert(result.get().getFirstName().equals(medicalRecordDTO1.getFirstName()));
        assert(result.get().getLastName().equals(medicalRecordDTO1.getLastName()));

        LOGGER.info("Assertions passed, verifying method interactions");
        verify(medicalRecordConvertorDTO, times(1)).convertDtoToEntity(medicalRecordDTO);
        verify(medicalRecordRepository, times(1)).update(medicalRecordEntities);
        verify(medicalRecordConvertorDTO, times(1)).convertEntityToDto(medicalRecordEntities);

        LOGGER.info("Test shouldReturnUpdate completed successfully");
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

        LOGGER.info("Test shouldReturnDeleteByFullName completed successfully");
    }
}
