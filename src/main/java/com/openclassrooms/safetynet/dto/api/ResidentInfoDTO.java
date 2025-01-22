package com.openclassrooms.safetynet.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    // Existing Builder (with all fields)
    protected ResidentInfoDTO(String firstName, String lastName, String address, String phone, int age, String email,
                              List<String> medications, List<String> allergies, Integer stationNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.age = age;
        this.email = email;
        this.medications = medications;
        this.allergies = allergies;
        this.stationNumber = stationNumber;
    }

    /* 1
    http://localhost:8080/firestation?stationNumber=<station_number>
    Cette url doit retourner une liste des personnes couvertes par la caserne de pompiers
    correspondante. Donc, si le numéro de station = 1, elle doit renvoyer les habitants
    couverts par la station numéro 1. La liste doit inclure les informations spécifiques
    suivantes : prénom, nom, adresse, numéro de téléphone. De plus, elle doit fournir un
    décompte du nombre d'adultes et du nombre d'enfants (tout individu âgé de 18 ans ou
    moins) dans la zone desservie.
    http://localhost:8080/firestation/addressNumber?stationNumber=1
    */
    public ResidentInfoDTO(String firstName, String lastName, String address, String phone, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.age = age;
    }

    /*  2
    http://localhost:8080/childAlert?address=<address>
    http://localhost:8080/childAlert?address=1509 Culver St
    Cette url doit retourner une liste d'enfants (tout individu âgé de 18 ans ou moins)
    habitant à cette adresse. La liste doit comprendre le prénom et le nom de famille de
    chaque enfant, son âge et une liste des autres membres du foyer. S'il n'y a pas
    d'enfant, cette url peut renvoyer une chaîne vide.
    */
    public ResidentInfoDTO(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;

    }

    /* 4
   http://localhost:8080/fire?address=<address>
   Cette url doit retourner la liste des habitants vivant à l’adresse donnée ainsi que le
   numéro de la caserne de pompiers la desservant. La liste doit inclure le nom, le
   numéro de téléphone, l'âge et les antécédents médicaux (médicaments, posologie et
   allergies) de chaque personne
   http://localhost:8080/fire?address=1509 Culver St
  */
    public ResidentInfoDTO(Integer stationNumber,String lastName, String phone, int age, List<String> medications, List<String> allergies) {
        this.stationNumber = stationNumber;
        this.lastName = lastName;
        this.phone = phone;
        this.age = age;
        this.medications = medications;
        this.allergies = allergies;

    }

    /* 5
    http://localhost:8080/flood/stations?stations=<a list of
    station_numbers>
    Cette url doit retourner une liste de tous les foyers desservis par la caserne. Cette
    liste doit regrouper les personnes par adresse. Elle doit aussi inclure le nom, le
    numéro de téléphone et l'âge des habitants, et faire figurer leurs antécédents
    médicaux (médicaments, posologie et allergies) à côté de chaque nom.
    http://localhost:8080/flood/stations?stations=1,2,4
    */
    public ResidentInfoDTO(String address, String lastName, String phone, int age, List<String> medications, List<String> allergies) {
        this.address = address;
        this.lastName = lastName;
        this.phone = phone;
        this.age = age;
        this.medications = medications;
        this.allergies = allergies;
    }

    /* 6
    http://localhost:8080/personInfolastName=<lastName>
    http://localhost:8080/personInfolastName?lastName=Boyd
    Cette url doit retourner le nom, l'adresse, l'âge, l'adresse mail et les antécédents
    médicaux (médicaments, posologie et allergies) de chaque habitant. Si plusieurs
    personnes portent le même nom, elles doivent toutes apparaître.
    */
    public ResidentInfoDTO(String lastName, String address, int age, String email, List<String> medications, List<String> allergies) {
        this.lastName = lastName;
        this.address = address;
        this.age = age;
        this.email = email;
        this.medications = medications;
        this.allergies = allergies;
    }

}
