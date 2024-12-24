package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//@AllArgsConstructor
public class PersonController {

    private final PersonService personService;

    // Constructor for dependency injection
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/persons")
    public ResponseEntity<List<PersonDTO>> getPersons() {
        List<PersonDTO> personDTOS = personService.getPersons();
        return new ResponseEntity<>(personDTOS, HttpStatus.OK);
    }

}
