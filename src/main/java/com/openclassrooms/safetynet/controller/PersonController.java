package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.exception.ExceptionHandling;

import com.openclassrooms.safetynet.exception.person.PersonNotFoundException;
import com.openclassrooms.safetynet.model.HttpResponse;
import com.openclassrooms.safetynet.service.PersonService;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;


@RestController
@AllArgsConstructor
public class PersonController extends ExceptionHandling {

    private final PersonService personService;
    //private final ErrorAttributes errorAttributes;

    @GetMapping("/person")
    public ResponseEntity<List<PersonDTO>> getPersons() {
        try {
            List<PersonDTO> personDTOS = personService.getPersons();
            return ResponseEntity.ok(personDTOS);
        } catch (PersonNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    /*
    @PostMapping("/person")
    public ResponseEntity<List <PersonDTO>> saveAll(@RequestBody List<PersonDTO> persons) {

        List <PersonDTO> personDTOS = personService.saveAll(persons);
        return new ResponseEntity<>(personDTOS, HttpStatus.CREATED);
    }
    */

    @PostMapping("/person")
    public ResponseEntity<PersonDTO> save(@RequestBody PersonDTO personDTO)
            {
        PersonDTO personDTOSaved = personService.save(personDTO);
        //return new ResponseEntity<>(personDTOSaved, HttpStatus.CREATED);
        //return new ResponseEntity<>(personDTOSaved, HttpStatus.OK);
        return ResponseEntity.ok().body(personDTOSaved);
    }

    @PutMapping("/person")
    public ResponseEntity<PersonDTO> update(@RequestBody PersonDTO updatedPersonDTO) throws PersonNotFoundException {
        Optional<PersonDTO> updated = personService.update(updatedPersonDTO);
        //return new ResponseEntity<>(updated.get(), HttpStatus.OK);
        return ResponseEntity.ok().body(updated.get());
    }

    @DeleteMapping("/person")
    public ResponseEntity<HttpResponse> deleteByFullName(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName, WebRequest request)
            throws PersonNotFoundException, IllegalArgumentException {

        // http://localhost:8080/person?firstName=John&lastName=Boyd
        boolean deleted = personService.deleteByFullName(firstName, lastName);
        return response(NO_CONTENT, "User deleted successfully", request);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message, WebRequest request) {
        HttpResponse httpResponse = new HttpResponse(
                httpStatus.value(),
                httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(httpResponse, httpStatus);
    }
}
