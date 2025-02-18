package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.exception.PersonNotFoundException;
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

import static com.openclassrooms.safetynet.constant.PersonConstant.*;
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
    void shouldReturnGetPersons() throws PersonNotFoundException {

        LOGGER.info("Start method :shouldReturnGetPersons");

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

        LOGGER.info("End shouldReturnGetPersons : completed successfully");
    }

    @Test
    void shouldReturnGetPersonsNotFound() {

        LOGGER.info("Start method: shouldReturnGetPersonsNotFound");

        // Arrange
        when(personRepository.getPersons()).thenReturn(null);

        // Act & Assert
        PersonNotFoundException exception = assertThrows(PersonNotFoundException.class, () -> personService.getPersons());

        assertEquals("No persons found in the repository.", exception.getMessage());

        verify(personRepository,times(1)).getPersons();
        verifyNoInteractions(personConvertorDTO);

        LOGGER.info("End method : shouldReturnGetPersonsNotFound completed successfully");
    }


    @Test
    void shouldReturnSave() {

        LOGGER.info("Start method : shouldReturnSave");

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

        // Vérifier
        verify(personConvertorDTO,times(1)).convertDtoToEntity(personDTO);
        verify(personRepository,times(1)).save(personEntity);
        verify(personConvertorDTO,times(1)).convertEntityToDto(personEntity);

        LOGGER.info("End shouldReturnSave : completed successfully");
    }

    @Test
    void shouldReturnSaveException() {

        LOGGER.info("Start method: shouldReturnSaveException");

        // Test with null personDTO
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> personService.save(null));

        assertEquals(PERSON_ERROR, exception.getMessage());

        PersonDTO personDTO = new PersonDTO();
        Person personEntity = new Person();

        when(personConvertorDTO.convertDtoToEntity(personDTO)).thenReturn(personEntity);

        when(personRepository.save(personEntity)).thenThrow(new IllegalArgumentException(PERSON_ERROR_SAVING_DATA_BASE));

        // Act & Assert
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> personService.save(personDTO));

        assertTrue(exception2.getMessage().contains(SYSTEM_ERROR));

        // Verify the interactions
        verify(personConvertorDTO, times(1)).convertDtoToEntity(personDTO);
        verify(personRepository, times(1)).save(personEntity);

        LOGGER.info("End method shouldReturnSaveException :  completed successfully");
    }


    @Test
    void shouldReturnUpdate() {

        LOGGER.info("Start method: shouldReturnUpdate");

        // Arrange
        PersonDTO personDTO = personDTO1;
        Person personEntities = person1;

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

        LOGGER.info("End method shouldReturnUpdate :  completed successfully");
    }

    @Test
    void shouldReturnUpdateException() {

        LOGGER.info("Start method: shouldReturnUpdateException");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> personService.update(null));
        assertEquals(PERSON_ERROR_UPDATING, exception.getMessage());
        LOGGER.info("Test shouldReturnSave_ExceptionThrownByRepository completed successfully");
    }

    @Test
    void shouldReturnUpdatePersonNotFound() {

        LOGGER.info("Start method: shouldReturnUpdatePersonNotFound");
        // Given
        PersonDTO updatedPersonDTO = new PersonDTO();
        Person personEntity = new Person();

        when(personConvertorDTO.convertDtoToEntity(updatedPersonDTO)).thenReturn(personEntity);
        when(personRepository.update(personEntity)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> personService.update(updatedPersonDTO));

        assertEquals(PERSON_NOT_FOUND_UPDATING, exception.getMessage());
        LOGGER.info("End method : shouldReturnUpdatePersonNotFound completed successfully");
    }

    @Test
    void shouldReturnDeleteByFullName() {

        LOGGER.info("Start method: shouldReturnDeleteByFullName");

        when(personRepository.deleteByFullName(personDTO1.getFirstName(), personDTO1.getLastName())).thenReturn(true);
        Boolean result = personService.deleteByFullName(personDTO1.getFirstName(), personDTO1.getLastName());

        // Assert
        assertTrue(result);

        verify(personRepository, times(1)).deleteByFullName(personDTO1.getFirstName(), personDTO1.getLastName());
        LOGGER.info("Test shouldReturnDeleteByFullName completed successfully");
    }

    @Test
    void shouldReturnDeleteByFullNameException() {

        LOGGER.info("Start method: shouldReturnDeleteByFullNameException");

        // Given
        when(personRepository.deleteByFullName(person1.getFirstName(), person1.getLastName())).thenReturn(false);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> personService.deleteByFullName(person1.getFirstName(), person1.getLastName()));

        assertEquals(PERSON_ERROR_DELETING_NOT_FOUND, exception.getMessage());
        LOGGER.info("End method : shouldReturnDeleteByFullNameException completed successfully");
    }

    @Test
    void shouldReturnDeleteByFullNameNull() {

        LOGGER.info("Start method: shouldReturnDeleteByFullNameNull");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                personService.deleteByFullName(null, null));

        assertEquals(PERSON_ERROR_DELETING, exception.getMessage());
        LOGGER.info("End method : shouldReturnDeleteByFullNameNull completed successfully");

    }

}
