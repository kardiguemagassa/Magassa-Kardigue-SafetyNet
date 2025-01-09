package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.MedicalRecordDTO;

import com.openclassrooms.safetynet.service.MedicalRecordService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MedicalRecordController.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//@TestMethodOrder(MethodOrderer.MethodName.class)
//@TestMethodOrder(MethodOrderer.DisplayName.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MedicalRecordControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordControllerTest.class);
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private MedicalRecordService medicalRecordService;
    private MedicalRecordDTO mockMedicalRecordDTO1;
    private MedicalRecordDTO mockMedicalRecordDTO2;

    @BeforeEach
    public void setUpBeforeEach() {

        mockMedicalRecordDTO1 = new MedicalRecordDTO("John", "Doe", "01/01/1990",
                List.of("aznol:350mg","hydrapermazol:100mg"), List.of("nillacilan"));

        mockMedicalRecordDTO2 = new MedicalRecordDTO("Jane", "Doe", "01/01/2000",
                List.of("aznol:350mg","hydrapermazol:100mg"), List.of("nillacilan"));

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
    void shouldReturnListOfMedicalRecords() throws Exception {

        // Mock data
        List<MedicalRecordDTO> mockMedicalRecordList = List.of(mockMedicalRecordDTO1, mockMedicalRecordDTO2);

        // Perform GET request
        when(medicalRecordService.getMedicalRecords()).thenReturn(mockMedicalRecordList);

        String response = mockMvc.perform(get("/medicalrecord"))
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
    @Order(2)
    void shouldReturnSaveAllMedicalRecord() throws Exception {

        String json = """
                [
                    {
                    "firstName": "John",
                    "lastName": "Doe",
                    "medications": ["Allergy", "Flu"],
                    "allergies": ["Allergy", "Flu"],
                    "birthdate": "1990-01-01"
                    },
                    {
                    "firstName": "Jane",
                    "lastName": "Doe",
                    "medications": ["Allergy", "Flu"],
                    "allergies": ["Allergy", "Flu"],
                    "birthdate": "01/01/2000"
                    }
                ]
                """;

        mockMedicalRecordDTO1 = new MedicalRecordDTO();
        mockMedicalRecordDTO1.setFirstName("John");
        mockMedicalRecordDTO1.setLastName("Doe");
        mockMedicalRecordDTO1.setMedications(List.of("Allergy", "Flu"));
        mockMedicalRecordDTO1.setAllergies(List.of("Allergy", "Flu"));
        mockMedicalRecordDTO1.setBirthdate("1990-01-01");

        mockMedicalRecordDTO2 = new MedicalRecordDTO();
        mockMedicalRecordDTO2.setFirstName("Jane");
        mockMedicalRecordDTO2.setLastName("Doe");
        mockMedicalRecordDTO2.setMedications(List.of("Allergy", "Flu"));
        mockMedicalRecordDTO2.setAllergies(List.of("Allergy", "Flu"));
        mockMedicalRecordDTO2.setBirthdate("01/01/2000");

        List<MedicalRecordDTO> savedMedicalRecords = List.of(mockMedicalRecordDTO1, mockMedicalRecordDTO2);

        // Mock data
        when(medicalRecordService.saveAll(anyList())).thenReturn(savedMedicalRecords);

        // Perform POST request
        String response = mockMvc.perform(post("/medicalrecord/saveAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value(mockMedicalRecordDTO1.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(mockMedicalRecordDTO1.getLastName()))
                .andExpect(jsonPath("$[0].birthdate").value(mockMedicalRecordDTO1.getBirthdate()))
                .andExpect(jsonPath("$[0].medications[0]").value(mockMedicalRecordDTO1.getMedications().get(0)))
                .andExpect(jsonPath("$[0].allergies[1]").value(mockMedicalRecordDTO1.getAllergies().get(1)))
                .andExpect(jsonPath("$[1].firstName").value(mockMedicalRecordDTO2.getFirstName()))
                .andExpect(jsonPath("$[1].lastName").value(mockMedicalRecordDTO2.getLastName()))
                .andExpect(jsonPath("$[1].birthdate").value(mockMedicalRecordDTO2.getBirthdate()))
                .andExpect(jsonPath("$[1].medications[0]").value(mockMedicalRecordDTO2.getMedications().get(0)))
                .andExpect(jsonPath("$[1].allergies[1]").value(mockMedicalRecordDTO2.getAllergies().get(1)))
                .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseSaveList: " + response);

        // Verify service interaction
        verify(medicalRecordService, times(1)).saveAll(anyList());
    }

    @Test
    @Order(3)
    void shouldReturnSaveMedicalRecord() throws Exception {

        String json = """
                {
                "firstName": "John",
                "lastName": "Doe",
                "medications": ["Allergy", "Flu"],
                "allergies": ["Allergy", "Flu"],
                "birthdate": "1990-01-01"
                }
                """;

        mockMedicalRecordDTO1 = new MedicalRecordDTO();
        mockMedicalRecordDTO1.setFirstName("John");
        mockMedicalRecordDTO1.setLastName("Doe");
        mockMedicalRecordDTO1.setMedications(List.of("Allergy", "Flu"));
        mockMedicalRecordDTO1.setAllergies(List.of("Allergy", "Flu"));
        mockMedicalRecordDTO1.setBirthdate("1990-01-01");

        // Mock data
        when(medicalRecordService.save(any(MedicalRecordDTO.class))).thenReturn(mockMedicalRecordDTO1);

        // Perform POST request
        String response = mockMvc.perform(post("/medicalrecord/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
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

        String json = """
                {
                "firstName": "John",
                "lastName": "Doe",
                "medications": ["Allergy", "Flu"],
                "allergies": ["Allergy", "Flu"],
                "birthdate": "1990-01-01"
                }
                """;

        mockMedicalRecordDTO1 = new MedicalRecordDTO();
        mockMedicalRecordDTO1.setFirstName("John");
        mockMedicalRecordDTO1.setLastName("Doe");
        mockMedicalRecordDTO1.setMedications(List.of("Allergy", "Flu"));
        mockMedicalRecordDTO1.setAllergies(List.of("Allergy", "Flu"));
        mockMedicalRecordDTO1.setBirthdate("1990-01-01");

        // Mock data
        when(medicalRecordService.update(any(MedicalRecordDTO.class))).thenReturn(Optional.of(mockMedicalRecordDTO1));

        // Perform POST request
        String responseUpdate = mockMvc.perform(put("/medicalrecord/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
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

        mockMedicalRecordDTO1 = new MedicalRecordDTO();
        mockMedicalRecordDTO1.setFirstName("John");
        mockMedicalRecordDTO1.setLastName("Doe");

        when(medicalRecordService.deleteByFullName(anyString(), anyString())).thenReturn(true);

        String response = mockMvc.perform(delete("/medicalrecord/delete")
                        .param("firstName", mockMedicalRecordDTO1.getFirstName())
                        .param("lastName",mockMedicalRecordDTO1.getLastName()))
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseOfDeletedMedicalRecord: " + response);

        verify(medicalRecordService, times(1)).deleteByFullName(mockMedicalRecordDTO1.getFirstName(),
                mockMedicalRecordDTO1.getLastName());
    }

}
