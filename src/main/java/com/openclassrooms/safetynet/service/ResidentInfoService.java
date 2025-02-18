package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.FireStationConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.FireStationDTO;
import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.dto.FireStationResponseDTO;
import com.openclassrooms.safetynet.dto.ResidentInfoDTO;
import com.openclassrooms.safetynet.exception.ResidentInfoNotFoundException;
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

import static com.openclassrooms.safetynet.constant.ResidentInfoConstant.*;

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


    public FireStationResponseDTO getPersonsByStation(int stationNumber) {

        List<String> addresses = fireStationRepository.findAddressesByStationNumber(stationNumber);

        if (addresses == null || addresses.isEmpty()) {
            throw new IllegalArgumentException(API_ADDRESS_NUMBER_NOT_FOUND);
        }

        List<Person> residents = personRepository.findByAddresses(addresses);

        List<ResidentInfoDTO> enrichedResidents = residents.stream()
                .map(person -> enrichResident(person, person.getAddress())).filter(Objects::nonNull)
                .map(resident -> {
                    resident.setEmail(null);
                    resident.setMedications(null);
                    resident.setAllergies(null);
                    resident.setStationNumber(null);
                    resident.setHouseholdMembers(null);
                    return resident;
                }).toList();

        // Calculate the number of adults and children
        long childCount = enrichedResidents.stream().filter(resident -> resident.getAge() <= 18).count();
        long adultCount = enrichedResidents.size() - childCount;

        return new FireStationResponseDTO((int) adultCount, (int) childCount, enrichedResidents);
    }

    public List<ResidentInfoDTO> getChildrenByAddress(String address) {

        List<Person> residents = personRepository.findByAddress(address);

        if (residents == null || residents.isEmpty()) {
            LOGGER.info(API_ADDRESS_NOT_FOUND, address);
            throw new IllegalArgumentException(API_ADDRESS_NOT_FOUND);
        }

        List<ResidentInfoDTO> children = filterChildHouseMemberInfoResidents(residents, true);
        List<ResidentInfoDTO> householdMembers = filterChildHouseMemberInfoResidents(residents, false);

        // Associer les membres du foyer aux enfants
        children.forEach(child -> child.setHouseholdMembers(new ArrayList<>(householdMembers)));

        return children;
    }

    private List<ResidentInfoDTO> filterChildHouseMemberInfoResidents(List<Person> residents, boolean isChild) {
        return residents.stream()
                .map(person -> enrichResident(person, person.getAddress()))
                .filter(Objects::nonNull)
                .filter(resident -> isChild == (resident.getAge() <= 18))
                .map(this::childHouseMemberInfo)
                .toList();
    }

    private ResidentInfoDTO childHouseMemberInfo(ResidentInfoDTO resident) {
        resident.setAddress(null);
        resident.setPhone(null);
        resident.setEmail(null);
        resident.setMedications(null);
        resident.setAllergies(null);
        resident.setStationNumber(null);
        resident.setHouseholdMembers(null);
        return resident;
    }

    public List<ResidentInfoDTO> getResidentsByAddress(String address) {

        FireStation fireStation = fireStationRepository.findByAddress(address);

        FireStationDTO fireStationDTO = fireStationConvertorDTO.convertEntityToDto(fireStation);

        if (fireStationDTO == null) {
            LOGGER.error(API_ADDRESS_NOT_FOUND, address);
            throw new IllegalArgumentException(API_ADDRESS_NOT_FOUND);
        }

        // Retrieve the barracks number
        int fireStationNumber = Integer.parseInt(fireStationDTO.getStation());
        List<Person> residents = personRepository.findByAddress(address);

        List<ResidentInfoDTO> enrichedResidents;
        enrichedResidents = residents.stream()
                .map(person -> enrichResident(person, person.getAddress())).filter(Objects::nonNull)
                .map(resident -> {
                    resident.setFirstName(null);
                    resident.setAddress(null);
                    resident.setEmail(null);
                    resident.setStationNumber(fireStationNumber);
                    resident.setHouseholdMembers(null);
                    return resident;
                }).toList();

        return enrichedResidents;
    }

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
                .map(resident -> {
                    resident.setFirstName(null);
                    resident.setEmail(null);
                    resident.setStationNumber(null);
                    resident.setHouseholdMembers(null);
                    return resident;
                }).toList();

        return enrichedResidents;
    }

    public List<ResidentInfoDTO> getPersonInfo(String lastName) {

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException(LAST_NAME_NOT_NULL);
        }

        List<Person> persons = personRepository.findByLastName(lastName);

        List<ResidentInfoDTO> enrichedResidents;

        enrichedResidents = persons.stream()
                .map(person -> enrichResident(person, person.getAddress())).filter(Objects::nonNull)
                .map(resident -> {
                    resident.setFirstName(null);
                    resident.setPhone(null);
                    resident.setStationNumber(null);
                    resident.setHouseholdMembers(null);
                    return resident;
                }).toList();

        return enrichedResidents;
    }

    private ResidentInfoDTO enrichResident(Person resident, String address) {

        PersonDTO residentDTO = enrichPerson(resident);
        MedicalRecordDTO medicalRecordDTO = enrichMedicalRecord(residentDTO.getFirstName(), residentDTO.getLastName());

        if (medicalRecordDTO == null || medicalRecordDTO.getBirthdate().isEmpty()) {
            throw new IllegalArgumentException(RESIDENTS_INVALID + residentDTO.getFirstName() + residentDTO.getLastName());
        }

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
    }

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
    private int calculateAge(LocalDate birthDate) {
        return (birthDate != null) ? Period.between(birthDate, LocalDate.now()).getYears() : 0;
    }

}
