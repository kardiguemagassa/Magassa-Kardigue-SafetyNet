package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
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
    }

    @Test
    public void getPersons_shouldReturnListOfPersons() throws Exception {
        // Mock data
        List<PersonDTO> mockPersonList = Arrays.asList(mockPersonDTO);
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
    void deletePerson_shouldReturnSuccess() throws Exception {
        // Arrange
        String firstName = "John";
        String lastName = "Boyd";

        // Simule que la méthode retourne true
        when(personService.deleteByFullName(firstName, lastName)).thenReturn(true);

        // Act
        mockMvc.perform(delete("/person")
                        .param("firstName", firstName)
                        .param("lastName", lastName))
                .andExpect(status().isOk());

        // Assert
        verify(personService).deleteByFullName(firstName, lastName);
    }



}
