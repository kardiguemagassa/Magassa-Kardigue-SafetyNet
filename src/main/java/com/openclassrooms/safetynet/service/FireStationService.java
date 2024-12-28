package com.openclassrooms.safetynet.service;

import com.openclassrooms.safetynet.convertorDTO.FireStationConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.MedicalRecordConvertorDTO;
import com.openclassrooms.safetynet.convertorDTO.PersonConvertorDTO;
import com.openclassrooms.safetynet.dto.FireStationDTO;

import com.openclassrooms.safetynet.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.dto.PersonDTO;
import com.openclassrooms.safetynet.model.FireStation;

import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;
import com.openclassrooms.safetynet.repository.FireStationRepository;
import com.openclassrooms.safetynet.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.repository.PersonRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class FireStationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FireStationService.class);

    private final FireStationRepository fireStationRepository;
    private final PersonRepository personRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final FireStationConvertorDTO fireStationConvertorDTO;
    private final PersonConvertorDTO personConvertorDTO;
    private final MedicalRecordConvertorDTO medicalRecordConvertorDTO;

    public FireStationService(FireStationRepository fireStationRepository, PersonRepository personRepository,
                              MedicalRecordRepository medicalRecordRepository,
                              FireStationConvertorDTO fireStationConvertorDTO,
                              PersonConvertorDTO personConvertorDTO,
                              MedicalRecordConvertorDTO medicalRecordConvertorDTO) {
        this.fireStationRepository = fireStationRepository;
        this.personRepository = personRepository;
        this.fireStationConvertorDTO = fireStationConvertorDTO;
        this.personConvertorDTO = personConvertorDTO;
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalRecordConvertorDTO = medicalRecordConvertorDTO;
    }

    // CRUD
    public List<FireStationDTO> getFireStations() {
        try {
            List<FireStation> fireStations = fireStationRepository.getFireStations();

            if (fireStations == null|| fireStations.isEmpty()) {
                LOGGER.warn("No fireStation found in the repository.");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No fireStation found in the repository.");
            }

            return fireStations.stream()
                    .map(fireStationConvertorDTO::convertEntityToDto)
                    .collect(Collectors.toList());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while retrieving the persons.");
        }
    }

    public List<FireStationDTO> saveAll (List<FireStationDTO> fireStationDTOList) {

        try {
            if (fireStationDTOList == null || fireStationDTOList.isEmpty()) {
                throw new IllegalArgumentException("Person list cannot be null or empty.");
            }

            List<FireStation> fireStationEntities = fireStationConvertorDTO.convertDtoToEntity(fireStationDTOList);
            List<FireStation> savedFireStationEntities = fireStationRepository.saveAll(fireStationEntities);
            return fireStationConvertorDTO.convertEntityToDto(savedFireStationEntities);

        } catch (IllegalArgumentException e) {
            LOGGER.error("Validation error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid fireStations data: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error saving persons: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while saving fireStations.", e);
            // return ResponseEntity.status(404).body(List.of("Aucune personne trouvée pour
            // l'adresse : " + address));
        }

    }

    public FireStationDTO save(FireStationDTO fireStationDTO) {
        try {
            if (fireStationDTO == null) {
                throw new IllegalArgumentException("fireStationDTO cannot be null.");
            }

            // Convertir le DTO en entité
            FireStation fireStationEntity = fireStationConvertorDTO.convertDtoToEntity(fireStationDTO);

            // Sauvegarder l'entité dans le repository
            FireStation savedFireStationEntity = fireStationRepository.save(fireStationEntity);

            // Convertir l'entité sauvegardée en DTO
            return fireStationConvertorDTO.convertEntityToDto(savedFireStationEntity);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Validation error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid fireStation data: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error saving fireStation: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while saving the fireStation.", e);
        }
    }

    public Optional<FireStationDTO> update(FireStationDTO updatedFireStationDTO) {

        try {
            if (updatedFireStationDTO == null) {
                throw new IllegalArgumentException("Updated fireStation data cannot be null.");
            }

            FireStation fireStationEntity = fireStationConvertorDTO.convertDtoToEntity(updatedFireStationDTO);
            Optional<FireStation> savedFireStationEntity = fireStationRepository.update(fireStationEntity);
            return savedFireStationEntity.map(fireStationConvertorDTO::convertEntityToDto);

        } catch (IllegalArgumentException e) {
            LOGGER.error("Validation error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid fireStation data: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error updating fireStation: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while updating the fireStation.", e);
        }
    }

    public Boolean deleteByAddress(String address) {

        if (address == null || address.isBlank()) {
            LOGGER.warn("Invalid parameters: address is null/empty. Cannot perform deletion.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "address name or last name cannot be null or empty.");
        }

        try {
            boolean isDeleted = fireStationRepository.deleteByAddress(address);

            LOGGER.info(isDeleted ?
                    "Address {} {} deleted successfully via repository." :
                    "Address {} {} not found for deletion in repository.", address);

            if (!isDeleted) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FireStation not found for deletion.");
            }

            return isDeleted;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error while deleting address {} {} via repository: {}", address, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while deleting the address.", e);
        }
    }

    // NEW ENDPOINT
    // 1 OK
    public Map<String, ?> getPersonsByStation(int stationNumber) {

        // Récupérer les adresses associées à une station donnée
        List<String> addresses = fireStationRepository.findAddressesByStationNumber(stationNumber);
        if (addresses == null || addresses.isEmpty()) {
            LOGGER.warn("No addresses found for station number {}", stationNumber);
            return Map.of("message", "No addresses found for the given station number");
        }

        // Récupérer les résidents associés à ces adresses
        List<Person> residents = personRepository.findByAddresses(addresses);
        if (residents == null || residents.isEmpty()) {
            LOGGER.warn("No residents found for addresses: {}", addresses);
            return Map.of("message", "No residents found for the given station number");
        }

        // Définir le formatteur pour analyser les dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Initialiser les compteurs d'adultes et d'enfants
        AtomicInteger adults = new AtomicInteger(0);
        AtomicInteger children = new AtomicInteger(0);

        // Utiliser un stream pour traiter les résidents
        List<Map<String, Object>> persons = residents.stream()
                .map(person -> {
                    //MedicalRecord record = medicalRecordRepository.findByFullName(person.getFirstName(), person.getLastName());
                    // ConvertiR EntityToDTO ==> C'EST PAS UNE LISTE
                    MedicalRecordDTO record = Optional.ofNullable(medicalRecordRepository.findByFullName(person.getFirstName(),
                                    person.getLastName()))
                            .map(medicalRecordConvertorDTO::convertEntityToDto)
                            .orElse(null);

                    if (record != null && record.getBirthdate() != null) {
                        try {
                            // Analyser la date de naissance et calculer l'âge
                            LocalDate birthDate = LocalDate.parse(record.getBirthdate(), formatter);
                            int age = calculateAge(birthDate);

                            // Incrémenter les compteurs d'adultes et d'enfants
                            if (age > 18) {
                                adults.incrementAndGet();
                            } else {
                                children.incrementAndGet();
                            }

                            // Retourner les informations du résident
                            Map<String, Object> personMap = new HashMap<>();
                            personMap.put("firstName", person.getFirstName());
                            personMap.put("lastName", person.getLastName());
                            personMap.put("address", person.getAddress());
                            personMap.put("phone", person.getPhone());
                            return personMap;
                        } catch (DateTimeParseException e) {
                            LOGGER.error("Erreur lors de l'analyse de la date pour {} {}: {}", person.getFirstName(),
                                    person.getLastName(), record.getBirthdate(), e);
                        }
                    }
                    return null;
                })
                //.filter(Objects::nonNull) // Supprimer les valeurs nulles
                .collect(Collectors.toList());

        // Retourner les résultats
        return Map.of(
                "persons", persons,
                "adults", adults.get(),
                "children", children.get()
        );
    }

    //1 OK
    public FireStationResponse getPersonsByStationA(int stationNumber) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Récupérer les adresses associées à une station donnée
        List<String> addresses = fireStationRepository.findAddressesByStationNumber(stationNumber);

        if (addresses == null || addresses.isEmpty()) {
            LOGGER.warn("No addresses found for station number {}", stationNumber);
        }

        // Récupérer les résidents associés à ces adresses
        List<Person> residents = personRepository.findByAddresses(addresses);

        if (residents == null || residents.isEmpty()) {
            LOGGER.warn("No residents found for addresses: {}", addresses);
        }

        // Décomptes des adultes et des enfants
        AtomicInteger adultCount = new AtomicInteger(0);
        AtomicInteger childCount = new AtomicInteger(0);

        // Transformer les résidents en une liste enrichie
        List<FireStationResponse.ResidentInfo> enrichedResidents = residents.stream()
                .map(person -> enrichPersonData(person, formatter, adultCount, childCount))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        LOGGER.info("Number of adults: {}", adultCount.get());
        LOGGER.info("Number of children: {}", childCount.get());

        // Retourner la réponse enrichie avec les décomptes
        FireStationResponse response = new FireStationResponse();
        response.setAdultCount(adultCount.get());
        response.setChildCount(childCount.get());
        response.setResidents(enrichedResidents);

        return response;
    }

    private FireStationResponse.ResidentInfo enrichPersonData(Person person, DateTimeFormatter formatter,
                                                              AtomicInteger adultCount, AtomicInteger childCount) {

        // Convertion en DTO
        MedicalRecordDTO medicalRecord = Optional.ofNullable(medicalRecordRepository.findByFullName(person.getFirstName(), person.getLastName()))
                .map(medicalRecordConvertorDTO::convertEntityToDto)
                .orElse(null);


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
                return new FireStationResponse.ResidentInfo(
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getPhone(),
                        age
                );
            } catch (DateTimeParseException e) {
                LOGGER.error("Error parsing date for person {} {}: {}",
                        person.getFirstName(), person.getLastName(), medicalRecord.getBirthdate(), e);
            }
        } else {
            LOGGER.warn("Missing medical record for person {} {}", person.getFirstName(), person.getLastName());
        }
        return null; // En cas de dossier médical manquant ou invalide
    }


    public class FireStationResponse {
        private int adultCount;
        private int childCount;
        private List<ResidentInfo> residents;

        // Getters et Setters
        public int getAdultCount() {
            return adultCount;
        }

        public void setAdultCount(int adultCount) {
            this.adultCount = adultCount;
        }

        public int getChildCount() {
            return childCount;
        }

        public void setChildCount(int childCount) {
            this.childCount = childCount;
        }

        public List<ResidentInfo> getResidents() {
            return residents;
        }

        public void setResidents(List<ResidentInfo> residents) {
            this.residents = residents;
        }

        // Classe imbriquée pour stocker les informations des résidents
        @Data
        public static class ResidentInfo {
            private String firstName;
            private String lastName;
            private String address;
            private String phone;
            private int age;


            // Constructeurs, Getters et Setters
            public ResidentInfo(String firstName, String lastName, String address, String phone, int age) {
                this.firstName = firstName;
                this.lastName = lastName;
                this.address = address;
                this.phone = phone;
                this.age = age;
            }

            public String getFirstName() {
                return firstName;
            }

            public void setFirstName(String firstName) {
                this.firstName = firstName;
            }

            public String getLastName() {
                return lastName;
            }

            public void setLastName(String lastName) {
                this.lastName = lastName;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public int getAge() {
                return age;
            }

            public void setAge(int age) {
                this.age = age;
            }

        }
    }

    // 2 ok
    public List<String> getPhoneNumbersByStation(int stationNumber) {
        // Étape 1 : Récupérer les adresses associées à la station
        List<String> addresses = fireStationRepository.findAddressesByStationNumber(stationNumber);

        if (addresses == null || addresses.isEmpty()) {
            LOGGER.warn("No addresses found for station number {}", stationNumber);
            return Collections.emptyList(); // Aucune adresse trouvée
        }

        // Étape 2 : Récupérer les résidents associés aux adresses et les convertir en DTOs
        List<PersonDTO> personDTOs = personRepository.findByAddresses(addresses)
                .stream()
                .map(personConvertorDTO::convertEntityToDto) // Conversion entité -> DTO
                .collect(Collectors.toList());

        if (personDTOs.isEmpty()) {
            LOGGER.warn("No residents found for addresses: {}", addresses);
            return Collections.emptyList(); // Aucun résident trouvé
        }

        // Étape 3 : Extraire les numéros de téléphone des DTOs
        return personDTOs.stream()
                .map(PersonDTO::getPhone) // Récupérer les numéros de téléphone depuis les DTOs
                .filter(Objects::nonNull) // Exclure les numéros nulls
                .distinct() // Éliminer les doublons
                .collect(Collectors.toList());
    }

    // 3 OK ============================================================================================================>
    public Map<String, List<Map<String, Object>>> getFloodInfoByStations(List<Integer> stationNumbers) {

        // Récupérer les adresses par numéro de station
        List<String> addresses = fireStationRepository.findAddressesByStationNumbers(stationNumbers);

        if (addresses.isEmpty()) {
            LOGGER.warn("No addresses found for the provided station numbers.");
            return Collections.emptyMap(); // Retourner une Map vide si aucune adresse
        }

        // Construire les informations pour chaque foyer
        return addresses.stream()
                .collect(Collectors.toMap(
                        address -> address, // Clé : adresse
                        address -> getResidentsInfoByAddress(address), // Valeur : liste des résidents
                        (existing, duplicate) -> existing // Stratégie : conserver la première valeur (ou choisir une autre stratégie)
                ));
    }
    // 3
    private List<Map<String, Object>> getResidentsInfoByAddress(String address) {
        // Récupérer les résidents par adresse
        List<Person> residents = personRepository.findByAddress(address);

        if (residents == null || residents.isEmpty()) {
            LOGGER.warn("No residents found for address: {}", address);
            return Collections.emptyList(); // Retourner une liste vide si aucun résident
        }

        // Format pour la date de naissance
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Construire les informations des résidents
        return residents.stream()
                .map(resident -> {
                    // Récupérer les antécédents médicaux
                    MedicalRecord medicalRecord = medicalRecordRepository.findByFullName(
                            resident.getFirstName(), resident.getLastName()
                    );

                    // Calculer l'âge
                    int age = 0;
                    if (medicalRecord != null && medicalRecord.getBirthdate() != null) {
                        try {
                            LocalDate birthDate = LocalDate.parse(medicalRecord.getBirthdate(), formatter);
                            age = calculateAge(birthDate);
                        } catch (DateTimeParseException e) {
                            LOGGER.warn("Invalid birthdate format for {} {}: {}",
                                    resident.getFirstName(), resident.getLastName(), medicalRecord.getBirthdate());
                        }
                    }

                    // Retourner les informations du résident
                    return Map.of(
                            "name", resident.getFirstName() + " " + resident.getLastName(),
                            "phone", resident.getPhone(),
                            "age", age,
                            "medications", medicalRecord != null ? medicalRecord.getMedications() : List.of(),
                            "allergies", medicalRecord != null ? medicalRecord.getAllergies() : List.of()
                    );
                })
                .filter(Objects::nonNull) // Exclure les entrées nulles
                .collect(Collectors.toList());
    }


    // ================================================================================================================>
    // 4
    public Map<String, Object> getResidentsByAddressObject(String address) {
        // Récupérer les DTO des résidents à partir du service
        List<PersonDTO> residentsDTOs = personRepository.findByAddress(address).stream()
                .map(personConvertorDTO::convertEntityToDto)
                .collect(Collectors.toList());

        // Récupérer la station associée à l'adresse et convert
        FireStation fireStation = fireStationRepository.findByAddress(address);
        FireStationDTO fireStationDTO = fireStation != null ? fireStationConvertorDTO.convertEntityToDto(fireStation) : null;


        // Vérifier si la caserne ou les résidents existent
        if (fireStationDTO == null) {
            throw new IllegalArgumentException("No fire station found for the given address: " + address);
        }

        if (residentsDTOs.isEmpty()) {
            return Map.of("message", "No residents found for the given address: " + address);
        }

        // Format pour les dates dans les dossiers médicaux
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Transformer les résidents en une liste enrichie avec leurs détails
        List<Map<String, Object>> residentDetails = residentsDTOs.stream()
                .map(resident -> enrichResidentDetails(resident, formatter))
                .filter(Objects::nonNull) // Exclure les résultats nuls
                .collect(Collectors.toList());

        // Retourner les détails des résidents et le numéro de station
        return Map.of(
                "residents", residentDetails,
                "station", fireStation.getStation()
        );
    }
    // 4
    private Map<String, Object> enrichResidentDetails(PersonDTO resident, DateTimeFormatter formatter) {
        try {
            // Récupérer le dossier médical associé au résident
            MedicalRecordDTO medicalRecordDTO = Optional.ofNullable(medicalRecordRepository.findByFullName(resident.getFirstName(),
                    resident.getLastName())).map(medicalRecordConvertorDTO::convertEntityToDto).orElse(null);

            if (medicalRecordDTO != null && medicalRecordDTO.getBirthdate() != null) {
                // Analyse de la date de naissance et calcul de l'âge
                LocalDate birthDate = LocalDate.parse(medicalRecordDTO.getBirthdate(), formatter);
                int age = calculateAge(birthDate);

                // Retourner une Map contenant les détails enrichis
                return Map.of(
                        "name", resident.getFirstName() + " " + resident.getLastName(),
                        "phone", resident.getPhone(),
                        "age", age,
                        "medications", medicalRecordDTO.getMedications(),
                        "allergies", medicalRecordDTO.getAllergies()
                );
            }

            // Retourner null si le dossier médical est invalide ou manquant
            return null;
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Erreur lors de l'analyse de la date : " + resident.getFirstName()
                    + " " + resident.getLastName() + " - " + e.getMessage(), e);
        }
    }

    // 4
    public ResidentsByAddressResponse getResidentsByAddress(String address) {
        // Récupérer les DTO des résidents à partir du service
        List<PersonDTO> residentsDTOs = personRepository.findByAddress(address).stream()
                .map(personConvertorDTO::convertEntityToDto)
                .collect(Collectors.toList());

        // Récupérer la station associée à l'adresse et convertir en DTO
        FireStationDTO fireStationDTO = Optional.ofNullable(fireStationRepository.findByAddress(address))
                .map(fireStationConvertorDTO::convertEntityToDto)
                .orElseThrow(() -> new IllegalArgumentException("No fire station found for the given address: " + address));

        if (residentsDTOs.isEmpty()) {
            throw new IllegalArgumentException("No residents found for the given address: " + address);
        }

        // Format pour les dates dans les dossiers médicaux
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Transformer les résidents en une liste enrichie
        List<Map<String, Object>> residentDetails = residentsDTOs.stream()
                .map(resident -> {
                    // Récupérer le dossier médical en DTO
                    MedicalRecordDTO medicalRecordDTO = Optional.ofNullable(medicalRecordRepository.findByFullName(resident.getFirstName(),
                                    resident.getLastName()))
                            .map(medicalRecordConvertorDTO::convertEntityToDto)
                            .orElse(null);

                    if (medicalRecordDTO != null && medicalRecordDTO.getBirthdate() != null) {
                        try {
                            // Analyse de la date de naissance et calcul de l'âge
                            LocalDate birthDate = LocalDate.parse(medicalRecordDTO.getBirthdate(), formatter);
                            int age = calculateAge(birthDate);

                            // Retourner une Map contenant les détails du résident
                            return Map.of(
                                    "name", resident.getFirstName() + " " + resident.getLastName(),
                                    "phone", resident.getPhone(),
                                    "age", age,
                                    "medications", medicalRecordDTO.getMedications(),
                                    "allergies", medicalRecordDTO.getAllergies()
                            );
                        } catch (DateTimeParseException e) {
                            LOGGER.warn("Invalid birthdate format for resident {} {}: {}",
                                    resident.getFirstName(), resident.getLastName(), medicalRecordDTO.getBirthdate());
                        }
                    }

                    // Si le dossier médical est manquant ou invalide, retourner une Map minimale
                    return Map.of(
                            "name", resident.getFirstName() + " " + resident.getLastName(),
                            "phone", resident.getPhone(),
                            "age", "Unknown",
                            "medications", List.of(),
                            "allergies", List.of()
                    );
                })
                .filter(Objects::nonNull) // Exclure les résultats nuls
                .collect(Collectors.toList());

        // Retourner les détails des résidents et la station en réponse
        return new ResidentsByAddressResponse(residentDetails, fireStationDTO);
    }


    public class ResidentsByAddressResponse {
        private List<Map<String, Object>> residents;
        private FireStationDTO station;

        public ResidentsByAddressResponse(List<Map<String, Object>> residents, FireStationDTO station) {
            this.residents = residents;
            this.station = station;
        }

        public List<Map<String, Object>> getResidents() {
            return residents;
        }

        public FireStationDTO getStation() {
            return station;
        }
    }

    // Méthode utilitaire pour calculer l'âge à partir de la date de naissance
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

}




