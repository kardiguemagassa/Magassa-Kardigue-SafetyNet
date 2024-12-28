package com.openclassrooms.safetynet.convertorDTO;

import com.openclassrooms.safetynet.dto.FireStationDTO;

import com.openclassrooms.safetynet.model.FireStation;

import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FireStationConvertorDTO {

    public FireStationDTO convertEntityToDto(FireStation fireStation) {
        /*
        return FireStationDTO.builder()
                .station(fireStation.getStation())
                .address(fireStation.getAddress())
                .build();

         */
        return new FireStationDTO(fireStation.getAddress(), fireStation.getStation());
    }

    public FireStation convertDtoToEntity(FireStationDTO fireStationDTO) {
        /*
        return FireStation.builder()
                .station(fireStationDTO.getStation())
                .address(fireStationDTO.getAddress())
                .build();

         */
        return new FireStation(fireStationDTO.getAddress(), fireStationDTO.getStation());
    }

    public List<FireStationDTO> convertEntityToDto(List<FireStation> entities) {
        return entities.stream().map(entity -> convertEntityToDto(entity)).collect(Collectors.toList());
    }

    public List<FireStation> convertDtoToEntity(List<FireStationDTO> dtos) {
        return dtos.stream().map(entity -> convertDtoToEntity(entity)).collect(Collectors.toList());
    }
}
