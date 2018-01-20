package com.schedules.rest;


import com.schedules.csv.CsvParser;
import com.schedules.exception.CsvMalformedException;
import com.schedules.exception.ScheduleNotFoundException;
import com.schedules.model.Job;
import com.schedules.model.JobTimeFrame;
import com.schedules.model.Schedule;
import com.schedules.scheduler.Scheduler;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.schedules.model.JobTimeFrame.next;
import static com.schedules.model.JobTimeFrame.start;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RunWith(SpringRunner.class)
@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {

    private final static String SCHEDULE_URL = "/schedule";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Scheduler scheduler;

    @MockBean
    private CsvParser csvParser;

    @MockBean
    private ScheduleRegistry scheduleRegistry;

    @Test
    @SneakyThrows
    public void shouldReturnLocationOfCreatedSchedule() {
        final String csv = getCsv();
        final List<Job> jobs = getJobs();
        final Schedule schedule = getSchedule(jobs);
        final Integer id = 0;
        when(csvParser.parseString(csv)).thenReturn(jobs);
        when(scheduler.createSchedule(jobs)).thenReturn(schedule);
        when(scheduleRegistry.add(schedule)).thenReturn(id);

        final ResultActions result = this.mockMvc.perform(
                post(SCHEDULE_URL)
                        .contentType(TEXT_PLAIN_VALUE)
                        .content(csv));

        result
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", equalTo("/schedules" + SCHEDULE_URL + "/" + id)));
    }

    @Test
    @SneakyThrows
    public void shouldReturnBadRequestWhenCsvParserThrowsCsvMalformedException() {
        final String csv = getMalformedCsv();
        final String errorMessage = "error";
        when(csvParser.parseString(csv)).thenThrow(new CsvMalformedException(errorMessage));

        final ResultActions result = this.mockMvc.perform(
                post(SCHEDULE_URL)
                        .contentType(TEXT_PLAIN_VALUE)
                        .content(csv));

        result
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"errorMessage\":\"" + errorMessage + "\"}"));
    }

    @Test
    @SneakyThrows
    public void shouldReturnRequestedSchedule() {
        final Schedule schedule = getSchedule(getJobs());
        final Integer id = 0;
        when(scheduleRegistry.get(id)).thenReturn(schedule);

        final ResultActions result = this.mockMvc.perform(
                get(SCHEDULE_URL + "/" + id)
                        .accept(APPLICATION_JSON));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleTable", hasSize(10)))
                .andExpect(jsonPath("$.scheduleTable[0]", hasSize(4)))
                .andExpect(jsonPath("$.scheduleTable[1]", hasSize(3)))
                .andExpect(jsonPath("$.scheduleTable[2]", hasSize(1)))
                .andExpect(jsonPath("$.scheduleTable[3]", hasSize(1)))
                .andExpect(jsonPath("$.scheduleTable[4]", hasSize(0)))
                .andExpect(jsonPath("$.scheduleTable[5]", hasSize(2)))
                .andExpect(jsonPath("$.scheduleTable[6]", hasSize(1)))
                .andExpect(jsonPath("$.scheduleTable[7]", hasSize(0)))
                .andExpect(jsonPath("$.scheduleTable[8]", hasSize(0)))
                .andExpect(jsonPath("$.scheduleTable[9]", hasSize(0)));
    }

    @Test
    @SneakyThrows
    public void shouldReturnNotFoundWhenScheduleDoesNotExists() {
        final Integer id = 0;
        final String errorMessage = "error";
        when(scheduleRegistry.get(id))
                .thenThrow(new ScheduleNotFoundException(errorMessage));

        final ResultActions result = this.mockMvc.perform(
                get(SCHEDULE_URL + "/" + id)
                        .accept(APPLICATION_JSON));

        result
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"errorMessage\":\"" + errorMessage + "\"}"));
    }

    @Test
    @SneakyThrows
    public void shouldReturnCostForRequestedScheduleAndTime() {
        final Schedule schedule = getSchedule(getJobs());
        final Integer id = 0;
        final String time = "0";
        when(scheduleRegistry.get(id)).thenReturn(schedule);

        final ResultActions result = this.mockMvc.perform(
                get(SCHEDULE_URL + "/" + id + "/cost")
                        .param("time", time)
                        .accept(APPLICATION_JSON));

        result
                .andExpect(status().isOk())
                .andExpect(content().json("11"));
    }

    @Test
    @SneakyThrows
    public void shouldReturnZeroForOutOfBoundsTime() {
        final Schedule schedule = getSchedule(getJobs());
        final Integer id = 0;
        final String time = "-99999999";
        when(scheduleRegistry.get(id)).thenReturn(schedule);

        final ResultActions result = this.mockMvc.perform(
                get(SCHEDULE_URL + "/" + id + "/cost")
                        .param("time", time)
                        .accept(APPLICATION_JSON));

        result
                .andExpect(status().isOk())
                .andExpect(content().json("0"));
    }

    @Test
    @SneakyThrows
    public void shouldReturnListOfJobsForRequestedScheduleAndTime() {
        final Schedule schedule = getSchedule(getJobs());
        final Integer id = 0;
        final String time = "0";
        when(scheduleRegistry.get(id)).thenReturn(schedule);

        final ResultActions result = this.mockMvc.perform(
                get(SCHEDULE_URL + "/" + id + "/list")
                        .param("time", time)
                        .accept(APPLICATION_JSON));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    @SneakyThrows
    public void shouldReturnEmptyResponseWhenNoJobsAreRunning() {
        final Schedule schedule = getSchedule(getJobs());
        final Integer id = 0;
        final String time = "4";
        when(scheduleRegistry.get(id)).thenReturn(schedule);

        final ResultActions result = this.mockMvc.perform(
                get(SCHEDULE_URL + "/" + id + "/list")
                        .param("time", time)
                        .accept(APPLICATION_JSON));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private List<Job> getJobs() {
        return asList(
                Job.builder().id(0).period(10).duration(4).cost(2).build(),
                Job.builder().id(1).period(5).duration(2).cost(3).build(),
                Job.builder().id(2).period(10).duration(2).cost(2).build(),
                Job.builder().id(3).period(5).duration(1).cost(4).build()
        );
    }

    private Schedule getSchedule(List<Job> jobs) {
        ArrayList<List<JobTimeFrame>> scheduleTable = new ArrayList<>();
        scheduleTable.add(asList(start(0), start(1), start(2), start(3)));
        scheduleTable.add(asList(next(0), next(1), next(2)));
        scheduleTable.add(singletonList(next(0)));
        scheduleTable.add(singletonList(next(0)));
        scheduleTable.add(emptyList());
        scheduleTable.add(asList(start(1), start(3)));
        scheduleTable.add(singletonList(next(1)));
        scheduleTable.add(emptyList());
        scheduleTable.add(emptyList());
        scheduleTable.add(emptyList());

        final Map<Integer, Job> jobsWithIds = jobs.stream().collect(Collectors.toMap(Job::getId, identity()));

        final int [] costs = {11, 7, 2, 2, 0, 7, 3, 0, 0, 0};

        return new Schedule(scheduleTable, jobsWithIds, costs);
    }

    private String getCsv() {
        return "0, 10, 4, 2\n" +
               "1, 5, 2, 3\n" +
               "2, 10, 2, 2\n" +
               "3, 5, 1, 4";
    }

    private String getMalformedCsv() {
        return "0, ten, 4\n" +
                "1, 5, 2, 3\n" +
                "2, 10, 2, 2\n" +
                "3, 5, 1, 4";
    }
}
