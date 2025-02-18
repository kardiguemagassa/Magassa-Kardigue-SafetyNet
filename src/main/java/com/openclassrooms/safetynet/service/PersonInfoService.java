package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;

import com.openclassrooms.safetynet.repository.FireStationRepository;
import com.openclassrooms.safetynet.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.openclassrooms.safetynet.constant.ResidentInfoConstant.*;

@Service
@AllArgsConstructor
public class PersonInfoService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final PersonRepository personRepository;
    private final PersonConvertorDTO personConvertorDTO;
    private final FireStationRepository fireStationRepository;

    public List<String> getPhoneNumbersByStation(int stationNumber) {

        List<String> addresses = fireStationRepository.findAddressesByStationNumber(stationNumber);

        if (addresses == null || addresses.isEmpty()) {
            LOGGER.error(API_ADDRESS_NUMBER_NOT_FOUND, stationNumber);
            throw new IllegalArgumentException(API_ADDRESS_NUMBER_NOT_FOUND);
        }

        // Convert Person objects to PersonDTO
        return personRepository.findByAddresses(addresses).stream()
                .map(personConvertorDTO::convertEntityToDto)
                .map(PersonDTO::getPhone)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<String> getCommunityEmails(String city) {

        if (city == null || city.isBlank()) {
            LOGGER.error(CITY_NOT_FOUND);
            throw new IllegalArgumentException(CITY_NOT_FOUND);
        }

        // Recovery of city residents
        List<PersonDTO> residents = personRepository.findByCity(city).stream()
                .map(personConvertorDTO::convertEntityToDto)
                .toList();

        return residents.stream()
                .map(PersonDTO::getEmail)
                .filter(email -> email != null && !email.isBlank())
                .distinct()
                .toList();
    }
}
