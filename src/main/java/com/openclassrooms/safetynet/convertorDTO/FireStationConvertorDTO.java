package com.openclassrooms.safetynet.convertorDTO;

import com.openclassrooms.safetynet.dto.FireStationDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.Person;
import org.springframework.stereotype.Component;

@Component
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
}
