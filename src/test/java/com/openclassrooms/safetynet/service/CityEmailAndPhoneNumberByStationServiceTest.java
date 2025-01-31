package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.exception.residentInfo.EmailNotFoundException;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.FireStationDTO;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.FireStationRepository;
import com.openclassrooms.safetynet.repository.PersonRepository;

import java.util.Arrays;
import java.util.List;
import java.util.*;


@SpringBootTest
public class CityEmailAndPhoneNumberByStationServiceTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @InjectMocks
    private CityEmailAndPhoneNumberByStationService cityEmailAndPhoneNumberByStationService;

    @Mock
    private PersonRepository personRepository;
    @Mock
    private PersonConvertorDTO personConvertorDTO;
    @Mock
    private FireStationRepository fireStationRepository;

    private Person person1;
    private Person person2;
    private PersonDTO personDTO1;
    private PersonDTO personDTO2;
    private FireStationDTO fireStationDTO1;


    @BeforeEach
    void setUp() {

        LOGGER.info("Setting up test data...");

        fireStationDTO1 = FireStationDTO.builder()
                .address("149 Bd Pei ere 75007 Paris")
                .station("1")
                .build();

        // Person
        person1 = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .address("123 Main St")
                .city("Springfield")
                .zip("75016")
                .phone("0144445151")
                .email("johndoe@gmail.com")
                .build();
        person2 = Person.builder()
                .firstName("Jane")
                .lastName("Doe")
                .address("123 Main St")
                .city("Springfield")
                .zip("75017")
                .phone("0144445152")
                .email("janedoe@gmail.com")
                .build();
        // PersonDTO
        personDTO1 = PersonDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .address("123 Main St")
                .city("Springfield")
                .zip("75016")
                .phone("0144445151")
                .email("johndoe@gmail.com")
                .build();
        personDTO2 = PersonDTO.builder()
                .firstName("Jane")
                .lastName("Doe")
                .address("123 Main St")
                .city("Springfield")
                .zip("75017")
                .phone("0144445152")
                .email("janedoe@gmail.com")
                .build();
    }

    @Test
    void shouldReturnGetPhoneNumbersByStation() {

        LOGGER.info("Starting test: shouldReturnGetPhoneNumbersByStation");

        // Arrange
        int stationNumber = Integer.parseInt(fireStationDTO1.getStation());
        List<String> addresses = Collections.singletonList(fireStationDTO1.getAddress());
        LOGGER.debug("Mocking fireStationRepository to return addresses: {}", addresses);

        // retrieve the repository for addresses
        when(fireStationRepository.findAddressesByStationNumber(stationNumber)).thenReturn(addresses);

        //retrieve of people associated with addresses
        List<Person> persons = List.of(person1, person2);

        LOGGER.debug("Mocking personRepository to return persons: {}", persons);
        when(personRepository.findByAddresses(addresses)).thenReturn(persons);

        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);

        // Act
        List<String> result = cityEmailAndPhoneNumberByStationService.getPhoneNumbersByStation(stationNumber);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(personDTO1.getPhone()));
        assertTrue(result.contains(personDTO2.getPhone()));

        LOGGER.info("Test shouldReturnGetPhoneNumbersByStation passed. Retrieved phone numbers: {}", result);

        // Check interactions with mocks
        verify(fireStationRepository, times(1)).findAddressesByStationNumber(stationNumber);
        verify(personRepository, times(1)).findByAddresses(addresses);
        verify(personConvertorDTO, times(1)).convertEntityToDto(person1);
        verify(personConvertorDTO, times(1)).convertEntityToDto(person2);
    }

    @Test
    void shouldReturnGetCommunityEmails() throws EmailNotFoundException {

        LOGGER.info("Starting test: shouldReturnGetCommunityEmails");

        // Arrange
        String city = personDTO1.getCity();

        LOGGER.debug("Mocking personRepository to return persons for city: {}", city);

        when(personRepository.findByCity(city)).thenReturn(Arrays.asList(person1, person2));
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);

        // Act
        List<String> resultEmails = cityEmailAndPhoneNumberByStationService.getCommunityEmails(city);

        // Assert
        assertNotNull(resultEmails);
        assertEquals(2, resultEmails.size());
        assertTrue(resultEmails.contains(personDTO1.getEmail()));
        assertTrue(resultEmails.contains(personDTO2.getEmail()));

        LOGGER.info("Test shouldReturnGetCommunityEmails passed. Retrieved emails: {}", resultEmails);

        verify(personRepository, times(1)).findByCity(city);
        verify(personConvertorDTO, times(1)).convertEntityToDto(person1);
        verify(personConvertorDTO, times(1)).convertEntityToDto(person2);
    }

}
