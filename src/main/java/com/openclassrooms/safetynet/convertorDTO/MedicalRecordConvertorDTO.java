package com.openclassrooms.safetynet.convertorDTO;

import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.model.MedicalRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MedicalRecordConvertorDTO {
    public MedicalRecordDTO convertEntityToDto(MedicalRecord medicalRecord) {
        return MedicalRecordDTO.builder()
                .firstName(medicalRecord.getFirstName())
                .lastName(medicalRecord.getLastName())
                .birthdate(medicalRecord.getBirthdate())
                .medications(medicalRecord.getMedications())
                .allergies(medicalRecord.getAllergies())
                .build();
    }

    public MedicalRecord convertDtoToEntity(MedicalRecordDTO medicalRecordDTO) {
        return MedicalRecord.builder()
                .firstName(medicalRecordDTO.getFirstName())
                .lastName(medicalRecordDTO.getLastName())
                .birthdate(medicalRecordDTO.getBirthdate())
                .medications(medicalRecordDTO.getMedications())
                .allergies(medicalRecordDTO.getAllergies())
                .build();
    }

    public List<MedicalRecordDTO> convertEntityToDto (List<MedicalRecord> medicalRecords) {
        return medicalRecords.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public List<MedicalRecord> convertDtoToEntity (List<MedicalRecordDTO> medicalRecordDTOs) {
        return medicalRecordDTOs.stream()
                .map(this::convertDtoToEntity)
                .collect(Collectors.toList());
    }
}
