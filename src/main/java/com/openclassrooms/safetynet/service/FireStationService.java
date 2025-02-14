package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.FireStationConvertorDTO;
import com.openclassrooms.safetynet.dto.FireStationDTO;

import com.openclassrooms.safetynet.exception.fireStation.FireStationNotFoundException;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.repository.FireStationRepository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import static com.openclassrooms.safetynet.constant.service.FireStationImplConstant.*;

@Service
@AllArgsConstructor
public class FireStationService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final FireStationRepository fireStationRepository;
    private final FireStationConvertorDTO fireStationConvertorDTO;

    // CRUD
    public List<FireStationDTO> getFireStations() throws FireStationNotFoundException {

        List<FireStation> fireStations = fireStationRepository.getFireStations();

        if (fireStations == null|| fireStations.isEmpty()) {
            throw new FireStationNotFoundException(FIRE_STATION_NOT_FOUND);
        }

        return fireStations.stream()
                .map(fireStationConvertorDTO::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public FireStationDTO save(FireStationDTO fireStationDTO) {

        if (fireStationDTO == null) {
            throw new IllegalArgumentException(FIRE_STATION_ERROR);
        }

        // Convert DTO to entity
        FireStation fireStationEntity = fireStationConvertorDTO.convertDtoToEntity(fireStationDTO);

        // Save the entity to the repository
        FireStation savedFireStationEntity = fireStationRepository.save(fireStationEntity);

        // Convert entity saving in DTO
        return fireStationConvertorDTO.convertEntityToDto(savedFireStationEntity);
    }

    public Optional<FireStationDTO> update(FireStationDTO updatedFireStationDTO) {

        if (updatedFireStationDTO == null) {
            throw new IllegalArgumentException(FIRE_STATION_ERROR_UPDATING);
        }

        FireStation fireStationEntity = fireStationConvertorDTO.convertDtoToEntity(updatedFireStationDTO);
        Optional<FireStation> savedFireStationEntity = fireStationRepository.update(fireStationEntity);
        return savedFireStationEntity.map(fireStationConvertorDTO::convertEntityToDto);
    }

    public Boolean deleteByAddress(String address) {

        if (address == null || address.isBlank()) {
            LOGGER.error(FIRE_STATION_ERROR_DELETING);
            throw new FireStationNotFoundException(FIRE_STATION_ERROR_DELETING + address);
        }

        boolean isDeleted = fireStationRepository.deleteByAddress(address);

        LOGGER.info(isDeleted ? FIRE_STATION_DELETING_SUCCESS : FIRE_STATION_ERROR_DELETING_NOT_FOUND, address);

        if (!isDeleted) {
            throw new FireStationNotFoundException(FIRE_STATION_ERROR_DELETING + address);
        }

        return true;
    }

}




