package com.openclassrooms.safetynet.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.dto.PersonDTO;

import com.openclassrooms.safetynet.service.PersonService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PersonController.class)
@ExtendWith(MockitoExtension.class)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonControllerTest.class);

    @Autowired
    private MockMvc mockMvc; // make requests on the controller

    @MockitoBean // Inject personService
    private PersonService personService;

    private PersonDTO mockPersonDTO1;
    private PersonDTO mockPersonDTO2;

    @BeforeEach
    void setUp() {
        mockPersonDTO1 = new PersonDTO("John", "Doe", "johndoe@gmail.com", "123 Main St",
                "Springfield", "75016", "0144445151");

        mockPersonDTO2 = new PersonDTO("Jane", "Doe", "janedoe@gmail.com", "123 Main St",
                "Springfield", "75016", "0144445151");
        LOGGER.info("@BeforeEach executes before the execution of every test method in this class");
    }

    @Test
    @Order(1)
    void shouldReturnGetPersons() throws Exception {

        when(personService.getPersons()).thenReturn(List.of(mockPersonDTO1,mockPersonDTO2));
        // Perform GET request
        String response = mockMvc.perform(get("/person"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstName").value(mockPersonDTO1.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(mockPersonDTO1.getLastName()))
                .andExpect(jsonPath("$[1].firstName").value(mockPersonDTO2.getFirstName()))
                .andExpect(jsonPath("$[1].lastName").value(mockPersonDTO2.getLastName()))
                .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseOfAllPersons: " + response);

        // Verify service interaction
        verify(personService, times(1)).getPersons();
    }

    @Test
    @Order(3)
    void shouldReturnSave() throws Exception {

        String json = """
                {
                "firstName": "John",
                "lastName": "Doe",
                "address": "123 Main St",
                "email": "johndoe@gmail.com"
                }
                """;

        mockPersonDTO1 = new PersonDTO();
        mockPersonDTO1.setFirstName("John");
        mockPersonDTO1.setLastName("Doe");
        mockPersonDTO1.setAddress("123 Main St");
        mockPersonDTO1.setEmail("johndoe@gmail.com");

        // Mock data
        when(personService.save(any(PersonDTO.class))).thenReturn(mockPersonDTO1);

        // Perform POST request
        String response = mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.firstName").value(mockPersonDTO1.getFirstName()))
                        .andExpect(jsonPath("$.lastName").value(mockPersonDTO1.getLastName()))
                        .andExpect(jsonPath("$.address").value(mockPersonDTO1.getAddress()))
                        .andExpect(jsonPath("$.email").value(mockPersonDTO1.getEmail()))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("Response: " + response);

        // Verify service interaction
        verify(personService, times(1)).save(any(PersonDTO.class));
    }

    @Test
    @Order(4)
    void shouldReturnUpdate() throws Exception {
        // Mock data
        String json = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "address": "123 Main St",
                "email": "johndoe@gmail.com",
                "city": "Springfield",
                "phone": "0144445151",
                "zip": "75016"
            }
            """;

        mockPersonDTO1 = new PersonDTO();
        mockPersonDTO1.setFirstName("John");
        mockPersonDTO1.setLastName("Doe");
        mockPersonDTO1.setAddress("123 Main St");
        mockPersonDTO1.setEmail("johndoe@gmail.com");
        mockPersonDTO1.setCity("Springfield");
        mockPersonDTO1.setPhone("0144445151");
        mockPersonDTO1.setZip("75016");

        // Mocking service
        when(personService.update(any(PersonDTO.class))).thenReturn(Optional.of(mockPersonDTO1));

        // Perform PUT request
        String responseUpdate = mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.firstName").value(mockPersonDTO1.getFirstName()))
                        .andExpect(jsonPath("$.lastName").value(mockPersonDTO1.getLastName()))
                        .andExpect(jsonPath("$.address").value(mockPersonDTO1.getAddress()))
                        .andExpect(jsonPath("$.email").value(mockPersonDTO1.getEmail()))
                        .andExpect(jsonPath("$.city").value(mockPersonDTO1.getCity()))
                        .andExpect(jsonPath("$.phone").value(mockPersonDTO1.getPhone()))
                        .andExpect(jsonPath("$.zip").value(mockPersonDTO1.getZip()))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseUpdate: " + responseUpdate);

        // Verify service interaction
        verify(personService, times(1)).update(any(PersonDTO.class));
    }

    @Test
    @Order(5)
    void shouldReturnDeleteByFullName() throws Exception {
        // Arrange
        // Ensure mock data setup
        mockPersonDTO1 = new PersonDTO();
        mockPersonDTO1.setFirstName("John");
        mockPersonDTO1.setLastName("Doe");

        when(personService.deleteByFullName(mockPersonDTO1.getFirstName(), mockPersonDTO1.getLastName())).thenReturn(true);

        // Act
        String response = mockMvc.perform(delete("/person")
                        .param("firstName", mockPersonDTO1.getFirstName())
                        .param("lastName", mockPersonDTO1.getLastName()))
                        .andExpect(status().isNoContent())
                        .andExpect(jsonPath("$.httpStatusCode").value(204)) // HTTP STATUS CODE
                        .andExpect(jsonPath("$.httpStatus").value("NO_CONTENT")) // HTTP STATUS TEXT
                        //.andExpect(jsonPath("$.message").value("USER DELETED SUCCESSFULLY")) // OR USE equalToIgnoringCase
                        .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.equalToIgnoringCase("User deleted successfully"))) //Case
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseDelete: " + response);

        // Parse JSON response for validation
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response);

        // Assert
        verify(personService, times(1)).deleteByFullName(mockPersonDTO1.getFirstName(), mockPersonDTO1.getLastName());
        //Assertions.assertEquals("true", response, "The delete response should be 'true' as returned by the service.");
        Assertions.assertEquals("NO_CONTENT", jsonResponse.get("httpStatus").asText(), "Expected HTTP status NO_CONTENT in the response.");
    }
}
