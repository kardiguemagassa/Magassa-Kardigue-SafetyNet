package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.openclassrooms.safetynet.constant.repository.MedicalRecordRepositoryConstant.MEDICAL_RECORD_ERROR_LOADING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordRepositoryTest.class);

    @Mock
    private DataBaseInMemoryWrapper dataBaseInMemoryWrapper;

    @InjectMocks
    private MedicalRecordRepository medicalRecordRepository;

    private MedicalRecord medicalRecord1;
    private MedicalRecord medicalRecord2;

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
    }

    @Test
    void shouldReturnGetMedicalRecordsLoadedSuccessfully() {

        LOGGER.info("Start method : shouldReturnGetMedicalRecordsLoadedSuccessfully");
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenReturn(List.of(medicalRecord1, medicalRecord2));

        // Act
        List<MedicalRecord> medicalRecordDTOList = medicalRecordRepository.getMedicalRecords();

        // Assert
        assertNotNull(medicalRecordDTOList);
        assert(medicalRecordDTOList.size() == 2);

        verify(dataBaseInMemoryWrapper,times(1)).getMedicalRecords();
        LOGGER.info("End method : shouldReturnGetMedicalRecordsLoadedSuccessfully : Test passed");

    }

    @Test
    void shouldReturnGetMedicalRecordsDataBaseException() {
        LOGGER.info("Start method : shouldReturnGetMedicalRecordsDataBaseExceptionThrown");
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenThrow(new RuntimeException(MEDICAL_RECORD_ERROR_LOADING));

        List<MedicalRecord> medicalRecordDTOList = medicalRecordRepository.getMedicalRecords();
        assertNotNull(medicalRecordDTOList);
        assert(medicalRecordDTOList.isEmpty());
        verify(dataBaseInMemoryWrapper,times(1)).getMedicalRecords();
        LOGGER.info("End method : shouldReturnGetMedicalRecordsDataBaseExceptionThrown : Test passed");
    }

    @Test
    void shouldReturnSaveMedicalRecordsSuccessfully() {
        LOGGER.info("Start method : shouldReturnSaveMedicalRecordsSuccessfully");
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenReturn(new ArrayList<>());

        MedicalRecord saveMedicalRecord = medicalRecordRepository.save(medicalRecord1);
        assertNotNull(saveMedicalRecord);
        assertEquals(medicalRecord1, saveMedicalRecord);
        verify(dataBaseInMemoryWrapper,times(1)).getMedicalRecords();
        LOGGER.info("End method : shouldReturnSaveMedicalRecordsSuccessfully : Test passed");
    }

    @Test
    void shouldReturnSavingNullMedicalRecords() {
        LOGGER.info("Start method : shouldReturnSavingNullMedicalRecords");
        MedicalRecord saveMedicalRecord = medicalRecordRepository.save(null);
        assertNull(saveMedicalRecord);
        LOGGER.info("End method : shouldReturnSavingNullMedicalRecords : Test passed");
    }

    @Test
    void shouldReturnSaveDataBaseException() {
        LOGGER.info("Start method : shouldReturnSaveDataBaseExceptionThrown");
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenThrow(new RuntimeException(MEDICAL_RECORD_ERROR_LOADING));
        MedicalRecord saveMedicalRecord = medicalRecordRepository.save(medicalRecord1);
        assertNotNull(saveMedicalRecord);
        assertEquals(medicalRecord1, saveMedicalRecord);
        verify(dataBaseInMemoryWrapper,times(1)).getMedicalRecords();
        LOGGER.info("End method : shouldReturnSaveDataBaseExceptionThrown : Test passed");
    }

    @Test
    void shouldReturnFindByMedicationsAndAllergiesSuccessfully() {
        LOGGER.info("Start method : shouldReturnFindByMedicationsAndAllergiesSuccessfully");
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenReturn(List.of(medicalRecord1, medicalRecord2));

        List<String> medications = medicalRecord1.getMedications();
        List<String> allergies = medicalRecord1.getAllergies();

        Optional<MedicalRecord> medicalRecord = medicalRecordRepository.findByMedicationsAndAllergies(medications, allergies);

        assertTrue(medicalRecord.isPresent());
        assertEquals(medicalRecord1, medicalRecord.get());
        verify(dataBaseInMemoryWrapper,times(1)).getMedicalRecords();
        LOGGER.info("End method : shouldReturnFindByMedicationsAndAllergiesSuccessfully : Test passed");
    }

    @Test
    void shouldReturnEmptyWhenMedicalRecordsDataBaseIsNull() {
        LOGGER.info("Start method : shouldReturnEmptyWhenMedicalRecordsDataBaseIsNull");

        // Arrange
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenReturn(null);

        List<String> medications = medicalRecord1.getMedications();
        List<String> allergies = medicalRecord1.getAllergies();

        // Act
        Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findByMedicationsAndAllergies(medications, allergies);

        // Assert
        assertTrue(medicalRecordOptional.isEmpty());
        verify(dataBaseInMemoryWrapper, times(1)).getMedicalRecords();
        LOGGER.info("End method : shouldReturnEmptyWhenMedicalRecordsDataBaseIsNull : Test passed");
    }


    @Test
    void shouldReturnFindByMedicationsAndAllergiesException() {
        LOGGER.info("Start method : shouldReturnFindByMedicationsAndAllergiesExceptionThrown");

        // Arrange
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenThrow(new RuntimeException(MEDICAL_RECORD_ERROR_LOADING));

        List<String> medications = medicalRecord1.getMedications();
        List<String> allergies = medicalRecord1.getAllergies();

        Optional<MedicalRecord> medicalRecordOptional = medicalRecordRepository.findByMedicationsAndAllergies(medications, allergies);

        assertTrue(medicalRecordOptional.isEmpty());
        verify(dataBaseInMemoryWrapper,times(1)).getMedicalRecords();

        LOGGER.info("End method : shouldReturnFindByMedicationsAndAllergiesExceptionThrown : Test passed");
    }

    @Test
    void shouldReturnUpdateSuccessfully() {
        LOGGER.info("Start method : shouldReturnUpdateSuccessfully");
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenReturn(List.of(medicalRecord1));

        MedicalRecord saveMedicalRecord = new MedicalRecord(medicalRecord1.getFirstName(), medicalRecord1.getLastName(),
                medicalRecord1.getBirthdate(), medicalRecord1.getMedications(), medicalRecord1.getAllergies());

        Optional<MedicalRecord> optionalMedicalRecord = medicalRecordRepository.update(saveMedicalRecord);
        assertTrue(optionalMedicalRecord.isPresent());
        assertEquals(saveMedicalRecord.getBirthdate(), optionalMedicalRecord.get().getBirthdate());
        verify(dataBaseInMemoryWrapper,times(1)).getMedicalRecords();
        LOGGER.info("End method : shouldReturnUpdateSuccessfully : Test passed");
    }

    @Test
    void shouldReturnUpdateNull() {
        LOGGER.info("Start method : shouldReturnUpdateNull");
        Optional<MedicalRecord> optionalMedicalRecord = medicalRecordRepository.update(null);
        assertTrue(optionalMedicalRecord.isEmpty());
        LOGGER.info("End method : shouldReturnUpdateNull : Test passed");
    }

    @Test
    void shouldReturnUpdateDataBaseNull() {
        LOGGER.info("Start method : shouldReturnUpdateDataBaseNull");
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenReturn(null);
        Optional<MedicalRecord> optionalMedicalRecord = medicalRecordRepository.update(medicalRecord1);
        assertTrue(optionalMedicalRecord.isPresent());
        LOGGER.info("End method : shouldReturnUpdateDataBaseNull : Test passed");
    }

    @Test
    void shouldReturnUpdateException() {
        LOGGER.info("Start method : shouldReturnUpdateException");
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenThrow(new RuntimeException(MEDICAL_RECORD_ERROR_LOADING));
        Optional<MedicalRecord> optionalMedicalRecord = medicalRecordRepository.update(medicalRecord1);
        assertTrue(optionalMedicalRecord.isEmpty());
        LOGGER.info("End method : shouldReturnUpdateException : Test passed");
    }

    @Test
    void shouldReturnDeleteSuccessfully() {
        LOGGER.info("Start method : shouldReturnDeleteSuccessfully");

        List<MedicalRecord> medicalRecordsList = new ArrayList<>();
        medicalRecordsList.add(medicalRecord1);
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenReturn(medicalRecordsList);

        boolean isDeleted = medicalRecordRepository.deleteByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName());


        assertTrue(isDeleted, "The deletion must be successful");

        // Verify interactions
        verify(dataBaseInMemoryWrapper, times(1)).getMedicalRecords();
        assertTrue(medicalRecordsList.isEmpty(), "The deleted person should no longer be in the list");

        LOGGER.info("End method: shouldDeletePersonSuccessfully : Test passed");
    }

    @Test
    void shouldReturnDeleteException() {
        LOGGER.info("Start method : shouldReturnDeleteException");
        AtomicReference<List<MedicalRecord>> medicalRecordsList = new AtomicReference<>(new ArrayList<>());
        medicalRecordsList.get().add(medicalRecord1);
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenThrow(new RuntimeException(MEDICAL_RECORD_ERROR_LOADING));
        boolean isDeleted = medicalRecordRepository.deleteByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName());
        assertFalse(isDeleted);
        verify(dataBaseInMemoryWrapper, times(1)).getMedicalRecords();
        LOGGER.info("End method : shouldReturnDeleteException : Test passed");

    }

    @Test
    void shouldReturnDeleteDataNull() {
        LOGGER.info("Start method : shouldReturnDeleteDataNull");
        boolean isDeleted = medicalRecordRepository.deleteByFullName(medicalRecord1.getFirstName(), null);
        assertFalse(isDeleted);
        LOGGER.info("End method : shouldReturnDeleteDataNull : Test passed");
    }

    @Test
    void shouldReturnDeleteDataBaseNull() {
        LOGGER.info("Start method : shouldReturnDeleteDataBaseNull");
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenReturn(null);
        boolean isDeleted = medicalRecordRepository.deleteByFullName(medicalRecord1.getFirstName(), null);
        assertFalse(isDeleted);
        LOGGER.info("End method : shouldReturnDeleteDataBaseNull : Test passed");
    }

    @Test
    void shouldReturnFindByFullNameSuccessfully () {
        LOGGER.info("Start method : shouldReturnFindByFullNameSuccessfully");
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenReturn(List.of(medicalRecord1));

        MedicalRecord medicalRecord = medicalRecordRepository.findByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName());

        assertNotNull(medicalRecord);
        assertEquals(medicalRecord1.getFirstName(), medicalRecord.getFirstName());
        assertEquals(medicalRecord1.getLastName(), medicalRecord.getLastName());
        verify(dataBaseInMemoryWrapper, times(1)).getMedicalRecords();
        LOGGER.info("End method : shouldReturnFindByFullNameSuccessfully : Test passed");
    }

    @Test
    void shouldReturnFindByFullNameException(){
        LOGGER.info("Start method : shouldReturnFindByFullNameException");
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenThrow(new RuntimeException(MEDICAL_RECORD_ERROR_LOADING));

        MedicalRecord medicalRecord = medicalRecordRepository.findByFullName("John","Smith");

        assertNull(medicalRecord);
        verify(dataBaseInMemoryWrapper, times(1)).getMedicalRecords();

        LOGGER.info("End method : shouldReturnFindByFullNameException : Test passed");
    }

    @Test
    void shouldReturnFindByFullNameDataNull(){
        LOGGER.info("Start method : shouldReturnFindByFullNameDataNull");
        when(dataBaseInMemoryWrapper.getMedicalRecords()).thenReturn(null);

        MedicalRecord medicalRecord = medicalRecordRepository.findByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName());
        assertNull(medicalRecord);
        verify(dataBaseInMemoryWrapper, times(1)).getMedicalRecords();
        LOGGER.info("End method : shouldReturnFindByFullNameDataNull : Test passed");
    }

}
