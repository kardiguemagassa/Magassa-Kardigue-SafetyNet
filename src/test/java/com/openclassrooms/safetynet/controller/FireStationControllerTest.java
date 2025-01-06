package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.FireStationDTO;
import com.openclassrooms.safetynet.service.FireStationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FireStationController.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FireStationControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FireStationControllerTest.class);

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private FireStationService fireStationService;
    private FireStationDTO mockFireStationDTO;

    @BeforeEach
    public void setUp() {
        mockFireStationDTO = new FireStationDTO();
        mockFireStationDTO.setStation("Station");
        mockFireStationDTO.setAddress("Address");
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
