package com.openclassrooms.safetynet.service;

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




}
