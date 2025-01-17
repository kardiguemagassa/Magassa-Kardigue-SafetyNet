package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.api.FireStationResponseDTO;
import com.openclassrooms.safetynet.dto.api.ResidentInfoDTO;
import com.openclassrooms.safetynet.exception.person.EmailNotFoundException;
import com.openclassrooms.safetynet.exception.person.PersonNotFoundException;
import com.openclassrooms.safetynet.service.ApiService;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ApiController {

    private final ApiService apiService;

    /* 1
    http://localhost:8080/firestation?stationNumber=<station_number>
    Cette url doit retourner une liste des personnes couvertes par la caserne de pompiers
    correspondante. Donc, si le numéro de station = 1, elle doit renvoyer les habitants
    couverts par la station numéro 1. La liste doit inclure les informations spécifiques
    suivantes : prénom, nom, adresse, numéro de téléphone. De plus, elle doit fournir un
    décompte du nombre d'adultes et du nombre d'enfants (tout individu âgé de 18 ans ou
    moins) dans la zone desservie.
    http://localhost:8080/firestation/addressNumber?stationNumber=1
    */
    @GetMapping("/firestation/addressNumber")
    public ResponseEntity<FireStationResponseDTO> getPersonsByStation(@RequestParam("stationNumber") int stationNumber) {

        FireStationResponseDTO fireStationDTO = apiService.getPersonsByStation(stationNumber);
        return new ResponseEntity<>(fireStationDTO, HttpStatus.OK);
    }

    /*  2
    http://localhost:8080/childAlert?address=<address>
    http://localhost:8080/childAlert?address=1509 Culver St
    Cette url doit retourner une liste d'enfants (tout individu âgé de 18 ans ou moins)
    habitant à cette adresse. La liste doit comprendre le prénom et le nom de famille de
    chaque enfant, son âge et une liste des autres membres du foyer. S'il n'y a pas
    d'enfant, cette url peut renvoyer une chaîne vide.
    */
    @GetMapping("/childAlert")
    public ResponseEntity<FireStationResponseDTO> getChildrenByAddressObject(@RequestParam("address") String address) {
        FireStationResponseDTO children = apiService.getChildrenByAddress(address);
        return new ResponseEntity<>(children, HttpStatus.OK);
    }

    /* 3
    http://localhost:8080/phoneAlert?firestation=<firestation_number>
    Cette url doit retourner une liste des numéros de téléphone des résidents desservis
    par la caserne de pompiers. Nous l'utiliserons pour envoyer des messages texte
    d'urgence à des foyers spécifiques.
    http://localhost:8080/phoneAlert?firestation=1
   */
    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneNumbersByStation(@RequestParam("firestation") int stationNumber) {
        List<String> phoneNumbers = apiService.getPhoneNumbersByStation(stationNumber);
        return new ResponseEntity<>(phoneNumbers, HttpStatus.OK);
    }

    /* 4
    http://localhost:8080/fire?address=<address>
    Cette url doit retourner la liste des habitants vivant à l’adresse donnée ainsi que le
    numéro de la caserne de pompiers la desservant. La liste doit inclure le nom, le
    numéro de téléphone, l'âge et les antécédents médicaux (médicaments, posologie et
    allergies) de chaque personne
    http://localhost:8080/fire?address=1509 Culver St
   */
    @GetMapping("/fire")
    public ResponseEntity<FireStationResponseDTO> getResidentsByAddress(@RequestParam("address") String address) {

        FireStationResponseDTO fireStationResponseDTO = apiService.getResidentsByAddress(address);

        return new ResponseEntity<>(fireStationResponseDTO, HttpStatus.OK);
    }

    /* 5
    http://localhost:8080/flood/stations?stations=<a list of
    station_numbers>
    Cette url doit retourner une liste de tous les foyers desservis par la caserne. Cette
    liste doit regrouper les personnes par adresse. Elle doit aussi inclure le nom, le
    numéro de téléphone et l'âge des habitants, et faire figurer leurs antécédents
    médicaux (médicaments, posologie et allergies) à côté de chaque nom.
    http://localhost:8080/flood/stations?stations=1,2,3
    */
    @GetMapping("/flood/stations")
    public ResponseEntity<Map<String, List<ResidentInfoDTO>>> getFloodInfo (@RequestParam("stations") List < Integer > stations) {

        Map<String, List<ResidentInfoDTO>> response = apiService.getFloodInfoByStations(stations);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /* 6
    http://localhost:8080/personInfolastName=<lastName>
    http://localhost:8080/personInfolastName?lastName=Boyd
    Cette url doit retourner le nom, l'adresse, l'âge, l'adresse mail et les antécédents
    médicaux (médicaments, posologie et allergies) de chaque habitant. Si plusieurs
    personnes portent le même nom, elles doivent toutes apparaître.
    */
    //@GetMapping("/flood/stations")
    @GetMapping("/personInfolastName")
    public ResponseEntity<List <ResidentInfoDTO>>getPersonInfo (@RequestParam String lastName) {

        List<ResidentInfoDTO> residentInfoDTOS = apiService.getPersonInfo(lastName);

        return new ResponseEntity<>(residentInfoDTOS, HttpStatus.OK);
    }

    /* 7
    http://localhost:8080/communityEmail?city=<city>
    http://localhost:8080/communityEmail?city=Culver
    Cette url doit retourner les adresses mail de tous les habitants de la ville
    */
    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getCommunityEmails(@RequestParam("city") String city)
            throws EmailNotFoundException, PersonNotFoundException, IllegalArgumentException {

        List<String> emails = apiService.getCommunityEmails(city);

        return ResponseEntity.ok(emails);
    }

}
