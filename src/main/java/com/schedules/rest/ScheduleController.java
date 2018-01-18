package com.schedules.rest;

import com.schedules.csv.CsvLoader;
import com.schedules.exception.InputNotFoundException;
import com.schedules.model.Job;
import com.schedules.model.Schedule;
import com.schedules.scheduler.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private static final String CSV = "text/csv;charset=UTF-8";

    private final Scheduler scheduler;

    private final CsvLoader csvLoader;

    @Autowired
    public ScheduleController(Scheduler scheduler, CsvLoader csvLoader) {
        this.scheduler = scheduler;
        this.csvLoader = csvLoader;
    }

    @PostMapping(consumes = TEXT_PLAIN_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSchedule(@RequestBody String inputPath) {
        try {
            final List<Job> inputJobs = csvLoader.loadAsJobs(inputPath);
            final Schedule schedule = scheduler.createSchedule(inputJobs);
            return ResponseEntity.ok(schedule);
        } catch (InputNotFoundException e) {
            return e.getResponseEntity();
        }

    }
}
