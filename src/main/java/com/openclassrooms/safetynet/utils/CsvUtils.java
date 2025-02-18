package com.openclassrooms.safetynet.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.openclassrooms.safetynet.constant.CsvUtilsConstant.*;

public class CsvUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvUtils.class);

    public static <T> void saveToCsv(String filePath, List<T> records) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            String csvContent = records.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n"));
            fileWriter.write(csvContent);
            LOGGER.info(SAVING_SUCCESS + "{}", records.size(), filePath);
        } catch (IOException e) {
            LOGGER.error(SAVING_FAILED, e.getMessage());
        }
    }
}
