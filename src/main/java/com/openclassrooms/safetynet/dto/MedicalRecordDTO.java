package com.openclassrooms.safetynet.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class MedicalRecordDTO {
    private String firstName;
    private String lastName;
    private String birthdate;
    private List<String> medications;
    private List<String> allergies;
}
