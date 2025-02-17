package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.MedicalRecordDTO;

import com.openclassrooms.safetynet.service.MedicalRecordService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MedicalRecordController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MedicalRecordControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordControllerTest.class);
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private MedicalRecordService medicalRecordService;

    private MedicalRecordDTO mockMedicalRecordDTO1;
    private MedicalRecordDTO mockMedicalRecordDTO2;
    private String medicalRecordJson;

    @BeforeEach
    public void setUpBeforeEach() {

        mockMedicalRecordDTO1 = new MedicalRecordDTO("John", "Doe", "01/01/1990",
                List.of("aznol:350mg","hydrapermazol:100mg"), List.of("nillacilan"));

        mockMedicalRecordDTO2 = new MedicalRecordDTO("Jane", "Doe", "01/01/2000",
                List.of("aznol:350mg","hydrapermazol:100mg"), List.of("nillacilan"));

        medicalRecordJson = """
                {
                "firstName": "Alain",
                "lastName": "Smith",
                "medications": ["Allergy", "Flu"],
                "allergies": ["Allergy", "Flu"],
                "birthdate": "1990-01-01"
                }
                """;
    }


    @Test
    @Order(1)
    void shouldReturnListOfMedicalRecords() throws Exception {

        // Mock data
        List<MedicalRecordDTO> mockMedicalRecordList = List.of(mockMedicalRecordDTO1, mockMedicalRecordDTO2);

        // Perform GET request
        when(medicalRecordService.getMedicalRecords()).thenReturn(mockMedicalRecordList);

        String response = mockMvc.perform(get("/medicalRecord"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstName").value(mockMedicalRecordDTO1.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(mockMedicalRecordDTO1.getLastName()))
                .andExpect(jsonPath("$[0].medications[0]").value(mockMedicalRecordDTO1.getMedications().get(0)))
                .andExpect(jsonPath("$[0].medications[1]").value(mockMedicalRecordDTO1.getMedications().get(1)))
                .andExpect(jsonPath("$[1].firstName").value(mockMedicalRecordDTO2.getFirstName()))
                .andExpect(jsonPath("$[1].lastName").value(mockMedicalRecordDTO2.getLastName()))
                .andExpect(jsonPath("$[1].medications[0]").value(mockMedicalRecordDTO2.getMedications().get(0)))
                .andExpect(jsonPath("$[1].medications[1]").value(mockMedicalRecordDTO2.getMedications().get(1)))
                .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseOfAllMedicalRecords: " + response);

        // Verify service interaction
        verify(medicalRecordService, times(1)).getMedicalRecords();
    }

    @Test
    @Order(3)
    void shouldReturnSaveMedicalRecord() throws Exception {

        mockMedicalRecordDTO1.setMedications(List.of("Allergy", "Flu"));
        mockMedicalRecordDTO1.setAllergies(List.of("Allergy", "Flu"));

        // Mock data
        when(medicalRecordService.save(any(MedicalRecordDTO.class))).thenReturn(mockMedicalRecordDTO1);

        // Perform POST request
        String response = mockMvc.perform(post("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(medicalRecordJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(mockMedicalRecordDTO1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(mockMedicalRecordDTO1.getLastName()))
                .andExpect(jsonPath("$.birthdate").value(mockMedicalRecordDTO1.getBirthdate()))
                .andExpect(jsonPath("$.medications[0]").value(mockMedicalRecordDTO1.getMedications().get(0)))
                .andExpect(jsonPath("$.allergies[1]").value(mockMedicalRecordDTO1.getAllergies().get(1)))
                .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseOfSavedMedicalRecord: " + response);

        // Verify service interaction
        verify(medicalRecordService, times(1)).save(any(MedicalRecordDTO.class));
    }

    @Test
    @Order(4)
    void shouldReturnUpdateMedicalRecord() throws Exception {

        mockMedicalRecordDTO1.setMedications(List.of("Allergy", "Flu"));
        mockMedicalRecordDTO1.setAllergies(List.of("Allergy", "Flu"));

        // Mock data
        when(medicalRecordService.update(any(MedicalRecordDTO.class))).thenReturn(Optional.of(mockMedicalRecordDTO1));

        // Perform POST request
        String responseUpdate = mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medicalRecordJson))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.firstName").value(mockMedicalRecordDTO1.getFirstName()))
                        .andExpect(jsonPath("$.lastName").value(mockMedicalRecordDTO1.getLastName()))
                        .andExpect(jsonPath("$.birthdate").value(mockMedicalRecordDTO1.getBirthdate()))
                        .andExpect(jsonPath("$.medications[0]").value(mockMedicalRecordDTO1.getMedications().get(0)))
                        .andExpect(jsonPath("$.allergies[1]").value(mockMedicalRecordDTO1.getAllergies().get(1)))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseOfUpdatedMedicalRecord: " + responseUpdate);

        // Verify service interaction
        verify(medicalRecordService, times(1)).update(any(MedicalRecordDTO.class));
    }

    @Test
    @Order(5)
    void shouldReturnDeleteByFullName() throws Exception {

        when(medicalRecordService.deleteByFullName(anyString(), anyString())).thenReturn(true);

        String response = mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", mockMedicalRecordDTO1.getFirstName())
                        .param("lastName",mockMedicalRecordDTO1.getLastName()))
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent())
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseOfDeletedMedicalRecord: " + response);

        verify(medicalRecordService, times(1)).deleteByFullName(mockMedicalRecordDTO1.getFirstName(),
                mockMedicalRecordDTO1.getLastName());
    }

}
