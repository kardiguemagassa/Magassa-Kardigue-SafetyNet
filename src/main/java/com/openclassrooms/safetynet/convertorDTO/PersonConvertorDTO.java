package com.openclassrooms.safetynet.convertorDTO;

import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.model.Person;

import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Builder
public class PersonConvertorDTO {

    // Convert une entity Person en un DTO
    public PersonDTO convertEntityToDto(Person person) {

        return PersonDTO.builder()
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .email(person.getEmail())
                .address(person.getAddress())
                .city(person.getCity())
                .zip(person.getZip())
                .phone(person.getPhone())
                .build();
    }

    // Convert un DTO entities Person
    public Person convertDtoToEntity(PersonDTO personDTO) {

        return Person.builder()
                .firstName(personDTO.getFirstName())
                .lastName(personDTO.getLastName())
                .email(personDTO.getEmail())
                .address(personDTO.getAddress())
                .city(personDTO.getCity())
                .zip(personDTO.getZip())
                .phone(personDTO.getPhone())
                .build();
    }

    // Convert list entities Person list DTOs
    public List<PersonDTO> convertEntityToDto(List<Person> entities) {
        return entities.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    // Convert list DTOs list entities Person
    public List<Person> convertDtoToEntity(List<PersonDTO> personDTOS) {
        return personDTOS.stream()
                .map(this::convertDtoToEntity)
                .collect(Collectors.toList());
    }
}
