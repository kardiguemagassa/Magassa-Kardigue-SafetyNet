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

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.openclassrooms.safetynet.constant.service.ResidentInfoImplConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResidentInfoServiceTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

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

    @InjectMocks
    private ResidentInfoService residentInfoService;


    @BeforeEach
    void setUp() {

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
    }

    @Test
    void shouldReturnGetPersonsByStationsSuccessfully() {

        LOGGER.info("Starting test: shouldReturnGetPersonsByStation");

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

        // Mock conversion Person -> PersonDTO
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);
        LOGGER.debug("Converted Person entities to DTOs");

        // Mock conversion MedicalRecord -> MedicalRecordDTO
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord2)).thenReturn(medicalRecordDTO2);

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

        ResidentInfoDTO jane = result.getResidents().stream()
                .filter(resident -> resident.getFirstName().equals(resident.getFirstName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Jane was not found in the list of residents"));
        LOGGER.debug("Verifying Jane's information: {}", jane);
        assertEquals(personDTO2.getLastName(), jane.getLastName());
        assertEquals(personDTO2.getAddress(), jane.getAddress());
        assertEquals(personDTO2.getPhone(), jane.getPhone());


        // Verify interactions
        verify(fireStationRepository, times(1)).findAddressesByStationNumber(stationNumber);
        verify(personRepository, times(1)).findByAddresses(mockAddresses);
        verify(medicalRecordRepository, times(1)).findByFullName(medicalRecordDTO1.getFirstName(), medicalRecordDTO1.getLastName());
        verify(medicalRecordRepository, times(1)).findByFullName(medicalRecordDTO2.getFirstName(), medicalRecordDTO2.getLastName());

        LOGGER.info("Test shouldReturnGetPersonsByStation completed successfully");
    }

    @Test
    void shouldReturnGetPersonsByStationsNull() {

        LOGGER.info("Start method : shouldReturnGetPersonsByStationsNull");
         assertThrows(IllegalArgumentException.class, ()-> residentInfoService.getPersonsByStation(0));
        assertNotNull(API_ADDRESS_NOT_FOUND);
        LOGGER.info("End method :  shouldReturnGetPersonsByStationsNull completed successfully");

    }

    @Test
    void shouldReturnChildrenByAddressSuccessfully() {

        LOGGER.info("Starting test: shouldReturnChildrenByAddress");

        // Input address
        String address = fireStationDTO1.getAddress();

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
        LOGGER.debug("Converted Person entities to DTOs:");

        // Mock conversion MedicalRecord -> MedicalRecordDTO
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord2)).thenReturn(medicalRecordDTO2);

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
    void shouldReturnChildrenByAddressNotFoundException() {
        LOGGER.info("Start method : shouldReturnChildrenByAddressNotFoundException");

        assertThrows(IllegalArgumentException.class, () -> residentInfoService.getChildrenByAddress(null));
        assertNotNull(API_ADDRESS_NUMBER_NOT_FOUND);
        LOGGER.info("End method : shouldReturnChildrenByAddressNotFoundException completed successfully");
    }

    @Test
    void shouldReturnResidentsByAddressSuccessfully() {
        LOGGER.info("Starting test: shouldReturnResidentsByAddressSuccessfully");

        // Arrange
        String address = fireStation1.getAddress();

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

        // Mock medical records
        when(medicalRecordRepository.findByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName())).thenReturn(medicalRecord1);
        when(medicalRecordRepository.findByFullName(medicalRecord2.getFirstName(), medicalRecord2.getLastName())).thenReturn(medicalRecord2);

        // Mock conversion MedicalRecord -> MedicalRecordDTO
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord2)).thenReturn(medicalRecordDTO2);

        // Act
        LOGGER.info("Calling getResidentsByAddress with address: {}", address);
        List<ResidentInfoDTO> residents = residentInfoService.getResidentsByAddress(address);

        // Assert
        assertNotNull(residents, "The residents list should not be null");
        assertEquals(2, residents.size(), "Expected 2 residents");

        ResidentInfoDTO Doe1 = residents.get(0);
        LOGGER.debug("Verifying second resident: {}", Doe1);
        assertEquals(medicalRecord1.getLastName(), Doe1.getLastName());
        assertEquals(person1.getPhone(), Doe1.getPhone());
        assertEquals(35, Doe1.getAge());

        ResidentInfoDTO Doe2 = residents.get(1);
        LOGGER.debug("Verifying second resident: {}", Doe2);
        assertEquals(medicalRecord2.getLastName(), Doe2.getLastName());
        assertEquals(person1.getPhone(), Doe2.getPhone());
        assertEquals(15, Doe2.getAge());

        LOGGER.info("Test shouldReturnResidentsByAddressSuccessfully completed successfully");
    }

    @Test
    void shouldReturnResidentsByAddressNull() {
        LOGGER.info("Start method: shouldReturnResidentsByAddressNull");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> residentInfoService.getResidentsByAddress(null));
        assertNotNull(API_ADDRESS_NOT_FOUND , exception.getMessage());
        LOGGER.info("End method : shouldReturnResidentsByAddressNull completed successfully");
    }


    @Test
    void shouldReturnFloodInfoByStationsSuccessfully() {

        LOGGER.info("Starting test: shouldReturnFloodInfoByStationsSuccessfully");

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

        // Mock residents at addresses
        when(personRepository.findByAddresses(List.of(fireStationDTO1.getAddress(), fireStationDTO2.getAddress())))
                .thenReturn(List.of(person1, person2));
        LOGGER.debug("Mocked residents at addresses: {}, {}", person1, person2);

        // Mock medical records
        when(medicalRecordRepository.findByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName()))
                .thenReturn(medicalRecord1);
        when(medicalRecordRepository.findByFullName(medicalRecord2.getFirstName(), medicalRecordDTO2.getLastName()))
                .thenReturn(medicalRecord2);

        // Mock Person -> PersonDTO conversion
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);

        // Mock MedicalRecord -> MedicalRecordDTO conversion
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord2)).thenReturn(medicalRecordDTO2);


        // Act
        LOGGER.info("Calling getFloodInfoByStations with station numbers: {}", stationNumbers);
        List<ResidentInfoDTO> residents = residentInfoService.getFloodInfoByStations(stationNumbers);

        // Assertions
        assertNotNull(residents);
        assertEquals(2, residents.size());
        assertEquals(personDTO1.getAddress(), residents.get(0).getAddress());
        assertEquals(personDTO1.getAddress(), residents.get(1).getAddress()); // TO BE REVIEWED

        LOGGER.info("Test shouldReturnFloodInfoByStationsSuccessfully completed successfully");
    }

    @Test
    void shouldReturnFloodInfoByStationsException() {
        LOGGER.info("Start method : shouldReturnFloodInfoByStationsException");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> residentInfoService.getFloodInfoByStations(null));
        assertNotNull(FIRE_STATION_LIST_NOT_FOUND , exception.getMessage());
        LOGGER.info("End method : shouldReturnFloodInfoByStationsException completed successfully");

    }

    @Test
    void shouldReturnPersonInfoByLastNameSuccessfully() {

        LOGGER.info("Start method: shouldReturnPersonInfoByLastNameSuccessfully");

        // Arrange
        String lastName = person1.getLastName();

        // Mock residents with the given last name
        when(personRepository.findByLastName(lastName)).thenReturn(List.of(person1, person2));
        LOGGER.debug("Mocked residents found: {}, {}", person1, person2);

        // Mock medical records
        when(medicalRecordRepository.findByFullName(medicalRecord1.getFirstName(), medicalRecord1.getLastName()))
                .thenReturn(medicalRecord1);
        when(medicalRecordRepository.findByFullName(medicalRecord2.getFirstName(), medicalRecord2.getLastName()))
                .thenReturn(medicalRecord2);

        // Mock Person -> PersonDTO conversion
        when(personConvertorDTO.convertEntityToDto(person1)).thenReturn(personDTO1);
        when(personConvertorDTO.convertEntityToDto(person2)).thenReturn(personDTO2);

        // Mock MedicalRecord -> MedicalRecordDTO conversion
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord1)).thenReturn(medicalRecordDTO1);
        when(medicalRecordConvertorDTO.convertEntityToDto(medicalRecord2)).thenReturn(medicalRecordDTO2);

        // Act
        LOGGER.info("Calling getPersonInfo with last name: {}", lastName);
        List<ResidentInfoDTO> residents = residentInfoService.getPersonInfo(lastName);
        LOGGER.info("Received residents: {}", residents);

        // Assertions
        assertNotNull(residents);
        assertEquals(2, residents.size(), "Expected 2 residents with last name: " + lastName);
        assertEquals(personDTO1.getLastName(), residents.get(0).getLastName());
        assertEquals(personDTO2.getLastName(), residents.get(1).getLastName());

        LOGGER.info("End method : shouldReturnPersonInfoByLastNameSuccessfully completed successfully");
    }

    @Test
    void shouldReturnPersonInfoByLastNameException() {

        LOGGER.info("Starting test: shouldReturnPersonInfoByLastNameException");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> residentInfoService.getPersonInfo(null));
        assertNotNull(FIRE_STATION_LIST_NOT_FOUND , exception.getMessage());
        LOGGER.info("End method : shouldReturnPersonInfoByLastNameException completed successfully");

    }


}
