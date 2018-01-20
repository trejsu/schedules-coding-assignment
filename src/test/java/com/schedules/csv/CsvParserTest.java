package com.schedules.csv;


import com.schedules.exception.CsvMalformedException;
import com.schedules.model.Job;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class CsvParserTest {

    @Test
    public void shouldCreateListOfJobsBasedOnCsvString() {
        final String csvString = getCsv();
        final CsvParser csvParser = new CsvParser();

        List<Job> jobs = csvParser.parseString(csvString);

        assertThat(jobs, hasSize(4));
        assertThat(map(jobs, Job::getId), containsInAnyOrder(0, 1, 2, 3));
        assertThat(map(jobs, Job::getPeriod), containsInAnyOrder(10, 5, 10, 5));
        assertThat(map(jobs, Job::getDuration), containsInAnyOrder(4, 2, 2, 1));
        assertThat(map(jobs, Job::getCost), containsInAnyOrder(2, 3, 2, 4));
    }

    @Test(expected = CsvMalformedException.class)
    public void shouldThrowCsvMalformedExceptionWhenCsvStringIsMalformed() {
        final String csvString = getMalformedCsv();
        final CsvParser csvParser = new CsvParser();

        csvParser.parseString(csvString);
    }

    private String getMalformedCsv() {
        return "0, ten, 4, 2\n" +
                "1, 5, 2, 3\n" +
                "2, 10, 2, 2\n" +
                "3, 5, 1, 4";
    }

    private String getCsv() {
        return "0, 10, 4, 2\n" +
                "1, 5, 2, 3\n" +
                "2, 10, 2, 2\n" +
                "3, 5, 1, 4";
    }

    private List<Integer> map(List<Job> jobs, Function<Job, Integer> mapper) {
        return jobs.stream().map(mapper).collect(toList());
    }
}
