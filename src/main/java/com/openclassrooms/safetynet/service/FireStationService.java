package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.FireStationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class FireStationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FireStationService.class);

    private final FireStationRepository fireStationRepository;

    public FireStationService(FireStationRepository fireStationRepository) {
        this.fireStationRepository = fireStationRepository;
    }

    public List<FireStation> getFireStations() {
        return fireStationRepository.getFireStations();
    }

    /*
    public Map<String, Object> getPersonsByStation(int stationNumber) {
        // Définir le formatteur pour les dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Récupérer les adresses associées à la station
        List<String> addresses = fireStationRepository.findAddressesByStationNumber(stationNumber);

        if (addresses == null || addresses.isEmpty()) {
            LOGGER.warn("No addresses found for station number {}", stationNumber);
            return Map.of(
                    "message", "No addresses found for the given station number.",
                    "adultCount", 0,
                    "childCount", 0,
                    "residents", Collections.emptyList()
            );
        }

        // Récupérer les résidents associés à ces adresses
        List<Person> residents = personRepository.findByAddresses(addresses);

        if (residents == null || residents.isEmpty()) {
            LOGGER.warn("No residents found for addresses: {}", addresses);
            return Map.of(
                    "message", "No residents found for the given station number.",
                    "adultCount", 0,
                    "childCount", 0,
                    "residents", Collections.emptyList()
            );
        }

        // Décomptes des adultes et des enfants
        AtomicInteger adultCount = new AtomicInteger(0);
        AtomicInteger childCount = new AtomicInteger(0);

        // Transformer les résidents en une liste enrichie
        List<EnrichedFireStationDTO> enrichedResidents = residents.stream()
                .map(person -> enrichPersonData(person, formatter, adultCount, childCount))
                .filter(Objects::nonNull) // Exclure les résultats nuls
                .collect(Collectors.toList());

        // Log le décompte des adultes et des enfants
        LOGGER.info("Number of adults: {}", adultCount.get());
        LOGGER.info("Number of children: {}", childCount.get());

        // Retourner la réponse enrichie avec les décomptes
        return Map.of(
                "adultCount", adultCount.get(),
                "childCount", childCount.get(),
                "residents", enrichedResidents
        );
    }

     */

    /**
     * Enrichit les données d'une personne avec son âge et d'autres informations.
     */
    /*
    private EnrichedFireStationDTO enrichPersonData(Person person, DateTimeFormatter formatter,
                                                    AtomicInteger adultCount, AtomicInteger childCount) {
        MedicalRecordDTO medicalRecord = medicalRecordService.findByFullName(person.getFirstName(), person.getLastName());

        if (medicalRecord != null && medicalRecord.getBirthdate() != null) {
            try {
                // Calculer l'âge du résident
                LocalDate birthDate = LocalDate.parse(medicalRecord.getBirthdate(), formatter);
                int age = calculateAge(birthDate);

                // Incrémenter les compteurs
                if (age > 18) {
                    adultCount.incrementAndGet();
                } else {
                    childCount.incrementAndGet();
                }

                // Retourner un objet enrichi
                return EnrichedFireStationDTO.builder()
                        .firstName(person.getFirstName())
                        .lastName(person.getLastName())
                        .address(person.getAddress())
                        .phone(person.getPhone())
                        .age(age)
                        .build();
            } catch (DateTimeParseException e) {
                LOGGER.error("Error parsing date for person {} {}: {}",
                        person.getFirstName(), person.getLastName(), medicalRecord.getBirthdate(), e);
            }
        } else {
            LOGGER.warn("Missing medical record for person {} {}", person.getFirstName(), person.getLastName());
        }
        return null; // En cas de dossier médical manquant ou invalide
    }

     */

}
