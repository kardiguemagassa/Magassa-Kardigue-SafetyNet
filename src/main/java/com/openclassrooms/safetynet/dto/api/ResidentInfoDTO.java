package com.openclassrooms.safetynet.dto.api;

import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
//@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResidentInfoDTO {

    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private int age;
    private MedicalRecordDTO medicalRecord;


    public ResidentInfoDTO(String firstName, String lastName, String address, String phone, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.age = age;
    }


    public ResidentInfoDTO(String firstName, String lastName, String address, String phone, int age, MedicalRecordDTO medicalRecord) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.age = age;
        this.medicalRecord = medicalRecord;
    }
}
