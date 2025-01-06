package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.service.FireStationService;
import com.openclassrooms.safetynet.service.MedicalRecordService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;


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
    private MedicalRecordDTO mockMedicalRecordDTO;

    @BeforeEach
    public void setUpBeforeEach() {
        mockMedicalRecordDTO = new MedicalRecordDTO();
        mockMedicalRecordDTO.setFirstName("John");
        mockMedicalRecordDTO.setLastName("Doe");
        List<String> medications = List.of("Allergy", "Flu");
        List<String> allergies = List.of("Allergy", "Flu");
        mockMedicalRecordDTO.setMedications(medications);
        mockMedicalRecordDTO.setAllergies(allergies);
        mockMedicalRecordDTO.setBirthdate("1990-01-01");
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


}
