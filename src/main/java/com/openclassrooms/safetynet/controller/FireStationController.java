package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.FireStationDTO;
import com.openclassrooms.safetynet.service.FireStationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class FireStationController {

    private final FireStationService fireStationService;

    @GetMapping("/firestation")
    public ResponseEntity<List<FireStationDTO>> getFireStations() {
        List<FireStationDTO> fireStations = fireStationService.getFireStations();
        return new ResponseEntity<>(fireStations, HttpStatus.OK);
        //http://localhost:8080/fireStations
    }

    @PostMapping("/firestation")
    public ResponseEntity<List<FireStationDTO>> saveAll(@RequestBody List<FireStationDTO> fireStations) {
        List<FireStationDTO> savedFireStations = fireStationService.saveAll(fireStations);
        return new ResponseEntity<>(savedFireStations, HttpStatus.CREATED);
        // http://localhost:8080/firestation
    }
    /*
    @PostMapping("/firestation/save")
    public ResponseEntity<FireStationDTO> save(@RequestBody FireStationDTO fireStation) {
        FireStationDTO savedFireStation = fireStationService.save(fireStation);
        return new ResponseEntity<>(savedFireStation, HttpStatus.OK);
        // http://localhost:8080/firestation
    }
     */

    @PutMapping("/firestation")
    public ResponseEntity<FireStationDTO> update(@RequestBody FireStationDTO fireStation) {
        Optional<FireStationDTO> savedFireStation = fireStationService.update(fireStation);
        return new ResponseEntity<>(savedFireStation.get(), HttpStatus.OK);
        // http://localhost:8080/firestation
    }

    @DeleteMapping("/firestation")
    public ResponseEntity<Boolean> delete(@RequestParam String address) {
        boolean deleted = fireStationService.deleteByAddress(address);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
        // http://localhost:8080/firestation/delete?address=1509%20Culver%20St
    }


    // NEW ENDPOINT
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
    // 1
    @GetMapping("/firestation/addressNumber")
    public ResponseEntity<Map<String, ?>> getPersonsByStation(@RequestParam("stationNumber") int stationNumber) {
        Map<String, ?> result = fireStationService.getPersonsByStation(stationNumber);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @GetMapping("/firestation/addressNumberA")
    public ResponseEntity<FireStationService> getPersonsByStationA(@RequestParam("stationNumber") int stationNumber) {

        FireStationService.FireStationResponse fireStationDTO = fireStationService.getPersonsByStationA(stationNumber);

        // I like this approach -:) return new ResponseEntity<>(true, HttpStatus.OK);
        return new ResponseEntity(fireStationDTO, HttpStatus.OK);
    }

    /* 2
    http://localhost:8080/phoneAlert?firestation=<firestation_number>
    Cette url doit retourner une liste des numéros de téléphone des résidents desservis
    par la caserne de pompiers. Nous l'utiliserons pour envoyer des messages texte
    d'urgence à des foyers spécifiques.
    http://localhost:8080/phoneAlert?firestation=1
    */
    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneNumbersByStation(@RequestParam("firestation") int stationNumber) {
            List<String> phoneNumbers = fireStationService.getPhoneNumbersByStation(stationNumber);
            return new ResponseEntity<>(phoneNumbers, HttpStatus.OK);
    }

    /* 3
    http://localhost:8080/flood/stations?stations=<a list of
    station_numbers>
    Cette url doit retourner une liste de tous les foyers desservis par la caserne. Cette
    liste doit regrouper les personnes par adresse. Elle doit aussi inclure le nom, le
    numéro de téléphone et l'âge des habitants, et faire figurer leurs antécédents
    médicaux (médicaments, posologie et allergies) à côté de chaque nom.
    http://localhost:8080/flood/stations?stations=1
    */
    @GetMapping("/flood/stations")
    public ResponseEntity<Object> getFloodInfo (@RequestParam("stations") List < Integer > stations) {
        Map<String, List<Map<String, Object>>> floodInfo = fireStationService.getFloodInfoByStations(stations);
        return new ResponseEntity<>(floodInfo, HttpStatus.OK);
    }
    /*
    @GetMapping("/flood/stations")
    public ResponseEntity<Object> getFloodInfo(@RequestParam("stations") List<Integer> stations) {
        try {
            // Appel au service pour récupérer les informations sur les inondations
            Map<String, List<Map<String, Object>>> floodInfo = fireStationService.getFloodInfo(stations);

            if (floodInfo == null || floodInfo.isEmpty()) {
                // Retourne un statut 404 si aucune donnée n'est trouvée
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No flood information found for the given stations."));
            }

            // Retourne un statut 200 avec les données trouvées
            return ResponseEntity.ok(floodInfo);

        } catch (IllegalArgumentException e) {
            // Gère les cas où les paramètres d'entrée sont invalides
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid station list", "details", e.getMessage()));

        } catch (Exception e) {
            // Gère toute autre exception imprévue
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred", "details", e.getMessage()));
        }
    }*/




    /* 4
    http://localhost:8080/fire?address=<address>
    Cette url doit retourner la liste des habitants vivant à l’adresse donnée ainsi que le
    numéro de la caserne de pompiers la desservant. La liste doit inclure le nom, le
    numéro de téléphone, l'âge et les antécédents médicaux (médicaments, posologie et
    allergies) de chaque personne
    http://localhost:8080/fire?address=1509 Culver St
    */
    @GetMapping("/fireObj")
    public ResponseEntity<Object> getResidentsByAddressObject(@RequestParam("address") String address) {

        Map<String, Object> result = fireStationService.getResidentsByAddressObject(address);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/fire")
    public ResponseEntity<FireStationService.ResidentsByAddressResponse> getResidentsByAddress(@RequestParam("address") String address) {
        FireStationService.ResidentsByAddressResponse result = fireStationService.getResidentsByAddress(address);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}