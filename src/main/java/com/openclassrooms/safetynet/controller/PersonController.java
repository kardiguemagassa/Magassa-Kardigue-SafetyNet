package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
//@AllArgsConstructor
//@RequestMapping("person")
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

    @PostMapping("/person/saveAll")
    public ResponseEntity<List <PersonDTO>> saveAll(@RequestBody List<PersonDTO> persons) {

        List <PersonDTO> personDTOS = personService.saveAll(persons);
        personService.saveAll(persons);
        return new ResponseEntity<>(personDTOS, HttpStatus.OK);
        //return new ResponseEntity<>(personDTOS, HttpStatus.CREATED);
    }

    @PostMapping("/person/save")
    public ResponseEntity<PersonDTO> save(@RequestBody PersonDTO personDTO) {
        PersonDTO personDTOSaved = personService.save(personDTO);
        //return new ResponseEntity<>(personDTOSaved, HttpStatus.CREATED);
        return new ResponseEntity<>(personDTOSaved, HttpStatus.OK);
    }

    @PutMapping("/person/update")
    public ResponseEntity<PersonDTO> update(@RequestBody PersonDTO updatedPersonDTO) {
        Optional<PersonDTO> updated = personService.update(updatedPersonDTO);
        return new ResponseEntity<>(updated.get(), HttpStatus.OK);
    }

    @DeleteMapping("/person/delete")
    public ResponseEntity<Boolean> deletePerson(
            @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) {

        // http://localhost:8080/person/delete?firstName=John&lastName=Boyd
        boolean isDeleted = personService.deleteByFullName(firstName, lastName);

        if (isDeleted) {
            return ResponseEntity.ok().build(); // Retourne 200 si suppression réussie
        } else {
            return ResponseEntity.notFound().build(); // Retourne 404 si l'entité n'est pas trouvée
        }

    }



    // NEW ENDPOINT
    /*
    http://localhost:8080/childAlert?address=<address>
    http://localhost:8080/person/childAlert?address=1509 Culver St
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
        // inachevé
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
    public ResponseEntity<List<String>> getCommunityEmails(@RequestParam("city") String city) {
        List<String> emails = personService.getCommunityEmails(city);
        return emails.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(emails);
    }
    /*
    @GetMapping("/communityEmailDTO")
    public ResponseEntity<List<PersonDTO>> getCommunityEmailsDTO(@RequestParam("city") String city) {
        List<PersonDTO> emails = personService.getCommunityEmailsDTO(city);
        return new ResponseEntity<>(emails, HttpStatus.OK);
        // retrieve only email == > small bug
    }
     */
}
