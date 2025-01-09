package com.openclassrooms.safetynet.dto;

import lombok.*;

@Builder
@Getter
@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class FireStationDTO {
    private String address;
    private String station;

    public FireStationDTO() {}

    public FireStationDTO(String address, String station) {
        this.address = address;
        this.station = station;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }
}
