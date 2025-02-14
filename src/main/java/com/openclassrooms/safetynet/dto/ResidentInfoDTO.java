package com.openclassrooms.safetynet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class ResidentInfoDTO {

    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private int age;
    private String email;
    private List<String> medications;
    private List<String> allergies;
    private Integer stationNumber;
    private List<ResidentInfoDTO> householdMembers;

}
