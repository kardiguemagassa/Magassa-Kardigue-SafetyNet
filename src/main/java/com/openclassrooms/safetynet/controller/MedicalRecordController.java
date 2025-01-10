package com.openclassrooms.safetynet.controller;

import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.service.MedicalRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @GetMapping("/medicalRecord")
    public ResponseEntity<List<MedicalRecordDTO>> getMedicalRecord () {
        List<MedicalRecordDTO> medicalRecords = medicalRecordService.getMedicalRecords();
        return new ResponseEntity<>(medicalRecords, HttpStatus.OK);
        //http://localhost:8080/medicalrecord
    }

    @PostMapping("/medicalRecord")
    public ResponseEntity<List<MedicalRecordDTO>> saveAll (@RequestBody List<MedicalRecordDTO> medicalRecords) {
        List<MedicalRecordDTO> savedMedicalRecords = medicalRecordService.saveAll(medicalRecords);
        return new ResponseEntity<>(savedMedicalRecords, HttpStatus.CREATED);
        //http://localhost:8080/medicalrecord
    }
    /*
    @PostMapping("/medicalRecord")
    public ResponseEntity<MedicalRecordDTO> save (@RequestBody MedicalRecordDTO medicalRecord) {
        MedicalRecordDTO savedMedicalRecord = medicalRecordService.save(medicalRecord);
        return new ResponseEntity<>(savedMedicalRecord, HttpStatus.OK);
        // http://localhost:8080/medicalrecord
    }
     */

    @PutMapping("/medicalRecord")
    public ResponseEntity<MedicalRecordDTO> update (@RequestBody MedicalRecordDTO medicalRecord) {
        Optional<MedicalRecordDTO> updatedMedicalRecord = medicalRecordService.update(medicalRecord);
        return new ResponseEntity<>(updatedMedicalRecord.get(), HttpStatus.OK);
        // http://localhost:8080/medicalrecord
    }

    @DeleteMapping("/medicalRecord")
    public ResponseEntity<Boolean> delete (@RequestParam String firstName, @RequestParam String lastName) {
        boolean deleted = medicalRecordService.deleteByFullName(firstName, lastName);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
        // http://localhost:8080/medicalrecord?firstName=John&lastName=Boyd
    }
}
