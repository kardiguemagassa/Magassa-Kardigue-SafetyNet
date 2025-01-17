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

    private  final Logger LOGGER = LoggerFactory.getLogger(getClass());

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
}
