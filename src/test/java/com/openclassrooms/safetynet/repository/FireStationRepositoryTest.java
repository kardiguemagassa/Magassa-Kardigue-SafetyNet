package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.FireStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.openclassrooms.safetynet.constant.repository.FireStationRepositoryConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FireStationRepositoryTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Mock
    private DataBaseInMemoryWrapper dataBaseInMemoryWrapper;

    @InjectMocks
    private FireStationRepository fireStationRepository;
    @Mock


    private FireStation fireStation1;
    private FireStation fireStation2;


    @BeforeEach
    void setUp() {
        fireStation1 = new FireStation("149 Bd Pei ere 75007 Paris", "1");
        fireStation2 = new FireStation("150 Bd Pei ere 75007 Paris", "2");
    }

    @Test
    public void shouldReturnGetFireStationsLoadedSuccessfully() {

        LOGGER.info("Start method : shouldReturnFireStationsLoadedSuccessfully");

        when(dataBaseInMemoryWrapper.getFireStations()).thenReturn(Arrays.asList(fireStation1, fireStation2));

        List<FireStation> result = fireStationRepository.getFireStations();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(dataBaseInMemoryWrapper, times(1)).getFireStations();
        LOGGER.info("End method: shouldReturnFireStationsLoadedSuccessfully : Test passed");
    }


    @Test
    public void shouldReturnGetFireStationsLoadedException() {

        LOGGER.info("Start method : shouldReturnGetFireStationsLoadedException");

        when(dataBaseInMemoryWrapper.getFireStations()).thenThrow(new RuntimeException());

        List<FireStation> result = fireStationRepository.getFireStations();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(dataBaseInMemoryWrapper).getFireStations();
        verifyNoMoreInteractions(dataBaseInMemoryWrapper);
        LOGGER.info("End method: shouldReturnGetFireStationsLoadedException : Test passed");
    }

    @Test
    public void shouldSaveFireStationSuccessfully() {

        LOGGER.info("Start method : shouldSaveFireStationSuccessfully");

        when(dataBaseInMemoryWrapper.getFireStations()).thenReturn(new ArrayList<>());

        // Act
        FireStation result = fireStationRepository.save(fireStation1);

        // Vérifications
        assertNotNull(result);
        assertEquals(fireStation1, result);
        verify(dataBaseInMemoryWrapper, times(1)).getFireStations();
        LOGGER.info("End method : shouldSaveFireStationSuccessfully : Test passed");
    }

    @Test
    public void shouldReturnSavingNullFireStation() {
        LOGGER.info("Start method : shouldReturnSavingNullFireStation");
        FireStation result = fireStationRepository.save(null);
        assertNull(result);
        LOGGER.info("End method : shouldReturnSavingNullFireStation : Test passed");
    }


    @Test
    public void shouldReturnSaveFireStationDataBaseException() {

        LOGGER.info("Start method : shouldReturnSaveFireStationException");
        when(dataBaseInMemoryWrapper.getFireStations()).thenThrow(new RuntimeException(FIRE_STATION_ERROR_SAVING_DATA_BASE_));

        // Act & Assert
        FireStation fireStations = fireStationRepository.save(fireStation1);

        assertNotNull(fireStations);
        verify(dataBaseInMemoryWrapper, times(1)).getFireStations();
        verifyNoMoreInteractions(dataBaseInMemoryWrapper);
        LOGGER.info("End method : shouldReturnSaveFireStationException : Test passed");
    }

    @Test
    public void shouldReturnUpdateFireStationSuccessfully() {

        LOGGER.info("Start method : shouldReturnUpdateFireStationSuccessfully");

        when(dataBaseInMemoryWrapper.getFireStations()).thenReturn(Collections.singletonList(fireStation1));

        FireStation updatedFireStation = new FireStation(fireStation1.getAddress(), "3");

        // Act
        Optional<FireStation> result = fireStationRepository.update(updatedFireStation);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(updatedFireStation.getStation(), result.get().getStation());
        assertEquals(updatedFireStation, result.get());
        verify(dataBaseInMemoryWrapper, times(2)).getFireStations();
        LOGGER.info("End method : shouldReturnUpdateFireStationSuccessfully : Test passed");
    }

    @Test
    public void shouldReturnUpdateFireStationAddSuccessfully() {
        LOGGER.info("Start method : shouldReturnUpdateFireStationAddSuccessfully");

        // Arrange
        List<FireStation> fireStationList = new ArrayList<>();
        fireStationList.add(fireStation1);

        when(dataBaseInMemoryWrapper.getFireStations()).thenReturn(fireStationList);

        FireStation updatedFireStation = new FireStation("New Address", "3");

        // Act
        Optional<FireStation> result = fireStationRepository.update(updatedFireStation);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(updatedFireStation.getStation(), result.get().getStation());
        assertEquals(updatedFireStation, result.get());

        // Vérifier que la station a bien été ajoutée
        assertTrue(fireStationList.contains(updatedFireStation));

        verify(dataBaseInMemoryWrapper, atLeastOnce()).getFireStations();
        LOGGER.info("End method : shouldReturnUpdateFireStationAddSuccessfully : Test passed");
    }

    @Test
    public void shouldReturnUpdateOptionalNullFireStation() {
        LOGGER.info("Start method : shouldReturnUpdateOptionalNullFireStation");
        Optional<FireStation> result = fireStationRepository.update(null);
        assertTrue(result.isEmpty());
        LOGGER.info("End method : shouldReturnUpdateOptionalNullFireStation : Test passed");
    }

    @Test
    void shouldReturnEmptyOptionalUpdateException() {
        LOGGER.info("Start method : shouldReturnEmptyOptionalUpdateException");
        when(dataBaseInMemoryWrapper.getFireStations()).thenThrow(new RuntimeException(FIRE_STATION_ERROR_SAVING_UPDATING_SUCCESS));
        Optional<FireStation> result = fireStationRepository.update(fireStation1);
        assertTrue(result.isEmpty());
        verify(dataBaseInMemoryWrapper, times(2)).getFireStations();
        LOGGER.info("End method : shouldReturnEmptyOptionalUpdateException : Test passed");
    }

    @Test
    public void shouldDeleteFireStationSuccessfully() {

        LOGGER.info("Start method : shouldDeleteFireStationWhenFound");

        List<FireStation> fireStations = new ArrayList<>();
        fireStations.add(fireStation1);
        fireStations.add(fireStation2);

        when(dataBaseInMemoryWrapper.getFireStations()).thenReturn(fireStations);

        boolean result = fireStationRepository.deleteByAddress(fireStation1.getAddress());

        assertTrue(result, "The fire station should be deleted.");
        assertFalse(fireStations.contains(fireStation1), "The fire station should no longer be in the list.");
        verify(dataBaseInMemoryWrapper, times(1)).getFireStations();
        LOGGER.info("End method : shouldDeleteFireStationWhenFound : Test passed");
    }

    @Test
    void shouldReturnDeleteFireStationDatabaseIsNull() {

        LOGGER.info("Start method : shouldReturnDeleteFireStationDatabaseIsNull");
        when(dataBaseInMemoryWrapper.getFireStations()).thenReturn(null);
        // Act
        boolean result = fireStationRepository.deleteByAddress(fireStation1.getAddress());

        // Assert
        assertFalse(result);
        verify(dataBaseInMemoryWrapper, times(1)).getFireStations();
        LOGGER.info("End method : shouldReturnDeleteFireStationDatabaseIsNull : Test passed");
    }

    @Test
    void shouldReturnDeleteFireStationException() {
        LOGGER.info("Start method : shouldReturnDeleteFireStationException");

        AtomicReference<List <FireStation>> fireStation = new AtomicReference<>(new ArrayList<>());
        fireStation.get().add(fireStation1);

        when(dataBaseInMemoryWrapper.getFireStations()).thenThrow(new RuntimeException(FIRE_STATION_ERROR_SAVING_DATA_BASE_));
        boolean result = fireStationRepository.deleteByAddress(fireStation1.getAddress());
        assertFalse(result);
        verify(dataBaseInMemoryWrapper, times(1)).getFireStations();
        LOGGER.info("End method : shouldReturnDeleteFireStationException : Test passed");

    }


    // NEW ENDPOINT
    @Test
    public void shouldReturnFindAddressesByStationNumberSuccessfully() {
        LOGGER.info("Start method : shouldReturnFindAddressesByStationNumberSuccessfully");

        // Arrange
        when(dataBaseInMemoryWrapper.getFireStations()).thenReturn(Collections.singletonList((fireStation1)));

        // Act
        List<String> result = fireStationRepository.findAddressesByStationNumber(Integer.parseInt(fireStation1.getStation()));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(fireStation1.getAddress()));


        verify(dataBaseInMemoryWrapper, times(1)).getFireStations();

        LOGGER.info("End method : shouldReturnFindAddressesByStationNumberSuccessfully : Test passed");
    }

    @Test
    void shouldReturnFindAddressByStationNumberDatabaseIsNull() {
        LOGGER.info("Start method : shouldReturnFindAddressesByStationNumberDatabaseIsNull");

        when(dataBaseInMemoryWrapper.getFireStations()).thenReturn(null);
        List<String> result = fireStationRepository.findAddressesByStationNumber(Integer.parseInt(fireStation1.getStation()));

        // Assert
        assertNull(result);
        verify(dataBaseInMemoryWrapper, times(1)).getFireStations();
        LOGGER.info("End method : shouldReturnFindAddressesByStationNumberDatabaseIsNull : Test passed");
    }

    @Test
    void shouldReturnFindAddressByStationNumberDatabaseException() {
        LOGGER.info("Start method : shouldReturnFindAddressesByStationNumberDatabaseException");
        when(dataBaseInMemoryWrapper.getFireStations()).thenThrow(new RuntimeException(ERROR_SEARCHING_ADDRESS_NUMBER));

        List<String> result = fireStationRepository.findAddressesByStationNumber(Integer.parseInt(fireStation1.getStation()));
        assertNull(result);
        verify(dataBaseInMemoryWrapper, times(1)).getFireStations();
        LOGGER.info("End method : shouldReturnFindAddressesByStationNumberDatabaseException : Test passed");
    }

    @Test
    public void shouldReturnFindAddressesByStationNumbersSuccessfully() {
        LOGGER.info("Start method : shouldReturnFindAddressesByStationNumbersSuccessfully");

        // Arrange
        when(dataBaseInMemoryWrapper.getFireStations()).thenReturn(Arrays.asList(fireStation1, fireStation2));

        // Act
        List<Integer> stationNumbers = Arrays.asList(Integer.parseInt(fireStation1.getStation()), Integer.parseInt(fireStation2.getStation()));
        List<String> result = fireStationRepository.findAddressesByStationNumbers(stationNumbers);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(fireStation1.getAddress()));
        assertTrue(result.contains(fireStation2.getAddress()));

        verify(dataBaseInMemoryWrapper, times(1)).getFireStations();
        LOGGER.info("End method : shouldReturnFindAddressesByStationNumbersSuccessfully : Test passed");
    }

    @Test
    void shouldReturnFindAddressByStationNumbersDatabaseIsNull() {
        LOGGER.info("Start method : shouldReturnFindAddressesByStationNumbersDatabaseIsNull");

        when(dataBaseInMemoryWrapper.getFireStations()).thenReturn(null);
        List<Integer> StationNumbers = Arrays.asList(Integer.parseInt(fireStation1.getStation()), Integer.parseInt(fireStation2.getStation()));
        List<String> result = fireStationRepository.findAddressesByStationNumbers(StationNumbers);

        // Assert
        assertNull(result);
        verify(dataBaseInMemoryWrapper, times(1)).getFireStations();
        LOGGER.info("End method : shouldReturnFindAddressesByStationNumbersDatabaseIsNull : Test passed");
    }

    @Test
    void shouldReturnFindAddressByStationNumbersDatabaseException() {
        LOGGER.info("Start method : shouldReturnFindAddressesByStationNumbersDatabaseException");
        when(dataBaseInMemoryWrapper.getFireStations()).thenThrow(new RuntimeException(ERROR_SEARCHING_ADDRESS_NUMBER));

        List<Integer> StationNumbers = Arrays.asList(Integer.parseInt(fireStation1.getStation()), Integer.parseInt(fireStation2.getStation()));
        List<String> result = fireStationRepository.findAddressesByStationNumbers(StationNumbers);

        assertNull(result);
        verify(dataBaseInMemoryWrapper, times(1)).getFireStations();
        LOGGER.info("End method : shouldReturnFindAddressesByStationNumbersDatabaseException : Test passed");
    }

    @Test
    void shouldReturnFindAddressByStationNumbersIsNull() {
        LOGGER.info("Start method : shouldReturnFindAddressesByStationNumbersIsNull");
        List<String> result = fireStationRepository.findAddressesByStationNumbers(null);
        assertNull(result);
        LOGGER.info("End method : shouldReturnFindAddressesByStationNumbersIsNull : Test passed");

    }

    @Test
    void shouldReturnFindAddressDatabaseIsNull() {
        LOGGER.info("Start method : shouldReturnFindAddressDatabaseIsNull");

        when(dataBaseInMemoryWrapper.getFireStations()).thenReturn(null);
        FireStation result = fireStationRepository.findByAddress(fireStation1.getAddress());

        // Assert
        assertNull(result);
        verify(dataBaseInMemoryWrapper, times(1)).getFireStations();
        LOGGER.info("End method : shouldReturnFindAddressDatabaseIsNull : Test passed");
    }

    @Test
    void shouldReturnFindAddressIsNull() {
        LOGGER.info("Start method : shouldReturnFindAddressIsNull");
        FireStation result = fireStationRepository.findByAddress(null);
        assertNull(result);
        LOGGER.info("End method : shouldReturnFindAddressIsNull : Test passed");
    }

}
