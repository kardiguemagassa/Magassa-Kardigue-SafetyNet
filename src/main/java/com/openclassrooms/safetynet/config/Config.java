//package com.openclassrooms.safetynet.config;

import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;

//@Configuration
//public class Config {
    //private static final String PERSON_CSV_CONFIG_FILE = "src/main/resources/csv/persons.csv";
    //private static final String MEDICAL_RECORD_CSV_CONFIG_FILE = "src/main/resources/csv/medicalRecords.csv";
    //private static final String FIRE_STATION_CSV_CONFIG_FILE = "src/main/resources/csv/fireStations.csv";

    /*
    @Bean
    public String csvFilePath() {
        return FIRE_STATION_CSV_CONFIG_FILE;
    }

     */


    /*
    // Déclaration des chemins des fichiers CSV
    private static final String PERSON_CSV_CONFIG_FILE = "/Users/kara/Documents/Developer/Learning/Open-c/procject/p-05/safetynet/src/main/resources/csv/persons.csv";
    private static final String MEDICAL_RECORD_CSV_CONFIG_FILE = "/Users/kara/Documents/Developer/Learning/Open-c/procject/p-05/safetynet/src/main/resources/csv/medicalRecord.csv";
    private static final String FIRE_STATION_CSV_CONFIG_FILE = "/Users/kara/Documents/Developer/Learning/Open-c/procject/p-05/safetynet/src/main/resources/csv/fireStation.csv";


     //Charge tous les chemins des fichiers CSV dans un Map, pour un accès centralisé.
    @Bean
    public Map<String, String> csvFilePaths() {
        return Map.of(
                "persons", PERSON_CSV_CONFIG_FILE,
                "medicalRecords", MEDICAL_RECORD_CSV_CONFIG_FILE,
                "fireStations", FIRE_STATION_CSV_CONFIG_FILE
        );
    }

     //Bean individuel pour le fichier des personnes (optionnel si `csvFilePaths` suffit).
    @Bean
    public String personCsvFilePath() {
        return PERSON_CSV_CONFIG_FILE;
    }


     //Bean individuel pour le fichier des dossiers médicaux (optionnel si `csvFilePaths` suffit).
    @Bean
    public String medicalRecordCsvFilePath() {
        return MEDICAL_RECORD_CSV_CONFIG_FILE;
    }


     //Bean individuel pour le fichier des stations de pompiers (optionnel si `csvFilePaths` suffit).
    @Bean
    public String fireStationCsvFilePath() {
        return FIRE_STATION_CSV_CONFIG_FILE;
    }

}

     */
