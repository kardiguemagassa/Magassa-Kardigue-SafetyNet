package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.exception.person.PersonNotFoundException;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.PersonRepository;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.openclassrooms.safetynet.constant.service.PersonImpConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class PersonServiceTest {

    private  final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Mock
    private PersonRepository personRepository;
    @Mock
    private PersonConvertorDTO personConvertorDTO;

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
        when(personRepository.getPersons()).thenReturn(Arrays.asList(person1, person2));
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);

        // Act
        List<PersonDTO> personDTOList = personService.getPersons();

        // Assert
        assertNotNull(personDTOList);


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

        PersonNotFoundException exception = assertThrows(PersonNotFoundException.class, () -> personService.getPersons());

        assertEquals("No persons found in the repository.", exception.getMessage());

        verify(personRepository,times(1)).getPersons();
        verifyNoInteractions(personConvertorDTO);
    }

    /*@Test
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
    }*/

    /*@Test
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
    }*/

    @Test
    void shouldReturnSave() {
        // Arrange
        PersonDTO personDTO = personDTO1;
        Person personEntity = person1;

        // Configurer les mocks
        when(personConvertorDTO.convertDtoToEntity(personDTO)).thenReturn(personEntity);
        when(personRepository.save(personEntity)).thenReturn(personEntity);
        when(personConvertorDTO.convertEntityToDto(personEntity)).thenReturn(personDTO);

        // Act
        PersonDTO result = personService.save(personDTO);

        // Assert
        assertNotNull(result);
        assertEquals(personDTO1.getFirstName(), result.getFirstName());
        assertEquals(personDTO1.getLastName(), result.getLastName());

        // Vérifier les interactions
        verify(personConvertorDTO,times(1)).convertDtoToEntity(personDTO);
        verify(personRepository,times(1)).save(personEntity);
        verify(personConvertorDTO,times(1)).convertEntityToDto(personEntity);
    }

    @Test
    void shouldReturnSave_ExceptionThrownByRepository() {

        // Test with null personDTO
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {personService.save(null);});
        assertEquals(PERSON_ERROR, exception.getMessage());

        PersonDTO personDTO = new PersonDTO();
        Person personEntity = new Person();

        when(personConvertorDTO.convertDtoToEntity(personDTO)).thenReturn(personEntity);
        when(personRepository.save(personEntity)).thenThrow(new PersonNotFoundException(PERSON_ERROR_SAVING_DATA_BASE));

        // Act & Assert
        PersonNotFoundException exception2 = assertThrows(PersonNotFoundException.class, () -> {personService.save(personDTO);});

        // Adjusted to check if the message contains the expected string
        assertTrue(exception2.getMessage().contains("System error while saving persons in the repository:"));

        // Verify the interactions
        verify(personConvertorDTO, times(1)).convertDtoToEntity(personDTO);
        verify(personRepository, times(1)).save(personEntity);
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
