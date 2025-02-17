package com.openclassrooms.safetynet.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.dto.PersonDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CsvUtilsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvUtilsTest.class);

    private String TEST_FILE_PATH;
    private String TEST_INVALID_FILE_PATH;
    private List<String> mockRecords;

    @BeforeEach
    void setUp() {

        //  Objects PersonDTO
        PersonDTO personDTO1 = new PersonDTO("John", "Doe", "johndoe@gmail.com", "123 Main St", "Springfield",
                "75016", "0144445151");
        PersonDTO personDTO2 = new PersonDTO("Jane", "Doe", "janedoe@gmail.com", "123 Main St",
                "Springfield", "75017", "0144445152");

        // Convert to CSV format
        mockRecords = List.of(
                String.join(",", personDTO1.getFirstName(), personDTO1.getLastName(), personDTO1.getEmail(),
                        personDTO1.getAddress(), personDTO1.getCity(), personDTO1.getZip(), personDTO1.getPhone()),
                String.join(",", personDTO2.getFirstName(), personDTO2.getLastName(), personDTO2.getEmail(),
                        personDTO2.getAddress(), personDTO2.getCity(), personDTO2.getZip(), personDTO2.getPhone()));

        TEST_FILE_PATH = "src/test/resources/test_output.csv";
        TEST_INVALID_FILE_PATH = "/invalid/path/test_invalid.csv";
    }


    @Test
    void shouldReturnSaveToCsvSuccessfully() throws IOException {

        LOGGER.info("Start method : shouldReturnSaveToCsvSuccessfully");

        CsvUtils.saveToCsv(TEST_FILE_PATH, mockRecords);

        // Reading the file to check the contents
        String fileContent = Files.readString(Path.of(TEST_FILE_PATH)).trim();

        // Expected content
        String expectedContent = String.join("\n", mockRecords);

        assertEquals(expectedContent, fileContent);
        LOGGER.info("End method : shouldReturnSaveToCsvSuccessfully");
    }


    @Test
    void shouldReturnSaveToCsvIOException() {
        LOGGER.info("Start method : shouldReturnSaveToCsvIOException");
        assertDoesNotThrow(() -> CsvUtils.saveToCsv(TEST_INVALID_FILE_PATH, mockRecords));
        LOGGER.info("End method : shouldReturnSaveToCsvIOException");
    }
}
