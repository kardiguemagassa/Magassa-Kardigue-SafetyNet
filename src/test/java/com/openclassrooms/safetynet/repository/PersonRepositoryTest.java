package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.Person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.openclassrooms.safetynet.constant.PersonConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonRepositoryTest {

    private  final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Mock
    private DataBaseInMemoryWrapper dataBaseInMemoryWrapper;

    @InjectMocks
    private PersonRepository personRepository;

    private Person person1;
    private Person person2;


    @BeforeEach
    void setUp() {
        person1 = new Person("John", "Doe", "123 Main St", "Springfield", "75016",
                "0144445151", "johndoe@gmail.com");
        person2 = new Person("Jane", "Doe", "123 Main St", "Springfield", "75017",
                "0144445152", "janedoe@gmail.com");
    }

    // CRUD
    @Test
    void shouldReturnGetPersonsLoadedSuccessfully() {

        LOGGER.info("Start method : shouldReturnGetPersons");
        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(Arrays.asList(person1, person2));

        // Act
        List<Person> personList = personRepository.getPersons();

        // Assert
        assertNotNull(personList);
        assertEquals(2, personList.size());

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();
        LOGGER.info("End method : shouldReturnGetPersons : Test passed");
    }

    @Test
    void shouldReturnGetPersonsDatabaseException() {

        LOGGER.info("Start method : shouldReturnGetPersonsListIsEmptyAndDatabaseReturnsEmptyList");
        when(dataBaseInMemoryWrapper.getPersons()).thenThrow(new RuntimeException("Database error"));

        List<Person> result = personRepository.getPersons();

        // Checks that the list is not null and that it is empty.
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();
        LOGGER.info("End method : shouldReturnGetPersonsListIsEmptyAndDatabaseReturnsEmptyList : Test passed");
    }

    @Test
    void shouldReturnSavePersonSuccessfully() {

        LOGGER.info("Start method : shouldSavePersonSuccessfully");

        // Arrange
        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(new ArrayList<>());

        // Act
        Person savedPerson = personRepository.save(person1);

        // Assert
        assertNotNull(savedPerson);
        assertEquals(person1, savedPerson);
        verify(dataBaseInMemoryWrapper, times(1)).getPersons();
        LOGGER.info("End method : shouldSavePersonSuccessfully : Test passed");
    }

    @Test
    void shouldReturnSavingNullPerson() {
        LOGGER.info("Start method : shouldReturnSavingNullPerson");
        Person result = personRepository.save(null);
        assertNull(result);
        LOGGER.info("End method : shouldReturnSavingNullPerson : Test passed");
    }

    @Test
    void shouldReturnSaveDataBaseException() {
        LOGGER.info("Start method : shouldReturnSaveDataBaseException");

        // Arrange
        when(dataBaseInMemoryWrapper.getPersons()).thenThrow(new RuntimeException(PERSON_ERROR_SAVING_DATA_BASE_));

        // Act & Assert
        Person savedPerson = personRepository.save(person1);

        // Assert
       assertNotNull(savedPerson);
       assertEquals(person1, savedPerson);

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();
        LOGGER.info("End method : shouldReturnSaveDataBaseException : Test passed");
    }

    @Test
    void shouldReturnFindByFullNameSuccessfully() {

        LOGGER.info("Start method: shouldReturnFindByFullNameSuccessfully");
        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(Collections.singletonList(person1));

        // Act
        Optional<Person> personList = personRepository.findByFullName(person1.getFirstName(),person1.getLastName());

        assertNotNull(personList);
        assertTrue(personList.isPresent());
        assertEquals(person1.getFirstName(), personList.get().getFirstName());
        assertEquals(person1.getLastName(), personList.get().getLastName());

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();

        LOGGER.info("End method: shouldReturnFindByFullNameSuccessfully : Test passed");
    }

    @Test
    void shouldReturnFindByFullNameNotFoundException() {

        LOGGER.info("Start method: shouldReturnFindByFullNameNotFoundException");

        when(dataBaseInMemoryWrapper.getPersons()).thenThrow(new RuntimeException(ERROR_SEARCHING_FULL_NAME));

        // Act :
        Optional<Person> personList = personRepository.findByFullName("Kardigué","MAGASSA"); // "John","Doe"

        assertNotNull(personList, "Checks that the returned list is not null");  //
        assertTrue(personList.isEmpty(), "The list must be empty because no one has that last name");

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();

        LOGGER.info("End method: shouldReturnFindByFullNameNotFoundException : Test passed");
    }

    @Test
    void shouldReturnEmptyOptionalDatabaseIsNull() {

        LOGGER.info("Start method : shouldReturnEmptyOptionalDatabaseIsNull");

        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(null);

        // Act
        Optional<Person> result = personRepository.findByFullName(person1.getFirstName(),person1.getLastName());

        // Assert
        assertTrue(result.isEmpty());
        verify(dataBaseInMemoryWrapper, times(1)).getPersons(); // Checks that getPersons has been called
        LOGGER.info("End method : shouldReturnEmptyOptionalDatabaseIsNull : Test passed");
    }

    @Test
    void shouldReturnUpdatePersonSuccessfully() {

        LOGGER.info("Start method : shouldReturnUpdatePersonSuccessfully");

        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(Collections.singletonList(person1));

        Person updatedPerson = new Person(person1.getFirstName(), person1.getLastName(), "newEmail@example.com",
                person1.getAddress(), person1.getZip(), person1.getCity(), person1.getPhone());


        Optional<Person> updated = personRepository.update(updatedPerson);

        assertTrue(updated.isPresent());
        assertEquals(updatedPerson.getEmail(), updated.get().getEmail());  // check that the email has been updated
        assertEquals(updatedPerson, updated.get());  // check that the updated person is correct

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();

        LOGGER.info("End method : shouldReturnUpdatePersonSuccessfully : Test passed");
    }

    @Test
    void shouldReturnUpdateOptionalNullPerson() {
        LOGGER.info("Start method : shouldReturnUpdateOptionalEmptyPerson");
        Optional<Person> result = personRepository.update(null);
        assertTrue(result.isEmpty(), "The person does not match");
        LOGGER.info("End method : shouldReturnUpdateOptionalEmptyPerson : Test passed");
    }


    @Test
    void shouldReturnUpdatePersonNotFound() {

        LOGGER.info("Start method : shouldReturnUpdatePersonNotFound");

        List<Person> personList = new ArrayList<>();
        personList.add(person1);

        // Utiliser doReturn() pour éviter l'erreur de type de retour
        doReturn(personList).when(dataBaseInMemoryWrapper).getPersons();

        // Créer un spy pour mocker findByFullName
        PersonRepository spyPersonRepository = spy(personRepository);
        doReturn(Optional.empty()).when(spyPersonRepository).findByFullName("newFirstName", "newLastName");


        Person updatedPerson = new Person("newFirstName", "newLastName", "newEmail@example.com",
                "newAddress", "12345", "NewCity", "123-456-7890");

        // Act: Appeler la méthode update avec une personne inexistante
        Optional<Person> updated = spyPersonRepository.update(updatedPerson);

        // Assert: Vérifier que la mise à jour retourne bien une personne vide (puisque non trouvée)
        assertTrue(updated.isPresent(), "La mise à jour devrait retourner une nouvelle personne ajoutée");
        assertEquals(updatedPerson, updated.get(), "La personne ajoutée ne correspond pas");

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();
        verify(spyPersonRepository, times(1)).findByFullName("newFirstName", "newLastName");
        verify(spyPersonRepository, times(0)).save(any(Person.class)); // save ne devrait pas être appelé !

        LOGGER.info("End method : shouldReturnUpdatePersonNotFound : Test passed");
    }

    @Test
    void shouldReturnEmptyOptionalUpdateException() {

        LOGGER.info("Start method : shouldReturnEmptyOptionalWhenUpdateThrowsException");
        when(dataBaseInMemoryWrapper.getPersons()).thenThrow(new RuntimeException(PERSON_ERROR_SAVING_UPDATING_SUCCESS));

        Optional<Person> result = personRepository.update(person1);

        assertTrue(result.isEmpty(), "The method must return an empty Optional in case of an exception");

        // Checks that getPersons has been called 2 times = 1 PersonRepository.findByFullName(), 2 PersonRepository.update()
        verify(dataBaseInMemoryWrapper, times(2)).getPersons();
        verifyNoMoreInteractions(dataBaseInMemoryWrapper);
        LOGGER.info("End method : shouldReturnEmptyOptionalWhenUpdateThrowsException : Test passed");
    }



    @Test
    void shouldDeletePersonSuccessfully() {

        LOGGER.info("Start method: shouldDeletePersonSuccessfully");

        // Arrange
        List<Person> personList = new ArrayList<>();
        personList.add(person1);
        personList.add(person2);

        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(personList);

        // Act
        boolean isDeleted = personRepository.deleteByFullName(person1.getFirstName(), person1.getLastName());

        // Assert
        assertTrue(isDeleted, "The deletion must be successful");
        verify(dataBaseInMemoryWrapper, times(1)).getPersons();
        assertFalse(personList.contains(person1), "The deleted person should no longer be in the list");

        LOGGER.info("End method: shouldDeletePersonSuccessfully : Test passed");
    }

    @Test
    void shouldReturnDeletePersonDatabaseIsNull() {

        LOGGER.info("Start method : shouldReturnDatabaseIsNull");
        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(null);
        boolean result = personRepository.deleteByFullName(person1.getFirstName(),person1.getLastName());
        assertFalse(result);
        verify(dataBaseInMemoryWrapper, times(1)).getPersons(); // Checks that getPersons has been called
        LOGGER.info("End method : shouldReturnDatabaseIsNull : Test passed");
    }

    @Test
    void shouldReturnDeletePersonException() {

        LOGGER.info("Start method : shouldReturnDeletePersonException");

        // Arrange
        AtomicReference<List<Person>> personList = new AtomicReference<>(new ArrayList<>());

        personList.get().add(person2);

        when(dataBaseInMemoryWrapper.getPersons()).thenThrow(new RuntimeException( PERSON_ERROR_DELETING_BY_FULL_NAME));

        // Act
        boolean isDeleted = personRepository.deleteByFullName(person1.getFirstName(), person1.getLastName());

        // Assert
        assertFalse(isDeleted, "Deletion should fail because the person does not exist");
        verify(dataBaseInMemoryWrapper, times(1)).getPersons();

        LOGGER.info("End method : shouldReturnDeletePersonException : Test passed");
    }


    // NEW ENDPOINT
    @Test
    void shouldReturnPersonsAddressesMatch() {

        LOGGER.info("Start method : shouldReturnPersonsAddressesMatch");
        // Arrange
        List<Person> personList = new ArrayList<>();
        personList.add(person1);
        personList.add(person2);

        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(personList);

        List<String> addressesToSearch = List.of(person1.getAddress(), person2.getAddress());

        // Act
        List<Person> foundPersons = personRepository.findByAddresses(addressesToSearch);

        // Assert
        assertNotNull(foundPersons, "The returned list must not be null");
        assertEquals(2, foundPersons.size(), "There must be 2 people found");
        assertTrue(foundPersons.contains(person1), "The list must contain person1");
        assertTrue(foundPersons.contains(person2), "The list must contain person2");

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();

        LOGGER.info("End method : shouldReturnPersonsAddressesMatch : Test passed");
    }

    @Test
    void shouldReturnAddressesMatchException() {

        LOGGER.info("Start method : shouldReturnAddressesMatchException");

        // Arrange
        when(dataBaseInMemoryWrapper.getPersons()).thenThrow(new RuntimeException( ERROR_SEARCHING_ADDRESSES));

        List<String> addressesToSearch = List.of("Unknown Address 123");

        // Act
        List<Person> foundPersons = personRepository.findByAddresses(addressesToSearch);

        // Assert
        assertNotNull(foundPersons);
        assertTrue(foundPersons.isEmpty(), "The list must be empty because no addresses match");

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();

        LOGGER.info("End method : shouldReturnAddressesMatchException : Test passed");
    }

    @Test
    void shouldReturnAddressesIsNullInDatabase() {

        LOGGER.info("Start method : shouldReturnAddressesIsNullInDatabase");

        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(null);

        List<String> addressesToSearch = List.of("Unknown Address 123");

        // Act
        List<Person> result = personRepository.findByAddresses(addressesToSearch);

        // Assert
        assertNotNull(result);
        verify(dataBaseInMemoryWrapper, times(1)).getPersons();
        LOGGER.info("End method : shouldReturnAddressesIsNullInDatabase : Test passed");
    }

    @Test
    void shouldReturnAddressesIsNull() {

        LOGGER.info("Start method : shouldReturnAddressesIsNull");
        // Act
        List<Person> result = personRepository.findByAddresses(null);

        // Assert
        assertNotNull(result);
        LOGGER.info("Start method : shouldReturnAddressesIsNull : Test passed");
    }

    @Test
    void shouldReturnPersonsAddressExists() {

        LOGGER.info("Start method : shouldReturnPersonsAddressExists");

        // Arrange
        String address = "123 Main St";
        person1.setAddress(address);
        person2.setAddress(address);

        List<Person> personList = List.of(person1, person2);

        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(personList);

        // Act
        List<Person> foundPersons = personRepository.findByAddress(address);

        // Assert
        assertNotNull(foundPersons);
        assertEquals(2, foundPersons.size(), "There must be 2 people found");
        assertTrue(foundPersons.contains(person1), "The list must contain person1");
        assertTrue(foundPersons.contains(person2), "The list must contain person2");

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();

        LOGGER.info("End method : shouldReturnPersonsAddressExists : Test passed");
    }

    @Test
    void shouldReturnAddressIsNull() {

        LOGGER.info("Start method : shouldReturnAddressIsNull");
        // Act
        List<Person> result = personRepository.findByAddress(null);

        // Assert
        assertNotNull(result);
        LOGGER.info("End method : shouldReturnAddressIsNull : Test passed");
    }

    @Test
    void shouldReturnAddressIsNullInDatabase() {

        LOGGER.info("Start method : shouldReturnAddressIsNullInDatabase");

        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(null);

        String addressesToSearch = ("Unknown Address 123");

        // Act
        List<Person> result = personRepository.findByAddress(addressesToSearch);

        // Assert
        assertNotNull(result);
        verify(dataBaseInMemoryWrapper, times(1)).getPersons();
        LOGGER.info("End method : shouldReturnAddressIsNullInDatabase : Test passed");
    }

    @Test
    void shouldReturnEmptyListWhenAddressDoesNotExist() {

        LOGGER.info("Start method : shouldReturnEmptyListWhenAddressDoesNotExist");

        // Arrange
        person1.setAddress("456 Elm St");
        person2.setAddress("789 Oak St");

        when(dataBaseInMemoryWrapper.getPersons()).thenThrow(new RuntimeException( ERROR_SEARCHING_ADDRESSES));

        // Act
        List<Person> foundPersons = personRepository.findByAddress("999 Pine St");

        // Assert
        assertNotNull(foundPersons);
        assertTrue(foundPersons.isEmpty());

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();

        LOGGER.info("End method : shouldReturnEmptyListWhenAddressDoesNotExist : passed");
    }

    @Test
    void shouldReturnPersonsWhenLastNameExists() {

        LOGGER.info("Start method : shouldReturnPersonsWhenLastNameExists");

        // Arrange
        String lastName = "Doe";
        person1.setLastName(lastName);
        person2.setLastName(lastName);

        List<Person> personList = List.of(person1, person2);

        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(personList);

        // Act
        List<Person> foundPersons = personRepository.findByLastName(lastName);

        // Assert
        assertNotNull(foundPersons);
        assertEquals(2, foundPersons.size());
        assertTrue(foundPersons.contains(person1));
        assertTrue(foundPersons.contains(person2));

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();

        LOGGER.info("End method : shouldReturnPersonsWhenLastNameExists : Test passed");
    }

    @Test
    void shouldReturnLastNameExistException() {

        LOGGER.info("Start method : shouldReturnLastNameExistException");

        // Arrange
        when(dataBaseInMemoryWrapper.getPersons()).thenThrow(new RuntimeException( ERROR_SEARCHING_ADDRESSES));

        // Act
        List<Person> foundPersons = personRepository.findByLastName("Williams");

        // Assert
        assertNotNull(foundPersons);
        assertTrue(foundPersons.isEmpty());

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();

        LOGGER.info("End method : shouldReturnLastNameExistException : Test passed");

    }

    @Test
    void shouldReturnEmptyListLastNameDoesNotExistInDatabase() {

        LOGGER.info("Start method : shouldReturnEmptyListLastNameDoesNotExistInDatabase");

        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(null);

        // Act
        List <Person> result = personRepository.findByLastName(person1.getLastName());

        // Assert
        assertTrue(result.isEmpty());
        verify(dataBaseInMemoryWrapper, times(1)).getPersons();

        LOGGER.info("End method : shouldReturnEmptyListLastNameDoesNotExistInDatabase : Test passed");

    }

    @Test
    void shouldReturnEmptyListLastNameDoesNotExist() {

        LOGGER.info("Start method : shouldReturnEmptyListLastNameDoesNotExist");

        // Act
        List <Person> result = personRepository.findByLastName(null);

        // Assert
        assertNotNull(result);
        LOGGER.info("End method : shouldReturnEmptyListLastNameDoesNotExist : Test passed");

    }

    @Test
    void shouldReturnPersonsWhenCityExists() {

        LOGGER.info("Start method : shouldReturnPersonsWhenCityExists");
        // Arrange

        List<Person> personList = new ArrayList<>();
        person1.setCity("Paris");
        person2.setCity("Paris");

        personList.add(person1);
        personList.add(person2);

        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(personList);

        // Act
        List<Person> foundPersons = personRepository.findByCity("Paris");

        // Assert
        assertNotNull(foundPersons);// "La liste retournée ne doit pas être nulle"
        assertEquals(2, foundPersons.size());
        assertTrue(foundPersons.contains(person1));
        assertTrue(foundPersons.contains(person2));

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();
        LOGGER.info("End method : shouldReturnPersonsWhenCityExists : Test passed");
    }

    @Test
    void shouldReturnCityDoesNotExistException() {

        LOGGER.info("Start method : shouldReturnCityDoesNotExistException ");

        when(dataBaseInMemoryWrapper.getPersons()).thenThrow(new RuntimeException( ERROR_SEARCHING_ADDRESSES));

        // Act
        List<Person> foundPersons = personRepository.findByCity("Toulouse");

        // Assert
        assertNotNull(foundPersons);
        assertTrue(foundPersons.isEmpty());

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();
        LOGGER.info("End method : shouldReturnCityDoesNotExistException : Test passed ");
    }

    @Test
    void shouldReturnCityDoesNotExistInDatabase() {

        LOGGER.info("Start method : shouldReturnCityDoesNotExistInDatabase");

        when(dataBaseInMemoryWrapper.getPersons()).thenReturn(null);

        // Act
        List<Person> foundPersons = personRepository.findByCity("Toulouse");

        // Assert
        assertNotNull(foundPersons);
        assertTrue(foundPersons.isEmpty());

        verify(dataBaseInMemoryWrapper, times(1)).getPersons();

        LOGGER.info("End method : shouldReturnCityDoesNotExistInDatabase : Test passed");
    }

    @Test
    void shouldReturnCityDoesNotExist() {

        LOGGER.info("Start method : shouldReturnCityDoesNotExist");

        // Act
        List<Person> foundPersons = personRepository.findByCity(null);

        // Assert
        assertNotNull(foundPersons);
        assertTrue(foundPersons.isEmpty());

        LOGGER.info("End method : shouldReturnCityDoesNotExist : Test passed");

    }
}
