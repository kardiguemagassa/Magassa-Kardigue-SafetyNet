package com.openclassrooms.safetynet.convertorDTO;

import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.model.MedicalRecord;
import lombok.Builder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Builder
public class MedicalRecordConvertorDTO {

    public MedicalRecordDTO convertEntityToDto(MedicalRecord medicalRecord) {

        if (medicalRecord == null) {
            return null;
        }

        return MedicalRecordDTO.builder()
                .firstName(medicalRecord.getFirstName())
                .lastName(medicalRecord.getLastName())
                .birthdate(medicalRecord.getBirthdate())
                .medications(medicalRecord.getMedications())
                .allergies(medicalRecord.getAllergies())
                .build();
    }

    public MedicalRecord convertDtoToEntity(MedicalRecordDTO medicalRecordDTO) {

        if (medicalRecordDTO == null) {
            return null;
        }

        return MedicalRecord.builder()
                .firstName(medicalRecordDTO.getFirstName())
                .lastName(medicalRecordDTO.getLastName())
                .birthdate(medicalRecordDTO.getBirthdate())
                .medications(medicalRecordDTO.getMedications())
                .allergies(medicalRecordDTO.getAllergies())
                .build();
    }

    public List<MedicalRecordDTO> convertEntityToDto (List<MedicalRecord> medicalRecords) {

        if (CollectionUtils.isEmpty(medicalRecords)) {
            return List.of();
        }

        return medicalRecords.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public List<MedicalRecord> convertDtoToEntity (List<MedicalRecordDTO> medicalRecordDTOs) {

        if (CollectionUtils.isEmpty(medicalRecordDTOs)) {
            return List.of();
        }

        return medicalRecordDTOs.stream()
                .map(this::convertDtoToEntity)
                .collect(Collectors.toList());
    }
}
