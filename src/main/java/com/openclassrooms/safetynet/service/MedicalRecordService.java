package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.dto.MedicalRecordDTO;

import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicalRecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordService.class);

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordConvertorDTO medicalRecordConvertorDTO;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                MedicalRecordConvertorDTO medicalRecordConvertorDTO) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalRecordConvertorDTO = medicalRecordConvertorDTO;
    }

    public List<MedicalRecordDTO> getMedicalRecords() {
        try {

            List<MedicalRecord> medicalRecords = medicalRecordRepository.getMedicalRecords();

            if (medicalRecords == null || medicalRecords.isEmpty()) {
                LOGGER.warn("No medical records found");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No medical records found.");
            }

            return medicalRecords.stream()
                    .map(medicalRecordConvertorDTO::convertEntityToDto)
                    .collect(Collectors.toList());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error while retrieving and converting MedicalRecord: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while retrieving the MedicalRecord.", e);
        }
    }

    public List<MedicalRecordDTO> saveAll(List<MedicalRecordDTO> medicalRecordDTOList) {
        try {
            // Validation de la liste d'entrée
            validateMedicalRecordDTOList(medicalRecordDTOList);

            // Conversion des DTOs en entités
            List<MedicalRecord> medicalRecordEntities = medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTOList);

            // Sauvegarde des entités
            List<MedicalRecord> savedMedicalRecords = medicalRecordRepository.saveAll(medicalRecordEntities);

            // Logging du succès
            LOGGER.info("Successfully saved {} medical records.", savedMedicalRecords.size());

            // Conversion des entités sauvegardées en DTOs
            return medicalRecordConvertorDTO.convertEntityToDto(savedMedicalRecords);

        } catch (IllegalArgumentException e) {
            LOGGER.error("Validation error while saving medical records: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected error encountered in MedicalRecordRepository: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while saving medical records. Please try again later.", e);
        }
    }

    // Méthode privée pour valider la liste d'entrée
    private void validateMedicalRecordDTOList(List<MedicalRecordDTO> medicalRecordDTOList) {
        if (medicalRecordDTOList == null || medicalRecordDTOList.isEmpty()) {
            LOGGER.warn("No medical records found in the provided list.");
            throw new IllegalArgumentException("No medical records were provided for saving.");
        }
    }

    public MedicalRecordDTO save(MedicalRecordDTO medicalRecordDTO) {
        try {
            if (medicalRecordDTO == null) {
                throw new IllegalArgumentException("PersonDTO cannot be null.");
            }

            MedicalRecord medicalRecordEntity = medicalRecordConvertorDTO.convertDtoToEntity(medicalRecordDTO);
            MedicalRecord savedMedicalRecordEntity = medicalRecordRepository.save(medicalRecordEntity);
            return medicalRecordConvertorDTO.convertEntityToDto(savedMedicalRecordEntity);

        } catch (IllegalArgumentException e) {
            LOGGER.error("Validation error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid medicalRecord data: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error saving medicalRecord: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while saving the medicalRecord.", e);
        }

    }

    public Optional<MedicalRecordDTO> update(MedicalRecordDTO updatedMedicalRecordDTO) {

        try {
            if (updatedMedicalRecordDTO == null) {
                throw new IllegalArgumentException("Updated medicalRecord data cannot be null.");
            }

            MedicalRecord medicalRecordEntity = medicalRecordConvertorDTO.convertDtoToEntity(updatedMedicalRecordDTO);
            Optional<MedicalRecord> savedMedicalRecordEntity = medicalRecordRepository.update(medicalRecordEntity);
            return savedMedicalRecordEntity.map(medicalRecordConvertorDTO::convertEntityToDto);

        } catch (IllegalArgumentException e) {
            LOGGER.error("Validation error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid medicalRecord data: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error updating medicalRecord: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while updating the medicalRecord.", e);
        }
    }

    public Boolean deleteByFullName(String firstName, String lastName) {

        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            LOGGER.warn("Invalid parameters: firstName or lastName is null/empty. Cannot perform deletion.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First name or last name cannot be null or empty.");
        }

        try {
            boolean isDeleted = medicalRecordRepository.deleteByFullName(firstName, lastName);

            LOGGER.info(isDeleted ?
                    "Person {} {} deleted successfully via repository." :
                    "Person {} {} not found for deletion in repository.", firstName, lastName);

            if (!isDeleted) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found for deletion.");
            }

            return isDeleted;
        } catch (ResponseStatusException e) {
            throw e; // Re-throw HTTP-specific exceptions
        } catch (Exception e) {
            LOGGER.error("Error while deleting person {} {} via repository: {}", firstName, lastName, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while deleting the person.", e);
        }
    }

}
