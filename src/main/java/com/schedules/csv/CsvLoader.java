package com.schedules.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.schedules.exception.InputNotFoundException;
import com.schedules.model.Job;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class CsvLoader {

    @SneakyThrows
    public List<Job> loadAsJobs(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        String path = "data/" + fileName;
        File csvFile = ofNullable(classLoader.getResource(path))
                .map(p -> new File(p.getFile()))
                .orElseThrow(() -> new InputNotFoundException("{\"errorMessage\":\"Requested input file does not exist under resources/data/ path\"}"));
        MappingIterator<Job> mappingIterator = new CsvMapper()
                .readerWithTypedSchemaFor(Job.class)
                .readValues(csvFile);
        return mappingIterator.readAll();
    }
}
