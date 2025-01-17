package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.FireStationDTO;
import com.openclassrooms.safetynet.exception.ExceptionHandling;
import com.openclassrooms.safetynet.service.FireStationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class FireStationController extends ExceptionHandling {

    private final FireStationService fireStationService;

    @GetMapping("/firestation")
    public ResponseEntity<List<FireStationDTO>> getFireStations() {
        List<FireStationDTO> fireStations = fireStationService.getFireStations();
        return new ResponseEntity<>(fireStations, HttpStatus.OK);
        //http://localhost:8080/firestation
    }

     /*
    @PostMapping("/firestation")
    public ResponseEntity<List<FireStationDTO>> saveAll(@RequestBody List<FireStationDTO> fireStations) {
        List<FireStationDTO> savedFireStations = fireStationService.saveAll(fireStations);
        return new ResponseEntity<>(savedFireStations, HttpStatus.CREATED);
        // http://localhost:8080/firestation
    }*/

    @PostMapping("/firestation")
    public ResponseEntity<FireStationDTO> save(@RequestBody FireStationDTO fireStation) {
        FireStationDTO savedFireStation = fireStationService.save(fireStation);
        return new ResponseEntity<>(savedFireStation, HttpStatus.OK);
        // http://localhost:8080/firestation
    }


    @PutMapping("/firestation")
    public ResponseEntity<FireStationDTO> update(@RequestBody FireStationDTO fireStation) {
        Optional<FireStationDTO> savedFireStation = fireStationService.update(fireStation);
        return new ResponseEntity<>(savedFireStation.get(), HttpStatus.OK);
        // http://localhost:8080/firestation
    }

    @DeleteMapping("/firestation")
    public ResponseEntity<Boolean> delete(@RequestParam String address) {
        boolean deleted = fireStationService.deleteByAddress(address);
        return new ResponseEntity<>(deleted, HttpStatus.NO_CONTENT);
        // http://localhost:8080/firestation?address=1509%20Culver%20St
    }
}