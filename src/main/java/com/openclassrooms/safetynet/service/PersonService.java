package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.repository.PersonRepository;
import lombok.AllArgsConstructor;
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

@Service
//@AllArgsConstructor
public class PersonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository personRepository;
    private final PersonConvertorDTO personConvertorDTO;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordConvertorDTO medicalRecordConvertorDTO;

    public PersonService(PersonRepository personRepository, PersonConvertorDTO personConvertorDTO,
                         MedicalRecordRepository medicalRecordRepository, MedicalRecordConvertorDTO medicalRecordConvertorDTO) {
        this.personRepository = personRepository;
        this.personConvertorDTO = personConvertorDTO;
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalRecordConvertorDTO = medicalRecordConvertorDTO;
    }

    public List<PersonDTO> getPersons() {
        try {
            /*
                return personRepository.getPersons().stream()
                    .map(person -> personConvertorDTO.convertEntityToDto(person)) // Lambda pour appeler la méthode
                    .collect(Collectors.toList());
            */

            List<Person> persons = personRepository.getPersons();

            if (persons == null || persons.isEmpty()) {
                LOGGER.warn("No persons found in the repository.");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No persons found.");
            }

            return persons.stream()
                    .map(personConvertorDTO::convertEntityToDto)
                    .collect(Collectors.toList());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error while retrieving and converting persons: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while retrieving the persons.", e);
        }
    }

    public List<PersonDTO> saveAll (List<PersonDTO> personDTOList) {

        try {
            if (personDTOList == null || personDTOList.isEmpty()) {
                throw new IllegalArgumentException("Person list cannot be null or empty.");
            }

            // Convert DTO en entity
            List<Person> personEntities = personConvertorDTO.convertDtoToEntity(personDTOList);

            // Save entities in repository
            List<Person> savedPersonEntities = personRepository.saveAll(personEntities);

            // Convert les entities save DTO
            return personConvertorDTO.convertEntityToDto(savedPersonEntities);

        } catch (IllegalArgumentException e) {
            LOGGER.error("Error saving personList: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid person data: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            LOGGER.error("Runtime error during saving persons: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Database/system error while saving persons: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error saving persons: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while saving persons.", e);
        }
    }

    public PersonDTO save(PersonDTO personDTO) {
        try {
            if (personDTO == null) {
                throw new IllegalArgumentException("PersonDTO cannot be null.");
            }

            // Convert DTO  entity
            Person personEntity = personConvertorDTO.convertDtoToEntity(personDTO);

            // Save entity in repository
            Person savedPersonEntity = personRepository.save(personEntity);

            // Convert entity save DTO
            return personConvertorDTO.convertEntityToDto(savedPersonEntity);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Error person: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid person data: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error saving person: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while saving the person.", e);
        }
    }

    public Optional<PersonDTO> update(PersonDTO updatedPersonDTO) {

        try {
            if (updatedPersonDTO == null) {
                throw new IllegalArgumentException("Updated person data cannot be null.");
            }

            // Convert le DTO entity
            Person personEntity = personConvertorDTO.convertDtoToEntity(updatedPersonDTO);

            // Save entity in repository
            Optional<Person> savedPersonEntity = personRepository.update(personEntity);

            // Check if an entity has been saved
            return savedPersonEntity.map(personConvertorDTO::convertEntityToDto);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Validation error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid person data: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error updating person: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while updating the person.", e);
        }
    }

    public Boolean deleteByFullName(String firstName, String lastName) {

        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            LOGGER.warn("Invalid parameters: firstName or lastName is null/empty. Cannot perform deletion.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First name or last name cannot be null or empty.");
        }

        try {
            boolean isDeleted = personRepository.deleteByFullName(firstName, lastName);

            LOGGER.info(isDeleted ?
                    "Person {} {} deleted successfully via repository." :
                    "Person {} {} not found for deletion in repository.", firstName, lastName);

            if (!isDeleted) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found for deletion.");
            }

            return true;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error while deleting person {} {} via repository: {}", firstName, lastName, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while deleting the person.", e);
        }
    }

    // NEW ENDPOINT
    public List<PersonDTO> findByAddresses(List<String> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            LOGGER.warn("Invalid addresses: null or empty list provided.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Addresses list cannot be null or empty.");
        }

        try {
            return personRepository.findByAddresses(addresses).stream()
                    .map(personConvertorDTO::convertEntityToDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            LOGGER.error("Error while finding persons by addresses: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching persons by addresses.", e);
        }
    }


    // 1
    public List<Map<String, PersonDTO>> getChildrenByAddress(String address) {
        if (address == null || address.isBlank()) {
            LOGGER.warn("Invalid address: null or empty.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address cannot be null or empty.");
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


    // 3
    public List<String> getCommunityEmails(String city) {
        if (city == null || city.isBlank()) {
            LOGGER.warn("Invalid city: null or empty.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "City cannot be null or empty.");
        }
        try {
            List<PersonDTO> residents = personRepository.findByCity(city).stream()
                    .map(personConvertorDTO::convertEntityToDto)
                    .toList();
            if (residents.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No persons found with last name: " + city);
            }
            return residents.stream()
                    .map(PersonDTO::getEmail)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (ResponseStatusException e) {
            LOGGER.error("Error while retrieving community emails for city {}: {}", city, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving community emails.", e);
        }
    }
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
