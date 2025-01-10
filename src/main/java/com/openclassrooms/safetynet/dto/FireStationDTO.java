package com.openclassrooms.safetynet.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FireStationDTO {
    private String address;
    private String station;
}
