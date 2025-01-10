package com.openclassrooms.safetynet.convertorDTO;

import com.openclassrooms.safetynet.dto.FireStationDTO;

import com.openclassrooms.safetynet.model.FireStation;

import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Builder
public class FireStationConvertorDTO {

    public FireStationDTO convertEntityToDto(FireStation fireStation) {

        return FireStationDTO.builder()
                .station(fireStation.getStation())
                .address(fireStation.getAddress())
                .build();
    }

    public FireStation convertDtoToEntity(FireStationDTO fireStationDTO) {

        return FireStation.builder()
                .station(fireStationDTO.getStation())
                .address(fireStationDTO.getAddress())
                .build();
    }

    public List<FireStationDTO> convertEntityToDto(List<FireStation> entities) {
        return entities.stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    public List<FireStation> convertDtoToEntity(List<FireStationDTO> fireStationDTOS) {
        return fireStationDTOS.stream().map(this::convertDtoToEntity).collect(Collectors.toList());
    }
}
