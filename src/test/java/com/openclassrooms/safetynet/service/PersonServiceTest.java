package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.controller.MedicalRecordControllerTest;
import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceTest.class);

    @Mock
    private PersonRepository personRepository;
    @Mock
    private PersonConvertorDTO personConvertorDTO;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private MedicalRecordConvertorDTO medicalRecordConvertorDTO;

    @InjectMocks
    private PersonService personService;

    private Person person1;
    private Person person2;
    private PersonDTO personDTO1;
    private PersonDTO personDTO2;

    private MedicalRecord medicalRecord1;
    private MedicalRecord medicalRecord2;
    private MedicalRecordDTO medicalRecordDTO1;
    private MedicalRecordDTO medicalRecordDTO2;

    @BeforeEach
    void setUp() {
        person1 = new Person("John", "Doe", "123 Main St", "Springfield", "12345",
                "123-456-7890", "johndoe@email.com");
        person2 = new Person("Jane", "Doe", "123 Main St", "Springfield", "12345",
                "123-456-7890", "janedoe@email.com");

        personDTO1 = new PersonDTO("John", "Doe", "123 Main St", "Springfield",
                "12345", "123-456-7890", "johndoe@email.com");
        personDTO2 = new PersonDTO("Jane", "Doe", "123 Main St", "Springfield",
                "12345", "123-456-7890", "janedoe@email.com");
    }

    // CRUD

    @Test
    void testGetAllPersons_Success() {
        // Arrange
        when(personRepository.getPersons()).thenReturn(Arrays.asList(person1, person2));
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);

        // Act
        List<PersonDTO> personDTOList = personService.getPersons();

        // Assert
        assertNotNull(personDTOList);

        // The operation or method tester returns a list containing 2 elements
        assertEquals(2, personDTOList.size());
        assertEquals("John", personDTOList.get(0).getFirstName());
        assertEquals("Jane", personDTOList.get(1).getFirstName());

        verify(personRepository,times(1)).getPersons();
        verify(personConvertorDTO,times(1)).convertEntityToDto(person1);
        verify(personConvertorDTO,times(1)).convertEntityToDto(person2);
    }


    @Test
    void testGetPersons_NotFound() {
        // Arrange
        when(personRepository.getPersons()).thenReturn(null);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> personService.getPersons());
        assertEquals("404 NOT_FOUND \"No persons found.\"", exception.getMessage());

        verify(personRepository,times(1)).getPersons();
        verifyNoInteractions(personConvertorDTO);
    }

    @Test
    void testSaveAll_Success() {
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
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(0).getLastName());
        assertEquals("Jane", result.get(1).getFirstName());
        assertEquals("Doe", result.get(1).getLastName());

        // Vérifier les interactions avec les mocks
        verify(personConvertorDTO, times(1)).convertDtoToEntity(personDTOList);
        verify(personRepository,times(1)).saveAll(personEntities);
        verify(personConvertorDTO,times(1)).convertEntityToDto(personEntities);

    }

    @Test
    public  void testSaveAll_NullOrEmptyList() {
        // Tester avec une liste null
        ResponseStatusException exception1 = assertThrows(ResponseStatusException.class, () -> {
            personService.saveAll(null);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception1.getStatusCode());
        assertTrue(exception1.getReason().contains("Person list cannot be null or empty."));

        // Tester avec une liste vide
        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class, () -> {
            personService.saveAll(Collections.emptyList());
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception2.getStatusCode());
        assertTrue(exception2.getReason().contains("Person list cannot be null or empty."));
    }

    @Test
    void testSaveAll_ExceptionThrownByRepository() {
        // Arrange
        List<PersonDTO> personDTOList = Arrays.asList(personDTO1, personDTO2);
        List<Person> personEntities = Arrays.asList(person1, person2);

        // Configurer les mocks pour déclencher une exception
        when(personConvertorDTO.convertDtoToEntity(personDTOList)).thenReturn(personEntities);
        when(personRepository.saveAll(personEntities)).thenThrow(new RuntimeException("Something went wrong"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> personService.saveAll(personDTOList));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("An error occurred while saving persons."));

        // Vérifier les interactions
        verify(personConvertorDTO,times(1)).convertDtoToEntity(personDTOList);
        verify(personRepository,times(1)).saveAll(personEntities);
    }

    @Test
    void testSave_Success() {
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
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());

        // Vérifier les interactions
        verify(personConvertorDTO,times(1)).convertDtoToEntity(personDTO);
        verify(personRepository,times(1)).save(personEntities);
        verify(personConvertorDTO,times(1)).convertEntityToDto(personEntities);

    }

    @Test
    void testUpdatePerson_Success() {
        // Arrange
        PersonDTO personDTO = personDTO1; // Le DTO à mettre à jour
        Person personEntities = person1;  // L'entité correspondant à ce DTO

        // Configurer les mocks
        when(personConvertorDTO.convertDtoToEntity(personDTO)).thenReturn(personEntities);
        when(personRepository.update(personEntities)).thenReturn(Optional.of(personEntities));
        when(personConvertorDTO.convertEntityToDto(personEntities)).thenReturn(personDTO);

        // Act
        Optional<PersonDTO> result = personService.update(personDTO);

        // Assert
        assertTrue(result.isPresent()); // Vérifier que le résultat n'est pas vide
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());

        // Vérifier les interactions
        verify(personConvertorDTO, times(1)).convertDtoToEntity(personDTO);
        verify(personRepository, times(1)).update(personEntities);
        verify(personConvertorDTO, times(1)).convertEntityToDto(personEntities);
    }

    @Test
    void testDelete_Success() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";

        when(personRepository.deleteByFullName(firstName, lastName)).thenReturn(true);

        // Act
        Boolean result = personService.deleteByFullName(firstName, lastName);

        // Assert
        assertTrue(result);

        // Vérifier que la méthode du repository a été appelée
        verify(personRepository, times(1)).deleteByFullName(firstName, lastName);
    }

    // NEW ENDPOINT
    @Test
    void getChildrenByAddress_Success() {
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
    public void getPersonInfo_Success() {
        // Arrange
        String lastName = "Doe";

        // Création de la personne et du DTO
        //Person person = new Person("John", lastName, "john.doe@example.com", "123 Main St", "Springfield", "12345", "123-456-7890");
        //PersonDTO personDTO = new PersonDTO("John", lastName, "john.doe@example.com", "123 Main St", "Springfield", "12345", "123-456-7890");

        // Arrange
        Person person = person1;  // L'entité correspondant à ce DTO
        PersonDTO personDTO = personDTO1; // Le DTO à mettre à jour


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
    public void getCommunityEmail_Success() {
        // Arrange
        String city = "Springfield";
        List<Person> personEntities = List.of(person1, person2);
        List<PersonDTO> personDTOList = List.of(personDTO1, personDTO2);

        // Configurer les mocks
        when(personRepository.findByCity(city)).thenReturn(personEntities);
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);

        // Act
        List<String> emails = personService.getCommunityEmails(city);

        // Assert
        assertNotNull(emails);
        assertEquals(2, emails.size());
        assertTrue(emails.contains("john.doe@example.com"));
        assertTrue(emails.contains("jane.doe@example.com"));

        // Vérifier les interactions
        verify(personRepository, times(1)).findByCity(city);
        verify(personConvertorDTO, times(1)).convertEntityToDto(person1);
        verify(personConvertorDTO, times(1)).convertEntityToDto(person2);
    }


}
