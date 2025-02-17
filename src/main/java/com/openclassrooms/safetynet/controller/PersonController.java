package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.PersonDTO;

import com.openclassrooms.safetynet.model.HttpResponse;
import com.openclassrooms.safetynet.service.PersonService;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;


@RestController
@AllArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping("/person")
    public ResponseEntity<List<PersonDTO>> getPersons() {
        List<PersonDTO> personDTOS = personService.getPersons();
        return new ResponseEntity<>(personDTOS, HttpStatus.OK);
    }

    @PostMapping("/person")
    public ResponseEntity<PersonDTO> save(@RequestBody PersonDTO personDTO) {
        PersonDTO personDTOSaved = personService.save(personDTO);
        return new ResponseEntity<>(personDTOSaved, HttpStatus.CREATED);
    }

    @PutMapping("/person")
    public ResponseEntity<PersonDTO> update(@RequestBody PersonDTO updatedPersonDTO) {
        Optional<PersonDTO> updated = personService.update(updatedPersonDTO);
        return new ResponseEntity<>(updated.get(), HttpStatus.OK);
    }

    @DeleteMapping("/person")
    public ResponseEntity<HttpResponse> deleteByFullName(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName, WebRequest request) {

        // http://localhost:8080/person?firstName=John&lastName=Boyd
        personService.deleteByFullName(firstName, lastName);
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
