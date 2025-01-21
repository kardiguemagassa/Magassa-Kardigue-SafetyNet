package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.FireStationConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.FireStationDTO;
import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.dto.api.FireStationResponseDTO;
import com.openclassrooms.safetynet.dto.api.ResidentInfoDTO;
import com.openclassrooms.safetynet.exception.person.EmailNotFoundException;
import com.openclassrooms.safetynet.exception.person.PersonNotFoundException;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.FireStationRepository;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.repository.PersonRepository;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.openclassrooms.safetynet.constant.service.ApiImplConstant.*;


@Service
@AllArgsConstructor
public class ApiService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final PersonRepository personRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final FireStationRepository fireStationRepository;

    private final MedicalRecordConvertorDTO medicalRecordConvertorDTO;
    private final PersonConvertorDTO personConvertorDTO;
    private final FireStationConvertorDTO fireStationConvertorDTO;


    // 1 FINISH
    public FireStationResponseDTO getPersonsByStation(int stationNumber) {

        List<String> addresses = fireStationRepository.findAddressesByStationNumber(stationNumber);

        if (addresses == null || addresses.isEmpty()) {
            LOGGER.error(API_ADDRESS_NUMBER_NOT_FOUND, stationNumber);
            //return new FireStationResponseDTO();
        }

        List<Person> residents = personRepository.findByAddresses(addresses);

        if (residents == null || residents.isEmpty()) {
            LOGGER.warn("No residents found for addresses: {}", addresses);
            //return new FireStationResponseDTO();
        }

        List<ResidentInfoDTO> enrichedResidents = residents.stream()
                .map(person -> enrichResident(person, person.getAddress()))
                .filter(Objects::nonNull)
                .map(resident -> new ResidentInfoDTO(
                        resident.getFirstName(),
                        resident.getLastName(),
                        resident.getAddress(),
                        resident.getPhone(),
                        resident.getAge()
                ))
                .toList();

        // Calculer le nombre d'adultes et d'enfants
        long childCount = enrichedResidents.stream().filter(resident -> resident.getAge() <= 18).count();

        long adultCount = enrichedResidents.size() - childCount;

        // Créer un objet FireStationResponseDTO avec les informations de résidents et les comptes d'adultes/enfants
        return new FireStationResponseDTO((int) adultCount, (int) childCount, enrichedResidents);
    }

    // 2 FINISH
    public List<ResidentInfoDTO> getChildrenByAddress(String address) {

        List<Person> residents = personRepository.findByAddress(address);

        if (residents == null || residents.isEmpty()) {
            LOGGER.info("No residents found at address: {}", address);
            return Collections.emptyList();
        }

        return residents.stream()
                .map(person -> enrichResident(person, person.getAddress())).filter(Objects::nonNull)
                .map(resident -> new ResidentInfoDTO(
                        resident.getFirstName(),
                        resident.getLastName(),
                        resident.getAge()
                ))
                //.filter(Objects::nonNull)
                .toList();
    }

    // 3 FINISH
    public List<String> getPhoneNumbersByStation(int stationNumber) {

        List<String> addresses = fireStationRepository.findAddressesByStationNumber(stationNumber);
        if (addresses == null || addresses.isEmpty()) {
            LOGGER.error("No addresses found for station number {}", stationNumber);
            return Collections.emptyList();
        }

        return personRepository.findByAddresses(addresses).stream()
                .map(Person::getPhone)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    // 4 FINISH
    public List<ResidentInfoDTO> getResidentsByAddress(String address) {

        FireStation fireStation = fireStationRepository.findByAddress(address);

        FireStationDTO fireStationDTO = fireStationConvertorDTO.convertEntityToDto(fireStation);

        if (fireStationDTO == null) {
            LOGGER.warn("No fire station found for address: {}", address);
            throw new IllegalStateException("No fire station found for address: " + address);
        }

        // Récupérer le numéro de la caserne
        int fireStationNumber = Integer.parseInt(fireStationDTO.getStation());

        List<Person> residents = personRepository.findByAddress(address);

        if (residents == null || residents.isEmpty()) {
            LOGGER.warn("No residents found for address: {}", address);
            throw new IllegalStateException("No residents found for address: " + address);
        }

        List<ResidentInfoDTO> enrichedResidents;
        enrichedResidents = residents.stream()
                .map(person -> enrichResident(person, person.getAddress())).filter(Objects::nonNull)
                .map(resident -> new ResidentInfoDTO(
                        fireStationNumber,
                        resident.getLastName(),
                        resident.getPhone(),
                        resident.getAge(),
                        resident.getMedications(),
                        resident.getAllergies()))
                .toList();

        //LOGGER.info("Residents for address {}: {}", address, enrichedResidents);

        return enrichedResidents;
    }

    // 5 FINISH
    public List<ResidentInfoDTO> getFloodInfoByStations(List<Integer> stationNumbers) {

        List<String> addresses = fireStationRepository.findAddressesByStationNumbers(stationNumbers);


        if (addresses == null || addresses.isEmpty()) {
            LOGGER.error("No addresses found for the provided station numbers.");
            return Collections.emptyList();
        }

        List<Person> residents = personRepository.findByAddresses(addresses);

        List<ResidentInfoDTO> enrichedResidents;

        enrichedResidents = residents.stream()
                .map(person -> enrichResident(person, person.getAddress())).filter(Objects::nonNull)
                .map(resident -> new ResidentInfoDTO(
                        resident.getAddress(),
                        resident.getLastName(),
                        resident.getPhone(),
                        resident.getAge(),
                        resident.getMedications(),
                        resident.getAllergies()
                ))
                //.filter(Objects::nonNull)
                .collect(Collectors.toList());

        return enrichedResidents;
    }

    // 6 FINISH
    public List<ResidentInfoDTO> getPersonInfo(String lastName) {

        if (lastName == null || lastName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last name cannot be null or empty.");
        }

        List<Person> persons = personRepository.findByLastName(lastName);

        // Enrichir les informations des résidents
        List<ResidentInfoDTO> enrichedResidents;

        enrichedResidents = persons.stream()
                .map(person -> enrichResident(person, person.getAddress())).filter(Objects::nonNull)
                .map(resident -> new ResidentInfoDTO(
                        resident.getLastName(),
                        resident.getAddress(),
                        resident.getAge(),
                        resident.getEmail(),
                        resident.getMedications(),
                        resident.getAllergies()
                ))
                .toList();

        return enrichedResidents;
    }


    // 7 FINISH
    public List<String> getCommunityEmails(String city) throws PersonNotFoundException, EmailNotFoundException {

        if (city == null || city.isBlank()) {
            LOGGER.info(EMAIL_NOT_FOUND);
            throw new IllegalArgumentException(CITY_NOT_FOUND);
        }

        try {
            // Récupération des résidents de la ville
            List<PersonDTO> residents = personRepository.findByCity(city).stream()
                    .map(personConvertorDTO::convertEntityToDto)
                    .toList();

            return residents.stream()
                    .map(PersonDTO::getEmail)
                    .filter(email -> email != null && !email.isBlank())
                    .distinct()
                    .toList();

        } catch (RuntimeException e) {
            LOGGER.error(PERSON_ERROR_EMAIL, city, e.getMessage(), e);
            throw new RuntimeException(PERSON_ERROR_EMAIL, e);
        }
    }

    // Generic method
    private <T> ResidentInfoDTO enrichResident(T resident, String address) {

        PersonDTO residentDTO;

        if (resident instanceof Person person) {
            residentDTO = personConvertorDTO.convertEntityToDto(person);
        } else if (resident instanceof PersonDTO dto) {
            residentDTO = dto;
        } else {
            LOGGER.error("Invalid resident type: {}", resident.getClass().getSimpleName());
            throw new IllegalArgumentException("Unsupported resident type: " + resident.getClass().getSimpleName());
        }

        MedicalRecord medicalRecord = medicalRecordRepository.findByFullName(residentDTO.getFirstName(), residentDTO.getLastName());
        MedicalRecordDTO medicalRecordDTO = medicalRecordConvertorDTO.convertEntityToDto(medicalRecord);

        if (medicalRecordDTO == null || medicalRecordDTO.getBirthdate() == null) {
            LOGGER.error("Missing medical record for {} {}", residentDTO.getFirstName(), residentDTO.getLastName());
            return null;
        }

        try {
            LocalDate birthDate = LocalDate.parse(medicalRecord.getBirthdate(), DATE_FORMATTER);
            int age = calculateAge(birthDate);

            return ResidentInfoDTO.builder()
                    .firstName(residentDTO.getFirstName())
                    .lastName(residentDTO.getLastName())
                    .address(address != null ? address : residentDTO.getAddress())
                    .phone(residentDTO.getPhone())
                    .age(age)
                    .email(residentDTO.getEmail())
                    .medications(medicalRecord.getMedications())
                    .allergies(medicalRecord.getAllergies())
                    .build();

        } catch (Exception e) {
            LOGGER.error("Error parsing birthdate for {} {}: {}", residentDTO.getFirstName(), residentDTO.getLastName(), medicalRecord.getBirthdate(), e);
            return null;
        }
    }

    // Méthode utilitaire pour calculer l'âge
    private int calculateAge(LocalDate birthDate) {
        return (birthDate != null) ? Period.between(birthDate, LocalDate.now()).getYears() : 0;
    }

}
