package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.exception.ExceptionHandling;
//import com.openclassrooms.safetynet.exception.controller.ResourceNotFoundException;
//import com.openclassrooms.safetynet.exception.controller.ResourceNotFoundException;
import com.openclassrooms.safetynet.exception.person.EmailNotFoundException;
import com.openclassrooms.safetynet.exception.person.PersonNotFoundException;
import com.openclassrooms.safetynet.model.HttpResponse;
import com.openclassrooms.safetynet.service.PersonService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

//@RequestMapping("person")
@RestController
@AllArgsConstructor
public class PersonController extends ExceptionHandling {

    private final PersonService personService;
    private final ErrorAttributes errorAttributes;

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

        // http://localhost:8080/person/delete?firstName=John&lastName=Boyd
        boolean deleted = personService.deleteByFullName(firstName, lastName);
        return response(OK, "User deleted successfully", request);
    }


    // NEW ENDPOINT
    /*
    http://localhost:8080/childAlert?address=<address>
    http://localhost:8080/childAlert?address=1509 Culver St
    Cette url doit retourner une liste d'enfants (tout individu âgé de 18 ans ou moins)
    habitant à cette adresse. La liste doit comprendre le prénom et le nom de famille de
    chaque enfant, son âge et une liste des autres membres du foyer. S'il n'y a pas
    d'enfant, cette url peut renvoyer une chaîne vide.
     */
    @GetMapping("/childAlert")
    public ResponseEntity<List<Map<String, PersonDTO>>> getChildrenByAddress(@RequestParam String address) {
        List<Map<String, PersonDTO>> children = personService.getChildrenByAddress(address);
        return new ResponseEntity<>(children, HttpStatus.OK);
        //
        // //age : absent
    }
    @GetMapping("/childAlerts")
    public ResponseEntity<List<Map<String, Object>>> getChildrenByAddressObject(@RequestParam("address") String address) {
        List<Map<String, Object>> children = personService.getChildrenByAddressObject(address);
        return children.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(children);
    }


    /*
    http://localhost:8080/personInfolastName=<lastName>
    http://localhost:8080/personInfolastName?lastName=Boyd
    Cette url doit retourner le nom, l'adresse, l'âge, l'adresse mail et les antécédents
    médicaux (médicaments, posologie et allergies) de chaque habitant. Si plusieurs
    personnes portent le même nom, elles doivent toutes apparaître.
    */
    //@GetMapping("/flood/stations")
    @GetMapping("/personInfolastName")
    public ResponseEntity<List <Map<String,Object>>>getPersonInfo (@RequestParam String lastName) {
        List<Map<String,Object>> personInfo = personService.getPersonInfo(lastName);
        // OK
        return new ResponseEntity<>(personInfo, HttpStatus.OK);
    }

    @GetMapping("/personInfoDTO")
    public ResponseEntity<List<Map<String, PersonDTO>>> getPersonInfoDTO(@RequestParam("lastName") String lastName) {
        List<Map<String, PersonDTO>> personInfo = personService.getPersonInfoDTO(lastName);
        //age : absent
        return new ResponseEntity<>(personInfo, HttpStatus.OK);
    }

    /*
    http://localhost:8080/communityEmail?city=<city>
    http://localhost:8080/communityEmail?city=Culver
    Cette url doit retourner les adresses mail de tous les habitants de la ville
    */
    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getCommunityEmails(@RequestParam("city") String city)
            throws EmailNotFoundException, PersonNotFoundException, IllegalArgumentException {
        List<String> emails = personService.getCommunityEmails(city);
        return ResponseEntity.ok(emails);
    }

    /*
    @GetMapping("/communityEmailDTO")
    public ResponseEntity<List<PersonDTO>> getCommunityEmailsDTO(@RequestParam("city") String city) {
        List<PersonDTO> emails = personService.getCommunityEmailsDTO(city);
        return new ResponseEntity<>(emails, HttpStatus.OK);
        // retrieve only email == > small bug
    }
     */

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
