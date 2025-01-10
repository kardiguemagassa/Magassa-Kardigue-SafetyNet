package com.openclassrooms.safetynet.dataBaseInMemory;

import com.openclassrooms.safetynet.model.FireStation;
import com.openclassrooms.safetynet.model.MedicalRecord;
import com.openclassrooms.safetynet.model.Person;
import lombok.Data;
import java.util.List;

// JSON structure
@Data
public class DataWrapper {
    private List<Person> persons;
    private List<FireStation> firestations;
    private List<MedicalRecord> medicalrecords;
}
