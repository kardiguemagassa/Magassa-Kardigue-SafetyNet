package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.FireStationConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.FireStationDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.exception.person.PersonNotFoundException;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ApiServiceTest {

    private  final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Mock
    private PersonRepository personRepository;
    @Mock
    private PersonConvertorDTO personConvertorDTO;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private PersonService personService;
    @InjectMocks
    private FireStationService fireStationService;
    @InjectMocks
    private ApiService apiService;

    private Person person1;
    private Person person2;
    private PersonDTO personDTO1;
    private PersonDTO personDTO2;

    @Mock
    private FireStationRepository fireStationRepository;
    @Mock
    private MedicalRecordConvertorDTO medicalRecordConvertorDTO;
    @Mock
    private FireStationConvertorDTO fireStationConvertorDTO;

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


/*
    // NEW ENDPOINT
    @Test
    void shouldReturnGetChildrenByAddress() {
        // Arrange
        String address = "123 Main St";

        List<Person> personEntities = List.of(person1, person2); // add 2 residents
        List<PersonDTO> personDTOList = List.of(personDTO1, personDTO2);

        MedicalRecord medicalRecordJohn = new MedicalRecord("John", "Doe", "01/01/2015", null, null);
        MedicalRecord medicalRecordJane = new MedicalRecord("Jane", "Doe", "01/01/1990", null, null);

        when(personRepository.findByAddress(address)).thenReturn(personEntities);
        when(personConvertorDTO.convertEntityToDto(personEntities.get(0))).thenReturn(personDTOList.get(0));
        when(personConvertorDTO.convertEntityToDto(personEntities.get(1))).thenReturn(personDTOList.get(1));

        when(medicalRecordRepository.findByFullName("John", "Doe")).thenReturn(medicalRecordJohn);
        when(medicalRecordRepository.findByFullName("Jane", "Doe")).thenReturn(medicalRecordJane);
        when(personRepository.calculateAge(LocalDate.of(2015, 1, 1))).thenReturn(8); // children
        when(personRepository.calculateAge(LocalDate.of(1990, 1, 1))).thenReturn(33); // adult

        // Act
        List<Map<String, PersonDTO>> result = apiService.getChildrenByAddress(address);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size()); // 1 founded child
        assertEquals("John", result.get(0).get("child").getFirstName());
        assertEquals("Jane", result.get(0).get("householdMember").getFirstName());

        // Vérifier les appels aux mocks
        verify(personRepository, times(1)).findByAddress(address);
        verify(medicalRecordRepository, times(1)).findByFullName("John", "Doe");
        verify(medicalRecordRepository, times(1)).findByFullName("Jane", "Doe");
    }

    @Test
    void shouldReturnGetPersonInfo() {
        // Arrange
        String lastName = "Doe";

        // Arrange
        Person person = person1;
        PersonDTO personDTO = personDTO1;

        // Simulation de la méthode findByLastName
        when(personRepository.findByLastName(lastName)).thenReturn(List.of(person));
        when(personConvertorDTO.convertEntityToDto(person)).thenReturn(personDTO);

        // Simulation de la méthode findByFullName pour obtenir les informations médicales
        MedicalRecord medicalRecord = new MedicalRecord("John", lastName, "01/01/1980", List.of("Med1", "Med2"), List.of("Allergy1"));
        when(medicalRecordRepository.findByFullName(personDTO.getFirstName(), personDTO.getLastName())).thenReturn(medicalRecord);

        // Simulation du calcul de l'âge
        when(personRepository.calculateAge(LocalDate.parse("01/01/1980", DateTimeFormatter.ofPattern("MM/dd/yyyy")))).thenReturn(44);

        // Act
        List<Map<String, Object>> personInfoList = apiService.getPersonInfo(lastName);

        // Assert
        assertNotNull(personInfoList);
        assertEquals(1, personInfoList.size(), "La taille de la liste doit être de 1.");

        Map<String, Object> personInfo = personInfoList.get(0);
        assertNotNull(personInfo.get("person"));
        assertTrue(personInfo.containsKey("age"));
        assertEquals(44, personInfo.get("age"));
        assertTrue(personInfo.containsKey("medications"));
        assertTrue(personInfo.containsKey("allergies"));
    }

    @Test
    void shouldReturnGetCommunityEmail() throws PersonNotFoundException {
        // Arrange
        // Call personRepository and you will get back to me the list with these 2 people.
        when(personRepository.getPersons()).thenReturn(Arrays.asList(person1, person2));
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);

        // Act
        List<PersonDTO> personDTOList = personService.getPersons();

        // Assert
        assertNotNull(personDTOList);

        //assertThat(personDTOList).containsExactly(personDTO1, personDTO2);
        assertEquals(2, personDTOList.size());
        assertEquals(personDTO1.getEmail(),personDTOList.get(0).getEmail());
        assertEquals(personDTO2.getEmail(),personDTOList.get(1).getEmail());

        verify(personRepository,times(1)).getPersons();
        verify(personConvertorDTO,times(1)).convertEntityToDto(person1);
        verify(personConvertorDTO,times(1)).convertEntityToDto(person2);
    }

 */

/*
    // NEW ENDPOINT
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
        Map<String, ?> result = apiService.getPersonsByStation(stationNumber);

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
        ApiService.FireStationResponse response = apiService.getPersonsByStationA(stationNumber);

        // Assertions sur la réponse
        assertNotNull(response, "The response should not be null");
        assertEquals(1, response.getAdultCount(), "There should be 1 adult");
        assertEquals(1, response.getChildCount(), "There should be 1 child");

        List<ApiService.FireStationResponse.ResidentInfo> residents = response.getResidents();
        assertNotNull(residents, "The residents list should not be null");
        assertEquals(2, residents.size(), "There should be 2 residents");

        // Vérifications spécifiques pour John
        ApiService.FireStationResponse.ResidentInfo johnInfo = residents.stream()
                .filter(r -> "John".equals(r.getFirstName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("John not found in residents list"));

        assertEquals("Doe", johnInfo.getLastName());
        assertEquals("123 Main St", johnInfo.getAddress());
        assertEquals("123-456-7890", johnInfo.getPhone());
        //assertEquals("Adult", johnInfo.getAgeCategory());

        // Vérifications spécifiques pour Jane
        ApiService.FireStationResponse.ResidentInfo janeInfo = residents.stream()
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

     */

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
        List<String> result = apiService.getPhoneNumbersByStation(stationNumber);

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
