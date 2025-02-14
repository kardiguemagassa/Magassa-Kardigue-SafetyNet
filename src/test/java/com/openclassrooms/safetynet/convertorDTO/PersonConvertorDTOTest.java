package com.openclassrooms.safetynet.convertorDTO;

import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PersonConvertorDTOTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonConvertorDTOTest.class);

    @InjectMocks
    private PersonConvertorDTO personConvertorDTO;

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

    @Test
    void shouldReturnConvertEntityToDto() {
        LOGGER.info("Start method: convertEntityToDto");
        PersonDTO personDTO = personConvertorDTO.convertEntityToDto(person1);

        // Then
        assertNotNull(personDTO);
        assertEquals(person1.getFirstName(), personDTO.getFirstName());
        assertEquals(person1.getLastName(), personDTO.getLastName());
        assertEquals(person1.getEmail(), personDTO.getEmail());
        LOGGER.info("End method: convertEntityToDto : Test passed");
    }

    @Test
    void shouldReturnConvertEntityToDtoNull () {
        LOGGER.info("Start method: convertEntityToDtoNull");
        assertNull(personConvertorDTO.convertEntityToDto((Person) null));
        LOGGER.info("End method: convertEntityToDtoNull : Test passed");
    }

    @Test
    void shouldReturnConvertDtoToEntity() {
        LOGGER.info("Start method: shouldReturnConvertDtoToEntity");
        Person person = personConvertorDTO.convertDtoToEntity(personDTO1);

        assertNotNull(person);
        assertEquals(person1.getFirstName(), person.getFirstName());
        assertEquals(person1.getLastName(), person.getLastName());
        assertEquals(person1.getEmail(), person.getEmail());
        LOGGER.info("End method: shouldReturnConvertDtoToEntity : Test passed");
    }

    @Test
    void shouldReturnConvertDtoToEntityNull() {
        LOGGER.info("Start method: shouldReturnConvertDtoToEntityNull");
        assertNull(personConvertorDTO.convertDtoToEntity((PersonDTO)null));
        LOGGER.info("End method: shouldReturnConvertDtoToEntityNull : Test passed");
    }

    @Test
    void shouldReturnConvertEntityToDtoList() {
        LOGGER.info("Start method: shouldReturnConvertEntityToDtoList");
        List<Person> persons = List.of(person1, person2);

        List<PersonDTO> personDTOs = personConvertorDTO.convertEntityToDto(persons);

        assertNotNull(personDTOs);
        assertEquals(2, personDTOs.size());
        assertEquals(person1.getFirstName(), personDTOs.get(0).getFirstName());
        assertEquals(person2.getFirstName(), personDTOs.get(1).getFirstName());
        LOGGER.info("End method: shouldReturnConvertEntityToDtoList : Test passed");
    }

    @Test
    void shouldReturnConvertEntityToDtoListIsEmpty() {
        LOGGER.info("Start method: shouldReturnConvertEntityToDtoListIsEmpty");
        assertTrue(personConvertorDTO.convertEntityToDto(List.of()).isEmpty());
        LOGGER.info("End method: shouldReturnConvertEntityToDtoListIsEmpty : Test passed");
    }

   @Test
    void shouldReturnConvertDtoToEntityList() {
        LOGGER.info("Start method: shouldReturnConvertDtoToEntityList");
        List<PersonDTO> personDTOs = List.of(personDTO1, personDTO2);
        List<Person> persons = personConvertorDTO.convertDtoToEntity(personDTOs);

        assertNotNull(persons);
        assertEquals(2, persons.size());
        assertEquals(personDTO1.getFirstName(), persons.get(0).getFirstName());
        assertEquals(personDTO2.getFirstName(), persons.get(1).getFirstName());
        LOGGER.info("End method: shouldReturnConvertDtoToEntityList : Test passed");
    }

    @Test
    void shouldReturnConvertDtoToEntityListIsEmpty() {
        LOGGER.info("Start method: shouldReturnConvertDtoToEntityListIsEmpty");
        assertTrue(personConvertorDTO.convertDtoToEntity(List.of()).isEmpty());
        LOGGER.info("End method: shouldReturnConvertDtoToEntityListIsEmpty : Test passed");
    }
}

