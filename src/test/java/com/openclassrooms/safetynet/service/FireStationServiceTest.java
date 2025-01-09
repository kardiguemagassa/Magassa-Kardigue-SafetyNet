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

    private static final Logger LOGGER = LoggerFactory.getLogger(FireStationServiceTest.class);

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

    @Test
    void shouldReturnGetPersonsByStation() {
        // Arrange - Prepare mocked data
        int stationNumber = 1;
        List<String> mockAddresses = List.of("123 Main St", "456 Oak St");

        // Mock resident list
        List<Person> mockResidents = List.of(
                new Person("John", "Doe", "123 Main St", "Springfield", "75016",
                        "123-456-7890", "johndoe@gmail.com"),
                new Person("Jane", "Smith", "456 Oak St", "Springfield", "75016",
                        "987-654-3210", "janesmith@gmail.com")
        );

        // Mock medical records
        MedicalRecord johnMedicalRecord = new MedicalRecord("John", "Doe", "01/01/1980", null, null);
        MedicalRecord janeMedicalRecord = new MedicalRecord("Jane", "Smith", "12/31/2015", null, null);

        // Mock repository and service calls
        when(fireStationRepository.findAddressesByStationNumber(stationNumber)).thenReturn(mockAddresses);
        when(personRepository.findByAddresses(mockAddresses)).thenReturn(mockResidents);
        when(medicalRecordRepository.findByFullName("John", "Doe")).thenReturn(johnMedicalRecord);
        when(medicalRecordRepository.findByFullName("Jane", "Smith")).thenReturn(janeMedicalRecord);

        // Act - Call the method being tested
        Map<String, ?> result = fireStationService.getPersonsByStation(stationNumber);

        // Assert - Validate the result structure and content
        assertNotNull(result, "The result should not be null");
        assertTrue(result.containsKey("persons"), "Result should contain 'persons' key");
        assertTrue(result.containsKey("adults"), "Result should contain 'adults' key");
        assertTrue(result.containsKey("children"), "Result should contain 'children' key");

        // Validate persons list
        List<Map<String, Object>> persons = (List<Map<String, Object>>) result.get("persons");
        assertEquals(2, persons.size(), "There should be 2 persons in the result");

        // Validate John Doe's data
        Map<String, Object> john = persons.stream()
                .filter(p -> "John".equals(p.get("firstName")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("John not found in persons list"));

        assertEquals("Doe", john.get("lastName"));
        assertEquals("123 Main St", john.get("address"));
        assertEquals("123-456-7890", john.get("phone"));

        // Validate Jane Smith's data
        Map<String, Object> jane = persons.stream()
                .filter(p -> "Jane".equals(p.get("firstName")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Jane not found in persons list"));

        assertEquals("Smith", jane.get("lastName"));
        assertEquals("456 Oak St", jane.get("address"));
        assertEquals("987-654-3210", jane.get("phone"));

        // Validate counts of adults and children
        assertEquals(1, result.get("adults"), "There should be 1 adult");
        assertEquals(1, result.get("children"), "There should be 1 child");

        // Verify interactions with the mocked repositories
        verify(fireStationRepository, times(1)).findAddressesByStationNumber(stationNumber);
        verify(personRepository, times(1)).findByAddresses(mockAddresses);
        verify(medicalRecordRepository, times(1)).findByFullName("John", "Doe");
        verify(medicalRecordRepository, times(1)).findByFullName("Jane", "Smith");
    }

    @Test
    void shouldReturnGetPersonsByStationA() {
        // Mock des données d'entrée
        int stationNumber = 1;
        List<String> mockAddresses = List.of("123 Main St", "456 Oak St");
        List<Person> mockResidents = List.of();

        MedicalRecord johnMedicalRecord = new MedicalRecord("John", "Doe", "01/01/1980", null, null);
        MedicalRecord janeMedicalRecord = new MedicalRecord("Jane", "Smith", "12/31/2015", null, null);

        // Configurer les comportements des mocks
        when(fireStationRepository.findAddressesByStationNumber(stationNumber)).thenReturn(mockAddresses);
        when(personRepository.findByAddresses(mockAddresses)).thenReturn(mockResidents);
        when(medicalRecordRepository.findByFullName("John", "Doe")).thenReturn(johnMedicalRecord);
        when(medicalRecordRepository.findByFullName("Jane", "Smith")).thenReturn(janeMedicalRecord);

        // Appeler la méthode à tester
        FireStationService.FireStationResponse response = fireStationService.getPersonsByStationA(stationNumber);

        // Assertions sur la réponse
        assertNotNull(response, "The response should not be null");
        assertEquals(1, response.getAdultCount(), "There should be 1 adult");
        assertEquals(1, response.getChildCount(), "There should be 1 child");

        List<FireStationService.FireStationResponse.ResidentInfo> residents = response.getResidents();
        assertNotNull(residents, "The residents list should not be null");
        assertEquals(2, residents.size(), "There should be 2 residents");

        // Vérifications spécifiques pour John
        FireStationService.FireStationResponse.ResidentInfo johnInfo = residents.stream()
                .filter(r -> "John".equals(r.getFirstName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("John not found in residents list"));

        assertEquals("Doe", johnInfo.getLastName());
        assertEquals("123 Main St", johnInfo.getAddress());
        assertEquals("123-456-7890", johnInfo.getPhone());
        //assertEquals("Adult", johnInfo.getAgeCategory());

        // Vérifications spécifiques pour Jane
        FireStationService.FireStationResponse.ResidentInfo janeInfo = residents.stream()
                .filter(r -> "Jane".equals(r.getFirstName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Jane not found in residents list"));

        assertEquals("Smith", janeInfo.getLastName());
        assertEquals("456 Oak St", janeInfo.getAddress());
        assertEquals("987-654-3210", janeInfo.getPhone());
        //assertEquals("Child", janeInfo.getAgeCategory());

        // Vérifier les interactions avec les mocks
        verify(fireStationRepository, times(1)).findAddressesByStationNumber(stationNumber);
        verify(personRepository, times(1)).findByAddresses(mockAddresses);
        verify(medicalRecordRepository, times(1)).findByFullName("John", "Doe");
        verify(medicalRecordRepository, times(1)).findByFullName("Jane", "Smith");
    }

    @Test
    void shouldReturnGetPhoneNumbersByStation() {
        // Arrange

        int stationNumber = Integer.parseInt(fireStationDTO1.getStation());
        //List<String> addresses = List.of("123 Main St", "456 Oak St");
        List<String> addresses = Collections.singletonList(fireStationDTO1.getAddress()); // Mocked addresses list
        when(fireStationRepository.findAddressesByStationNumber(stationNumber)).thenReturn(addresses);

        // Simulate retrieval of persons associated with the addresses
        List<Person> persons = List.of(person1, person2);
        when(personRepository.findByAddresses(addresses)).thenReturn(persons);

        // Simulate the conversion of Person entities to PersonDTO objects
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);

        // Act
        List<String> result = fireStationService.getPhoneNumbersByStation(stationNumber);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(personDTO1.getPhone()));
        assertTrue(result.contains(personDTO2.getPhone()));

        // Verify the interactions with the mocks
        verify(fireStationRepository, times(1)).findAddressesByStationNumber(stationNumber);
        verify(personRepository, times(1)).findByAddresses(addresses);
        verify(personConvertorDTO, times(1)).convertEntityToDto(person1);
        verify(personConvertorDTO, times(1)).convertEntityToDto(person2);
    }

}
