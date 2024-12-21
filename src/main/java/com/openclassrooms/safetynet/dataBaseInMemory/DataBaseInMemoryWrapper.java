package com.openclassrooms.safetynet.dataBaseInMemory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class DataBaseInMemoryWrapper {
    private final List<Person> persons = new ArrayList<>();
    private final List<MedicalRecord> medicalRecords = new ArrayList<>();
    private final List<FireStation> fireStations = new ArrayList<>();

    @PostConstruct
    public void loadData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Charger les données depuis le fichier JSON
            DataWrapper dataWrapper = loadJson("/dada/data.json", objectMapper, new TypeReference<>() {});

            // Ajouter les données aux listes
            persons.addAll(dataWrapper.getPersons());
            medicalRecords.addAll(dataWrapper.getMedicalrecords());
            fireStations.addAll(dataWrapper.getFirestations());

            System.out.println("Données chargées avec succès !");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des données JSON : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Méthode générique pour charger un fichier JSON dans un objet Java.
     *
     * @param <T>           Le type d'objet à retourner.
     * @param path          Le chemin vers le fichier JSON.
     * @param objectMapper  L'instance d'ObjectMapper pour la désérialisation.
     * @param typeReference Le type attendu (ex : DataWrapper).
     * @return L'objet Java désérialisé à partir du fichier JSON.
     * @throws Exception Si une erreur se produit pendant la lecture du fichier JSON.
     */
    private <T> T loadJson(String path, ObjectMapper objectMapper, TypeReference<T> typeReference) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(path);

        if (inputStream == null) {
            throw new IllegalArgumentException("Fichier JSON introuvable : " + path);
        }

        return objectMapper.readValue(inputStream, typeReference);
    }

    // Classe interne pour mapper la structure JSON
    @Data
    private static class DataWrapper {
        private List<Person> persons;
        private List<FireStation> firestations;
        private List<MedicalRecord> medicalrecords;
    }
}
