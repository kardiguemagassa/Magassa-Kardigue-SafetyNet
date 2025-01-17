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

        try {

            List<MedicalRecord> medicalRecords = medicalRecordRepository.getMedicalRecords();

            if (medicalRecords == null || medicalRecords.isEmpty()) {
                LOGGER.error(MEDICAL_RECORD_NOT_FOUND);
                throw new MedicalRecordNotFoundException(MEDICAL_RECORD_NOT_FOUND);
            }

            return medicalRecords.stream()
                    .map(medicalRecordConvertorDTO::convertEntityToDto)
                    .collect(Collectors.toList());

        } catch (MedicalRecordNotFoundException e) {
            LOGGER.error(MEDICAL_RECORD_NOT_FOUND_MSG, e.getMessage());
            throw e;

        } catch (Exception e) {
            LOGGER.error(ERROR_CONVERTING, e.getMessage(), e);
            throw new RuntimeException(MEDICAL_RECORD_UNEXPECT, e);
        }
    }

    public List<MedicalRecordDTO> saveAll(List<MedicalRecordDTO> medicalRecordDTOList) {
        try {
            // Validation of the input list
            validateMedicalRecordDTOList(medicalRecordDTOList);

            // Conversion DTOs to entities
            List<MedicalRecord> medicalRecordEntities = medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTOList);

            // save entities
            List<MedicalRecord> savedMedicalRecords = medicalRecordRepository.saveAll(medicalRecordEntities);

            // Logging du success  Validation error while saving medical records: {}
            //LOGGER.info("Successfully saved {} medical records.", savedMedicalRecords.size());

            return medicalRecordConvertorDTO.convertEntityToDto(savedMedicalRecords);

        } catch (RuntimeException e) {
            LOGGER.error(MEDICAL_RECORD_ERROR_SAVING_DATA_BASE, e.getMessage());
            throw new MedicalRecordNotFoundException(MEDICAL_RECORD_ERROR_SAVING_DATA_BASE);

        } catch (Exception e) {
            // Catch-all for other unanticipated exceptions
            LOGGER.error(MEDICAL_RECORD_ERROR_SAVING_REPO, e.getMessage(), e);
            throw new MedicalRecordNotFoundException(MEDICAL_RECORD_ERROR_SAVING_REPO);
        }
    }

    // Private method to validate the input list
    private void validateMedicalRecordDTOList(List<MedicalRecordDTO> medicalRecordDTOList) {
        if (medicalRecordDTOList == null || medicalRecordDTOList.isEmpty()) {
            LOGGER.warn(MEDICAL_RECORD_ERROR_SAVING);
            throw new IllegalArgumentException(MEDICAL_RECORD_ERROR_SAVING);
        }
    }

    public MedicalRecordDTO save(MedicalRecordDTO medicalRecordDTO) {
        try {
            if (medicalRecordDTO == null) {
                LOGGER.error(MEDICAL_RECORD_ERROR);
                throw new IllegalArgumentException(MEDICAL_RECORD_ERROR);
            }

            MedicalRecord medicalRecordEntity = medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTO);
            MedicalRecord savedMedicalRecordEntity = medicalRecordRepository.save(medicalRecordEntity);
            return medicalRecordConvertorDTO.convertEntityToDto(savedMedicalRecordEntity);

        } catch (Exception e) {
            LOGGER.error(MEDICAL_RECORD_ERROR_SAVING_DATA_BASE, e.getMessage(), e);
            throw new MedicalRecordNotFoundException(MEDICAL_RECORD_NOT_FOUND);
        }
    }

    public Optional<MedicalRecordDTO> update(MedicalRecordDTO updatedMedicalRecordDTO) {

        try {
            if (updatedMedicalRecordDTO == null) {
                LOGGER.error(MEDICAL_RECORD_ERROR_UPDATING);
                throw new IllegalArgumentException(MEDICAL_RECORD_ERROR_UPDATING);
            }

            MedicalRecord medicalRecordEntity = medicalRecordConvertorDTO.convertDtoToEntity(updatedMedicalRecordDTO);
            Optional<MedicalRecord> savedMedicalRecordEntity = medicalRecordRepository.update(medicalRecordEntity);

            return savedMedicalRecordEntity.map(medicalRecordConvertorDTO::convertEntityToDto);

        } catch (Exception e) {
            LOGGER.error(MEDICAL_RECORD_ERROR_SAVING_UPDATING_SUCCESS, e.getMessage(), e);
            throw new MedicalRecordNotFoundException(MEDICAL_RECORD_ERROR_SAVING_UPDATING_SUCCESS);
        }
    }

    public Boolean deleteByFullName(String firstName, String lastName) {

        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            LOGGER.warn(MEDICAL_RECORD_ERROR_DELETING);
            throw new IllegalArgumentException(MEDICAL_RECORD_ERROR_DELETING);
        }

        try {
            boolean isDeleted = medicalRecordRepository.deleteByFullName(firstName, lastName);

            LOGGER.info(isDeleted ? MEDICAL_RECORD_DELETING_SUCCESS : MEDICAL_RECORD_ERROR_DELETING_NOT_FOUND, firstName, lastName);

            if (!isDeleted) {

                throw new MedicalRecordNotFoundException(MEDICAL_RECORD_NOT_FOUND);
            }

            return true;

        } catch (MedicalRecordNotFoundException e) {
            LOGGER.error(MEDICAL_RECORD_ERROR_DELETING_NOT_, e.getMessage());
            throw e;

        } catch (Exception e) {
            LOGGER.error(MEDICAL_RECORD_ERROR_DELETING_NOT_, firstName, lastName, e.getMessage(), e);
            throw new MedicalRecordNotFoundException(MEDICAL_RECORD_ERROR_DELETING_NOT_);

        }
    }

}
