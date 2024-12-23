package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Component
public class PersonRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonRepository.class);

    // Liste des personnes (sera remplie après le chargement des données)
    private final List<Person> persons = new ArrayList<>();
    private final DataBaseInMemoryWrapper dataBaseInMemoryWrapper;

    public PersonRepository (DataBaseInMemoryWrapper dataBaseInMemoryWrapper) {
        this.dataBaseInMemoryWrapper = dataBaseInMemoryWrapper;
    }

    /**
     * Retourne la liste des personnes après chargement des données.
     *
     * @return Liste des personnes ou une liste vide si une erreur survient.
     */
    public List<Person> getPersons() {
        try {
            if (persons.isEmpty()) {

                LOGGER.info("Person list is empty, loading data...");
                dataBaseInMemoryWrapper.loadData();


                List<Person> loadedPersons = dataBaseInMemoryWrapper.getPersons();

                if (loadedPersons != null) {
                    persons.addAll(loadedPersons);
                    LOGGER.info("Successfully loaded {} persons.", loadedPersons.size());
                } else {
                    LOGGER.warn("No persons found in DataBaseInMemoryWrapper.");
                }
            }
            return new ArrayList<>(persons); // Retourner une copie pour protéger la liste interne
        } catch (Exception e) {
            LOGGER.error("Error loading Json data: {}", e.getMessage(), e);
        }
        return List.of(); // Retourner une liste vide pour éviter une exception en aval
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
