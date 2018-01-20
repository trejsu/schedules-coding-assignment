package com.schedules.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.schedules.exception.CsvMalformedException;
import com.schedules.model.Job;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
public class CsvParser {

    public List<Job> parseString(String csv) {
        try {
            MappingIterator<Job> mappingIterator = new CsvMapper()
                    .readerWithTypedSchemaFor(Job.class)
                    .readValues(csv);
            return mappingIterator.readAll();
        } catch (IOException e) {
            throw new CsvMalformedException(e.getMessage());
        }
    }
}
