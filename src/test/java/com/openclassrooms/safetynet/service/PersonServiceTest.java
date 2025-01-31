package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.exception.person.PersonNotFoundException;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.PersonRepository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
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
        LOGGER.info("Arranging the test: mocking repository and DTO conversion");
        when(personRepository.getPersons()).thenReturn(Arrays.asList(person1, person2));
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);

        // Act
        LOGGER.info("Acting: calling the service to get the list of persons");
        List<PersonDTO> personDTOList = personService.getPersons();

        // Assert
        assertNotNull(personDTOList);
        assertEquals(2, personDTOList.size());

        LOGGER.info("Verifying individual persons in the list");
        assertEquals(personDTO1.getFirstName(),personDTOList.get(0).getFirstName());
        assertEquals(personDTO2.getFirstName(),personDTOList.get(1).getFirstName());

        LOGGER.info("Verifying repository and DTO conversion method calls");
        verify(personRepository,times(1)).getPersons();
        verify(personConvertorDTO,times(1)).convertEntityToDto(person1);
        verify(personConvertorDTO,times(1)).convertEntityToDto(person2);

        LOGGER.info("Test shouldReturnGetPersons completed successfully");
    }

    @Test
    void shouldReturnGetPersons_NotFound() {

        // Arrange
        LOGGER.info("Arranging the test: mocking person repository to return null");
        when(personRepository.getPersons()).thenReturn(null);

        // Act & Assert

        LOGGER.info("Acting: calling the service to get the list of persons, expecting an exception");
        PersonNotFoundException exception = assertThrows(PersonNotFoundException.class, () -> personService.getPersons());

        LOGGER.info("Asserting the exception message");
        assertEquals("No persons found in the repository.", exception.getMessage());

        LOGGER.info("Verifying interactions: checking that the repository was called once");
        verify(personRepository,times(1)).getPersons();
        LOGGER.info("Verifying no interactions with the person converter DTO");
        verifyNoInteractions(personConvertorDTO);

        LOGGER.info("Test shouldReturnGetPersons_NotFound completed successfully");
    }

    @Test
    void shouldReturnSave() {

        // Arrange
        LOGGER.info("Arranging the test: preparing mock data for PersonDTO and Person entity");
        PersonDTO personDTO = personDTO1;
        Person personEntity = person1;

        // Configurer les mocks
        LOGGER.info("Mocking convertDtoToEntity to return the Person entity when called");
        when(personConvertorDTO.convertDtoToEntity(personDTO)).thenReturn(personEntity);
        LOGGER.info("Mocking save method of the repository to return the Person entity");
        when(personRepository.save(personEntity)).thenReturn(personEntity);
        LOGGER.info("Mocking convertEntityToDto to return the PersonDTO after saving");
        when(personConvertorDTO.convertEntityToDto(personEntity)).thenReturn(personDTO);

        // Act
        LOGGER.info("Acting: calling personService.save(personDTO) to save the person");
        PersonDTO result = personService.save(personDTO);

        // Assert
        LOGGER.info("Asserting the result: checking if the returned PersonDTO is not null");
        assertNotNull(result);
        assertEquals(personDTO1.getFirstName(), result.getFirstName());
        assertEquals(personDTO1.getLastName(), result.getLastName());

        // Vérifier les interactions
        LOGGER.info("Verifying interactions: checking that convertDtoToEntity was called once");
        verify(personConvertorDTO,times(1)).convertDtoToEntity(personDTO);
        verify(personRepository,times(1)).save(personEntity);
        verify(personConvertorDTO,times(1)).convertEntityToDto(personEntity);

        LOGGER.info("Test shouldReturnSave completed successfully");
    }

    @Test
    void shouldReturnSave_ExceptionThrownByRepository() {

        // Test with null personDTO
        LOGGER.info("Testing: Null PersonDTO passed to save method.");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {personService.save(null);});
        LOGGER.info("Asserting the exception message for null PersonDTO.");
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
        LOGGER.info("Verifying that convertDtoToEntity was called once.");
        verify(personConvertorDTO, times(1)).convertDtoToEntity(personDTO);
        verify(personRepository, times(1)).save(personEntity);

        LOGGER.info("Test shouldReturnSave_ExceptionThrownByRepository completed successfully");
    }


    @Test
    void shouldReturnUpdate() {

        // Arrange
        PersonDTO personDTO = personDTO1;
        Person personEntities = person1;

        // Configurer les mocks
        LOGGER.info("Preparing the mock data for the update test. PersonDTO: {}, PersonEntity: {}", personDTO, personEntities);
        when(personConvertorDTO.convertDtoToEntity(personDTO)).thenReturn(personEntities);
        when(personRepository.update(personEntities)).thenReturn(Optional.of(personEntities));
        when(personConvertorDTO.convertEntityToDto(personEntities)).thenReturn(personDTO);

        // Act
        LOGGER.info("Calling personService.update() with the mock PersonDTO.");
        Optional<PersonDTO> result = personService.update(personDTO);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(personDTO1.getFirstName(), result.get().getFirstName());
        assertEquals(personDTO1.getLastName(), result.get().getLastName());

        // Vérifier les interactions
        LOGGER.info("Verifying that update method was called once on the repository.");
        verify(personConvertorDTO, times(1)).convertDtoToEntity(personDTO);
        verify(personRepository, times(1)).update(personEntities);
        verify(personConvertorDTO, times(1)).convertEntityToDto(personEntities);

        LOGGER.info("Test shouldReturnUpdate completed successfully");
    }

    @Test
    void shouldReturnDeleteByFullName() {

        // Arrange
        LOGGER.info("Preparing the mock data for the deleteByFullName test. Deleting person with first name: {} and last name: {}."
                , personDTO1.getFirstName(), personDTO1.getLastName());
        when(personRepository.deleteByFullName(personDTO1.getFirstName(), personDTO1.getLastName())).thenReturn(true);

        // Act
        LOGGER.info("Calling personService.deleteByFullName() with the mock person full name.");
        Boolean result = personService.deleteByFullName(personDTO1.getFirstName(), personDTO1.getLastName());

        // Assert
        assertTrue(result);

        // Vérifier que la méthode du repository a été appelée
        LOGGER.info("Verifying that deleteByFullName method was called once on the repository.");
        verify(personRepository, times(1)).deleteByFullName(personDTO1.getFirstName(), personDTO1.getLastName());

        LOGGER.info("Test shouldReturnDeleteByFullName completed successfully");
    }
}
