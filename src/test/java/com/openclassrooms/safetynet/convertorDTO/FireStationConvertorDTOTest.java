package com.openclassrooms.safetynet.convertorDTO;

import com.openclassrooms.safetynet.dto.FireStationDTO;
import com.openclassrooms.safetynet.model.FireStation;
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
public class FireStationConvertorDTOTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @InjectMocks
    private FireStationConvertorDTO fireStationConvertor;

    private FireStation fireStation1;
    private FireStation fireStation2;
    private FireStationDTO fireStationDTO1;
    private FireStationDTO fireStationDTO2;

    @BeforeEach
    void setUp() {
        // FireStations
        fireStation1 = new FireStation("149 Bd Pei ere 75007 Paris", "1");
        fireStation2 = new FireStation("150 Bd Pei ere 75007 Paris", "2");

        //FireStationDTO
        fireStationDTO1 = new FireStationDTO("149 Bd Pei ere 75007 Paris", "1");
        fireStationDTO2 = new FireStationDTO("150 Bd Pei ere 75007 Paris", "2");
    }

    @Test
    void shouldReturnConvertEntityToDto() {
        LOGGER.info("Start method: convertEntityToDto");
        FireStationDTO fireStationDTO = fireStationConvertor.convertEntityToDto(fireStation1);

        assertNotNull(fireStationDTO);
        assertEquals(fireStation1.getAddress(), fireStationDTO.getAddress());
        assertEquals(fireStation1.getStation(), fireStationDTO.getStation());
        LOGGER.info("End method: convertEntityToDto : Test passed");
    }

    @Test
    void shouldReturnConvertEntityToDtoNull () {
        LOGGER.info("Start method: convertEntityToDtoNull");
        assertNull(fireStationConvertor.convertEntityToDto((FireStation) null));
        LOGGER.info("End method: convertEntityToDtoNull : Test passed");
    }

    @Test
    void shouldReturnConvertDtoToEntity() {
        LOGGER.info("Start method: shouldReturnConvertDtoToEntity");
        FireStation fireStation = fireStationConvertor.convertDtoToEntity(fireStationDTO1);

        assertNotNull(fireStation);
        assertEquals(fireStation1.getStation(), fireStation.getStation());
        assertEquals(fireStation1.getAddress(), fireStation.getAddress());
        LOGGER.info("End method: shouldReturnConvertDtoToEntity : Test passed");
    }

    @Test
    void shouldReturnConvertDtoToEntityNull() {
        LOGGER.info("Start method: shouldReturnConvertDtoToEntityNull");
        assertNull(fireStationConvertor.convertDtoToEntity((FireStationDTO) null));
        LOGGER.info("End method: shouldReturnConvertDtoToEntityNull : Test passed");
    }

    @Test
    void shouldReturnConvertEntityToDtoList() {
        LOGGER.info("Start method: shouldReturnConvertEntityToDtoList");
        List<FireStation> fireStations = List.of(fireStation1, fireStation2);

        List<FireStationDTO> fireStationDTOS = fireStationConvertor.convertEntityToDto(fireStations);

        assertNotNull(fireStationDTOS);
        assertEquals(2, fireStationDTOS.size());
        assertEquals(fireStation1.getAddress(), fireStationDTOS.get(0).getAddress());
        assertEquals(fireStation2.getStation(), fireStationDTOS.get(1).getStation());
        LOGGER.info("End method: shouldReturnConvertEntityToDtoList : Test passed");
    }

    @Test
    void shouldReturnConvertEntityToDtoListIsEmpty() {
        LOGGER.info("Start method: shouldReturnConvertEntityToDtoListIsEmpty");
        assertTrue(fireStationConvertor.convertEntityToDto(List.of()).isEmpty());
        LOGGER.info("End method: shouldReturnConvertEntityToDtoListIsEmpty : Test passed");
    }

    @Test
    void shouldReturnConvertDtoToEntityList() {
        LOGGER.info("Start method: shouldReturnConvertDtoToEntityList");
        List<FireStationDTO> fireStationDTOS = List.of(fireStationDTO1, fireStationDTO2);
        List<FireStation> fireStations = fireStationConvertor.convertDtoToEntity(fireStationDTOS);

        assertNotNull(fireStations);
        assertEquals(2, fireStations.size());
        assertEquals(fireStationDTO1.getAddress(), fireStations.get(0).getAddress());
        assertEquals(fireStationDTO2.getStation(), fireStations.get(1).getStation());
        LOGGER.info("End method: shouldReturnConvertDtoToEntityList : Test passed");
    }

    @Test
    void shouldReturnConvertDtoToEntityListIsEmpty() {
        LOGGER.info("Start method: shouldReturnConvertDtoToEntityListIsEmpty");
        assertTrue(fireStationConvertor.convertDtoToEntity(List.of()).isEmpty());
        LOGGER.info("End method: shouldReturnConvertDtoToEntityListIsEmpty : Test passed");
    }

}
