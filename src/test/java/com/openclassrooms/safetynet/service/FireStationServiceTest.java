package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.FireStationConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.FireStationDTO;
import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.FireStationRepository;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FireStationServiceTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Mock
    private FireStationRepository fireStationRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private MedicalRecordConvertorDTO medicalRecordConvertorDTO;
    @Mock
    private FireStationConvertorDTO fireStationConvertorDTO;
    @Mock
    private PersonConvertorDTO personConvertorDTO;

    @InjectMocks
    private FireStationService fireStationService;
    @InjectMocks
    private PersonService personService;

    private FireStation fireStation1;
    private FireStation fireStation2;
    private FireStationDTO fireStationDTO1;
    private FireStationDTO fireStationDTO2;

    private Person person1;
    private Person person2;
    private PersonDTO personDTO1;
    private PersonDTO personDTO2;

    @BeforeEach
    void setUp() {
        // FireStations
        fireStation1 = new FireStation("149 Bd Pei ere 75007 Paris", "1");
        fireStation2 = new FireStation("150 Bd Pei ere 75007 Paris", "2");

        //FireStationDTO
        fireStationDTO1 = new FireStationDTO("149 Bd Pei ere 75007 Paris", "1");
        fireStationDTO2 = new FireStationDTO("150 Bd Pei ere 75007 Paris", "2");

        // Person
        person1 = new Person("John", "Doe", "123 Main St", "Springfield", "75016",
                "0144445151", "johndoe@gmail.com");
        person2 = new Person("Jane", "Doe", "123 Main St", "Springfield", "75017",
                "0144445152", "janedoe@gmail.com");

        // PersonDTO
        personDTO1 = new PersonDTO("John", "Doe", "johndoe@gmail.com", "123 Main St", "Springfield",
                "75016", "0144445151");
        personDTO2 = new PersonDTO("Jane", "Doe", "janedoe@gmail.com", "123 Main St",
                "Springfield", "75017", "0144445152");
    }

    @Test
    void shouldReturnGetFireStations() {

        when(fireStationRepository.getFireStations()).thenReturn(List.of(fireStation1, fireStation2));
        when(fireStationConvertorDTO.convertEntityToDto(fireStation1)).thenReturn(fireStationDTO1);
        when(fireStationConvertorDTO.convertEntityToDto(fireStation2)).thenReturn(fireStationDTO2);

        List<FireStationDTO> fireStationDTOList = fireStationService.getFireStations();

        assertNotNull(fireStationDTOList);
        assertEquals(2, fireStationDTOList.size());
        assertEquals(fireStationDTO1.getAddress(), fireStationDTOList.get(0).getAddress());
        assertEquals(fireStationDTO2.getAddress(), fireStationDTOList.get(1).getAddress());

        verify(fireStationRepository, times(1)).getFireStations();
        verify(fireStationConvertorDTO, times(1)).convertEntityToDto(fireStation1);
        verify(fireStationConvertorDTO, times(1)).convertEntityToDto(fireStation2);
    }

    @Test void shouldReturnGetFireStations_NotFound() {
        when(fireStationRepository.getFireStations()).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> fireStationService.getFireStations());
        //assertEquals(HttpStatus.NOT_FOUND, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode()); // Vérification du code HTTP

        verify(fireStationRepository, times(1)).getFireStations();
        verifyNoInteractions(fireStationConvertorDTO);
    }

    @Test
    void shouldReturnSaveAll() {
        List<FireStationDTO> fireStationDTOList = List.of(fireStationDTO1, fireStationDTO2);
        List<FireStation> fireStationEntities = List.of(fireStation1, fireStation2);

        when(fireStationConvertorDTO.convertDtoToEntity(fireStationDTOList)).thenReturn(fireStationEntities);
        when(fireStationRepository.saveAll(fireStationEntities)).thenReturn(fireStationEntities);
        when(fireStationConvertorDTO.convertEntityToDto(fireStationEntities)).thenReturn(fireStationDTOList);

        List<FireStationDTO> result = fireStationService.saveAll(fireStationDTOList);

        assertNotNull(fireStationDTOList);
        assertEquals(2, result.size());
        assertEquals(fireStationDTO1.getAddress(), result.get(0).getAddress());
        assertEquals(fireStationDTO2.getAddress(), result.get(1).getAddress());

        verify(fireStationConvertorDTO, times(1)).convertDtoToEntity(fireStationDTOList);
        verify(fireStationRepository, times(1)).saveAll(fireStationEntities);
        verify(fireStationConvertorDTO, times(1)).convertEntityToDto(fireStationEntities);
    }

    @Test
    void shouldReturnSaveAll_NullOrEmptyList() {
        ResponseStatusException exception1 = assertThrows(ResponseStatusException.class, () -> {
            fireStationService.saveAll(null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception1.getStatusCode());
        assertTrue(Objects.requireNonNull(exception1.getReason()).contains("FireStation list cannot be null or empty."));

        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class, () -> {
            fireStationService.saveAll(List.of());
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception2.getStatusCode());
        assertTrue(Objects.requireNonNull(exception1.getReason()).contains("FireStation list cannot be null or empty."));
    }

    @Test
    void shouldReturnSaveAll_ExceptionThrownByRepository() {

        List<FireStationDTO> fireStationDTOList = List.of(fireStationDTO1, fireStationDTO2);
        List<FireStation> fireStationEntities = List.of(fireStation1, fireStation2);

        when(fireStationConvertorDTO.convertDtoToEntity(fireStationDTOList)).thenReturn(fireStationEntities);
        when(fireStationRepository.saveAll(fireStationEntities)).thenThrow(new RuntimeException("Invalid fireStations data:"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> fireStationService.saveAll(fireStationDTOList));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(Objects.requireNonNull(exception.getReason()).contains("Database/system error while saving fireStations:"));

        verify(fireStationConvertorDTO, times(1)).convertDtoToEntity(fireStationDTOList);
        verify(fireStationRepository, times(1)).saveAll(fireStationEntities);
    }

    @Test
    void shouldReturnSave() {
        FireStationDTO fireStationDTO = fireStationDTO1;
        FireStation fireStationEntities = fireStation1;

        when(fireStationConvertorDTO.convertDtoToEntity(fireStationDTO)).thenReturn(fireStationEntities);
        when(fireStationRepository.save(fireStationEntities)).thenReturn(fireStationEntities);
        when(fireStationConvertorDTO.convertEntityToDto(fireStationEntities)).thenReturn(fireStationDTO);

        FireStationDTO result = fireStationService.save(fireStationDTO);

        assertNotNull(result);
        assertEquals(fireStationDTO1.getAddress(), result.getAddress());
        assertEquals(fireStationDTO1.getStation(), result.getStation());

        verify(fireStationConvertorDTO, times(1)).convertDtoToEntity(fireStationDTO);
        verify(fireStationRepository, times(1)).save(fireStationEntities);
        verify(fireStationConvertorDTO, times(1)).convertEntityToDto(fireStationEntities);
    }

    @Test
    void shouldReturnUpdate() {
        FireStationDTO fireStationDTO = fireStationDTO1;
        FireStation fireStationEntities = fireStation1;

        when(fireStationConvertorDTO.convertDtoToEntity(fireStationDTO)).thenReturn(fireStationEntities);
        when(fireStationRepository.update(fireStationEntities)).thenReturn(Optional.of(fireStationEntities));
        when(fireStationConvertorDTO.convertEntityToDto(fireStationEntities)).thenReturn(fireStationDTO);

        Optional<FireStationDTO> result = fireStationService.update(fireStationDTO);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(fireStationDTO1.getAddress(), result.get().getAddress());
        assertEquals(fireStationDTO1.getStation(), result.get().getStation());

        verify(fireStationConvertorDTO, times(1)).convertDtoToEntity(fireStationDTO);
        verify(fireStationRepository, times(1)).update(fireStationEntities);
        verify(fireStationConvertorDTO, times(1)).convertEntityToDto(fireStationEntities);
    }

    @Test
    void shouldReturnDeleteByAddress() {
        when(fireStationRepository.deleteByAddress(fireStation1.getAddress())).thenReturn(true);

        Boolean result = fireStationService.deleteByAddress(fireStation1.getAddress());

        assertNotNull(result);
        assertTrue(result);

        verify(fireStationRepository, times(1)).deleteByAddress(fireStation1.getAddress());
        verifyNoInteractions(fireStationConvertorDTO);
    }

}
