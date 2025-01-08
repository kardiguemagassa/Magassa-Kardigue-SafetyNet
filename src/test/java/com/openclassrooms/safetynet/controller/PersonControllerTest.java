package com.openclassrooms.safetynet.controller;

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
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonControllerTest.class);
    @Autowired
    private MockMvc mockMvc; // make requests on the controller
    @MockitoBean // Inject personService
    private PersonService personService;

    private PersonDTO personDTO1;
    private PersonDTO personDTO2;

    @BeforeEach
    void setUp() {
        personDTO1 = new PersonDTO("John", "Doe", "johndoe@gmail.com", "123 Main St",
                "Springfield", "75016", "0144445151");

        personDTO2 = new PersonDTO("Jane", "Doe", "janedoe@gmail.com", "123 Main St",
                "Springfield", "75016", "0144445151");
        LOGGER.info("@BeforeEach executes before the execution of every test method in this class");
    }

    @AfterEach
    public void tearDownAfterEach() {
        LOGGER.info("Running @AfterEach");
        System.out.println();
    }

    @BeforeAll
    static void setUpBeforeClass() {
        LOGGER.info("@BeforeAll executes only once before all test methods execute in this class");
        System.out.println();
    }

    @AfterAll
    static void tearDownAfterAll() {
        LOGGER.info("@AfterAll executes only once after all test methods execute in this class");
        System.out.println();
    }

    @Test
    @Order(1)
    void shouldReturnGetPersons() throws Exception {

        when(personService.getPersons()).thenReturn(List.of(personDTO1,personDTO2));
        // Perform GET request
        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"));

        // Verify service interaction
        verify(personService, times(1)).getPersons();
    }

    @Test
    @Order(2)
    void shouldReturnSaveAll() throws Exception {
        // Mock data
        // JSON request to send
        String json = """
        [
            {
                "firstName": "John",
                "lastName": "Doe",
                "address": "123 Main St",
                "email": "johndoe@gmail.com"
            },
            {
                "firstName": "Jane",
                "lastName": "Doe",
                "address": "123 Main St",
                "email": "janedoe@gmail.com"
            }
        ]
        """;
        // Prepare PersonDTO that mocks will use
        personDTO1 = new PersonDTO();
        personDTO1.setFirstName("John");
        personDTO1.setLastName("Doe");
        personDTO1.setAddress("123 Main St");
        personDTO1.setEmail("johndoe@gmail.com");

        personDTO2 = new PersonDTO();
        personDTO2.setFirstName("Jane");
        personDTO2.setLastName("Doe");
        personDTO2.setAddress("123 Main St");
        personDTO2.setEmail("janedoe@gmail.com");

        List<PersonDTO> savedPersons = List.of(personDTO1, personDTO2);

        // Mock the service to expect and return the PersonDTO list
        when(personService.saveAll(anyList())).thenReturn(savedPersons);

        // Perform POST with serialized JSON
        String response = mockMvc.perform(post("/person/saveAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].firstName").value(personDTO1.getFirstName()))
                        .andExpect(jsonPath("$[0].lastName").value(personDTO1.getLastName()))
                        .andExpect(jsonPath("$[0].address").value(personDTO1.getAddress()))
                        .andExpect(jsonPath("$[0].email").value(personDTO1.getEmail()))
                        .andExpect(jsonPath("$[1].firstName").value(personDTO2.getFirstName()))
                        .andExpect(jsonPath("$[1].lastName").value(personDTO2.getLastName()))
                        .andExpect(jsonPath("$[1].address").value(personDTO2.getAddress()))
                        .andExpect(jsonPath("$[1].email").value(personDTO2.getEmail()))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseSaveList: " + response);

        // Verify service interaction
        verify(personService, times(1)).saveAll(anyList());
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

        personDTO1 = new PersonDTO();
        personDTO1.setFirstName("John");
        personDTO1.setLastName("Doe");
        personDTO1.setAddress("123 Main St");
        personDTO1.setEmail("johndoe@gmail.com");

        // Mock data
        when(personService.save(any(PersonDTO.class))).thenReturn(personDTO1);

        // Perform POST request
        String response = mockMvc.perform(post("/person/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.firstName").value(personDTO1.getFirstName()))
                        .andExpect(jsonPath("$.lastName").value(personDTO1.getLastName()))
                        .andExpect(jsonPath("$.address").value(personDTO1.getAddress()))
                        .andExpect(jsonPath("$.email").value(personDTO1.getEmail()))
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

        personDTO1 = new PersonDTO();
        personDTO1.setFirstName("John");
        personDTO1.setLastName("Doe");
        personDTO1.setAddress("123 Main St");
        personDTO1.setEmail("johndoe@gmail.com");
        personDTO1.setCity("Springfield");
        personDTO1.setPhone("0144445151");
        personDTO1.setZip("75016");

        // Mocking service
        when(personService.update(any(PersonDTO.class))).thenReturn(Optional.of(personDTO1));

        // Perform PUT request
        String responseUpdate = mockMvc.perform(put("/person/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.firstName").value(personDTO1.getFirstName()))
                        .andExpect(jsonPath("$.lastName").value(personDTO1.getLastName()))
                        .andExpect(jsonPath("$.address").value(personDTO1.getAddress()))
                        .andExpect(jsonPath("$.email").value(personDTO1.getEmail()))
                        .andExpect(jsonPath("$.city").value(personDTO1.getCity()))
                        .andExpect(jsonPath("$.phone").value(personDTO1.getPhone()))
                        .andExpect(jsonPath("$.zip").value(personDTO1.getZip()))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseUpdate: " + responseUpdate);

        // Verify service interaction
        verify(personService, times(1)).update(any(PersonDTO.class));
    }

    @Test
    @Order(5)
    void shouldReturnDeleteByFullName() throws Exception {
        // Arrange
        /*
        String firstName = "John";
        String lastName = "Boyd";
        personDTO1 = new PersonDTO();
        personDTO1.setFirstName("John");
        personDTO1.setLastName("Doe");

         */
        when(personService.deleteByFullName(personDTO1.getFirstName(), personDTO1.getLastName())).thenReturn(true);

        // Act
        mockMvc.perform(delete("/person/delete")
                        .param("firstName", personDTO1.getFirstName())
                        .param("lastName", personDTO1.getLastName()))
                .andExpect(status().isOk());

        // Assert
        verify(personService, times(1)).deleteByFullName(personDTO1.getFirstName(), personDTO1.getLastName());
    }
}
