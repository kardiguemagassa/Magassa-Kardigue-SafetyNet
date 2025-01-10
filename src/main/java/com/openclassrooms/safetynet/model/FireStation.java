package com.openclassrooms.safetynet.model;

import lombok.*;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FireStation {
    private String address;
    private String station;
}
