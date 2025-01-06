package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.service.PersonService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

import java.util.Arrays;
import java.util.List;

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
    private MockMvc mockMvc;
    @MockitoBean
    //@InjectMocks
    private PersonService personService;
    private PersonDTO mockPersonDTO;

    @BeforeEach
    void setUp() {
        mockPersonDTO = new PersonDTO();
        mockPersonDTO.setFirstName("John");
        mockPersonDTO.setLastName("Doe");
        mockPersonDTO.setEmail("john.doe@example.com");
        mockPersonDTO.setAddress("123 Main St");
        mockPersonDTO.setCity("San Francisco");
        mockPersonDTO.setZip("94200");
        mockPersonDTO.setPhone("1234567890");
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
    public void getPersons_shouldReturnListOfPersons() throws Exception {
        // Mock data
        List<PersonDTO> mockPersonList = List.of(mockPersonDTO);
        when(personService.getPersons()).thenReturn(mockPersonList);

        // Perform GET request
        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));

        // Verify service interaction
        verify(personService, times(1)).getPersons();
    }

    @Test
    @Order(2)
    public void savePerson_shouldSaveAndReturnPerson() throws Exception {
        // Mock data
        when(personService.save(any(PersonDTO.class))).thenReturn(mockPersonDTO);

        // Perform POST request
        mockMvc.perform(post("/person/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "firstName": "John",
                            "lastName": "Doe",
                            "address": "123 Main St",
                            "email": "johndoe@example.com"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        // Verify service interaction
        verify(personService, times(1)).save(any(PersonDTO.class));
    }

    @Test
    @Order(3)
    void deletePerson_shouldReturnSuccess() throws Exception {
        // Arrange
        String firstName = "John";
        String lastName = "Boyd";

        // Simule que la méthode retourne true
        when(personService.deleteByFullName(firstName, lastName)).thenReturn(true);

        // Act
        mockMvc.perform(delete("/person/delete")
                        .param("firstName", firstName)
                        .param("lastName", lastName))
                .andExpect(status().isOk());

        // Assert
        verify(personService, times(1)).deleteByFullName(firstName, lastName);

    }
}
