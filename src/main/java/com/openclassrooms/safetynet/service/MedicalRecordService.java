package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.dto.MedicalRecordDTO;

import com.openclassrooms.safetynet.exception.medicalRecord.MedicalRecordNotFoundException;

import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.openclassrooms.safetynet.constant.service.MedicalRecordImplConstant.*;


@Service
@AllArgsConstructor
public class MedicalRecordService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordConvertorDTO medicalRecordConvertorDTO;

    public List<MedicalRecordDTO> getMedicalRecords() throws MedicalRecordNotFoundException {

        List<MedicalRecord> medicalRecords = medicalRecordRepository.getMedicalRecords();

        if (medicalRecords == null || medicalRecords.isEmpty()) {
            LOGGER.error(MEDICAL_RECORD_NOT_FOUND);
            throw new MedicalRecordNotFoundException(MEDICAL_RECORD_NOT_FOUND);
        }

        return medicalRecords.stream()
                .map(medicalRecordConvertorDTO::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public MedicalRecordDTO save(MedicalRecordDTO medicalRecordDTO) {

        if (medicalRecordDTO == null) {
            LOGGER.error(MEDICAL_RECORD_ERROR);
            throw new IllegalArgumentException(MEDICAL_RECORD_ERROR);
        }

        MedicalRecord medicalRecordEntity = medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTO);
        MedicalRecord savedMedicalRecordEntity = medicalRecordRepository.save(medicalRecordEntity);

        return medicalRecordConvertorDTO.convertEntityToDto(savedMedicalRecordEntity);
    }

    public Optional<MedicalRecordDTO> update(MedicalRecordDTO updatedMedicalRecordDTO) {

        if (updatedMedicalRecordDTO == null) {
            LOGGER.error(MEDICAL_RECORD_ERROR_UPDATING);
            throw new IllegalArgumentException(MEDICAL_RECORD_ERROR_UPDATING);
        }

        MedicalRecord medicalRecordEntity = medicalRecordConvertorDTO.convertDtoToEntity(updatedMedicalRecordDTO);
        Optional<MedicalRecord> savedMedicalRecordEntity = medicalRecordRepository.update(medicalRecordEntity);

        return savedMedicalRecordEntity.map(medicalRecordConvertorDTO::convertEntityToDto);
    }

    public Boolean deleteByFullName(String firstName, String lastName) {

        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            LOGGER.warn(MEDICAL_RECORD_ERROR_DELETING);
            throw new IllegalArgumentException(MEDICAL_RECORD_ERROR_DELETING);
        }

        boolean isDeleted = medicalRecordRepository.deleteByFullName(firstName, lastName);

        LOGGER.info(isDeleted ? MEDICAL_RECORD_DELETING_SUCCESS : MEDICAL_RECORD_ERROR_DELETING_NOT_FOUND, firstName, lastName);

        if (!isDeleted) {

            throw new IllegalArgumentException(MEDICAL_RECORD_NOT_FOUND);
        }

        return true;
    }

}
