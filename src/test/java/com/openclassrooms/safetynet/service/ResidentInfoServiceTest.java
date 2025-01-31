package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.FireStationConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.*;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.FireStationRepository;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.repository.PersonRepository;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@SpringBootTest
public class ResidentInfoServiceTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Mock
    private PersonRepository personRepository;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private FireStationRepository fireStationRepository;
    @Mock
    private PersonConvertorDTO personConvertorDTO;
    @Mock
    private MedicalRecordConvertorDTO medicalRecordConvertorDTO;
    @Mock
    private FireStationConvertorDTO fireStationConvertorDTO;

    private Person person1;
    private Person person2;
    private PersonDTO personDTO1;
    private PersonDTO personDTO2;

    private MedicalRecord medicalRecord1;
    private MedicalRecord medicalRecord2;
    private MedicalRecordDTO medicalRecordDTO1;
    private MedicalRecordDTO medicalRecordDTO2;

    private FireStation fireStation1;
    private FireStation fireStation2;
    private FireStationDTO fireStationDTO1;
    private FireStationDTO fireStationDTO2;

    private ResidentInfoDTO residentInfoDTO1;
    private ResidentInfoDTO residentInfoDTO2;

    @InjectMocks
    private ResidentInfoService residentInfoService;


    @BeforeEach
    void setUp() {

        //LocalDate birthDate1 = LocalDate.parse("01/01/1990", DATE_TIME_FORMATTER);
        //LocalDate birthDate2 = LocalDate.parse("01/01/2000", DATE_TIME_FORMATTER);

        //int age1 = Period.between(birthDate1, LocalDate.now()).getYears();
        //int age2 = Period.between(birthDate2, LocalDate.now()).getYears();


        // Person
        person1 = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .address("123 Main St")
                .city("Springfield")
                .zip("75016")
                .phone("0144445151")
                .email("johndoe@gmail.com")
                .build();
        person2 = Person.builder()
                .firstName("Jane")
                .lastName("Doe")
                .address("123 Main St")
                .city("Springfield")
                .zip("75017")
                .phone("0144445151")
                .email("janedoe@gmail.com")
                .build();

        // PersonDTO
        personDTO1 = PersonDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .address("123 Main St")
                .city("Springfield")
                .zip("75016")
                .phone("0144445151")
                .email("johndoe@gmail.com")
                .build();
        personDTO2 = PersonDTO.builder()
                .firstName("Jane")
                .lastName("Doe")
                .address("123 Main St")
                .city("Springfield")
                .zip("75017")
                .phone("0144445151")
                .email("janedoe@gmail.com")
                .build();

        // MedicalRecord
        medicalRecord1 = MedicalRecord.builder()
                .firstName("John")
                .lastName("Doe")
                .birthdate("01/01/1990")
                .medications(List.of("aznol:350mg", "hydrapermazol:100mg"))
                .allergies(List.of("nillacilan"))
                .build();
        medicalRecord2 = MedicalRecord.builder()
                .firstName("Jane")
                .lastName("Doe")
                .birthdate("01/01/2000")
                .medications(List.of("aznol:350mg", "hydrapermazol:100mg"))
                .allergies(List.of("nillacilan"))
                .build();

        // MedicalRecordDTO
        medicalRecordDTO1 = MedicalRecordDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .birthdate("01/01/1990")
                .medications(List.of("aznol:350mg", "hydrapermazol:100mg"))
                .allergies(List.of("nillacilan"))
                .build();
        medicalRecordDTO2 = MedicalRecordDTO.builder()
                .firstName("Jane")
                .lastName("Doe")
                .birthdate("01/01/2010")
                //.birthdate(String.valueOf(age2))
                .medications(List.of("aznol:350mg", "hydrapermazol:100mg"))
                .allergies(List.of("nillacilan"))
                .build();


        // FireStations
        fireStation1 = FireStation.builder()
                .address("149 Bd Pei ere 75007 Paris")
                .station("1")
                .build();
        fireStation2 = FireStation.builder()
                .address("150 Bd Pei ere 75007 Paris")
                .station("2")
                .build();

        // FireStationDTO
        fireStationDTO1 = FireStationDTO.builder()
                .address("149 Bd Pei ere 75007 Paris")
                .station("1")
                .build();
        fireStationDTO2 = FireStationDTO.builder()
                .address("150 Bd Pei ere 75007 Paris")
                .station("2")
                .build();


    /*
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
                .phone("0144445151")
                .age(age2)
                .email("janedoe@gmail.com")
                .medications(List.of("aznol:350mg", "hydrapermazol:100mg"))
                .allergies(List.of("nillacilan"))
                .stationNumber(2)
                .build();

     */
    }

    @Test
    void shouldReturnGetPersonsByStations() {

        LOGGER.info("Starting test: shouldReturnGetPersonsByStation");

        int expectedAge = Period.between(LocalDate.parse(medicalRecordDTO1.getBirthdate(), DATE_TIME_FORMATTER), LocalDate.now()).getYears();
        int expectedAgeJane = Period.between(LocalDate.parse(medicalRecordDTO2.getBirthdate(), DATE_TIME_FORMATTER), LocalDate.now()).getYears();


        // Arrange
        int stationNumber = Integer.parseInt(fireStationDTO1.getStation());

        // Mock addresses associated with the station
        List<String> mockAddresses = List.of(person1.getAddress(), person2.getAddress());
        when(fireStationRepository.findAddressesByStationNumber(stationNumber)).thenReturn(mockAddresses);
        LOGGER.debug("Mocked addresses associated with the station: {}", mockAddresses);

        // Mock residents at the addresses
        List<Person> mockResidents = List.of(person1, person2);
        when(personRepository.findByAddresses(mockAddresses)).thenReturn(mockResidents);
        LOGGER.debug("Retrieved residents from addresses: {}", mockResidents);

        // Mock medical records
        when(medicalRecordRepository.findByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName())).thenReturn(medicalRecord1);
        when(medicalRecordRepository.findByFullName(medicalRecord2.getFirstName(), medicalRecord2.getLastName())).thenReturn(medicalRecord2);
        //LOGGER.debug("Retrieved medical records for residents");

        // Mock conversion Person -> PersonDTO
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);
        //LOGGER.debug("Converted Person entities to DTOs");

        // Mock conversion MedicalRecord -> MedicalRecordDTO
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord2)).thenReturn(medicalRecordDTO2);
        //LOGGER.debug("Converted MedicalRecord entities to DTOs");

        // Act
        LOGGER.info("Calling service getPersonsByStation with stationNumber={}", stationNumber);
        FireStationResponseDTO result = residentInfoService.getPersonsByStation(stationNumber);
        LOGGER.info("Received result: {}", result);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getResidents().size());

        ResidentInfoDTO john = result.getResidents().stream()
                .filter(resident -> resident.getFirstName().equals(resident.getFirstName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("John was not found in the list of residents"));
        LOGGER.debug("Verifying John's information: {}", john);
        assertEquals(personDTO1.getLastName(), john.getLastName());
        assertEquals(personDTO1.getAddress(), john.getAddress());
        assertEquals(personDTO1.getPhone(), john.getPhone());
        //assertEquals(expectedAge, john.getAge());

        ResidentInfoDTO jane = result.getResidents().stream()
                .filter(resident -> resident.getFirstName().equals(resident.getFirstName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Jane was not found in the list of residents"));
        LOGGER.debug("Verifying Jane's information: {}", jane);
        assertEquals(personDTO2.getLastName(), jane.getLastName());
        assertEquals(personDTO2.getAddress(), jane.getAddress());
        assertEquals(personDTO2.getPhone(), jane.getPhone());
        //assertEquals(expectedAgeJane, jane.getAge());


        // Verify interactions
        verify(fireStationRepository, times(1)).findAddressesByStationNumber(stationNumber);
        verify(personRepository, times(1)).findByAddresses(mockAddresses);
        verify(medicalRecordRepository, times(1)).findByFullName(medicalRecordDTO1.getFirstName(), medicalRecordDTO1.getLastName());
        verify(medicalRecordRepository, times(1)).findByFullName(medicalRecordDTO2.getFirstName(), medicalRecordDTO2.getLastName());

        LOGGER.info("Test shouldReturnGetPersonsByStation completed successfully");
    }


    @Test
    void shouldReturnChildrenByAddress() {

        LOGGER.info("Starting test: shouldReturnChildrenByAddress");

        // Input address
        String address = fireStationDTO1.getAddress();
        //LOGGER.debug("Provided address: {}", address);

        // Mock residents
        when(personRepository.findByAddress(address)).thenReturn(List.of(person1, person2));
        LOGGER.debug("Residents found at address {}: {}, {}", address, person1, person2);

        // Mock medical records
        when(medicalRecordRepository.findByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName())).thenReturn(medicalRecord1); // 33 years old (adult)
        when(medicalRecordRepository.findByFullName(medicalRecord2.getFirstName(), medicalRecord2.getLastName())).thenReturn(medicalRecord2); // 23 years old (adult)
        LOGGER.debug("Retrieved associated medical records");

        // Mock conversion Person -> PersonDTO
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);
        //LOGGER.debug("Converted Person entities to DTOs");

        // Mock conversion MedicalRecord -> MedicalRecordDTO
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord2)).thenReturn(medicalRecordDTO2);
        //LOGGER.debug("Converted MedicalRecord entities to DTOs");

        // Call the method
        LOGGER.info("Calling service getChildrenByAddress with address: {}", address);
        List<ResidentInfoDTO> children = residentInfoService.getChildrenByAddress(address);
        LOGGER.info("Result obtained: {}", children);

        // Assertions
        assertNotNull(children);
        assertEquals(1, children.size()); // No children expected as both residents are adults
        LOGGER.info("Test shouldReturnChildrenByAddress completed successfully");
    }

    @Test
    void shouldReturnResidentsByAddress() {
        LOGGER.info("Starting test: shouldReturnResidentsByAddress");

        // Arrange
        String address = fireStation1.getAddress();
        LOGGER.debug("Provided address: {}", address);

        // Mock fire station for the given address
        FireStation fireStation = new FireStation(address, fireStation1.getStation());
        when(fireStationRepository.findByAddress(address)).thenReturn(fireStation);
        LOGGER.debug("Mocked FireStation: {}", fireStation);

        // Mock conversion FireStation -> FireStationDTO
        FireStationDTO fireStationDTO = new FireStationDTO(address, fireStation1.getStation());
        when(fireStationConvertorDTO.convertEntityToDto(fireStation)).thenReturn(fireStationDTO);
        LOGGER.debug("Converted FireStation to DTO: {}", fireStationDTO);

        // Mock residents at the address
        when(personRepository.findByAddress(address)).thenReturn(List.of(person1, person2));
        LOGGER.debug("Mocked residents found at address {}: {}, {}", address, person1, person2);

        // Mock conversion Person -> PersonDTO
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);
        LOGGER.debug("Converted residents to DTOs");

        // Mock medical records
        when(medicalRecordRepository.findByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName())).thenReturn(medicalRecord1);
        when(medicalRecordRepository.findByFullName(medicalRecord2.getFirstName(), medicalRecord2.getLastName())).thenReturn(medicalRecord2);
        //LOGGER.debug("Retrieved medical records for residents");

        // Mock conversion MedicalRecord -> MedicalRecordDTO
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord2)).thenReturn(medicalRecordDTO2);
        LOGGER.debug("Converted medical records to DTOs");

        // Act
        LOGGER.info("Calling getResidentsByAddress with address: {}", address);
        List<ResidentInfoDTO> residents = residentInfoService.getResidentsByAddress(address);
        //LOGGER.info("Received residents: {}", residents);

        // Assert
        assertNotNull(residents, "The residents list should not be null");
        assertEquals(2, residents.size(), "Expected 2 residents");

        ResidentInfoDTO Doe1 = residents.get(0);
        LOGGER.debug("Verifying first resident: {}", Doe1);
        assertEquals(medicalRecord1.getLastName(), Doe1.getLastName());
        assertEquals("0144445151", Doe1.getPhone());
        assertEquals(35, Doe1.getAge());

        ResidentInfoDTO Doe2 = residents.get(1);
        LOGGER.debug("Verifying second resident: {}", Doe2);
        assertEquals(medicalRecord2.getLastName(), Doe2.getLastName());
        assertEquals("0144445151", Doe2.getPhone());
        assertEquals(15, Doe2.getAge());

        LOGGER.info("Test shouldReturnResidentsByAddress completed successfully");
    }

    @Test
    void shouldReturnFloodInfoByStations() {

        LOGGER.info("Starting test: shouldReturnFloodInfoByStations");

        // Arrange
        List<Integer> stationNumbers = List.of(
                Integer.parseInt(fireStationDTO1.getStation()),
                Integer.parseInt(fireStationDTO2.getStation())
        );
        LOGGER.debug("Provided station numbers: {}", stationNumbers);

        // Mock addresses associated with fire stations
        when(fireStationRepository.findAddressesByStationNumbers(stationNumbers))
                .thenReturn(List.of(fireStation1.getAddress(), fireStation2.getAddress()));
        LOGGER.debug("Mocked fire station addresses: {}, {}", fireStation1.getAddress(), fireStation2.getAddress());

        // Mock FireStation conversion
        when(fireStationConvertorDTO.convertEntityToDto(fireStation1)).thenReturn(fireStationDTO1);
        when(fireStationConvertorDTO.convertEntityToDto(fireStation2)).thenReturn(fireStationDTO2);
        LOGGER.debug("Converted FireStation entities to DTOs");

        // Mock residents at addresses
        when(personRepository.findByAddresses(List.of(fireStationDTO1.getAddress(), fireStationDTO2.getAddress())))
                .thenReturn(List.of(person1, person2));
        LOGGER.debug("Mocked residents at addresses: {}, {}", person1, person2);

        // Mock medical records
        when(medicalRecordRepository.findByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName()))
                .thenReturn(medicalRecord1);
        when(medicalRecordRepository.findByFullName(medicalRecord2.getFirstName(), medicalRecordDTO2.getLastName()))
                .thenReturn(medicalRecord2);
        //LOGGER.debug("Retrieved medical records for residents");

        // Mock Person -> PersonDTO conversion
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);
        //LOGGER.debug("Converted Person entities to DTOs");

        // Mock MedicalRecord -> MedicalRecordDTO conversion
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord2)).thenReturn(medicalRecordDTO2);
        //LOGGER.debug("Converted MedicalRecord entities to DTOs");

        // Act
        LOGGER.info("Calling getFloodInfoByStations with station numbers: {}", stationNumbers);
        List<ResidentInfoDTO> residents = residentInfoService.getFloodInfoByStations(stationNumbers);
        //LOGGER.info("Received residents: {}", residents);

        // Assertions
        assertNotNull(residents);
        assertEquals(2, residents.size());
        assertEquals(personDTO1.getAddress(), residents.get(0).getAddress());
        assertEquals(personDTO1.getAddress(), residents.get(1).getAddress()); // TO BE REVIEWED

        LOGGER.info("Test shouldReturnFloodInfoByStations completed successfully");
    }

    @Test
    void shouldReturnPersonInfoByLastName() {

        LOGGER.info("Starting test: shouldReturnPersonInfoByLastName");

        // Arrange
        String lastName = person1.getLastName();
        LOGGER.debug("Searching for residents with last name: {}", lastName);

        // Mock residents with the given last name
        when(personRepository.findByLastName(lastName)).thenReturn(List.of(person1, person2));
        LOGGER.debug("Mocked residents found: {}, {}", person1, person2);

        // Mock medical records
        when(medicalRecordRepository.findByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName()))
                .thenReturn(medicalRecord1);
        when(medicalRecordRepository.findByFullName(medicalRecord2.getFirstName(), medicalRecord2.getLastName()))
                .thenReturn(medicalRecord2);
        LOGGER.debug("Retrieved medical records for residents");

        // Mock Person -> PersonDTO conversion
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);
        LOGGER.debug("Converted Person entities to DTOs");

        // Mock MedicalRecord -> MedicalRecordDTO conversion
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord2)).thenReturn(medicalRecordDTO2);
        LOGGER.debug("Converted MedicalRecord entities to DTOs");

        // Act
        LOGGER.info("Calling getPersonInfo with last name: {}", lastName);
        List<ResidentInfoDTO> residents = residentInfoService.getPersonInfo(lastName);
        LOGGER.info("Received residents: {}", residents);

        // Assertions
        assertNotNull(residents);
        assertEquals(2, residents.size(), "Expected 2 residents with last name: " + lastName);
        assertEquals(personDTO1.getLastName(), residents.get(0).getLastName());
        assertEquals(personDTO2.getLastName(), residents.get(1).getLastName());

        LOGGER.info("Test shouldReturnPersonInfoByLastName completed successfully");
    }


    //==================================================================================================================>
    /*@Test
    void shouldEnrichResident() {
        // Arrange
        Person resident = person1;
        String address = "123 Main St";

        // Mock de la conversion Person -> PersonDTO
        when(personConvertorDTO.convertEntityToDto(resident)).thenReturn(personDTO1);

        // Mock des dossiers médicaux
        when(medicalRecordRepository.findByFullName("John", "Doe")).thenReturn(medicalRecord1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);

        // Act
        ResidentInfoDTO enrichedResident = residentInfoService.enrichResident(resident, address);

        // Assert
        assertNotNull(enrichedResident);
        assertEquals("John", enrichedResident.getFirstName());
        assertEquals("Doe", enrichedResident.getLastName());
        assertEquals("123 Main St", enrichedResident.getAddress());
        assertEquals("0144445151", enrichedResident.getPhone());
        assertEquals(35, enrichedResident.getAge()); // date de naissance "01/01/1990"
        assertEquals(List.of("aznol:350mg", "hydrapermazol:100mg"), enrichedResident.getMedications());
        assertEquals(List.of("nillacilan"), enrichedResident.getAllergies());
    }

    @Test
    void shouldEnrichPerson() {
        // Arrange
        Person resident = person1;
        when(personConvertorDTO.convertEntityToDto(resident)).thenReturn(personDTO1);

        // Act
        PersonDTO result = residentInfoService.enrichPerson(resident);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("123 Main St", result.getAddress());
        assertEquals("0144445151", result.getPhone());
        assertEquals("johndoe@gmail.com", result.getEmail());
    }

    @Test
    void shouldEnrichMedicalRecord() {
        // Arrange
        when(medicalRecordRepository.findByFullName("John", "Doe")).thenReturn(medicalRecord1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);

        // Act
        MedicalRecordDTO result = residentInfoService.enrichMedicalRecord("John", "Doe");

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("01/01/1990", result.getBirthdate());
        assertEquals(List.of("aznol:350mg", "hydrapermazol:100mg"), result.getMedications());
        assertEquals(List.of("nillacilan"), result.getAllergies());
    }

     */

}
