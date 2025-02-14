package com.openclassrooms.safetynet.convertorDTO;

import com.openclassrooms.safetynet.dto.FireStationDTO;

import com.openclassrooms.safetynet.model.FireStation;

import lombok.Builder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Builder
public class FireStationConvertorDTO {

    public FireStationDTO convertEntityToDto(FireStation fireStation) {

        if (fireStation == null) {
            return null;
        }

        return FireStationDTO.builder()
                .station(fireStation.getStation())
                .address(fireStation.getAddress())
                .build();
    }

    public FireStation convertDtoToEntity(FireStationDTO fireStationDTO) {

        if (fireStationDTO == null) {
            return null;
        }

        return FireStation.builder()
                .station(fireStationDTO.getStation())
                .address(fireStationDTO.getAddress())
                .build();
    }

    public List<FireStationDTO> convertEntityToDto(List<FireStation> entities) {

        if (CollectionUtils.isEmpty(entities)) {
            return List.of();
        }

        return entities.stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    public List<FireStation> convertDtoToEntity(List<FireStationDTO> fireStationDTOS) {

        if (CollectionUtils.isEmpty(fireStationDTOS)) {
            return List.of();
        }

        return fireStationDTOS.stream().map(this::convertDtoToEntity).collect(Collectors.toList());
    }
}
