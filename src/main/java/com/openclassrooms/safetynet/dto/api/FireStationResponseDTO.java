package com.openclassrooms.safetynet.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FireStationResponseDTO {

    private int adultCount;
    private int childCount;
    private List<ResidentInfoDTO> residents;
}
