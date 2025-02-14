package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.*;

import com.openclassrooms.safetynet.service.ResidentInfoService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ResidentInfoController.class)
@ExtendWith(MockitoExtension.class)
public class ResidentInfoControllerTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResidentInfoService residentInfoService;

    private ResidentInfoDTO residentInfoDTO1;
    private ResidentInfoDTO residentInfoDTO2;

    @BeforeEach
    void setUp() {

        LocalDate birthDate1 = LocalDate.parse("01/01/1990", DATE_TIME_FORMATTER);
        LocalDate birthDate2 = LocalDate.parse("01/01/2000", DATE_TIME_FORMATTER);

        int age1 = Period.between(birthDate1, LocalDate.now()).getYears();
        int age2 = Period.between(birthDate2, LocalDate.now()).getYears();

        residentInfoDTO1 = ResidentInfoDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .address("123 Main St")
                .phone("0144445151")
                .age(age1)
                .email("johndoe@gmail.com")
                .medications(List.of("aznol:350mg", "hydrapermazol:100mg"))
                .allergies(List.of("nillacilan"))
                .stationNumber(1)
                .build();
        residentInfoDTO2 = ResidentInfoDTO.builder()
                .firstName("Jane")
                .lastName("Doe")
                .address("123 Main St")
                .phone("0144445152")
                .age(age2)
                .email("janedoe@gmail.com")
                .medications(List.of("aznol:350mg", "hydrapermazol:100mg"))
                .allergies(List.of("nillacilan"))
                .stationNumber(2)
                .build();
    }

    @Test
    void shouldReturnGetPersonsByStation_MockMvc() throws Exception {

        LOGGER.info("Start test - shouldReturnGetPersonsByStation_MockMvc");

        LocalDate birthDate1 = LocalDate.parse("01/01/2000", DATE_TIME_FORMATTER);
        LocalDate birthDate2 = LocalDate.parse("01/01/2010", DATE_TIME_FORMATTER);

        int age1 = Period.between(birthDate1, LocalDate.now()).getYears();
        int age2 = Period.between(birthDate2, LocalDate.now()).getYears();

        // Arrange
        int stationNumber = (residentInfoDTO1.getStationNumber());
        LOGGER.info("Station Number: {}", stationNumber);

        // retrieve just what I need, avoid calling null fields
        residentInfoDTO1 = new ResidentInfoDTO();
        residentInfoDTO1.setFirstName("John");
        residentInfoDTO1.setLastName("Doe");
        residentInfoDTO1.setAddress("123 Main St");
        residentInfoDTO1.setPhone("0144445151");
        residentInfoDTO1.setAge(age1);

        residentInfoDTO2 = new ResidentInfoDTO();
        residentInfoDTO2.setFirstName("Jane");
        residentInfoDTO2.setLastName("Doe");
        residentInfoDTO2.setAddress("123 Main St");
        residentInfoDTO2.setPhone("0144445152");
        residentInfoDTO2.setAge(age2);

        // Mock enriched residents
        List<ResidentInfoDTO> mockResidents = List.of(residentInfoDTO1, residentInfoDTO2);
        LOGGER.info("Residents mocked : {}", mockResidents);

        // Calculation of adults and children
        int adultCount = (int) mockResidents.stream().filter(resident -> resident.getAge() > 18).count();
        int childCount = mockResidents.size() - adultCount;
        LOGGER.info("Number of adults: {}, Number of children: {}", adultCount, childCount);

        // Mock the expected response
        FireStationResponseDTO responseDTO = new FireStationResponseDTO(adultCount, childCount, mockResidents);
        LOGGER.info("Mocked response: {}", responseDTO);

        // Simulate service
        when(residentInfoService.getPersonsByStation(stationNumber)).thenReturn(responseDTO);
        LOGGER.info("Service successfully mocked");

        // Act & Assert
        LOGGER.info("Sending GET request to /fire station/address Number with station Number={}", stationNumber);

        String response =  mockMvc.perform(get("/firestation/addressNumber")
                        .param("stationNumber", String.valueOf(stationNumber))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.adultCount").value(adultCount))
                        .andExpect(jsonPath("$.childCount").value(childCount))
                        .andExpect(jsonPath("$.residents.length()").value(2))

                        .andExpect(jsonPath("$.residents[0].firstName").value(residentInfoDTO1.getFirstName()))
                        .andExpect(jsonPath("$.residents[0].lastName").value(residentInfoDTO1.getLastName()))
                        .andExpect(jsonPath("$.residents[0].address").value(residentInfoDTO1.getAddress()))
                        .andExpect(jsonPath("$.residents[0].phone").value(residentInfoDTO1.getPhone()))
                        .andExpect(jsonPath("$.residents[0].age").value(residentInfoDTO1.getAge()))

                        .andExpect(jsonPath("$.residents[1].firstName").value(residentInfoDTO2.getFirstName()))
                        .andExpect(jsonPath("$.residents[1].lastName").value(residentInfoDTO2.getLastName()))
                        .andExpect(jsonPath("$.residents[1].address").value(residentInfoDTO2.getAddress()))
                        .andExpect(jsonPath("$.residents[1].phone").value(residentInfoDTO2.getPhone()))
                        .andExpect(jsonPath("$.residents[1].age").value(residentInfoDTO2.getAge()))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("shouldReturnGetPersonsByStation_MockMvc ==> {}", response);

        verify(residentInfoService,times(1)).getPersonsByStation(stationNumber);
    }

    @Test
    void shouldReturnChildrenByAddress_MockMvc() throws Exception {

        // Arrange
        String address = residentInfoDTO1.getAddress();
        LOGGER.info("Test shouldReturnChildrenByAddress_MockMvc - Address used: {}", address);

        // Création des enfants
        residentInfoDTO1 = new ResidentInfoDTO();
        residentInfoDTO1.setFirstName("John");
        residentInfoDTO1.setLastName("Doe");
        residentInfoDTO1.setAge(20);

        residentInfoDTO2 = new ResidentInfoDTO();
        residentInfoDTO2.setFirstName("Jane");
        residentInfoDTO2.setLastName("Doe");
        residentInfoDTO2.setAge(15);
        // À REVOIR POUR L'ENVOIE DE LISTE VIDE S'IL YA PAS D'ENFANT

        List<ResidentInfoDTO> mockChildren = List.of(residentInfoDTO1, residentInfoDTO2);
        LOGGER.info("Mock of created children: {}", mockChildren);

        when(residentInfoService.getChildrenByAddress(address)).thenReturn(mockChildren);
        LOGGER.info("Mocked service to return children to address: {}", address);

        // Act & Assert
        LOGGER.info("Sending GET request to /childAlert with address: {}", address);
        String response = mockMvc.perform(get("/childAlert")
                        .param("address", address)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(2))

                        .andExpect(jsonPath("$[0].firstName").value(residentInfoDTO1.getFirstName()))
                        .andExpect(jsonPath("$[0].lastName").value(residentInfoDTO1.getLastName()))
                        .andExpect(jsonPath("$[0].age").value(residentInfoDTO1.getAge()))

                        .andExpect(jsonPath("$[1].firstName").value(residentInfoDTO2.getFirstName()))
                        .andExpect(jsonPath("$[1].lastName").value(residentInfoDTO2.getLastName()))
                        .andExpect(jsonPath("$[1].age").value(residentInfoDTO2.getAge()))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("Test shouldReturnChildrenByAddress_MockMvc passed successfully ==> {}", response);

        verify(residentInfoService,times(1)).getChildrenByAddress(address);
    }

    @Test
    void shouldReturnResidentsByAddress_MockMvc() throws Exception {

        // Arrange
        String address = residentInfoDTO1.getAddress();
        LOGGER.info("Test shouldReturnResidentsByAddress_MockMvc - Address used: {}", address);

        residentInfoDTO1 = new ResidentInfoDTO();
        residentInfoDTO1.setLastName("Doe");
        residentInfoDTO1.setPhone("123456789");
        residentInfoDTO1.setAge(35);
        residentInfoDTO1.setMedications(List.of("aznol:350mg", "hydrapermazol:100mg"));
        residentInfoDTO1.setAllergies(List.of("nillacilan"));
        residentInfoDTO1.setStationNumber(1);

        residentInfoDTO2 = new ResidentInfoDTO();
        residentInfoDTO2.setLastName("Doe");
        residentInfoDTO2.setPhone("987654321");
        residentInfoDTO2.setAge(40);
        residentInfoDTO2.setMedications(List.of("aznol:450mg", "hydrapermazol:200mg"));
        residentInfoDTO2.setAllergies(List.of("nillacilan2"));
        residentInfoDTO2.setStationNumber(2);

        // Mock enriched residents
        List<ResidentInfoDTO> mockResidents = List.of(residentInfoDTO1, residentInfoDTO2);
        LOGGER.info("Mock residents created: {}", mockResidents);

        when(residentInfoService.getResidentsByAddress(address)).thenReturn(mockResidents);
        LOGGER.info("Mocked service to return residents to address: {}", address);

        // Act & Assert
        LOGGER.info("Sending GET request to /do with address: {}", address);
        String response =  mockMvc.perform(get("/fire")
                        .param("address", address)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(2))

                        .andExpect(jsonPath("$[0].lastName").value(residentInfoDTO1.getLastName()))
                        .andExpect(jsonPath("$[0].phone").value(residentInfoDTO1.getPhone()))
                        .andExpect(jsonPath("$[0].age").value(residentInfoDTO1.getAge()))
                        .andExpect(jsonPath("$[0].medications[0]").value(residentInfoDTO1.getMedications().get(0)))
                        .andExpect(jsonPath("$[0].medications[1]").value(residentInfoDTO1.getMedications().get(1)))
                        .andExpect(jsonPath("$[0].allergies[0]").value(residentInfoDTO1.getAllergies().get(0)))
                        .andExpect(jsonPath("$[0].stationNumber").value(residentInfoDTO1.getStationNumber()))

                        .andExpect(jsonPath("$[1].lastName").value(residentInfoDTO2.getLastName()))
                        .andExpect(jsonPath("$[1].phone").value(residentInfoDTO2.getPhone()))
                        .andExpect(jsonPath("$[1].age").value(residentInfoDTO2.getAge()))
                        .andExpect(jsonPath("$[1].medications[0]").value(residentInfoDTO2.getMedications().get(0)))
                        .andExpect(jsonPath("$[1].medications[1]").value(residentInfoDTO2.getMedications().get(1)))
                        .andExpect(jsonPath("$[1].allergies[0]").value(residentInfoDTO2.getAllergies().get(0)))
                        .andExpect(jsonPath("$[1].stationNumber").value(residentInfoDTO2.getStationNumber()))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("Test shouldReturnResidentsByAddress_MockMvc passed successfully ==> {}", response);

        verify(residentInfoService,times(1)).getResidentsByAddress(address);
    }

    @Test
    void shouldReturnFloodInfoByStations_MockMvc() throws Exception {

        // Arrange
        int stationNumber1 = residentInfoDTO1.getStationNumber();
        int stationNumber2 = residentInfoDTO2.getStationNumber();

        String address1 = residentInfoDTO1.getAddress();
        String address2 = residentInfoDTO2.getAddress();

        List<Integer> stationNumbers = List.of(stationNumber1, stationNumber2);
        LOGGER.info("Test shouldReturnFloodInfoByStations_MockMvc - Station numbers: {}", stationNumbers);

        // Mock addresses associated with stations
        List<String> mockAddresses = List.of(address1, address2);

        // appel à une méthode, mais le test n'en a pas besoin.
        //when(fireStationRepository.findAddressesByStationNumbers(stationNumbers)).thenReturn(mockAddresses);
        LOGGER.info("Mocked addresses for stations {}: {}", stationNumbers, mockAddresses);

        // Mock enriched residents
        residentInfoDTO1 = new ResidentInfoDTO();
        residentInfoDTO1.setLastName("Doe");
        residentInfoDTO1.setAddress("123 Main St");
        residentInfoDTO1.setPhone("123456789");
        residentInfoDTO1.setAge(50);
        residentInfoDTO1.setMedications(List.of("aznol:350mg", "hydrapermazol:100mg"));
        residentInfoDTO1.setAllergies(List.of("nillacilan"));

        residentInfoDTO2 = new ResidentInfoDTO();
        residentInfoDTO2.setLastName("Doe");
        residentInfoDTO2.setAddress("123 Main St");
        residentInfoDTO2.setPhone("987654321");
        residentInfoDTO2.setAge(40);
        residentInfoDTO2.setMedications(List.of("aznol:450mg", "hydrapermazol:200mg"));
        residentInfoDTO2.setAllergies(List.of("nillacilan2"));

        List<ResidentInfoDTO> mockResidentInfoDTOs = List.of(residentInfoDTO1, residentInfoDTO2);

        when(residentInfoService.getFloodInfoByStations(stationNumbers)).thenReturn(mockResidentInfoDTOs);
        LOGGER.info("Mocked residents for flood stations: {}", mockResidentInfoDTOs);

        // Act & Assert
        LOGGER.info("Sending GET request to /flood/stations with params: {}", stationNumbers);
        String response = mockMvc.perform(get("/flood/stations")
                        .param("stations", "1", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(2))

                        .andExpect(jsonPath("$[0].lastName").value(residentInfoDTO1.getLastName()))
                        .andExpect(jsonPath("$[0].address").value(residentInfoDTO1.getAddress()))
                        .andExpect(jsonPath("$[0].phone").value(residentInfoDTO1.getPhone()))
                        .andExpect(jsonPath("$[0].age").value(residentInfoDTO1.getAge()))
                        .andExpect(jsonPath("$[0].medications[0]").value(residentInfoDTO1.getMedications().get(0)))
                        .andExpect(jsonPath("$[0].medications[1]").value(residentInfoDTO1.getMedications().get(1)))
                        .andExpect(jsonPath("$[0].allergies[0]").value(residentInfoDTO1.getAllergies().get(0)))

                        .andExpect(jsonPath("$[1].lastName").value(residentInfoDTO2.getLastName()))
                        .andExpect(jsonPath("$[1].address").value(residentInfoDTO2.getAddress()))
                        .andExpect(jsonPath("$[1].phone").value(residentInfoDTO2.getPhone()))
                        .andExpect(jsonPath("$[1].age").value(residentInfoDTO2.getAge()))
                        .andExpect(jsonPath("$[1].medications[0]").value(residentInfoDTO2.getMedications().get(0)))
                        .andExpect(jsonPath("$[1].medications[1]").value(residentInfoDTO2.getMedications().get(1)))
                        .andExpect(jsonPath("$[1].allergies[0]").value(residentInfoDTO2.getAllergies().get(0)))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("Test shouldReturnFloodInfoByStations_MockMvc passed successfully ==> {}", response);

        verify(residentInfoService,times(1)).getFloodInfoByStations(stationNumbers);
    }


    @Test
    void shouldReturnPersonInfoByLastName() throws Exception {

        // Arrange
        String lastName = residentInfoDTO1.getLastName();
        LOGGER.info("Test shouldReturnPersonInfoByLastName - Last Name used: {}", lastName);

        residentInfoDTO1 = new ResidentInfoDTO();
        residentInfoDTO1.setLastName("Doe");
        residentInfoDTO1.setAddress("123 Main St");
        residentInfoDTO1.setAge(25);
        residentInfoDTO1.setEmail("john.doe@example.com");
        residentInfoDTO1.setMedications(List.of("aznol:350mg", "hydrapermazol:100mg"));
        residentInfoDTO1.setAllergies(List.of("nillacilan"));

        residentInfoDTO2 = new ResidentInfoDTO();
        residentInfoDTO2.setLastName("Doe");
        residentInfoDTO2.setAddress("123 Main St");
        residentInfoDTO2.setAge(20);
        residentInfoDTO2.setEmail("jane.doe@example.com");
        residentInfoDTO2.setMedications(List.of("aznol:450mg", "hydrapermazol:200mg"));
        residentInfoDTO2.setAllergies(List.of("nillacilan2"));

        // Mock enriched residents
        List<ResidentInfoDTO> mockResidentInfoDTOs = List.of(residentInfoDTO1, residentInfoDTO2);
        when(residentInfoService.getPersonInfo(lastName)).thenReturn(mockResidentInfoDTOs);
        LOGGER.info("Mocked residents for last name '{}': {}", lastName, mockResidentInfoDTOs);

        // Act & Assert
        LOGGER.info("Sending GET request to /personInfolastName with lastName: {}", lastName);
        String response = mockMvc.perform(get("/personInfolastName")
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(2))// Check that there are two people in the response

                        .andExpect(jsonPath("$[0].lastName").value(residentInfoDTO1.getLastName()))
                        .andExpect(jsonPath("$[0].address").value(residentInfoDTO1.getAddress()))
                        .andExpect(jsonPath("$[0].email").value(residentInfoDTO1.getEmail()))
                        .andExpect(jsonPath("$[0].age").value(residentInfoDTO1.getAge()))
                        .andExpect(jsonPath("$[0].medications[0]").value(residentInfoDTO1.getMedications().get(0)))
                        .andExpect(jsonPath("$[0].medications[1]").value(residentInfoDTO1.getMedications().get(1)))
                        .andExpect(jsonPath("$[0].allergies[0]").value(residentInfoDTO1.getAllergies().get(0)))

                        .andExpect(jsonPath("$[1].lastName").value(residentInfoDTO2.getLastName()))
                        .andExpect(jsonPath("$[1].address").value(residentInfoDTO2.getAddress()))
                        .andExpect(jsonPath("$[1].email").value(residentInfoDTO2.getEmail()))
                        .andExpect(jsonPath("$[1].age").value(residentInfoDTO2.getAge()))
                        .andExpect(jsonPath("$[1].medications[0]").value(residentInfoDTO2.getMedications().get(0)))
                        .andExpect(jsonPath("$[1].medications[1]").value(residentInfoDTO2.getMedications().get(1)))
                        .andExpect(jsonPath("$[1].allergies[0]").value(residentInfoDTO2.getAllergies().get(0)))
                        .andReturn().getResponse().getContentAsString();

        LOGGER.info("Test shouldReturnPersonInfoByLastName passed successfully ==> {}", response);
        verify(residentInfoService,times(1)).getPersonInfo(lastName);
    }


}