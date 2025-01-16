package com.openclassrooms.safetynet.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.openclassrooms.safetynet.constant.csvUtil.CsvUtilsConstant.*;

public class CsvUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvUtils.class);

    public static <T> List<T> loadFromCsv(String filePath, Function<String,T> mapper) {
        List<T> records = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {

            records = bufferedReader.lines()
                    .map(mapper)
                    .toList();

            LOGGER.info(LOADING_SUCCESS, records.size(), filePath);

        } catch (IOException ioException) {
            LOGGER.error(LOADING_FAILED, ioException.getMessage(), ioException);
        }
        return records;
    }

    public static <T> void saveToCsv(String filePath, List<T> records) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            String csvContent = records.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n"));
            fileWriter.write(csvContent);
            LOGGER.info(SAVING_SUCCESS, records.size(), filePath);
        } catch (IOException ioException) {
            LOGGER.error(SAVING_FAILED, ioException.getMessage(), ioException);
        }
    }
}
