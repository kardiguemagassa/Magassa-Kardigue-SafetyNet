package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.exception.person.EmailNotFoundException;
import com.openclassrooms.safetynet.exception.person.PersonNotFoundException;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.repository.PersonRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.openclassrooms.safetynet.constant.service.PersonImpConstant.*;

@Service
@AllArgsConstructor
public class PersonService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final PersonRepository personRepository;
    private final PersonConvertorDTO personConvertorDTO;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordConvertorDTO medicalRecordConvertorDTO;

    public List<PersonDTO> getPersons() throws PersonNotFoundException {

        try {
                /*return personRepository.getPersons().stream()
                    .map(person -> personConvertorDTO.convertEntityToDto(person)) // Lambda expression
                    .collect(Collectors.toList());*/

            List<Person> persons = personRepository.getPersons();

            if (persons == null || persons.isEmpty()) {
                LOGGER.warn(PERSON_NOT_FOUND);
                throw new PersonNotFoundException(PERSON_NOT_FOUND);
            }

            // Conversion entity to DTO
            return persons.stream()
                    .map(personConvertorDTO::convertEntityToDto)
                    .collect(Collectors.toList());

        } catch (PersonNotFoundException e) {
            LOGGER.error(PERSON_NOT_FOUND_MSG, e.getMessage());
            throw e;

        } catch (Exception e) {
            LOGGER.error(ERROR_CONVERTING, e.getMessage(), e);
            throw new RuntimeException(PERSON_UNEXPECT);
        }
    }

    public List<PersonDTO> saveAll(List<PersonDTO> personDTOList) throws PersonNotFoundException {

        if (personDTOList == null || personDTOList.isEmpty()) {
            LOGGER.error(PERSON_ERROR_SAVING);
            throw new IllegalArgumentException(PERSON_ERROR_SAVING);
        }

        try {
            // Convert  DTO to entities
            List<Person> personEntities = personConvertorDTO.convertDtoToEntity(personDTOList);

            // save entities in  repository
            List<Person> savedPersonEntities = personRepository.saveAll(personEntities);

            // Convert  entities save to DTO
            return personConvertorDTO.convertEntityToDto(savedPersonEntities);

        } catch (RuntimeException e) {
            LOGGER.error(PERSON_ERROR_SAVING_DATA_BASE, e.getMessage());
            throw new PersonNotFoundException(PERSON_ERROR_SAVING_DATA_BASE + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(PERSON_ERROR_SAVING_REPO, e.getMessage(), e);
            throw new PersonNotFoundException(PERSON_ERROR_SAVING_REPO, e);
        }
    }

    public PersonDTO save(PersonDTO personDTO) {
        if (personDTO == null) {
            throw new IllegalArgumentException(PERSON_ERROR);
        }

        try {
            // Convert DTO to entity
            Person personEntity = personConvertorDTO.convertDtoToEntity(personDTO);

            // Save entity in repository
            Person savedPersonEntity = personRepository.save(personEntity);

            // Convert saved entity to DTO
            return personConvertorDTO.convertEntityToDto(savedPersonEntity);
        } catch (Exception e) {
            LOGGER.error(PERSON_ERROR_SAVING_C, e.getMessage(), e);
            throw new PersonNotFoundException(PERSON_ERROR_SAVING_DATA_BASE_, e);
        }
    }

    public Optional<PersonDTO> update(PersonDTO updatedPersonDTO) {
        if (updatedPersonDTO == null) {
            throw new IllegalArgumentException(PERSON_ERROR_UPDATING);
        }

        try {
            // Convert DTO to entity
            Person personEntity = personConvertorDTO.convertDtoToEntity(updatedPersonDTO);

            // Update entity in repository
            Optional<Person> updatedPersonEntity = personRepository.update(personEntity);

            if (updatedPersonEntity.isEmpty()) {

                throw new PersonNotFoundException(PERSON_NOT_FOUND_UPDATING);
            }

            // Convert updated entity to DTO
            return updatedPersonEntity.map(personConvertorDTO::convertEntityToDto);

        } catch (Exception e) {
            LOGGER.error(PERSON_ERROR_UPDATING_SUCCESS, e.getMessage(), e);
            throw new PersonNotFoundException(PERSON_ERROR_UPDATING_SUCCESS, e);
        }
    }

    public Boolean deleteByFullName(String firstName, String lastName) throws PersonNotFoundException {

        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException(PERSON_ERROR_DELETING);
        }

        try {
            boolean isDeleted = personRepository.deleteByFullName(firstName, lastName);

            if (!isDeleted) {
                throw new PersonNotFoundException(firstName + " " + lastName + PERSON_ERROR_DELETING_NOT_FOUND);
            }
            return true;

        } catch (PersonNotFoundException e) {
            LOGGER.error(PERSON_ERROR_DELETING_NOT_, e.getMessage());
            throw e;

        }catch (IllegalArgumentException e) {
                throw e;

            } catch (Exception e) {
            throw new PersonNotFoundException(PERSON_ERROR_DELETING_BY_FULL_NAME, e);
        }
    }



    // NEW ENDPOINT
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


    // 1
    public List<Map<String, PersonDTO>> getChildrenByAddress(String address) {
        if (address == null || address.isBlank()) {
            LOGGER.warn("Invalid address: null or empty.");
            //throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address cannot be null or empty.");
        }

        try {
            List<PersonDTO> residents = personRepository.findByAddress(address).stream()
                    .map(personConvertorDTO::convertEntityToDto)
                    .toList();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            return residents.stream()
                    .flatMap(resident -> {
                        MedicalRecord medicalRecord = medicalRecordRepository.findByFullName(resident.getFirstName(), resident.getLastName());

                        if (medicalRecord != null && medicalRecord.getBirthdate() != null) {
                            try {
                                LocalDate birthDate = LocalDate.parse(medicalRecord.getBirthdate(), formatter);
                                int age = personRepository.calculateAge(birthDate);

                                if (age <= 18) {
                                    return residents.stream()
                                            .filter(householdMember -> !householdMember.equals(resident))
                                            .map(householdMember -> Map.of(
                                                    "child", resident,
                                                    "householdMember", householdMember
                                                    //"age",age
                                            ));
                                }
                            } catch (DateTimeParseException e) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid birthdate format for resident: "
                                        + resident.getFirstName() + " " + resident.getLastName(), e);
                            }
                        }
                        return Stream.empty();
                    })
                    .collect(Collectors.toList());
        } catch (ResponseStatusException e) {
            throw e; // Re-throw HTTP-specific exceptions
        } catch (Exception e) {
            LOGGER.error("Error while retrieving children by address: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving children by address.", e);
        }
    }

    // 1
    public List<Map<String, Object>> getChildrenByAddressObject(String address) {
        // Récupérer la liste des résidents à une adresse donnée et les convertir en DTO
        List<PersonDTO> residents = personRepository.findByAddress(address).stream()
                .map(personConvertorDTO::convertEntityToDto)
                .collect(Collectors.toList());

        List<Map<String, Object>> children = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        for (PersonDTO resident : residents) {
            MedicalRecord medicalRecord = medicalRecordRepository.findByFullName(resident.getFirstName(), resident.getLastName());

            if (medicalRecord != null && medicalRecord.getBirthdate() != null) {
                try {
                    LocalDate birthDate = LocalDate.parse(medicalRecord.getBirthdate(), formatter);
                    int age = personRepository.calculateAge(birthDate);

                    if (age <= 18) {
                        // Filtrer les membres du foyer en excluant l'enfant actuel
                        List<Map<String, String>> householdMembers = residents.stream()
                                .filter(r -> !r.equals(resident))
                                .map(r -> Map.of(
                                        "firstName", r.getFirstName(),
                                        "lastName", r.getLastName()
                                ))
                                .collect(Collectors.toList());

                        // Ajouter les informations de l'enfant à la liste des résultats
                        children.add(Map.of(
                                "firstName", resident.getFirstName(),
                                "lastName", resident.getLastName(),
                                "age", age,
                                "householdMembers", householdMembers
                        ));
                    }
                } catch (DateTimeParseException e) {
                    throw new RuntimeException("Invalid birthdate format for resident: " + resident.getFirstName() + " " + resident.getLastName(), e);
                }
            }
        }

        return children;
    }


    // 2
    public List<Map<String, Object>> getPersonInfo(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            LOGGER.warn("Invalid last name: null or empty.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last name cannot be null or empty.");
        }

        try {
            List<PersonDTO> persons = personRepository.findByLastName(lastName).stream()
                    .map(personConvertorDTO::convertEntityToDto)
                    .collect(Collectors.toList());

            if (persons.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No persons found with last name: " + lastName);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            return persons.stream()
                    .map(person -> {
                        MedicalRecord medicalRecord = medicalRecordRepository.findByFullName(person.getFirstName(), person.getLastName());
                        Map<String, Object> personInfo = new HashMap<>();
                        personInfo.put("person", person);

                        if (medicalRecord != null && medicalRecord.getBirthdate() != null) {
                            try {
                                LocalDate birthDate = LocalDate.parse(medicalRecord.getBirthdate(), formatter);
                                int age = personRepository.calculateAge(birthDate);
                                personInfo.put("age", age);
                                personInfo.put("medications", medicalRecord.getMedications());
                                personInfo.put("allergies", medicalRecord.getAllergies());
                            } catch (DateTimeParseException e) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid birthdate format for person: "
                                        + person.getFirstName() + " " + person.getLastName(), e);
                            }
                        }

                        return personInfo;
                    })
                    .collect(Collectors.toList());
        } catch (ResponseStatusException e) {
            throw e; // Re-throw HTTP-specific exceptions
        } catch (Exception e) {
            LOGGER.error("Error while retrieving person info for last name {}: {}", lastName, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving person info.", e);
        }
    }


    // 2
    public List<Map<String, PersonDTO>> getPersonInfoDTO(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            LOGGER.warn("Invalid last name: null or empty.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last name cannot be null or empty.");
        }

        try {
            // Récupérer les résidents par leur nom de famille et les convertir en DTO
            List<PersonDTO> persons = personRepository.findByLastName(lastName).stream()
                    .map(personConvertorDTO::convertEntityToDto)
                    .collect(Collectors.toList());

            if (persons.isEmpty()) {
                LOGGER.info("No persons found with last name: {}", lastName);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No persons found with last name: " + lastName);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            // Enrichir chaque PersonDTO avec des informations médicales
            return persons.stream()
                    .map(person -> {
                        MedicalRecord medicalRecord = medicalRecordRepository.findByFullName(person.getFirstName(), person.getLastName());
                        PersonDTO enrichedPerson = enrichPersonWithMedicalRecord(person, medicalRecord, formatter);

                        // Retourner une map avec la clé "person" et le DTO enrichi
                        return Map.of("person", enrichedPerson);
                    })
                    .collect(Collectors.toList());
        } catch (ResponseStatusException e) {
            throw e; // Propager les exceptions HTTP spécifiques
        } catch (Exception e) {
            LOGGER.error("Unexpected error while retrieving person info for last name {}: {}", lastName, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while retrieving person info.", e);
        }
    }

    private PersonDTO enrichPersonWithMedicalRecord(PersonDTO person, MedicalRecord medicalRecord, DateTimeFormatter formatter) {
        if (medicalRecord != null && medicalRecord.getBirthdate() != null) {
            try {
                // Calcul de l'âge à partir de la date de naissance
                LocalDate birthDate = LocalDate.parse(medicalRecord.getBirthdate(), formatter);
                int age = personRepository.calculateAge(birthDate);

                // Créer une instance de MedicalRecordDTO pour stocker les informations médicales
                MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO();
                medicalRecordDTO.setBirthdate(String.valueOf(age));
                medicalRecordDTO.setMedications(medicalRecord.getMedications());
                medicalRecordDTO.setAllergies(medicalRecord.getAllergies());

                // Ajouter les informations médicales au PersonDTO
                //person.setMedicalRecord(medicalRecordDTO);


            } catch (DateTimeParseException e) {
                String errorMsg = "Invalid birthdate format for person: " + person.getFirstName() + " " + person.getLastName();
                LOGGER.error(errorMsg, e);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg, e);
            }
        } else if (medicalRecord == null) {
            LOGGER.info("No medical record found for person: {} {}", person.getFirstName(), person.getLastName());
        }

        return person;
    }

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



    // 3
    /*
    public List<String> getCommunityEmails(String city) throws PersonNotFoundException, EmailNotFoundException {
        if (city == null || city.isBlank()) {
            LOGGER.warn("Invalid city: null or empty.");
            //throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "City cannot be null or empty.");
        }
        try {
            List<PersonDTO> residents = personRepository.findByCity(city).stream()
                    .map(personConvertorDTO::convertEntityToDto)
                    .toList();
            if (residents.isEmpty()) {
                //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No persons found with last name: " + city);
            }
            return residents.stream()
                    .map(PersonDTO::getEmail)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (ResponseStatusException e) {
            LOGGER.error("Error while retrieving community emails for city {}: {}", city, e.getMessage(), e);
            //throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving community emails.", e);
        }
    }

     */
    /*
    public List<PersonDTO> getCommunityEmailsDTO(String city) {
        if (city == null || city.isBlank()) {
            LOGGER.warn("Invalid city: null or empty.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "City cannot be null or empty.");
        }

        try {
            List<Person> residents = personRepository.findByCity(city);

            if (residents.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No persons found in city: " + city);
            }

            return residents.stream()
                    .filter(person -> person.getEmail() != null)
                    .map(personConvertorDTO::convertEntityToDto)
                    .collect(Collectors.toList());
                    /*
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(
                                    PersonDTO::getEmail,
                                    dto -> dto,
                                    (dto1, dto2) -> dto1,
                                    LinkedHashMap::new
                            ),
                            map -> new ArrayList<>(map.values())
                    ));
                    */
        /*

        } catch (ResponseStatusException e) {
            throw e; // Re-throw HTTP-specific exceptions
        } catch (Exception e) {
            LOGGER.error("Error while retrieving community emails for city {}: {}", city, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving community emails.", e);
        }
    }

         */


}
