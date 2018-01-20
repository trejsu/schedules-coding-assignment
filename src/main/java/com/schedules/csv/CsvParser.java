package com.schedules.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.schedules.model.Job;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CsvParser {

    @SneakyThrows
    public List<Job> parseString(String csv) {
        MappingIterator<Job> mappingIterator = new CsvMapper()
                .readerWithTypedSchemaFor(Job.class)
                .readValues(csv);
        return mappingIterator.readAll();
    }
}
