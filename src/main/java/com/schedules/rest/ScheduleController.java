package com.schedules.rest;

import com.schedules.csv.CsvLoader;
import com.schedules.exception.InputNotFoundException;
import com.schedules.exception.ScheduleNotFoundException;
import com.schedules.model.Job;
import com.schedules.model.Schedule;
import com.schedules.scheduler.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private static final String CSV = "text/csv;charset=UTF-8";

    private final Scheduler scheduler;

    private final CsvLoader csvLoader;

    private final ScheduleRegistry scheduleRegistry;

    @Autowired
    public ScheduleController(Scheduler scheduler, CsvLoader csvLoader, ScheduleRegistry scheduleRegistry) {
        this.scheduler = scheduler;
        this.csvLoader = csvLoader;
        this.scheduleRegistry = scheduleRegistry;
    }

    @PostMapping(consumes = TEXT_PLAIN_VALUE)
    public ResponseEntity<?> createSchedule(@RequestBody String inputPath) {
        try {
            final List<Job> inputJobs = csvLoader.loadAsJobs(inputPath);
            final Schedule schedule = scheduler.createSchedule(inputJobs);
            final Integer id = scheduleRegistry.add(schedule);
            final String location = "/schedules/schedule/" + id;
            return ResponseEntity.created(URI.create(location)).build();
        } catch (InputNotFoundException e) {
            return e.getResponseEntity();
        }
    }

    @GetMapping(value = "/{schedule_id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSchedule(@PathVariable("schedule_id") Integer scheduleId) {
        try {
            final Schedule schedule = scheduleRegistry.get(scheduleId);
            return ResponseEntity.ok(schedule);
        } catch (ScheduleNotFoundException e) {
            return e.getResponseEntity();
        }
    }
}
