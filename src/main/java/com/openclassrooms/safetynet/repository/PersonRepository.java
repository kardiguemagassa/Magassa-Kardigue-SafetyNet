package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // CRUD
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
        if (personList == null || personList.isEmpty()) {
            LOGGER.warn("Attempted to save an empty or null person list.");
            return new ArrayList<>(persons); // Retourner l'état actuel sans modification
        }

        try {
            persons.addAll(personList);

            List<Person> wrapperPersons = dataBaseInMemoryWrapper.getPersons();
            if (wrapperPersons != null) {
                wrapperPersons.addAll(personList);
            } else {
                LOGGER.warn("DataBaseInMemoryWrapper persons list is null, skipping wrapper update.");
            }

            LOGGER.info("Successfully registered {} persons.", personList.size());
        } catch (Exception e) {
            LOGGER.error("Error registering people: {}", e.getMessage(), e);
        }
        return new ArrayList<>(persons); // Retourner une copie immuable
    }

    public Person save(Person person) {
        if (person == null) {
            LOGGER.warn("Attempted to save a null person.");
            return null; // Indiquer que rien n'a été enregistré
        }

        try {
            persons.add(person);

            List<Person> wrapperPersons = dataBaseInMemoryWrapper.getPersons();
            if (wrapperPersons != null) {
                wrapperPersons.add(person);
            } else {
                LOGGER.warn("DataBaseInMemoryWrapper persons list is null, skipping wrapper update.");
            }

            LOGGER.info("Successfully registered person: {}", person);
        } catch (Exception e) {
            LOGGER.error("Error registering a person {}", e.getMessage(), e);
        }
        return person;
    }

    public Optional<Person> findByFullName(String firstName, String lastName) {
        try {
            List<Person> allPersons = dataBaseInMemoryWrapper.getPersons(); // Récupérer les données du wrapper
            if (allPersons == null) {
                LOGGER.warn("DataBaseInMemoryWrapper persons list is null.");
                return Optional.empty();
            }

            return allPersons.stream()
                    .filter(person -> person.getFirstName().equalsIgnoreCase(firstName) && person.getLastName().equalsIgnoreCase(lastName))
                    .findFirst();
        } catch (Exception e) {
            LOGGER.error("Error while searching for person by full name: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Person> update(Person updatedPerson) {
        if (updatedPerson == null) {
            LOGGER.warn("Attempted to update a null person.");
            return Optional.empty();
        }

        try {
            Optional<Person> existingPerson = findByFullName(updatedPerson.getFirstName(), updatedPerson.getLastName());

            if (existingPerson.isEmpty()) {
                LOGGER.info("Person not found, adding a new person: {} {}", updatedPerson.getFirstName(), updatedPerson.getLastName());
                persons.add(updatedPerson);
                dataBaseInMemoryWrapper.getPersons().add(updatedPerson); // Mise à jour du wrapper
                return Optional.of(updatedPerson);
            }

            existingPerson.ifPresent(person -> {
                person.setFirstName(updatedPerson.getFirstName());
                person.setLastName(updatedPerson.getLastName());
                person.setEmail(updatedPerson.getEmail());
                person.setAddress(updatedPerson.getAddress());
                person.setZip(updatedPerson.getZip());
                person.setCity(updatedPerson.getCity());
                person.setPhone(updatedPerson.getPhone());
            });

            LOGGER.info("Person updated successfully: {}", updatedPerson);
            return existingPerson;
        } catch (Exception e) {
            LOGGER.error("Error updating person: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Boolean deleteByFullName(String firstName, String lastName) {
        try {
            List<Person> allPersons = dataBaseInMemoryWrapper.getPersons(); // Recover data
            if (allPersons == null) {
                LOGGER.warn("DataBaseInMemoryWrapper persons list is null. Cannot perform deletion.");
                return false;
            }

            boolean isDeleted = allPersons.removeIf(person ->
                    person.getFirstName().equalsIgnoreCase(firstName) && person.getLastName().equalsIgnoreCase(lastName));

            if (isDeleted) {
                persons.removeIf(person ->
                        person.getFirstName().equalsIgnoreCase(firstName) && person.getLastName().equalsIgnoreCase(lastName));
                LOGGER.info("Person {} {} deleted successfully.", firstName, lastName);
            } else {
                LOGGER.warn("Person {} {} not found for deletion.", firstName, lastName);
            }

            return isDeleted;
        } catch (Exception e) {
            LOGGER.error("Error while deleting person by full name: {}", e.getMessage(), e);
        }
        return false;
    }

    // NEW ENDPOINT
    public List<Person> findByAddresses(List<String> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            LOGGER.warn("Attempted to search with an empty or null address list.");
            return List.of();
        }

        try {
            List<Person> allPersons = dataBaseInMemoryWrapper.getPersons();
            if (allPersons == null) {
                LOGGER.warn("DataBaseInMemoryWrapper persons list is null.");
                return List.of();
            }

            return allPersons.stream()
                    .filter(person -> addresses.contains(person.getAddress()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Error while searching for persons by addresses: {}", e.getMessage(), e);
        }
        return List.of();
    }

    public List<Person> findByAddress(String address) {
        if (address == null || address.isBlank()) {
            LOGGER.warn("Attempted to search with a null or blank address.");
            return List.of();
        }

        try {
            List<Person> allPersons = dataBaseInMemoryWrapper.getPersons();
            if (allPersons == null) {
                LOGGER.warn("DataBaseInMemoryWrapper persons list is null.");
                return List.of();
            }

            return allPersons.stream()
                    .filter(person -> person.getAddress().equalsIgnoreCase(address))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Error while searching for persons by address: {}", e.getMessage(), e);
        }
        return List.of();
    }

    public List<Person> findByLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            LOGGER.warn("Attempted to search with a null or blank last name.");
            return List.of();
        }

        try {
            List<Person> allPersons = dataBaseInMemoryWrapper.getPersons();
            if (allPersons == null) {
                LOGGER.warn("DataBaseInMemoryWrapper persons list is null.");
                return List.of();
            }

            return allPersons.stream()
                    .filter(person -> person.getLastName().equalsIgnoreCase(lastName))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Error while searching for persons by last name: {}", e.getMessage(), e);
        }
        return List.of();
    }

    public List<Person> findByCity(String city) {
        if (city == null || city.isBlank()) {
            LOGGER.warn("Attempted to search with a null or blank city.");
            return List.of();
        }

        try {
            List<Person> allPersons = dataBaseInMemoryWrapper.getPersons();
            if (allPersons == null) {
                LOGGER.warn("DataBaseInMemoryWrapper persons list is null.");
                return List.of();
            }

            LOGGER.debug("Total persons in database: {}", allPersons.size());

            List<Person> filteredPersons = allPersons.stream()
                    .filter(person -> person.getCity().equalsIgnoreCase(city))
                    .collect(Collectors.toList());

            LOGGER.debug("Filtered persons for city '{}': {}", city, filteredPersons.size());
            return filteredPersons;
        } catch (Exception e) {
            LOGGER.error("Error while searching for persons by city: {}", e.getMessage(), e);
        }
        return List.of();
    }

    public int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            LOGGER.warn("Cannot calculate age for a null birth date.");
            return 0;
        }

        try {
            return Period.between(birthDate, LocalDate.now()).getYears();
        } catch (Exception e) {
            LOGGER.error("Error while calculating age: {}", e.getMessage(), e);
        }
        return 0;
    }
}
