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
import lombok.Data;
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
import java.util.stream.Stream;

import static com.openclassrooms.safetynet.constant.service.PersonImpConstant.*;
import static com.openclassrooms.safetynet.constant.service.PersonImpConstant.PERSON_ERROR_EMAIL;

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
            LOGGER.warn("No addresses found for station number {}", stationNumber);
            return new FireStationResponseDTO();
        }

        List<Person> residents = personRepository.findByAddresses(addresses);

        if (residents == null || residents.isEmpty()) {
            LOGGER.warn("No residents found for addresses: {}", addresses);
            return new FireStationResponseDTO();
        }

        return buildFireStationResponse(residents);
    }

    private FireStationResponseDTO buildFireStationResponse(List<Person> residents) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        AtomicInteger adultCount = new AtomicInteger(0);
        AtomicInteger childCount = new AtomicInteger(0);

        List<ResidentInfoDTO> enrichedResidents = residents.stream()
                .map(person -> mapToResidentInfo (person, formatter, adultCount, childCount))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        FireStationResponseDTO response = new FireStationResponseDTO();
        response.setAdultCount(adultCount.get());
        response.setChildCount(childCount.get());
        response.setResidents(enrichedResidents);

        LOGGER.info("Adults: {}, Children: {}", adultCount.get(), childCount.get());

        return response;
    }

    private ResidentInfoDTO mapToResidentInfo(Person person, DateTimeFormatter formatter,
                                          AtomicInteger adultCount, AtomicInteger childCount) {

        var medicalRecord = medicalRecordRepository.findByFullName(person.getFirstName(), person.getLastName());

        if (medicalRecord == null || medicalRecord.getBirthdate() == null) {
            LOGGER.warn("Missing medical record for {} {}", person.getFirstName(), person.getLastName());
            return null;
        }

        try {
            LocalDate birthDate = LocalDate.parse(medicalRecord.getBirthdate(), formatter);
            int age = calculateAge(birthDate);

            if (age > 18) {
                adultCount.incrementAndGet();
            } else {
                childCount.incrementAndGet();
            }

            return new ResidentInfoDTO(person.getFirstName(), person.getLastName(), person.getAddress(), person.getPhone(), age);

        } catch (Exception e) {
            LOGGER.error("Error parsing birthdate for {} {}: {}", person.getFirstName(), person.getLastName(), medicalRecord.getBirthdate(), e);
            return null;
        }
    }


    // 2 FINISH
    public FireStationResponseDTO getChildrenByAddress(String address) {
        // Récupérer les résidents de l'adresse
        List<Person> residents = personRepository.findByAddress(address);

        if (residents == null || residents.isEmpty()) {
            LOGGER.info("No residents found at address: {}", address);
            return buildEmptyResponse();
        }

        return buildChildrenResponse(residents);
    }

    private FireStationResponseDTO buildChildrenResponse(List<Person> residents) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Séparer les enfants des adultes
        List<ResidentInfoDTO> children = new ArrayList<>();
        List<ResidentInfoDTO> householdMembers = new ArrayList<>();

        residents.stream()
                .forEach(resident -> {
                    MedicalRecord medicalRecord = medicalRecordRepository.findByFullName(resident.getFirstName(), resident.getLastName());
                    if (medicalRecord != null && medicalRecord.getBirthdate() != null) {
                        try {
                            LocalDate birthDate = LocalDate.parse(medicalRecord.getBirthdate(), formatter);
                            int age = calculateAge(birthDate);

                            ResidentInfoDTO residentInfo = ResidentInfoDTO.builder()
                                    .firstName(resident.getFirstName())
                                    .lastName(resident.getLastName())
                                    .address(resident.getAddress())
                                    .phone(resident.getPhone())
                                    .age(age)
                                    .build();

                            if (age <= 18) {
                                children.add(residentInfo);
                            } else {
                                householdMembers.add(residentInfo);
                            }
                        } catch (DateTimeParseException e) {
                            LOGGER.error("Invalid birthdate format for resident: {} {}, birthdate: {}",
                                    resident.getFirstName(), resident.getLastName(), medicalRecord.getBirthdate(), e);
                        }
                    }
                });

        // Retourner la réponse
        if (children.isEmpty()) {
            LOGGER.info("No children found at address.");
            return buildEmptyResponse();
        }

        return FireStationResponseDTO.builder()
                .adultCount(householdMembers.size())
                .childCount(children.size())
                .residents(children)
                .build();
    }

    private FireStationResponseDTO buildEmptyResponse() {
        return FireStationResponseDTO.builder()
                .adultCount(0)
                .childCount(0)
                .residents(Collections.emptyList())
                .build();
    }


    // 3 FINISH
    public List<String> getPhoneNumbersByStation(int stationNumber) {
        // Récupérer les adresses associées à la station
        List<String> addresses = fireStationRepository.findAddressesByStationNumber(stationNumber);

        if (addresses == null || addresses.isEmpty()) {
            LOGGER.error("No addresses found for station number {}", stationNumber);
            return Collections.emptyList(); // Aucune adresse trouvée
        }

        // Récupérer les résidents associés aux adresses et les convertir en DTOs
        List<PersonDTO> personDTOs = personRepository.findByAddresses(addresses)
                .stream()
                .map(personConvertorDTO::convertEntityToDto) // Conversion entité -> DTO
                .toList();

        if (personDTOs.isEmpty()) {
            LOGGER.error("No residents found for addresses: {}", addresses);
            return Collections.emptyList(); // Aucun résident trouvé
        }

        // Extraire les numéros de téléphone des DTOs
        return personDTOs.stream()
                .map(PersonDTO::getPhone)
                .filter(Objects::nonNull)
                .distinct() // Éliminer les doublons
                .collect(Collectors.toList());
    }


    // 4 FINISH
    public FireStationResponseDTO getResidentsByAddress(String address) {
        // Récupérer les DTO des résidents à partir du service
        List<PersonDTO> residentsDTOs = personRepository.findByAddress(address).stream()
                .map(personConvertorDTO::convertEntityToDto)
                .collect(Collectors.toList());

        // Récupérer la station associée à l'adresse et la convertir
        FireStation fireStation = fireStationRepository.findByAddress(address);
        FireStationDTO fireStationDTO = fireStation != null ? fireStationConvertorDTO.convertEntityToDto(fireStation) : null;

        // Vérifier si la caserne ou les résidents existent
        if (fireStationDTO == null) {
            throw new IllegalArgumentException("No fire station found for the given address: " + address);
        }

        if (residentsDTOs.isEmpty()) {
            return FireStationResponseDTO.builder()
                    .adultCount(0)
                    .childCount(0)
                    .residents(Collections.emptyList())
                    .build();
        }

        // Format pour les dates dans les dossiers médicaux
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Récupérer les informations médicales et enrichir les résidents
        List<ResidentInfoDTO> residentDetails = residentsDTOs.stream()
                .map(resident -> enrichResidentDetails(resident, formatter, address))
                .filter(Objects::nonNull) // Exclure les résultats nuls
                .collect(Collectors.toList());

        // Calculer le nombre d'adultes et d'enfants
        AtomicInteger adultCount = new AtomicInteger(0);
        AtomicInteger childCount = new AtomicInteger(0);

        residentDetails.forEach(resident -> {
            if (resident.getAge() <= 18) {
                childCount.incrementAndGet();
            } else {
                adultCount.incrementAndGet();
            }
        });

        // Retourner la réponse avec les détails des résidents et le numéro de station
        return FireStationResponseDTO.builder()
                .adultCount(adultCount.get())
                .childCount(childCount.get())
                .residents(residentDetails)
                .build();
    }

    private ResidentInfoDTO enrichResidentDetails(PersonDTO resident, DateTimeFormatter formatter, String address) {
        // Récupérer les informations médicales pour chaque résident
        MedicalRecord medicalRecord = medicalRecordRepository.findByFullName(resident.getFirstName(), resident.getLastName());

        if (medicalRecord == null || medicalRecord.getBirthdate() == null) {
            LOGGER.warn("Missing medical record for {} {}", resident.getFirstName(), resident.getLastName());
            return null;
        }

        try {
            // Calculer l'âge du résident
            LocalDate birthDate = LocalDate.parse(medicalRecord.getBirthdate(), formatter);
            int age = calculateAge(birthDate);

            // Créer le DTO de ResidentInfo avec les informations médicales
            MedicalRecordDTO medicalRecordDTO = MedicalRecordDTO.builder()
                    .firstName(resident.getFirstName())
                    .lastName(resident.getLastName())
                    .birthdate(medicalRecord.getBirthdate())
                    .medications(medicalRecord.getMedications())
                    .allergies(medicalRecord.getAllergies())
                    .build();

            return ResidentInfoDTO.builder()
                    .firstName(resident.getFirstName())
                    .lastName(resident.getLastName())
                    .address(address)
                    .phone(resident.getPhone())
                    .age(age)
                    .medicalRecord(medicalRecordDTO)
                    .build();
        } catch (Exception e) {
            LOGGER.error("Error parsing birthdate for {} {}: {}", resident.getFirstName(), resident.getLastName(), medicalRecord.getBirthdate(), e);
            return null;
        }
    }


    // 5 FINISH
    public Map<String, List<ResidentInfoDTO>> getFloodInfoByStations(List<Integer> stationNumbers) {
        // Récupérer les adresses par numéro de station
        List<String> addresses = fireStationRepository.findAddressesByStationNumbers(stationNumbers);

        if (addresses.isEmpty()) {
            LOGGER.warn("No addresses found for the provided station numbers.");
            return Collections.emptyMap(); // Retourner une Map vide si aucune adresse
        }

        // Construire les informations pour chaque foyer et les regrouper par adresse
        return addresses.stream()
                .collect(Collectors.toMap(
                        address -> address, // Clé : adresse
                        address -> getResidentsInfoByAddress(address) // Valeur : liste des résidents
                ));
    }

    private List<ResidentInfoDTO> getResidentsInfoByAddress(String address) {
        // Récupérer la liste des résidents pour l'adresse donnée
        List<PersonDTO> residents = personRepository.findByAddress(address).stream()
                .map(personConvertorDTO::convertEntityToDto)
                .collect(Collectors.toList());

        if (residents.isEmpty()) {
            return Collections.emptyList(); // Si aucun résident n'est trouvé
        }

        // DateTimeFormatter pour parser la date de naissance
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Transformer les résidents en une liste enrichie avec leurs détails et antécédents médicaux
        return residents.stream()
                .map(resident -> enrichResidentDetailsr(resident, formatter, address))
                .filter(Objects::nonNull) // Exclure les résultats nuls
                .collect(Collectors.toList());
    }

    private ResidentInfoDTO enrichResidentDetailsr(PersonDTO resident, DateTimeFormatter formatter, String address) {
        // Récupérer les informations médicales pour chaque résident
        MedicalRecord medicalRecord = medicalRecordRepository.findByFullName(resident.getFirstName(), resident.getLastName());

        if (medicalRecord == null || medicalRecord.getBirthdate() == null) {
            LOGGER.warn("Missing medical record for {} {}", resident.getFirstName(), resident.getLastName());
            return null;
        }

        try {
            // Calculer l'âge du résident
            LocalDate birthDate = LocalDate.parse(medicalRecord.getBirthdate(), formatter);
            int age = calculateAge(birthDate);

            // Créer le DTO de MedicalRecordDTO avec les informations médicales
            MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO(
                    resident.getFirstName(),
                    resident.getLastName(),
                    medicalRecord.getBirthdate(),
                    medicalRecord.getMedications(),
                    medicalRecord.getAllergies()
            );

            // Créer et retourner le DTO de ResidentInfo avec les informations médicales
            return new ResidentInfoDTO(
                    resident.getFirstName(),
                    resident.getLastName(),
                    address,
                    resident.getPhone(),
                    age,
                    medicalRecordDTO  // Passer medicalRecordDTO ici
            );
        } catch (Exception e) {
            LOGGER.error("Error parsing birthdate for {} {}: {}", resident.getFirstName(), resident.getLastName(), medicalRecord.getBirthdate(), e);
            return null;
        }
    }


    // 6 FINISH
    public List<ResidentInfoDTO> getPersonInfo(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            LOGGER.warn("Invalid last name: null or empty.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last name cannot be null or empty.");
        }

        try {
            // Récupérer la liste des personnes par nom de famille
            List<PersonDTO> persons = personRepository.findByLastName(lastName).stream()
                    .map(personConvertorDTO::convertEntityToDto)
                    .collect(Collectors.toList());

            if (persons.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No persons found with last name: " + lastName);
            }

            // DateTimeFormatter pour la date de naissance
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            // Transformer les informations des personnes en ResidentInfoDTO avec leurs antécédents médicaux
            return persons.stream()
                    .map(person -> {
                        // Récupérer les antécédents médicaux
                        MedicalRecord medicalRecord = medicalRecordRepository.findByFullName(person.getFirstName(), person.getLastName());

                        // Créer un objet ResidentInfoDTO
                        MedicalRecordDTO medicalRecordDTO = null;
                        if (medicalRecord != null) {
                            medicalRecordDTO = new MedicalRecordDTO(
                                    person.getFirstName(),
                                    person.getLastName(),
                                    medicalRecord.getBirthdate(),
                                    medicalRecord.getMedications(),
                                    medicalRecord.getAllergies()
                            );
                        }

                        // Calculer l'âge du résident à partir de la date de naissance
                        int age = 0;
                        if (medicalRecord != null && medicalRecord.getBirthdate() != null) {
                            try {
                                LocalDate birthDate = LocalDate.parse(medicalRecord.getBirthdate(), formatter);
                                age = personRepository.calculateAge(birthDate);
                            } catch (DateTimeParseException e) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid birthdate format for person: "
                                        + person.getFirstName() + " " + person.getLastName(), e);
                            }
                        }

                        // Créer le DTO ResidentInfoDTO avec toutes les informations nécessaires
                        return new ResidentInfoDTO(
                                person.getFirstName(),
                                person.getLastName(),
                                person.getAddress(),
                                person.getPhone(),
                                age,
                                medicalRecordDTO
                        );
                    })
                    .collect(Collectors.toList());
        } catch (ResponseStatusException e) {
            throw e; // Re-throw HTTP-specific exceptions
        } catch (Exception e) {
            LOGGER.error("Error while retrieving person info for last name {}: {}", lastName, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving person info.", e);
        }
    }


    // 7 FINISH
    public List<String> getCommunityEmails(String city) throws PersonNotFoundException, EmailNotFoundException {

        if (city == null || city.isBlank()) {
            //LOGGER.warn(EMAIL_NOT_FOUND);
            throw new IllegalArgumentException(CITY_NOT_FOUND);
        }

        try {
            // Récupération des résidents de la ville
            List<PersonDTO> residents = personRepository.findByCity(city).stream()
                    .map(personConvertorDTO::convertEntityToDto)
                    .toList();

            // Vérification si la liste des résidents est vide
            if (residents.isEmpty()) {
                LOGGER.error(RESIDENTS_NOT_FOUND, city);
                throw new PersonNotFoundException(RESIDENTS_NOT_FOUND + city);
            }

            // Extraction des emails distincts
            List<String> emails = residents.stream()
                    .map(PersonDTO::getEmail)
                    .filter(email -> email != null && !email.isBlank())
                    .distinct()
                    .collect(Collectors.toList());

            // Vérification si aucun email n'a été trouvé
            if (emails.isEmpty()) {
                LOGGER.error(EMAIL_NOT_FOUND, city);
                throw new EmailNotFoundException(EMAIL_NOT_FOUND + city);
            }

            return emails;

        } catch (RuntimeException e) {
            LOGGER.error(PERSON_ERROR_EMAIL, city, e.getMessage(), e);
            throw new RuntimeException(PERSON_ERROR_EMAIL, e);
        }
    }


    // Méthode utilitaire pour calculer l'âge à partir de la date de naissance
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }



    // Avoir
    public List<PersonDTO> findByAddresses(List<String> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            LOGGER.warn("Invalid addresses: null or empty list provided.");
            //throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Addresses list cannot be null or empty.");
        }

        try {
            return personRepository.findByAddresses(addresses).stream()
                    .map(personConvertorDTO::convertEntityToDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            LOGGER.error("Error while finding persons by addresses: {}", e.getMessage(), e);
            //throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching persons by addresses.", e);
        }
        return new ArrayList<>();
    }

}
