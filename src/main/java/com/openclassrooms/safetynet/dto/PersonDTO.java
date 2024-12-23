package com.openclassrooms.safetynet.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Builder
@Getter
@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class PersonDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String city;
    private String zip;
    private String phone;

    public PersonDTO(String firstName, String lastName, String email, String address, String city, String zip, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
