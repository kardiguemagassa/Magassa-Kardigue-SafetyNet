package com.openclassrooms.safetynet.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FireStationResponseDTO {

    private int adultCount;
    private int childCount;
    private List<ResidentInfoDTO> residents;

    public FireStationResponseDTO(int adultCount, int childCount, List<ResidentInfoDTO> enrichedResidents) {
        this.adultCount = adultCount;
        this.childCount = childCount;
        this.residents = enrichedResidents;
    }
}
