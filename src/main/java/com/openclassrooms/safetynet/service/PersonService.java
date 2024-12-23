package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
@AllArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public List<Person> getPersons() {
        return personRepository.getPersons();
    }

    public Person save(Person person) {
        return null;
    }
}
