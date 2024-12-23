package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.Person;
//import org.slf4j.LoggerFactory;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Component
@AllArgsConstructor
public class PersonRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonRepository.class);

    // Liste des personnes (sera remplie après le chargement des données)
    private final List<Person> persons = new ArrayList<>();
    private final DataBaseInMemoryWrapper dataBaseInMemoryWrapper;

    /**
     * Retourne la liste des personnes après chargement des données.
     *
     * @return Liste des personnes ou une liste vide si une erreur survient.
     */
    public List<Person> getPersons() {
        try {
            if (persons.isEmpty()) {
                // Charger les données depuis le wrapper
                dataBaseInMemoryWrapper.loadData();

                // Copier les données depuis DataBaseInMemoryWrapper
                persons.addAll(dataBaseInMemoryWrapper.getPersons());
            }
            return new ArrayList<>(persons); // Retourner une copie immuable
        } catch (Exception e) {
            //LOGGER.error("Erreur lors du chargement des données JSON : {}", e.getMessage(), e);
        }
        return List.of(); // Retourner une liste vide en cas d'erreur
    }

    public List<Person> saveAll(List<Person> personList) {
        try {
            // Ajout des nouvelles personnes à la base existante
            persons.addAll(personList);

            // Mise à jour du wrapper avec les nouvelles données
            dataBaseInMemoryWrapper.getPersons().addAll(personList);

            LOGGER.info("Successfully registered {} persons.", personList.size());
        } catch (Exception e) {
            LOGGER.error("Error registering people: {}", e.getMessage(), e);
        }
        return new ArrayList<>(persons); // Retourner une copie immuable
    }

}
