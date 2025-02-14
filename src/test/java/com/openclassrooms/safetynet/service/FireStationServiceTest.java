package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.constant.service.FireStationImplConstant;
import com.openclassrooms.safetynet.convertorDTO.FireStationConvertorDTO;
import com.openclassrooms.safetynet.dto.FireStationDTO;
import com.openclassrooms.safetynet.exception.fireStation.FireStationNotFoundException;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.repository.FireStationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.openclassrooms.safetynet.constant.repository.FireStationRepositoryConstant.FIRE_STATION_NOT_FOUND;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FireStationServiceTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @InjectMocks
    private FireStationService fireStationService;

    @Mock
    private FireStationRepository fireStationRepository;
    @Mock
    private FireStationConvertorDTO fireStationConvertorDTO;

    private FireStation fireStation1;
    private FireStation fireStation2;
    private FireStationDTO fireStationDTO1;
    private FireStationDTO fireStationDTO2;

    @BeforeEach
    void setUp() {
        fireStation1 = new FireStation("149 Bd Pei ere 75007 Paris", "1");
        fireStation2 = new FireStation("150 Bd Pei ere 75007 Paris", "2");


        fireStationDTO1 = new FireStationDTO("149 Bd Pei ere 75007 Paris", "1");
        fireStationDTO2 = new FireStationDTO("150 Bd Pei ere 75007 Paris", "2");
    }

    @Test
    void shouldReturnGetFireStations() {

        // Arrange
        LOGGER.info("Preparing mock data for getFireStations test.");


        when(fireStationRepository.getFireStations()).thenReturn(List.of(fireStation1, fireStation2));
        when(fireStationConvertorDTO.convertEntityToDto(fireStation1)).thenReturn(fireStationDTO1);
        when(fireStationConvertorDTO.convertEntityToDto(fireStation2)).thenReturn(fireStationDTO2);

        // Act
        LOGGER.info("Calling fireStationService.getFireStations() to retrieve fire station list.");
        List<FireStationDTO> fireStationDTOList = fireStationService.getFireStations();

        // Assert
        LOGGER.info("Asserting that the retrieved list is not null.");
        assertNotNull(fireStationDTOList);
        assertEquals(2, fireStationDTOList.size());
        LOGGER.info("Asserting that the first fire station has the expected address: {}", fireStationDTO1.getAddress());
        assertEquals(fireStationDTO1.getAddress(), fireStationDTOList.get(0).getAddress());
        assertEquals(fireStationDTO2.getAddress(), fireStationDTOList.get(1).getAddress());

        // Verify interactions
        LOGGER.info("Verifying that getFireStations() was called once on fireStationRepository.");
        verify(fireStationRepository, times(1)).getFireStations();
        LOGGER.info("Verifying that convertEntityToDto() was called once for each fire station.");
        verify(fireStationConvertorDTO, times(1)).convertEntityToDto(fireStation1);
        verify(fireStationConvertorDTO, times(1)).convertEntityToDto(fireStation2);

        LOGGER.info("Test shouldReturnGetFireStations completed successfully");
    }

    @Test void shouldReturnGetFireStations_NotFound() {

        // Arrange
        LOGGER.info("Preparing test for getFireStations when no fire stations are found.");
        when(fireStationRepository.getFireStations()).thenReturn(null);

        LOGGER.info("Calling fireStationService.getFireStations() expecting FireStationNotFoundException.");
        FireStationNotFoundException exception = assertThrows(FireStationNotFoundException.class, () -> fireStationService.getFireStations());
        assertEquals("No fireStation found in the repository.", exception.getMessage());

        LOGGER.info("Verifying that the thrown exception contains the expected message.");
        assertEquals("No fireStation found in the repository.", exception.getMessage());

        // Verify interactions
        verify(fireStationRepository, times(1)).getFireStations();
        verifyNoInteractions(fireStationConvertorDTO);

        LOGGER.info("Test shouldReturnGetFireStations_NotFound completed successfully");
    }

    @Test
    void shouldReturnSave() {

        LOGGER.info("Starting test: shouldReturnSave");

        // Arrange
        FireStationDTO fireStationDTO = fireStationDTO1;
        FireStation fireStationEntity = fireStation1;

        LOGGER.info("Mocking repository and converter interactions.");
        when(fireStationConvertorDTO.convertDtoToEntity(fireStationDTO)).thenReturn(fireStationEntity);
        when(fireStationRepository.save(fireStationEntity)).thenReturn(fireStationEntity);
        when(fireStationConvertorDTO.convertEntityToDto(fireStationEntity)).thenReturn(fireStationDTO);

        // Act
        LOGGER.info("Calling fireStationService.save()");
        FireStationDTO result = fireStationService.save(fireStationDTO);

        // Assert
        LOGGER.info("Verifying the result is not null.");
        assertNotNull(result);
        assertEquals(fireStationDTO1.getAddress(), result.getAddress());
        assertEquals(fireStationDTO1.getStation(), result.getStation());

        LOGGER.info("Verifying method calls.");
        verify(fireStationConvertorDTO, times(1)).convertDtoToEntity(fireStationDTO);
        verify(fireStationRepository, times(1)).save(fireStationEntity);
        verify(fireStationConvertorDTO, times(1)).convertEntityToDto(fireStationEntity);

        LOGGER.info("Test shouldReturnSave completed successfully");
    }

    @Test
    void shouldReturnSave_ExceptionThrownByRepository() {

        LOGGER.info("Starting test: shouldReturnSave_ExceptionThrownByRepository");

        // Test with null fireStationDTO
        LOGGER.info("Testing with a null FireStationDTO.");
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            fireStationService.save(null);});
        assertEquals(FireStationImplConstant.FIRE_STATION_ERROR, exception1.getMessage());

        FireStationDTO fireStationDTO = new FireStationDTO();
        FireStation fireStationEntity = new FireStation();

        LOGGER.info("Mocking repository to throw an exception.");
        when(fireStationConvertorDTO.convertDtoToEntity(fireStationDTO)).thenReturn(fireStationEntity);
        when(fireStationRepository.save(fireStationEntity))
                .thenThrow(new FireStationNotFoundException(FIRE_STATION_NOT_FOUND));

        // Act & Assert
        LOGGER.info("Expecting FireStationNotFoundException when calling fireStationService.save().");
        RuntimeException exception2 = assertThrows(RuntimeException.class, () -> {fireStationService.save(fireStationDTO);});
        assertFalse(exception2.getMessage().contains(FireStationImplConstant.FIRE_STATION_NOT_FOUND));

        LOGGER.info("Verifying");
        verify(fireStationConvertorDTO, times(1)).convertDtoToEntity(fireStationDTO);
        verify(fireStationRepository, times(1)).save(fireStationEntity);

        LOGGER.info("Test shouldReturnSave_ExceptionThrownByRepository completed successfully");
    }

    @Test
    void shouldReturnUpdate() {

        LOGGER.info("Starting test: shouldReturnUpdate");

        // Arrange
        FireStationDTO fireStationDTO = fireStationDTO1;
        FireStation fireStationEntities = fireStation1;

        LOGGER.info("Mocking repository and converter.");
        when(fireStationConvertorDTO.convertDtoToEntity(fireStationDTO)).thenReturn(fireStationEntities);
        when(fireStationRepository.update(fireStationEntities)).thenReturn(Optional.of(fireStationEntities));
        when(fireStationConvertorDTO.convertEntityToDto(fireStationEntities)).thenReturn(fireStationDTO);

        // Act
        LOGGER.info("Calling fireStationService.update()");
        Optional<FireStationDTO> result = fireStationService.update(fireStationDTO);

        // Assert
        LOGGER.info("Verifying the update result.");
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(fireStationDTO1.getAddress(), result.get().getAddress());
        assertEquals(fireStationDTO1.getStation(), result.get().getStation());

        LOGGER.info("Verifying.");
        verify(fireStationConvertorDTO, times(1)).convertDtoToEntity(fireStationDTO);
        verify(fireStationRepository, times(1)).update(fireStationEntities);
        verify(fireStationConvertorDTO, times(1)).convertEntityToDto(fireStationEntities);

        LOGGER.info("Test shouldReturnUpdate completed successfully");
    }

    @Test
    void shouldReturnDeleteByAddress() {

        LOGGER.info("Starting test: shouldReturnDeleteByAddress");

        // Arrange
        LOGGER.info("Mocking repository to return true on delete.");
        when(fireStationRepository.deleteByAddress(fireStation1.getAddress())).thenReturn(true);

        // Act
        LOGGER.info("Calling fireStationService.deleteByAddress()");
        Boolean result = fireStationService.deleteByAddress(fireStation1.getAddress());

        // Assert
        LOGGER.info("Verifying delete operation was successful.");
        assertNotNull(result);
        assertTrue(result);

        LOGGER.info("Verifying method .");
        verify(fireStationRepository, times(1)).deleteByAddress(fireStation1.getAddress());
        verifyNoInteractions(fireStationConvertorDTO);

        LOGGER.info("Test shouldReturnDeleteByAddress completed successfully");
    }

}
