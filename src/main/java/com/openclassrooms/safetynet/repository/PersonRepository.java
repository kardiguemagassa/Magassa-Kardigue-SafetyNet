package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.utils.CsvUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.openclassrooms.safetynet.constant.repository.PersonRepositoryConstant.*;

@Component
@AllArgsConstructor
public class PersonRepository {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    // Liste des personnes (sera remplie après le chargement des données)
    private final List<Person> persons = new ArrayList<>();
    private final DataBaseInMemoryWrapper dataBaseInMemoryWrapper;


    // CRUD
    public List<Person> getPersons() {

        try {
            // Vérifier si la liste est vide
            if (persons.isEmpty()) {
                LOGGER.info(PERSON_List_EMPTY);

                // Charger les données depuis la base de données en mémoire
                dataBaseInMemoryWrapper.loadData();
                List<Person> loadedPersons = dataBaseInMemoryWrapper.getPersons();

                // Si des données ont été chargées, les écrire dans le fichier CSV
                if (loadedPersons != null && !loadedPersons.isEmpty()) {
                    savePersonsToCsv(loadedPersons);
                    //loadPersonsFromCsv();
                    persons.addAll(loadedPersons);
                    LOGGER.info(PERSON_LOADED, loadedPersons.size());
                } else {
                    LOGGER.warn(PERSON_NOT_FOUND);
                }
            }

            // Retourner une copie de la liste des personnes
            return new ArrayList<>(persons);
        } catch (Exception e) {
            LOGGER.error(PERSON_ERROR_LOADING, e.getMessage(), e);
            return List.of();
        }
    }

    public List<Person> saveAll(List<Person> personList) {

        if (personList == null || personList.isEmpty()) {
            LOGGER.warn(PERSON_ERROR_SAVING);
            return new ArrayList<>(persons); // Retourner l'état actuel sans modification
        }

        try {
            //persons.addAll(personList);

            List<Person> wrapperPersons = dataBaseInMemoryWrapper.getPersons();
            if (wrapperPersons != null) {
                wrapperPersons.addAll(personList);
                savePersonsToCsv(wrapperPersons);
                //LOGGER.info("Successfully loaded {} persons.=======>", wrapperPersons.size());
                //LOGGER.info(PERSON_SAVING_CSV, wrapperPersons.size());

            } else {
                //LOGGER.warn("Nothing  persons found:");
                LOGGER.warn(PERSON_ERROR_SAVING_CSV);
            }

            LOGGER.info(PERSON_SAVING_DATA_BASE, personList.size());
        } catch (Exception e) {
            LOGGER.error(PERSON_ERROR_SAVING_DATA_BASE, e.getMessage(), e);
        }
        return new ArrayList<>(persons); // Retourner une copie immuable
    }

    public Person save(Person person) {

        if (person == null) {
            LOGGER.warn(PERSON_ERROR);
            return null;
        }

        try {
            persons.add(person);

            List<Person> wrapperPersons = dataBaseInMemoryWrapper.getPersons();
            if (wrapperPersons != null) {
                wrapperPersons.add(person);
                savePersonsToCsv(wrapperPersons);
                LOGGER.info(PERSON_SAVING_CSV, wrapperPersons.size());
            } else {
                LOGGER.warn(PERSON_ERROR_SAVING_CSV_FILE);
            }

            LOGGER.info(PERSON_SAVING_DATA_BASE_SUC, person);
        } catch (Exception e) {
            LOGGER.error(PERSON_ERROR_SAVING_DATA_BASE_, e.getMessage(), e);
        }
        return person;
    }

    public Optional<Person> findByFullName(String firstName, String lastName) {

        try {
            List<Person> allPersons = dataBaseInMemoryWrapper.getPersons(); // Récupérer les données du wrapper

            if (allPersons == null) {
                LOGGER.warn(FULL_NAME_NOT_FOUND);
                return Optional.empty();
            }

            return allPersons.stream()
                    .filter(person -> person.getFirstName().equalsIgnoreCase(firstName)
                            && person.getLastName().equalsIgnoreCase(lastName)).findFirst();
        } catch (Exception e) {
            LOGGER.error(ERROR_SEARCHING_FULL_NAME, e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Person> update(Person updatedPerson) {

        if (updatedPerson == null) {
            LOGGER.warn(PERSON_ERROR_UPDATING);
            return Optional.empty();
        }

        try {
            Optional<Person> existingPerson = findByFullName(updatedPerson.getFirstName(), updatedPerson.getLastName());

            if (existingPerson.isEmpty()) {
                LOGGER.info(PERSON_NOT_FOUND_UPDATING, updatedPerson.getFirstName(), updatedPerson.getLastName());
                persons.add(updatedPerson);
                dataBaseInMemoryWrapper.getPersons().add(updatedPerson); // Mise à jour du wrapper
                savePersonsToCsv(persons);
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

            LOGGER.info(PERSON_ERROR_UPDATING_SUCCESS, updatedPerson);
            return existingPerson;
        } catch (Exception e) {
            LOGGER.error(PERSON_ERROR_SAVING_UPDATING_SUCCESS, e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Boolean deleteByFullName(String firstName, String lastName) {

        try {
            List<Person> allPersons = dataBaseInMemoryWrapper.getPersons(); // Recover data

            if (allPersons == null || allPersons.isEmpty()) {
                LOGGER.warn(PERSON_ERROR_DELETING);
                return false;
            }

            savePersonsToCsv(allPersons);

            boolean isDeleted = allPersons.removeIf(person ->
                    person.getFirstName().equalsIgnoreCase(firstName) && person.getLastName().equalsIgnoreCase(lastName));
            //savePersonsToCsv(allPersons);

            if (isDeleted) {
                persons.removeIf(person ->
                        person.getFirstName().equalsIgnoreCase(firstName) && person.getLastName().equalsIgnoreCase(lastName));
                LOGGER.info(PERSON_DELETING_SUCCESS, firstName, lastName);
            } else {
                LOGGER.warn(PERSON_ERROR_DELETING_NOT_FOUND, firstName, lastName);
            }

            return isDeleted;
        } catch (Exception e) {
            LOGGER.error(PERSON_ERROR_DELETING_BY_FULL_NAME, e.getMessage(), e);
            return false;
        }

    }

    // NEW ENDPOINT
    public List<Person> findByAddresses(List<String> addresses) {

        if (addresses == null || addresses.isEmpty()) {
            LOGGER.warn(PERSON_ERROR_SEARCHING_ADDRESSES);
            return List.of();
        }

        try {
            List<Person> allPersons = dataBaseInMemoryWrapper.getPersons();
            if (allPersons == null) {
                LOGGER.warn(PERSON_ADDRESSES_NOT_FOUND);
                return List.of();
            }

            return allPersons.stream()
                    .filter(person -> addresses.contains(person.getAddress()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error(ERROR_SEARCHING_ADDRESSES, e.getMessage(), e);
        }
        return List.of();
    }

    public List<Person> findByAddress(String address) {

        if (address == null || address.isBlank()) {
            LOGGER.warn(PERSON_ERROR_SEARCHING_ADDRESS);
            return List.of();
        }

        try {
            List<Person> allPersons = dataBaseInMemoryWrapper.getPersons();
            if (allPersons == null) {
                LOGGER.warn(PERSON_ADDRESS_NOT_FOUND);
                return List.of();
            }

            return allPersons.stream()
                    .filter(person -> person.getAddress().equalsIgnoreCase(address))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error(ERROR_SEARCHING_ADDRESS, e.getMessage(), e);
        }
        return List.of();
    }

    public List<Person> findByLastName(String lastName) {

        if (lastName == null || lastName.isBlank()) {
            LOGGER.warn(LAST_NAME_NULL);
            return List.of();
        }

        try {
            List<Person> allPersons = dataBaseInMemoryWrapper.getPersons();
            if (allPersons == null) {
                LOGGER.warn(LAST_NAME_NOT_FOUND);
                return List.of();
            }

            return allPersons.stream()
                    .filter(person -> person.getLastName().equalsIgnoreCase(lastName))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error(LAST_NAME_ERROR_SEARCHING_ADDRESS, e.getMessage(), e);
        }
        return List.of();
    }

    public List<Person> findByCity(String city) {

        if (city == null || city.isBlank()) {
            LOGGER.warn(CITY_IS_NULL);
            return List.of();
        }
        try {
            List<Person> allPersons = dataBaseInMemoryWrapper.getPersons();
            if (allPersons == null) {
                LOGGER.warn(CITY_NOT_FOUND);
                return List.of();
            }

            LOGGER.debug(PERSON_TOTAL_IN_DATA_BASE, allPersons.size());

            List<Person> filteredPersons = allPersons.stream()
                    .filter(person -> person.getCity().equalsIgnoreCase(city))
                    .collect(Collectors.toList());

            LOGGER.debug(FILTERED_CITY, city, filteredPersons.size());
            return filteredPersons;
        } catch (Exception e) {
            LOGGER.error(CITY_ERROR_SEARCHING, e.getMessage(), e);
        }
        return List.of();
    }

    public int calculateAge(LocalDate birthDate) {

        if (birthDate == null) {
            LOGGER.warn(CALCULATED_AGE);
            return 0;
        }

        try {
            return Period.between(birthDate, LocalDate.now()).getYears();
        } catch (Exception e) {
            LOGGER.error(ERROR_CALCULATED_AGE, e.getMessage(), e);
        }
        return 0;
    }

    private void savePersonsToCsv(List<Person> personsToSave) {

        CsvUtils.saveToCsv(PERSON_CSV_CONFIG_FILE, personsToSave);
    }

    private List<Person> loadPersonsFromCsv() {

        List<Person> collect;
        collect = CsvUtils.loadFromCsv(PERSON_CSV_CONFIG_FILE, line -> {
                    String[] parts = line.split(",");
                    if (parts.length < 7) {
                        LOGGER.warn(ERROR_INVALID_CSV, line);
                        return null;
                    }
                    return new Person(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
                }).stream()
                .filter(person -> person != null)
                .collect(Collectors.toList());
        return collect;
    }
}
