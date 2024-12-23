package com.openclassrooms.safetynet.convertorDTO;

import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.model.Person;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonConvertorDTO {

    // Convertit une entité Person en un DTO
    public PersonDTO convertEntityToDto(Person person) {
        /*
        return PersonDTO.builder()
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .email(person.getEmail())
                .address(person.getAddress())
                .city(person.getCity())
                .zip(person.getZip())
                .phone(person.getPhone())
                .build();

         */
        return new PersonDTO(
                person.getFirstName(),
                person.getLastName(),
                person.getEmail(),
                person.getAddress(),
                person.getCity(),
                person.getZip(),
                person.getPhone()
        );
    }

    // Convertit un DTO en une entité Person
    public Person convertDtoToEntity(PersonDTO personDTO) {
        /*
        return Person.builder()
                .firstName(personDTO.getFirstName())
                .lastName(personDTO.getLastName())
                .email(personDTO.getEmail())
                .address(personDTO.getAddress())
                .city(personDTO.getCity())
                .zip(personDTO.getZip())
                .phone(personDTO.getPhone())
                .build();

         */
        return new Person(
                personDTO.getFirstName(),
                personDTO.getLastName(),
                personDTO.getAddress(),
                personDTO.getEmail(),
                personDTO.getCity(),
                personDTO.getAddress(),
                personDTO.getPhone()
        );
    }

    // Convertit une liste d'entités Person en une liste de DTOs
    public List<PersonDTO> convertEntityToDto(List<Person> entities) {
        return entities.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    // Convertit une liste de DTOs en une liste d'entités Person
    public List<Person> convertDtoToEntity(List<PersonDTO> dtos) {
        return dtos.stream()
                .map(this::convertDtoToEntity)
                .collect(Collectors.toList());
    }
}
