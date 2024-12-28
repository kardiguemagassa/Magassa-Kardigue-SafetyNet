package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.service.MedicalRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping("/medicalrecord")
    public ResponseEntity<List<MedicalRecordDTO>> getMedicalRecord () {
        List<MedicalRecordDTO> medicalRecords = medicalRecordService.getMedicalRecords();
        return new ResponseEntity<>(medicalRecords, HttpStatus.OK);
        //http://localhost:8080/medicalrecord
    }

    @PostMapping("/medicalrecord/saveAll")
    public ResponseEntity<List<MedicalRecordDTO>> saveAll (@RequestBody List<MedicalRecordDTO> medicalRecords) {
        List<MedicalRecordDTO> savedMedicalRecords = medicalRecordService.saveAll(medicalRecords);
        return new ResponseEntity<>(savedMedicalRecords, HttpStatus.OK);
        //http://localhost:8080/medicalrecord/saveAll
    }

    @PostMapping("/medicalrecord/save")
    public ResponseEntity<MedicalRecordDTO> save (@RequestBody MedicalRecordDTO medicalRecord) {
        MedicalRecordDTO savedMedicalRecord = medicalRecordService.save(medicalRecord);
        return new ResponseEntity<>(savedMedicalRecord, HttpStatus.OK);
        // http://localhost:8080/medicalrecord/save
    }

    @PutMapping("/medicalrecord/update")
    public ResponseEntity<MedicalRecordDTO> update (@RequestBody MedicalRecordDTO medicalRecord) {
        Optional<MedicalRecordDTO> updatedMedicalRecord = medicalRecordService.update(medicalRecord);
        return new ResponseEntity<>(updatedMedicalRecord.get(), HttpStatus.OK);
        // http://localhost:8080/medicalrecord/update
    }

    @DeleteMapping("/medicalrecord/delete")
    public ResponseEntity<Boolean> delete (@RequestParam String firstName, @RequestParam String lastName) {
        medicalRecordService.deleteByFullName(firstName, lastName);
        return new ResponseEntity<>(true, HttpStatus.OK);
        // http://localhost:8080/medicalrecord/delete?firstName=John&lastName=Boyd
    }
}
