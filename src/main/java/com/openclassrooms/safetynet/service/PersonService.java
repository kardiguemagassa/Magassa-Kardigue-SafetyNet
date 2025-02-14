package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.exception.person.PersonNotFoundException;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.PersonRepository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.openclassrooms.safetynet.constant.service.PersonImpConstant.*;

@Service
@AllArgsConstructor
public class PersonService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final PersonRepository personRepository;
    private final PersonConvertorDTO personConvertorDTO;


    public List<PersonDTO> getPersons() throws PersonNotFoundException {

            List<Person> persons = personRepository.getPersons();

            if (persons == null || persons.isEmpty()) {
                LOGGER.warn(PERSON_NOT_FOUND);
                throw new PersonNotFoundException(PERSON_NOT_FOUND);
            }

            // Conversion entity to DTO
            return persons.stream()
                    .map(personConvertorDTO::convertEntityToDto)
                    .collect(Collectors.toList());
    }

    public PersonDTO save(PersonDTO personDTO) {

        if (personDTO == null) {
            throw new IllegalArgumentException(PERSON_ERROR);
        }

        // Convert DTO to entity
        Person personEntity = personConvertorDTO.convertDtoToEntity(personDTO);

        // Save entity in repository
        Person savedPersonEntity = personRepository.save(personEntity);

        // Convert saved entity to DTO
        return personConvertorDTO.convertEntityToDto(savedPersonEntity);
    }

    public Optional<PersonDTO> update(PersonDTO updatedPersonDTO) {

        if (updatedPersonDTO == null) {
            throw new IllegalArgumentException(PERSON_ERROR_UPDATING);
        }

        // Convert DTO to entity
        Person personEntity = personConvertorDTO.convertDtoToEntity(updatedPersonDTO);

        // Update entity in repository
        Optional<Person> updatedPersonEntity = personRepository.update(personEntity);

        if (updatedPersonEntity.isEmpty()) {
            throw new PersonNotFoundException(PERSON_NOT_FOUND_UPDATING);
        }

        // Convert updated entity to DTO
        return updatedPersonEntity.map(personConvertorDTO::convertEntityToDto);
    }

    public Boolean deleteByFullName(String firstName, String lastName) throws PersonNotFoundException {

        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException(PERSON_ERROR_DELETING);
        }

        boolean isDeleted = personRepository.deleteByFullName(firstName, lastName);

            if (!isDeleted) {
                throw new PersonNotFoundException(firstName + " " + lastName + PERSON_ERROR_DELETING_NOT_FOUND);
            }
        return true;
    }

}
