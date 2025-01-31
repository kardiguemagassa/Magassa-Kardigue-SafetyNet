package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.*;

import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.FireStationRepository;
import com.openclassrooms.safetynet.repository.PersonRepository;
import com.openclassrooms.safetynet.service.PersonInfoService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PersonInfoController.class)
@ExtendWith(MockitoExtension.class)
public class PersonInfoControllerTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonInfoService cityEmailAndPhoneNumberByStationService;

    private Person person1;
    private Person person2;
    private PersonDTO personDTO1;
    private PersonDTO personDTO2;

    private FireStationDTO fireStationDTO1;
    private FireStationDTO fireStationDTO2;


    @BeforeEach
    void setUp() {
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

        // FireStationDTO
        fireStationDTO1 = FireStationDTO.builder()
                .address("149 Bd Pei ere 75007 Paris")
                .station("1")
                .build();
    }


    @Test
    void shouldReturnGetPhoneNumbersByStation_MockMvc() throws Exception {

        // Arrange
        int stationNumber = Integer.parseInt(fireStationDTO1.getStation());

        // retrieve phone numbers
        List<String> mockPhoneNumbers = List.of(person1.getPhone(), person2.getPhone());
        when(cityEmailAndPhoneNumberByStationService.getPhoneNumbersByStation(stationNumber)).thenReturn(mockPhoneNumbers);

        // Act & Assert
        String response = mockMvc.perform(get("/phoneAlert")
                        .param("firestation", String.valueOf(stationNumber))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(2))
                        .andExpect(jsonPath("$[0]").value(person1.getPhone()))
                        .andExpect(jsonPath("$[1]").value(person2.getPhone()))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseOfAllPhoneNumbers: {}", response);

        // Verify service interaction
        verify(cityEmailAndPhoneNumberByStationService, times(1)).getPhoneNumbersByStation(stationNumber);
    }

    @Test
    void shouldReturnGetCommunityEmails() throws Exception {

        // Arrange
        String city = personDTO1.getCity();

        List<String> mockEmails = List.of(personDTO1.getEmail(), personDTO2.getEmail());

        when(cityEmailAndPhoneNumberByStationService.getCommunityEmails(city)).thenReturn(mockEmails);

        // Act & Assert
        String response = mockMvc.perform(get("/communityEmail")
                        .param("city", city)  //Query parameter
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(2)) // Check that there are 2 emails in the reply
                        .andExpect(jsonPath("$[0]").value(personDTO1.getEmail()))
                        .andExpect(jsonPath("$[1]").value(personDTO2.getEmail()))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseOfAllCommunityEmails: {}", response);
        verify(cityEmailAndPhoneNumberByStationService, times(1)).getCommunityEmails(city);
    }

}
