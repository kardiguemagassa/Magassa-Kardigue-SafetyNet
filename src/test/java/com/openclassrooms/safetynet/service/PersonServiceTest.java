package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.exception.person.PersonNotFoundException;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;
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

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
//@SpringBootTest
public class PersonServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceTest.class);

    @Mock
    private PersonRepository personRepository;
    @Mock
    private PersonConvertorDTO personConvertorDTO;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private PersonService personService;

    private Person person1;
    private Person person2;
    private PersonDTO personDTO1;
    private PersonDTO personDTO2;

    @BeforeEach
    void setUp() {
        person1 = new Person("John", "Doe", "123 Main St", "Springfield", "75016",
                "0144445151", "johndoe@gmail.com");
        person2 = new Person("Jane", "Doe", "123 Main St", "Springfield", "75017",
                "0144445152", "janedoe@gmail.com");

        personDTO1 = new PersonDTO("John", "Doe", "johndoe@gmail.com", "123 Main St", "Springfield",
                "75016", "0144445151");
        personDTO2 = new PersonDTO("Jane", "Doe", "janedoe@gmail.com", "123 Main St",
                "Springfield", "75017", "0144445152");
    }

    // CRUD
    @Test
    void shouldReturnGetPersons() throws PersonNotFoundException { //getPersons
        // Arrange
        // Call personRepository and you will get back to me the list with these 2 people.
        when(personRepository.getPersons()).thenReturn(Arrays.asList(person1, person2));
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);

        // Act
        List<PersonDTO> personDTOList = personService.getPersons();

        // Assert
        assertNotNull(personDTOList);

        // The operation or method tester returns a list containing 2 elements
        //assertThat(personDTOList).containsExactly(personDTO1, personDTO2);
        assertEquals(2, personDTOList.size());
        assertEquals(personDTO1.getFirstName(),personDTOList.get(0).getFirstName());
        assertEquals(personDTO2.getFirstName(),personDTOList.get(1).getFirstName());

        verify(personRepository,times(1)).getPersons();
        verify(personConvertorDTO,times(1)).convertEntityToDto(person1);
        verify(personConvertorDTO,times(1)).convertEntityToDto(person2);
    }

    @Test
    void shouldReturnGetPersons_NotFound() {
        // Arrange
        when(personRepository.getPersons()).thenReturn(null);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> personService.getPersons());
        assertEquals("404 NOT_FOUND \"No persons found.\"", exception.getMessage());

        verify(personRepository,times(1)).getPersons();
        verifyNoInteractions(personConvertorDTO);
    }

    @Test
    void shouldReturnSaveAll() {
        // Arrange
        List<PersonDTO> personDTOList = Arrays.asList(personDTO1, personDTO2);
        List<Person> personEntities = Arrays.asList(person1, person2);

        // Configurer les mocks
        when(personConvertorDTO.convertDtoToEntity(personDTOList)).thenReturn(personEntities);
        when(personRepository.saveAll(personEntities)).thenReturn(personEntities);
        when(personConvertorDTO.convertEntityToDto(personEntities)).thenReturn(personDTOList);

        // Act
        List<PersonDTO> result = personService.saveAll(personDTOList);

        assertNotNull(personDTOList);
        assertEquals(2, result.size());
        assertEquals(personDTO1.getFirstName(), result.get(0).getFirstName());
        assertEquals(personDTO1.getLastName(), result.get(0).getLastName());
        assertEquals(personDTO2.getFirstName(), result.get(1).getFirstName());
        assertEquals(personDTO2.getLastName(), result.get(1).getLastName());

        // Vérifier les interactions avec les mocks
        verify(personConvertorDTO, times(1)).convertDtoToEntity(personDTOList);
        verify(personRepository,times(1)).saveAll(personEntities);
        verify(personConvertorDTO,times(1)).convertEntityToDto(personEntities);
    }

    @Test
    void shouldReturnSaveAll_NullOrEmptyList() {
        // Test with list null
        ResponseStatusException exception1 = assertThrows(ResponseStatusException.class, () -> {
            personService.saveAll(null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception1.getStatusCode());
        assertTrue(Objects.requireNonNull(exception1.getReason()).contains("Person list cannot be null or empty."));

        // Test with list empty
        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class, () -> {
            personService.saveAll(Collections.emptyList());
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception2.getStatusCode());
        assertTrue(Objects.requireNonNull(exception2.getReason()).contains("Person list cannot be null or empty."));
    }

    @Test
    void shouldReturnSaveAll_ExceptionThrownByRepository() {
        // Arrange
        List<PersonDTO> personDTOList = Arrays.asList(personDTO1, personDTO2);
        List<Person> personEntities = Arrays.asList(person1, person2);

        // Configurer les mocks pour déclencher une exception
        when(personConvertorDTO.convertDtoToEntity(personDTOList)).thenReturn(personEntities);
        when(personRepository.saveAll(personEntities)).thenThrow(new RuntimeException("Something went wrong"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> personService.saveAll(personDTOList));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(Objects.requireNonNull(exception.getReason()).contains("Database/system error while saving persons:"));

        // Vérifier les interactions
        verify(personConvertorDTO,times(1)).convertDtoToEntity(personDTOList);
        verify(personRepository,times(1)).saveAll(personEntities);
    }

    @Test
    void shouldReturnSave() {
        // Arrange
        PersonDTO personDTO = personDTO1;
        Person personEntities = person1;

        // Configurer les mocks
        when(personConvertorDTO.convertDtoToEntity(personDTO)).thenReturn(personEntities);
        when(personRepository.save(personEntities)).thenReturn(personEntities);
        when(personConvertorDTO.convertEntityToDto(personEntities)).thenReturn(personDTO);

        // Act
        PersonDTO result = personService.save(personDTO);

        // Assert
        assertNotNull(result);
        assertEquals(personDTO1.getFirstName(), result.getFirstName());
        assertEquals(personDTO1.getLastName(), result.getLastName());

        // Vérifier les interactions
        verify(personConvertorDTO,times(1)).convertDtoToEntity(personDTO);
        verify(personRepository,times(1)).save(personEntities);
        verify(personConvertorDTO,times(1)).convertEntityToDto(personEntities);
    }

    @Test
    void shouldReturnUpdate() {
        // Arrange
        PersonDTO personDTO = personDTO1;
        Person personEntities = person1;

        // Configurer les mocks
        when(personConvertorDTO.convertDtoToEntity(personDTO)).thenReturn(personEntities);
        when(personRepository.update(personEntities)).thenReturn(Optional.of(personEntities));
        when(personConvertorDTO.convertEntityToDto(personEntities)).thenReturn(personDTO);

        // Act
        Optional<PersonDTO> result = personService.update(personDTO);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(personDTO1.getFirstName(), result.get().getFirstName());
        assertEquals(personDTO1.getLastName(), result.get().getLastName());

        // Vérifier les interactions
        verify(personConvertorDTO, times(1)).convertDtoToEntity(personDTO);
        verify(personRepository, times(1)).update(personEntities);
        verify(personConvertorDTO, times(1)).convertEntityToDto(personEntities);
    }

    @Test
    void shouldReturnDeleteByFullName() {
        // Arrange
        when(personRepository.deleteByFullName(personDTO1.getFirstName(), personDTO1.getLastName())).thenReturn(true);

        // Act
        Boolean result = personService.deleteByFullName(personDTO1.getFirstName(), personDTO1.getLastName());

        // Assert
        assertTrue(result);

        // Vérifier que la méthode du repository a été appelée
        verify(personRepository, times(1)).deleteByFullName(personDTO1.getFirstName(), personDTO1.getLastName());
    }

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
        List<Map<String, PersonDTO>> result = personService.getChildrenByAddress(address);

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
        List<Map<String, Object>> personInfoList = personService.getPersonInfo(lastName);

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
}
