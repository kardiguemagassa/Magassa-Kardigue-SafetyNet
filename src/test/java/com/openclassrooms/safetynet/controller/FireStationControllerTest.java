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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private FireStationDTO mockFireStationDTO1, mockFireStationDTO2;

    @BeforeEach
    public void setUp() {
        mockFireStationDTO1 = new FireStationDTO("149 Bd Pei ere 75007 Paris", "1");
        mockFireStationDTO2 = new FireStationDTO("150 Bd Pei ere 75007 Paris", "2");

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
    void shouldReturnListOfFireStations() throws Exception {
        when(fireStationService.getFireStations()).thenReturn(List.of(mockFireStationDTO1, mockFireStationDTO2));

        // Perform GET request
        String response = mockMvc.perform(get("/firestation"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].address").value(mockFireStationDTO1.getAddress()))
                .andExpect(jsonPath("$[0].station").value(mockFireStationDTO1.getStation()))
                .andExpect(jsonPath("$[1].address").value(mockFireStationDTO2.getAddress()))
                .andExpect(jsonPath("$[1].station").value(mockFireStationDTO2.getStation()))
                .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseOfAllFireStations: " + response);

        //Verify service interaction
        verify(fireStationService, times(1)).getFireStations();
    }

    @Test
    void shouldReturnSaveAll() throws Exception {

        String json = """
                [
                    {
                    "address": "149 Bd Pei ere 75007 Paris",
                    "station": "1"
                    },
                    {
                    "address": "150 Bd Pei ere 75007 Paris",
                    "station": "2"
                    }
                ]
                """;
        mockFireStationDTO1 = new FireStationDTO();
        mockFireStationDTO1.setAddress("149 Bd Pei ere 75007 Paris");
        mockFireStationDTO1.setStation("1");

        mockFireStationDTO2 = new FireStationDTO();
        mockFireStationDTO2.setAddress("150 Bd Pei ere 75007 Paris");
        mockFireStationDTO2.setStation("2");

        List<FireStationDTO> saveFireStations = List.of(mockFireStationDTO1,mockFireStationDTO2);

        when(fireStationService.saveAll(anyList())).thenReturn(saveFireStations);

        String response = mockMvc.perform(post("/firestation/saveAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].address").value(mockFireStationDTO1.getAddress()))
                        .andExpect(jsonPath("$[0].station").value(mockFireStationDTO1.getStation()))
                        .andExpect(jsonPath("$[1].address").value(mockFireStationDTO2.getAddress()))
                        .andExpect(jsonPath("$[1].station").value(mockFireStationDTO2.getStation()))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseSaveList: " + response);
    }

    @Test
    void shouldReturnSave() throws Exception {

        String json = """
                {
                "address": "149 Bd Pei ere 75007 Paris",
                "station": "1"
                }
                """;

        mockFireStationDTO1 = new FireStationDTO();
        mockFireStationDTO1.setAddress("149 Bd Pei ere 75007 Paris");
        mockFireStationDTO1.setStation("1");

        when(fireStationService.save(any(FireStationDTO.class))).thenReturn(mockFireStationDTO1);
        String response = mockMvc.perform(post("/firestation/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.address").value(mockFireStationDTO1.getAddress()))
                        .andExpect(jsonPath("$.station").value(mockFireStationDTO1.getStation()))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseSave: " + response);
        verify(fireStationService, times(1)).save(any(FireStationDTO.class));
        verifyNoMoreInteractions(fireStationService);
    }

    @Test
    void shouldReturnUpdate() throws Exception {

        String json = """
                {
                "address": "149 Bd Pei ere 75007 Paris",
                "station": "1"
                }
                """;

        mockFireStationDTO1 = new FireStationDTO();
        mockFireStationDTO1.setAddress("149 Bd Pei ere 75007 Paris");
        mockFireStationDTO1.setStation("1");

        when(fireStationService.update(any(FireStationDTO.class))).thenReturn(Optional.of(mockFireStationDTO1));

        String response = mockMvc.perform(put("/firestation/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.address").value(mockFireStationDTO1.getAddress()))
                        .andExpect(jsonPath("$.station").value(mockFireStationDTO1.getStation()))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseUpdate: " + response);

        verify(fireStationService, times(1)).update(any(FireStationDTO.class));
        verifyNoMoreInteractions(fireStationService);
    }

    @Test
    void shouldReturnDeleteFireStation() throws Exception {

        mockFireStationDTO1 = new FireStationDTO();

        mockFireStationDTO1.setAddress("149 Bd Pei ere 75007 Paris");

        when(fireStationService.deleteByAddress(mockFireStationDTO1.getAddress())).thenReturn(true);

        String response = mockMvc.perform(delete("/firestation/delete")
                        .param("address", mockFireStationDTO1.getAddress()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("ResponseDelete: " + response);

    }
}
