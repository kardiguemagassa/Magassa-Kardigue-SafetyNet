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

import static com.openclassrooms.safetynet.constant.service.FireStationImplConstant.FIRE_STATION_ERROR_DELETING;
import static com.openclassrooms.safetynet.constant.service.FireStationImplConstant.FIRE_STATION_ERROR_UPDATING;
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

        LOGGER.info("Start method : shouldReturnGetFireStations");

        // Arrange
        when(fireStationRepository.getFireStations()).thenReturn(List.of(fireStation1, fireStation2));
        when(fireStationConvertorDTO.convertEntityToDto(fireStation1)).thenReturn(fireStationDTO1);
        when(fireStationConvertorDTO.convertEntityToDto(fireStation2)).thenReturn(fireStationDTO2);

        // Act
        List<FireStationDTO> fireStationDTOList = fireStationService.getFireStations();

        // Assert
        assertNotNull(fireStationDTOList);
        assertEquals(2, fireStationDTOList.size());
        assertEquals(fireStationDTO1.getAddress(), fireStationDTOList.get(0).getAddress());
        assertEquals(fireStationDTO2.getAddress(), fireStationDTOList.get(1).getAddress());

        // Verify interactions
        verify(fireStationRepository, times(1)).getFireStations();
        verify(fireStationConvertorDTO, times(1)).convertEntityToDto(fireStation1);
        verify(fireStationConvertorDTO, times(1)).convertEntityToDto(fireStation2);

        LOGGER.info("End method : shouldReturnGetFireStations completed successfully");
    }

    @Test void shouldReturnGetFireStationsNotFoundException() {

        LOGGER.info("Start method : shouldReturnGetFireStationsNotFoundException");

        when(fireStationRepository.getFireStations()).thenReturn(null);

        FireStationNotFoundException exception = assertThrows(FireStationNotFoundException.class, () -> fireStationService.getFireStations());

        assertEquals("No fireStation found in the repository.", exception.getMessage());

        // Verify interactions
        verify(fireStationRepository, times(1)).getFireStations();
        verifyNoInteractions(fireStationConvertorDTO);

        LOGGER.info("End method : shouldReturnGetFireStationsNotFoundException completed successfully");
    }

    @Test
    void shouldReturnSave() {

        LOGGER.info("Starting test: shouldReturnSave");

        // Arrange
        FireStationDTO fireStationDTO = fireStationDTO1;
        FireStation fireStationEntity = fireStation1;

        when(fireStationConvertorDTO.convertDtoToEntity(fireStationDTO)).thenReturn(fireStationEntity);
        when(fireStationRepository.save(fireStationEntity)).thenReturn(fireStationEntity);
        when(fireStationConvertorDTO.convertEntityToDto(fireStationEntity)).thenReturn(fireStationDTO);

        // Act
        FireStationDTO result = fireStationService.save(fireStationDTO);

        // Assert
        assertNotNull(result);
        assertEquals(fireStationDTO1.getAddress(), result.getAddress());
        assertEquals(fireStationDTO1.getStation(), result.getStation());

        verify(fireStationConvertorDTO, times(1)).convertDtoToEntity(fireStationDTO);
        verify(fireStationRepository, times(1)).save(fireStationEntity);
        verify(fireStationConvertorDTO, times(1)).convertEntityToDto(fireStationEntity);

        LOGGER.info("End method : shouldReturnSave completed successfully");
    }

    @Test
    void shouldReturnSaveException() {

        LOGGER.info("Start method : shouldReturnSaveException");


        LOGGER.info("Testing with a null FireStationDTO.");
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
                () -> fireStationService.save(null));
        assertEquals(FireStationImplConstant.FIRE_STATION_ERROR, exception1.getMessage());

        FireStationDTO fireStationDTO = new FireStationDTO();
        FireStation fireStationEntity = new FireStation();

        when(fireStationConvertorDTO.convertDtoToEntity(fireStationDTO)).thenReturn(fireStationEntity);
        when(fireStationRepository.save(fireStationEntity))
                .thenThrow(new FireStationNotFoundException(FIRE_STATION_NOT_FOUND));

        // Act & Assert
        RuntimeException exception2 = assertThrows(RuntimeException.class, () -> fireStationService.save(fireStationDTO));
        assertFalse(exception2.getMessage().contains(FireStationImplConstant.FIRE_STATION_NOT_FOUND));

        verify(fireStationConvertorDTO, times(1)).convertDtoToEntity(fireStationDTO);
        verify(fireStationRepository, times(1)).save(fireStationEntity);

        LOGGER.info("End method : shouldReturnSaveException completed successfully");
    }

    @Test
    void shouldReturnUpdate() {

        LOGGER.info("Start method: shouldReturnUpdate");

        // Arrange
        FireStationDTO fireStationDTO = fireStationDTO1;
        FireStation fireStationEntities = fireStation1;

        when(fireStationConvertorDTO.convertDtoToEntity(fireStationDTO)).thenReturn(fireStationEntities);
        when(fireStationRepository.update(fireStationEntities)).thenReturn(Optional.of(fireStationEntities));
        when(fireStationConvertorDTO.convertEntityToDto(fireStationEntities)).thenReturn(fireStationDTO);

        // Act
        Optional<FireStationDTO> result = fireStationService.update(fireStationDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(fireStationDTO1.getAddress(), result.get().getAddress());
        assertEquals(fireStationDTO1.getStation(), result.get().getStation());

        verify(fireStationConvertorDTO, times(1)).convertDtoToEntity(fireStationDTO);
        verify(fireStationRepository, times(1)).update(fireStationEntities);
        verify(fireStationConvertorDTO, times(1)).convertEntityToDto(fireStationEntities);

        LOGGER.info("End method : shouldReturnUpdate completed successfully");
    }

    @Test
    void shouldReturnUpdateException() {

        LOGGER.info("Start method: shouldReturnUpdateException");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> fireStationService.update(null));
        assertEquals(FIRE_STATION_ERROR_UPDATING, exception.getMessage());

        LOGGER.info("End method : shouldReturnUpdateException completed successfully");
    }

    @Test
    void shouldReturnDeleteByAddress() {

        LOGGER.info("Start method : shouldReturnDeleteByAddress");

        // Arrange
        when(fireStationRepository.deleteByAddress(fireStation1.getAddress())).thenReturn(true);

        // Act
        Boolean result = fireStationService.deleteByAddress(fireStation1.getAddress());

        // Assert
        assertNotNull(result);
        assertTrue(result);

        verify(fireStationRepository, times(1)).deleteByAddress(fireStation1.getAddress());
        verifyNoInteractions(fireStationConvertorDTO);

        LOGGER.info("End method : shouldReturnDeleteByAddress completed successfully");
    }

    @Test
    void shouldReturnDeleteByAddressException() {

        LOGGER.info("Start method: shouldReturnDeleteByAddressException");

        // Given
        when(fireStationRepository.deleteByAddress(fireStation1.getAddress())).thenReturn(false);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> fireStationService.deleteByAddress(fireStation1.getAddress()));

        assertEquals(FIRE_STATION_ERROR_DELETING, exception.getMessage());
        LOGGER.info("End method : shouldReturnDeleteByAddressException completed successfully");
    }

    @Test
    void shouldReturnDeleteByAddressNull() {

        LOGGER.info("Start method: shouldReturnDeleteByAddressNull");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                fireStationService.deleteByAddress(null));

        assertEquals(FIRE_STATION_ERROR_DELETING, exception.getMessage());
        LOGGER.info("End method : shouldReturnDeleteByAddressNull completed successfully");

    }

}
