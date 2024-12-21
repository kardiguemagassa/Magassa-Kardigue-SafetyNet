package com.openclassrooms.safetynet.dto;

import lombok.Builder;

@Builder
public class PersonTDO {
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String city;
    private String zip;
    private String phone;
}
