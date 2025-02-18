package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.utils.CsvUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.openclassrooms.safetynet.constant.PersonConstant.*;

@Component
public class PersonRepository {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    // List of people (will be filled after data loading)
    private final List<Person> persons = new ArrayList<>();
    private final DataBaseInMemoryWrapper dataBaseInMemoryWrapper;

    private boolean isLoading = false;

    @Autowired
    public PersonRepository(DataBaseInMemoryWrapper dataBaseInMemoryWrapper) {
        this.dataBaseInMemoryWrapper = dataBaseInMemoryWrapper;
    }

    // CRUD
    public List<Person> getPersons() {
        try {
            if (persons.isEmpty() && !isLoading) {
                isLoading = true;
                LOGGER.info(PERSON_List_EMPTY);

                List<Person> loadedPersons = dataBaseInMemoryWrapper.getPersons();

                if (loadedPersons != null && !loadedPersons.isEmpty()) {

                    persons.addAll(loadedPersons);
                    savePersonsToCsv(loadedPersons);

                    LOGGER.info(PERSON_LOADED, loadedPersons.size());
                } else {
                    LOGGER.warn(PERSON_NOT_FOUND);
                }
                isLoading = false;
            }
            return new ArrayList<>(persons);
        } catch (Exception e) {
            isLoading = false;
            LOGGER.error(PERSON_ERROR_LOADING, e.getMessage());

            return Collections.emptyList();
        }
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
            }

            LOGGER.info(PERSON_SAVING_DATA_BASE_SUC, person);

        } catch (Exception e) {
            LOGGER.error(PERSON_ERROR_SAVING_DATA_BASE_, e.getMessage());
        }
        return person;
    }

    public Optional<Person> findByFullName(String firstName, String lastName) {

        try {
            List<Person> allPersons = dataBaseInMemoryWrapper.getPersons(); // Retrieve data from the wrapper

            if (allPersons == null) {
                LOGGER.warn(FULL_NAME_NOT_FOUND);
                return Optional.empty();
            }

            return allPersons.stream()
                    .filter(person -> person.getFirstName().equalsIgnoreCase(firstName)
                            && person.getLastName().equalsIgnoreCase(lastName)).findFirst();
        } catch (Exception e) {
            LOGGER.error(ERROR_SEARCHING_FULL_NAME, e.getMessage());
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
            LOGGER.error(PERSON_ERROR_SAVING_UPDATING_SUCCESS, e.getMessage());
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


            if (isDeleted) {
                LOGGER.info(PERSON_DELETING_SUCCESS, firstName, lastName);
            }

            return isDeleted;
        } catch (Exception e) {
            LOGGER.error(PERSON_ERROR_DELETING_BY_FULL_NAME, e.getMessage());
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
            LOGGER.error(ERROR_SEARCHING_ADDRESSES, e.getMessage());
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
            LOGGER.error(ERROR_SEARCHING_ADDRESS, e.getMessage());
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
            LOGGER.error(LAST_NAME_ERROR_SEARCHING_ADDRESS, e.getMessage());
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
            LOGGER.error(CITY_ERROR_SEARCHING, e.getMessage());
        }
        return List.of();
    }

    private void savePersonsToCsv(List<Person> personsToSave) {

        CsvUtils.saveToCsv(PERSON_CSV_CONFIG_FILE, personsToSave);
    }

}
