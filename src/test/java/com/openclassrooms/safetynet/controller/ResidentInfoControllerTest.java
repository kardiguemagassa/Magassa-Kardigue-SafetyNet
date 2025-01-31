package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.convertorDTO.FireStationConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.FireStationDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.FireStationRepository;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.repository.PersonRepository;
import com.openclassrooms.safetynet.service.FireStationService;
import com.openclassrooms.safetynet.service.PersonService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(FireStationController.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiControllerTest {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Mock
    private FireStationRepository fireStationRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private MedicalRecordConvertorDTO medicalRecordConvertorDTO;
    @Mock
    private FireStationConvertorDTO fireStationConvertorDTO;
    @Mock
    private PersonConvertorDTO personConvertorDTO;

    @InjectMocks
    private FireStationService fireStationService;
    @InjectMocks
    private PersonService personService;

    private FireStation fireStation1;
    private FireStation fireStation2;
    private FireStationDTO fireStationDTO1;
    private FireStationDTO fireStationDTO2;

    private Person person1;
    private Person person2;
    private PersonDTO personDTO1;
    private PersonDTO personDTO2;

    @BeforeEach
    void setUp() {
        // FireStations
        fireStation1 = new FireStation("149 Bd Pei ere 75007 Paris", "1");
        fireStation2 = new FireStation("150 Bd Pei ere 75007 Paris", "2");

        //FireStationDTO
        fireStationDTO1 = new FireStationDTO("149 Bd Pei ere 75007 Paris", "1");
        fireStationDTO2 = new FireStationDTO("150 Bd Pei ere 75007 Paris", "2");

        // Person
        person1 = new Person("John", "Doe", "123 Main St", "Springfield", "75016",
                "0144445151", "johndoe@gmail.com");
        person2 = new Person("Jane", "Doe", "123 Main St", "Springfield", "75017",
                "0144445152", "janedoe@gmail.com");

        // PersonDTO
        personDTO1 = new PersonDTO("John", "Doe", "johndoe@gmail.com", "123 Main St", "Springfield",
                "75016", "0144445151");
        personDTO2 = new PersonDTO("Jane", "Doe", "janedoe@gmail.com", "123 Main St",
                "Springfield", "75017", "0144445152");
    }
}
