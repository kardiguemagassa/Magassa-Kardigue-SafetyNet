package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.FireStationConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.FireStationDTO;

import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.exception.fireStation.FireStationNotFoundException;
import com.openclassrooms.safetynet.model.FireStation;

import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.FireStationRepository;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.repository.PersonRepository;
import lombok.AllArgsConstructor;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.openclassrooms.safetynet.constant.service.FireStationImplConstant.*;

@Service
@AllArgsConstructor
public class FireStationService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final FireStationRepository fireStationRepository;
    private final PersonRepository personRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final FireStationConvertorDTO fireStationConvertorDTO;
    private final PersonConvertorDTO personConvertorDTO;
    private final MedicalRecordConvertorDTO medicalRecordConvertorDTO;

    // CRUD
    public List<FireStationDTO> getFireStations() throws FireStationNotFoundException {
        try {
            List<FireStation> fireStations = fireStationRepository.getFireStations();

            if (fireStations == null|| fireStations.isEmpty()) {
                throw new FireStationNotFoundException(FIRE_STATION_NOT_FOUND);
            }

            return fireStations.stream()
                    .map(fireStationConvertorDTO::convertEntityToDto)
                    .collect(Collectors.toList());

        } catch (FireStationNotFoundException e) {
            LOGGER.error(FIRE_STATION_NOT_FOUND, e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error(FIRE_STATION_ERROR_CONVERT, e.getMessage());
            throw new RuntimeException(FIRE_STATION_ERROR_RUN);
        }
    }

    public List<FireStationDTO> saveAll(List<FireStationDTO> fireStationDTOList) {

        try {
            if (fireStationDTOList == null || fireStationDTOList.isEmpty()) {
                throw new IllegalArgumentException(FIRE_STATION_ERROR_SAVING);
            }

            List<FireStation> fireStationEntities = fireStationConvertorDTO.convertDtoToEntity(fireStationDTOList);
            List<FireStation> savedFireStationEntities = fireStationRepository.saveAll(fireStationEntities);

            return fireStationConvertorDTO.convertEntityToDto(savedFireStationEntities);

        } catch (RuntimeException e) {
            LOGGER.error(FIRE_STATION_ERROR_SAVING_DATA_BASE, e.getMessage());
            throw new FireStationNotFoundException(FIRE_STATION_ERROR_SAVING_DATA_BASE + e.getMessage(),e);


        } catch (Exception e) {
            LOGGER.error(FIRE_STATION_ERROR_SAVING_DATA_BASE, e.getMessage(), e);
            throw new FireStationNotFoundException(FIRE_STATION_ERROR_SAVING_DATA_BASE + e.getMessage(),e);
        }
    }

    public FireStationDTO save(FireStationDTO fireStationDTO) {
        try {
            if (fireStationDTO == null) {
                throw new IllegalArgumentException(FIRE_STATION_ERROR);
            }

            // Convert DTO to entity
            FireStation fireStationEntity = fireStationConvertorDTO.convertDtoToEntity(fireStationDTO);

            // Save the entity to the repository
            FireStation savedFireStationEntity = fireStationRepository.save(fireStationEntity);

            // Convert entity saving in DTO
            return fireStationConvertorDTO.convertEntityToDto(savedFireStationEntity);

        } catch (RuntimeException e) {
            LOGGER.error(FIRE_STATION_ERROR_SAVING_DATA_BASE_, e.getMessage());
            throw new FireStationNotFoundException(FIRE_STATION_ERROR_SAVING_DATA_BASE + e.getMessage(),e);

        } catch (Exception e) {
            LOGGER.error(FIRE_STATION_ERROR_SAVING_DATA_BASE_, e.getMessage(), e);
            throw new FireStationNotFoundException(FIRE_STATION_ERROR_SAVING_DATA_BASE + e.getMessage(),e);
        }
    }

    public Optional<FireStationDTO> update(FireStationDTO updatedFireStationDTO) {

        try {
            if (updatedFireStationDTO == null) {
                throw new IllegalArgumentException(FIRE_STATION_ERROR_UPDATING);
            }

            FireStation fireStationEntity = fireStationConvertorDTO.convertDtoToEntity(updatedFireStationDTO);
            Optional<FireStation> savedFireStationEntity = fireStationRepository.update(fireStationEntity);
            return savedFireStationEntity.map(fireStationConvertorDTO::convertEntityToDto);

        } catch (IllegalArgumentException e) {
            LOGGER.error(FIRE_STATION_NOT_FOUND_UPDATING, e.getMessage());
            throw new FireStationNotFoundException(FIRE_STATION_NOT_FOUND_UPDATING + e.getMessage(),e);
        } catch (Exception e) {
            LOGGER.error(FIRE_STATION_ERROR_SAVING_UPDATING_SUCCESS, e.getMessage(), e);
            throw new FireStationNotFoundException(FIRE_STATION_ERROR_SAVING_UPDATING_SUCCESS + e.getMessage(),e);
        }
    }

    public Boolean deleteByAddress(String address) {

        if (address == null || address.isBlank()) {
            LOGGER.error(FIRE_STATION_ERROR_DELETING);
            throw new FireStationNotFoundException(FIRE_STATION_ERROR_DELETING + address);
        }

        try {
            boolean isDeleted = fireStationRepository.deleteByAddress(address);

            LOGGER.info(isDeleted ? FIRE_STATION_DELETING_SUCCESS : FIRE_STATION_ERROR_DELETING_NOT_FOUND, address);

            if (!isDeleted) {
                throw new FireStationNotFoundException(FIRE_STATION_ERROR_DELETING + address);
            }

            return true;

        } catch (FireStationNotFoundException e) {
            LOGGER.error(address, e.getMessage(),e);
            throw e;

        } catch (Exception e) {
            LOGGER.error(address, e.getMessage(), e);
            throw new FireStationNotFoundException(FIRE_STATION_ERROR_DELETING_BY_ADDRESS + e.getMessage());
        }
    }

}




