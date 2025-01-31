package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.exception.residentInfo.EmailNotFoundException;
import com.openclassrooms.safetynet.exception.person.PersonNotFoundException;
import com.openclassrooms.safetynet.service.PersonInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class PersonInfoController {

    private final PersonInfoService cityEmailAndPhoneNumberByStationService;


    /* 3
    http://localhost:8080/phoneAlert?firestation=<firestation_number>
    Cette url doit retourner une liste des numéros de téléphone des résidents desservis
    par la caserne de pompiers. Nous l'utiliserons pour envoyer des messages texte
    d'urgence à des foyers spécifiques.
    http://localhost:8080/phoneAlert?firestation=1
   */
    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneNumbersByStation(@RequestParam("firestation") int stationNumber) {

        List<String> phoneNumbers = cityEmailAndPhoneNumberByStationService.getPhoneNumbersByStation(stationNumber);
        return new ResponseEntity<>(phoneNumbers, HttpStatus.OK);
    }

    /* 7
    http://localhost:8080/communityEmail?city=<city>
    http://localhost:8080/communityEmail?city=Culver
    Cette url doit retourner les adresses mail de tous les habitants de la ville
    */
    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getCommunityEmails(@RequestParam("city") String city)
            throws EmailNotFoundException, PersonNotFoundException, IllegalArgumentException {

        List<String> emails = cityEmailAndPhoneNumberByStationService.getCommunityEmails(city);
        return ResponseEntity.ok(emails);
    }
}
