package com.openclassrooms.safetynet.repository;

import com.openclassrooms.safetynet.dataBaseInMemory.DataBaseInMemoryWrapper;
import com.openclassrooms.safetynet.model.MedicalRecord;

import com.openclassrooms.safetynet.utils.CsvUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

import static com.openclassrooms.safetynet.constant.MedicalRecordRepositoryConstant.*;


@AllArgsConstructor
@Component
public class MedicalRecordRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordRepository.class);

    private final List<MedicalRecord> medicalRecords = new ArrayList<>();
    private final DataBaseInMemoryWrapper dataBaseInMemoryWrapper;

    public List<MedicalRecord> getMedicalRecords() {

        try {
            if (medicalRecords.isEmpty()) {
                LOGGER.info(MEDICAL_RECORD_List_EMPTY);
                List<MedicalRecord> loadedMedicalRecords = dataBaseInMemoryWrapper.getMedicalRecords();
                if (loadedMedicalRecords != null) {
                    saveMedicalRecordToCsv(loadedMedicalRecords);
                    medicalRecords.addAll(loadedMedicalRecords);
                    LOGGER.info(MEDICAL_RECORD_LOADED, loadedMedicalRecords.size());
                } else {
                    LOGGER.warn(MEDICAL_RECORD_NOT_FOUND);
                }
            }
            return new ArrayList<>(medicalRecords);
        } catch (Exception e) {
            LOGGER.error(MEDICAL_RECORD_ERROR_LOADING, e.getMessage(), e);
        }
        return List.of();
    }

    public List<MedicalRecord> saveAll(List<MedicalRecord> medicalRecordList) {

        if (medicalRecordList == null || medicalRecordList.isEmpty()) {
            LOGGER.warn(MEDICAL_RECORD_ERROR_SAVING);
            return new ArrayList<>(medicalRecords);
        }

        try {
            medicalRecords.addAll(medicalRecordList);

            List<MedicalRecord> wrapperMedicalRecords = dataBaseInMemoryWrapper.getMedicalRecords();

            if (wrapperMedicalRecords != null) {
                wrapperMedicalRecords.addAll(medicalRecordList);
                saveMedicalRecordToCsv(wrapperMedicalRecords);
            } else {
                LOGGER.warn(MEDICAL_RECORD_ERROR_SAVING_CSV);
            }

            LOGGER.info(MEDICAL_RECORD_SAVING_DATA_BASE, medicalRecordList.size());

        } catch (Exception e) {
            LOGGER.error(MEDICAL_RECORD_ERROR_SAVING_DATA_BASE, e.getMessage(), e);
        }
        return new ArrayList<>(medicalRecords);
    }

    public MedicalRecord save(MedicalRecord medicalRecord) {

        if (medicalRecord == null) {
            LOGGER.warn(MEDICAL_RECORD_ERROR);
            return null;
        }

        try {
            medicalRecords.add(medicalRecord);

            List<MedicalRecord> wrapperMedicalRecords = dataBaseInMemoryWrapper.getMedicalRecords();

            if (wrapperMedicalRecords != null) {
                wrapperMedicalRecords.add(medicalRecord);
                saveMedicalRecordToCsv(wrapperMedicalRecords);
            } else {
                LOGGER.warn(MEDICAL_RECORD_ERROR_SAVING_CSV_FILE);
            }

            LOGGER.info(MEDICAL_RECORD_SAVING_DATA_BASE_SUC, medicalRecord);
        } catch (Exception e) {
            LOGGER.error(MEDICAL_RECORD_ERROR_SAVING_DATA_BASE_, e.getMessage(), e);
        }
        return medicalRecord;
    }

    public Optional<MedicalRecord> findByMedicationsAndAllergies(List<String> medications, List<String> allergies) {

        try {
            List<MedicalRecord> allMedicalRecords = dataBaseInMemoryWrapper.getMedicalRecords();
            if (allMedicalRecords == null) {
                LOGGER.warn(MEDICAL_RECORD_NOT_FOUND_BY_ALLERGIES);
                return Optional.empty();
            }

            return allMedicalRecords.stream()
                    .filter(medicalRecord -> medicalRecord.getMedications().equals(medications)
                            && medicalRecord.getAllergies().equals(allergies))
                    .findFirst();
        } catch (Exception e) {
            LOGGER.error(ERROR_MEDICAL_RECORD_NOT_FOUND_BY_ALLERGIES, e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<MedicalRecord> update(MedicalRecord updateMedicalRecord) {

        if (updateMedicalRecord == null) {
            LOGGER.warn(MEDICAL_RECORD_ERROR_UPDATING);
            return Optional.empty();
        }

        try {
            Optional<MedicalRecord> existingRecord = findByMedicationsAndAllergies(
                    updateMedicalRecord.getMedications(),
                    updateMedicalRecord.getAllergies()
            );

            if (existingRecord.isEmpty()) {

                LOGGER.info(MEDICAL_RECORD_NOT_FOUND_UPDATING);
                medicalRecords.add(updateMedicalRecord);

                List<MedicalRecord> wrapperRecords = dataBaseInMemoryWrapper.getMedicalRecords();

                if (wrapperRecords != null) {
                    wrapperRecords.add(updateMedicalRecord);
                    saveMedicalRecordToCsv(wrapperRecords);
                } else {
                    LOGGER.warn(MEDICAL_RECORD_ERROR_UPDATING_NULL_CSV);
                }
                return Optional.of(updateMedicalRecord);
            }

            existingRecord.ifPresent(record -> {
                record.setMedications(updateMedicalRecord.getMedications());
                record.setAllergies(updateMedicalRecord.getAllergies());
            });

            LOGGER.info(MEDICAL_RECORD_ERROR_UPDATING_SUCCESS);
            return existingRecord;

        } catch (Exception e) {
            LOGGER.error(MEDICAL_RECORD_ERROR_SAVING_UPDATING_SUCCESS, e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Boolean deleteByFullName(String firstName, String lastName) {

        try {
            List<MedicalRecord> allMedicalRecords = dataBaseInMemoryWrapper.getMedicalRecords();
            if (allMedicalRecords == null) {
                LOGGER.warn(MEDICAL_RECORD_ERROR_DELETING);
                return false;
            }

            saveMedicalRecordToCsv(allMedicalRecords);

            var isDeleted = allMedicalRecords.removeIf(record ->
                    record.getFirstName().equalsIgnoreCase(firstName) &&
                            record.getLastName().equalsIgnoreCase(lastName)
            );

            if (isDeleted) {
                medicalRecords.removeIf(record ->
                        record.getFirstName().equalsIgnoreCase(firstName) &&
                                record.getLastName().equalsIgnoreCase(lastName)
                );
                LOGGER.info(MEDICAL_RECORD_DELETING_SUCCESS, firstName, lastName);
            } else {
                LOGGER.warn(MEDICAL_RECORD_ERROR_NOT_FOUND, firstName, lastName);
            }

            return isDeleted;
        } catch (Exception e) {
            LOGGER.error(MEDICAL_RECORD_ERROR_DELETING_BY_FULL_NAME, firstName, lastName, e.getMessage(), e);
            return false;
        }
    }

    public MedicalRecord findByFullName(String firstName, String lastName) {

        try {
            List<MedicalRecord> allMedicalRecords = dataBaseInMemoryWrapper.getMedicalRecords();
            if (allMedicalRecords == null) {
                LOGGER.warn(FULL_NAME_NOT_FOUND);
                return null;
            }

            return allMedicalRecords.stream()
                    .filter(record -> record.getFirstName().equalsIgnoreCase(firstName)
                            && record.getLastName().equalsIgnoreCase(lastName))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            LOGGER.error(ERROR_SEARCHING_FULL_NAME, firstName, lastName, e.getMessage(), e);
            return null;
        }
    }

    private void saveMedicalRecordToCsv(List<MedicalRecord> medicalRecordToSave) {

        CsvUtils.saveToCsv(MEDICAL_RECORD_CSV_CONFIG_FILE, medicalRecordToSave);
    }

}
