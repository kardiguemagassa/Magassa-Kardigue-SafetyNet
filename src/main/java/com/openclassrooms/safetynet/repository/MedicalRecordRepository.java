package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.MedicalRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

@Repository
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
@Component
public class MedicalRecordRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordRepository.class);

    private final List<MedicalRecord> medicalRecords = new ArrayList<>();
    private final DataBaseInMemoryWrapper dataBaseInMemoryWrapper;

    public MedicalRecordRepository (DataBaseInMemoryWrapper dataBaseInMemoryWrapper) {
        this.dataBaseInMemoryWrapper = dataBaseInMemoryWrapper;
    }

    public List<MedicalRecord> getMedicalRecords() {
        try {
            if (medicalRecords.isEmpty()) {
                LOGGER.info("MedicalRecord list is empty, loading data...");
                List<MedicalRecord> loadedMedicalRecords = dataBaseInMemoryWrapper.getMedicalRecords();
                if (loadedMedicalRecords != null) {
                    medicalRecords.addAll(loadedMedicalRecords);
                    LOGGER.info("Successfully loaded {} medicalRecords.", loadedMedicalRecords.size());
                } else {
                    LOGGER.warn("No persons found in DataBaseInMemoryWrapper.");
                }
            }
            return new ArrayList<>(medicalRecords);
        } catch (Exception e) {
            LOGGER.error("Error loading Json data: {}", e.getMessage(), e);
        }
        return List.of();
    }

    public List<MedicalRecord> saveAll(List<MedicalRecord> medicalRecordList) {
        if (medicalRecordList == null || medicalRecordList.isEmpty()) {
            LOGGER.warn("Attempted to save an empty or null person list.");
            return new ArrayList<>(medicalRecords);
        }

        try {
            medicalRecords.addAll(medicalRecordList);

            List<MedicalRecord> wrapperMedicalRecords = dataBaseInMemoryWrapper.getMedicalRecords();
            if (wrapperMedicalRecords != null) {
                wrapperMedicalRecords.addAll(medicalRecordList);
            } else {
                LOGGER.warn("Nothing  medicalRecord found:");
            }

            LOGGER.info("Successfully registered {} medicalRecords.", medicalRecordList.size());
        } catch (Exception e) {
            LOGGER.error("Error registering medicalRecords: {}", e.getMessage(), e);
        }
        return new ArrayList<>(medicalRecords);
    }

    public MedicalRecord save(MedicalRecord medicalRecord) {
        if (medicalRecord == null) {
            LOGGER.warn("Attempted to save a null medicalRecord.");
            return null;
        }

        try {
            medicalRecords.add(medicalRecord);

            List<MedicalRecord> wrapperMedicalRecords = dataBaseInMemoryWrapper.getMedicalRecords();
            if (wrapperMedicalRecords != null) {
                wrapperMedicalRecords.add(medicalRecord);
            } else {
                LOGGER.warn("DataBaseInMemoryWrapper medicalRecords list is null, skipping wrapper update.");
            }

            LOGGER.info("Successfully registered medicalRecord: {}", medicalRecord);
        } catch (Exception e) {
            LOGGER.error("Error registering a medicalRecord {}", e.getMessage(), e);
        }
        return medicalRecord;
    }

    public Optional<MedicalRecord> findByMedicationsAndAllergies(List<String> medications, List<String> allergies) {
        try {
            List<MedicalRecord> allMedicalRecords = dataBaseInMemoryWrapper.getMedicalRecords();
            if (allMedicalRecords == null) {
                LOGGER.warn("Medical records list is null.");
                return Optional.empty();
            }

            return allMedicalRecords.stream()
                    .filter(medicalRecord -> medicalRecord.getMedications().equals(medications)
                            && medicalRecord.getAllergies().equals(allergies))
                    .findFirst();
        } catch (Exception e) {
            LOGGER.error("Error searching by medications and allergies: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<MedicalRecord> update(MedicalRecord updateMedicalRecord) {
        if (updateMedicalRecord == null) {
            LOGGER.warn("Attempted to update a null medical record.");
            return Optional.empty();
        }

        try {
            Optional<MedicalRecord> existingRecord = findByMedicationsAndAllergies(
                    updateMedicalRecord.getMedications(),
                    updateMedicalRecord.getAllergies()
            );

            if (existingRecord.isEmpty()) {
                LOGGER.info("Medical record not found, creating new record.");
                medicalRecords.add(updateMedicalRecord);
                List<MedicalRecord> wrapperRecords = dataBaseInMemoryWrapper.getMedicalRecords();
                if (wrapperRecords != null) {
                    wrapperRecords.add(updateMedicalRecord);
                } else {
                    LOGGER.warn(" medical records list is null.");
                }
                return Optional.of(updateMedicalRecord);
            }

            existingRecord.ifPresent(record -> {
                record.setMedications(updateMedicalRecord.getMedications());
                record.setAllergies(updateMedicalRecord.getAllergies());
            });

            LOGGER.info("Medical record updated successfully.");
            return existingRecord;
        } catch (Exception e) {
            LOGGER.error("Error updating medical record: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Boolean deleteByFullName(String firstName, String lastName) {
        try {
            List<MedicalRecord> allMedicalRecords = dataBaseInMemoryWrapper.getMedicalRecords();
            if (allMedicalRecords == null) {
                LOGGER.warn("DataBaseInMemoryWrapper medical records list is null. Cannot perform deletion.");
                return false;
            }

            boolean isDeleted = allMedicalRecords.removeIf(record ->
                    record.getFirstName().equalsIgnoreCase(firstName) &&
                            record.getLastName().equalsIgnoreCase(lastName)
            );

            if (isDeleted) {
                medicalRecords.removeIf(record ->
                        record.getFirstName().equalsIgnoreCase(firstName) &&
                                record.getLastName().equalsIgnoreCase(lastName)
                );
                LOGGER.info("Medical record {} {} deleted successfully.", firstName, lastName);
            } else {
                LOGGER.warn("Medical record {} {} not found for deletion.", firstName, lastName);
            }

            return isDeleted;
        } catch (Exception e) {
            LOGGER.error("Error deleting medical record {} {}: {}", firstName, lastName, e.getMessage(), e);
            return false;
        }
    }

    public MedicalRecord findByFullName(String firstName, String lastName) {
        try {
            List<MedicalRecord> allMedicalRecords = dataBaseInMemoryWrapper.getMedicalRecords();
            if (allMedicalRecords == null) {
                LOGGER.warn("medical records list is null.");
                return null;
            }

            return allMedicalRecords.stream()
                    .filter(record -> record.getFirstName().equalsIgnoreCase(firstName)
                            && record.getLastName().equalsIgnoreCase(lastName))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            LOGGER.error("Error finding medical record {} {}: {}", firstName, lastName, e.getMessage(), e);
            return null;
        }
    }

}
