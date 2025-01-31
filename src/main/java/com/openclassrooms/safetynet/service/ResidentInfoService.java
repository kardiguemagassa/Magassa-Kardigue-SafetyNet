package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.FireStationConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.FireStationDTO;
import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.dto.FireStationResponseDTO;
import com.openclassrooms.safetynet.dto.ResidentInfoDTO;
import com.openclassrooms.safetynet.exception.residentInfo.ResidentInfoNotFoundException;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.FireStationRepository;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.repository.PersonRepository;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

import java.util.*;
import java.util.stream.Collectors;

import static com.openclassrooms.safetynet.constant.service.ResidentInfoImplConstant.*;

@Service
@AllArgsConstructor
public class ResidentInfoService {

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
            LOGGER.warn(API_ADDRESS_NUMBER_NOT_FOUND, stationNumber);
            throw new IllegalArgumentException(API_ADDRESS_NUMBER_NOT_FOUND);

        }

        List<Person> residents = personRepository.findByAddresses(addresses);

        if (residents == null || residents.isEmpty()) {
            LOGGER.warn(API_ADDRESS_NOT_FOUND, addresses);
            throw new IllegalArgumentException(API_ADDRESS_NOT_FOUND);
        }

        List<ResidentInfoDTO> enrichedResidents = residents.stream()
                .map(person -> enrichResident(person, person.getAddress())).filter(Objects::nonNull)
                .map(resident -> new ResidentInfoDTO(
                        resident.getFirstName(),
                        resident.getLastName(),
                        resident.getAddress(),
                        resident.getPhone(),
                        resident.getAge(),
                        null,
                        null,
                        null,
                        null,
                        null
                ))
                .toList();

        // Calculate the number of adults and children
        long childCount = enrichedResidents.stream().filter(resident -> resident.getAge() <= 18).count();
        long adultCount = enrichedResidents.size() - childCount;

        return new FireStationResponseDTO((int) adultCount, (int) childCount, enrichedResidents);
    }

    // 2 FINISH
    public List<ResidentInfoDTO> getChildrenByAddress(String address) {

        List<Person> residents = personRepository.findByAddress(address);

        if (residents == null || residents.isEmpty()) {
            LOGGER.info(API_ADDRESS_NOT_FOUND, address);
            return Collections.emptyList();
        }

        List<ResidentInfoDTO> children = residents.stream()
                .map(person -> enrichResident(person, person.getAddress()))
                .filter(Objects::nonNull)
                .filter(resident -> resident.getAge() <= 18)
                .map(resident -> new ResidentInfoDTO(
                        resident.getFirstName(),
                        resident.getLastName(),
                        null, null,
                        resident.getAge(),
                        null,
                        null, null, null,
                        null
                ))
                .toList();

        // If no children, returns an empty list
        if (children.isEmpty()) {
            return Collections.emptyList();
        }


        List<ResidentInfoDTO> householdMembers = residents.stream()
                .map(person -> enrichResident(person, person.getAddress()))
                .filter(Objects::nonNull)
                .filter(resident -> resident.getAge() > 18)
                .map(resident -> new ResidentInfoDTO(
                        resident.getFirstName(),
                        resident.getLastName(),
                        null, null,
                        resident.getAge(),
                        null,
                        null, null, null,
                        null
                ))
                .toList();

        // Involves other household members with the children
        children.forEach(child -> child.setHouseholdMembers(new ArrayList<>(householdMembers)));

        return children;
    }



    // 4 FINISH
    public List<ResidentInfoDTO> getResidentsByAddress(String address) {

        FireStation fireStation = fireStationRepository.findByAddress(address);

        FireStationDTO fireStationDTO = fireStationConvertorDTO.convertEntityToDto(fireStation);

        if (fireStationDTO == null) {
            LOGGER.error(API_ADDRESS_NOT_FOUND, address);
            throw new IllegalStateException(API_ADDRESS_NOT_FOUND + address);
        }

        // Retrieve the barracks number
        int fireStationNumber = Integer.parseInt(fireStationDTO.getStation());
        List<Person> residents = personRepository.findByAddress(address);

        if (residents == null || residents.isEmpty()) {
            LOGGER.error(API_ADDRESS_NOT_FOUND, address);
            throw new IllegalStateException(API_ADDRESS_NOT_FOUND + address);
        }

        List<ResidentInfoDTO> enrichedResidents;
        enrichedResidents = residents.stream()
                .map(person -> enrichResident(person, person.getAddress())).filter(Objects::nonNull)
                .map(resident -> new ResidentInfoDTO(
                        null,
                        resident.getLastName(),
                        null,
                        resident.getPhone(),
                        resident.getAge(),
                        null,
                        resident.getMedications(),
                        resident.getAllergies(),
                        fireStationNumber,
                        null

                )).toList();
        //LOGGER.info("Residents for address {}: {}", address, enrichedResidents);
        return enrichedResidents;
    }

    // 5 FINISH
    public List<ResidentInfoDTO> getFloodInfoByStations(List<Integer> stationNumbers) {

        List<String> addresses = fireStationRepository.findAddressesByStationNumbers(stationNumbers);


        if (addresses == null || addresses.isEmpty()) {
            LOGGER.error(FIRE_STATION_LIST_NOT_FOUND);
            throw new IllegalArgumentException(FIRE_STATION_LIST_NOT_FOUND);
        }

        List<Person> residents = personRepository.findByAddresses(addresses);

        List<ResidentInfoDTO> enrichedResidents;
        enrichedResidents = residents.stream()
                .map(person -> enrichResident(person, person.getAddress())).filter(Objects::nonNull)
                .map(resident -> new ResidentInfoDTO(
                        null,
                        resident.getLastName(),
                        resident.getAddress(),
                        resident.getPhone(),
                        resident.getAge(),
                        null,
                        resident.getMedications(),
                        resident.getAllergies(),
                        null,
                        null

                ))
                //.filter(Objects::nonNull)
                .collect(Collectors.toList());

        return enrichedResidents;
    }

    // 6 FINISH
    public List<ResidentInfoDTO> getPersonInfo(String lastName) {

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException(LAST_NAME_NOT_NULL);
        }

        List<Person> persons = personRepository.findByLastName(lastName);

        List<ResidentInfoDTO> enrichedResidents;

        enrichedResidents = persons.stream()
                .map(person -> enrichResident(person, person.getAddress())).filter(Objects::nonNull)
                .map(resident -> new ResidentInfoDTO(
                        null,
                        resident.getLastName(),
                        resident.getAddress(),
                        null,
                        resident.getAge(),
                        resident.getEmail(),
                        resident.getMedications(),
                        resident.getAllergies(),
                        null,
                        null
                ))
                .toList();

        return enrichedResidents;
    }

    // Main method that uses the two separate methods
    private ResidentInfoDTO enrichResident(Person resident, String address) {

        PersonDTO residentDTO = enrichPerson(resident);
        MedicalRecordDTO medicalRecordDTO = enrichMedicalRecord(residentDTO.getFirstName(), residentDTO.getLastName());

        if (medicalRecordDTO == null || medicalRecordDTO.getBirthdate().isEmpty()) {
            throw new IllegalArgumentException(RESIDENTS_INVALID + residentDTO.getFirstName() + residentDTO.getLastName());
        }

        try {

            LocalDate birthDate = LocalDate.parse(medicalRecordDTO.getBirthdate(), DATE_FORMATTER);
            int age = calculateAge(birthDate);

            return ResidentInfoDTO.builder()
                    .firstName(residentDTO.getFirstName())
                    .lastName(residentDTO.getLastName())
                    .address(address != null ? address : residentDTO.getAddress())
                    .phone(residentDTO.getPhone())
                    .age(age)
                    .email(residentDTO.getEmail())
                    .medications(medicalRecordDTO.getMedications())
                    .allergies(medicalRecordDTO.getAllergies())
                    .build();

        } catch (RuntimeException e) {
            LOGGER.error(ERROR_REPOSITORIES, e.getMessage());
            throw new ResidentInfoNotFoundException(ERROR_REPOSITORIES + e.getMessage());

        } catch (Exception e) {
            LOGGER.error(MESSING_BIRTH_DATE, residentDTO.getFirstName(), residentDTO.getLastName(), medicalRecordDTO.getBirthdate(), e);
            throw new ResidentInfoNotFoundException(MESSING_BIRTH_DATE, e);
        }
    }

    /*
    public PersonDTO enrichPerson(Person resident) {
        if (resident == null) {
            LOGGER.error("The resident object cannot be null.");
            throw new IllegalArgumentException("The resident object cannot be null.");
        }
        return personConvertorDTO.convertEntityToDto(resident);
    }

    // Méthode pour enrichir les informations médicales
    public MedicalRecordDTO enrichMedicalRecord(String firstName, String lastName) {

        MedicalRecord medicalRecord = medicalRecordRepository.findByFullName(firstName, lastName);
        MedicalRecordDTO medicalRecordDTO = medicalRecordConvertorDTO.convertEntityToDto(medicalRecord);

        if (medicalRecordDTO == null || medicalRecordDTO.getBirthdate() == null) {
            LOGGER.error(MESSING_MEDICAL, firstName, lastName);
            return null;
        }

        return medicalRecordDTO;
    }

     */

    // Method to enrich the person's information
    private PersonDTO enrichPerson(Person resident) {

        return Optional.ofNullable(resident).map(personConvertorDTO::convertEntityToDto)
                .orElseThrow(() -> {
                    LOGGER.error(PERSON_NOT_FOUND);
                    return new ResidentInfoNotFoundException(PERSON_NOT_FOUND);
                });
    }



    // Method for enriching medical information
    private MedicalRecordDTO enrichMedicalRecord(String firstName, String lastName) {

        return Optional.ofNullable(medicalRecordRepository.findByFullName(firstName, lastName))
                .map(medicalRecordConvertorDTO::convertEntityToDto)
                .filter(medicalRecordDTO -> medicalRecordDTO.getBirthdate() != null && !medicalRecordDTO.getBirthdate().isEmpty())
                .orElseThrow(() -> {
                    LOGGER.error(MESSING_MEDICAL, firstName, lastName);
                    return new IllegalArgumentException(MESSING_MEDICAL + firstName + lastName);
                });
    }

    // Utility method for calculating age
    public int calculateAge(LocalDate birthDate) {
        return (birthDate != null) ? Period.between(birthDate, LocalDate.now()).getYears() : 0;
    }

}
