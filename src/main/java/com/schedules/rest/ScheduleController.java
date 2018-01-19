package com.schedules.rest;

import com.schedules.csv.CsvLoader;
import com.schedules.exception.InputNotFoundException;
import com.schedules.exception.ScheduleNotFoundException;
import com.schedules.model.Job;
import com.schedules.model.JobTimeFrame;
import com.schedules.model.Schedule;
import com.schedules.model.ScheduledJob;
import com.schedules.scheduler.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping("/schedule")
@NoArgsConstructor
public class ScheduleController {

    private static final String CSV = "text/csv;charset=UTF-8";

    private Scheduler scheduler;

    private CsvLoader csvLoader;

    private ScheduleRegistry scheduleRegistry;

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
            return ResponseEntity.ok(ScheduleOutputDto.fromSchedule(schedule));
        } catch (ScheduleNotFoundException e) {
            return e.getResponseEntity();
        }
    }

    @GetMapping(value = "/{schedule_id}/cost", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCost(@PathVariable("schedule_id") Integer scheduleId, @RequestParam(name = "time") Integer time) {
        try {
            final Schedule schedule = scheduleRegistry.get(scheduleId);
            final int cost = schedule.getCost(time);
            return ResponseEntity.ok(cost);
        } catch (ScheduleNotFoundException e) {
            return e.getResponseEntity();
        }
    }

    @GetMapping(value = "/{schedule_id}/list", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getJobs(@PathVariable("schedule_id") Integer scheduleId, @RequestParam(name = "time") Integer time) {
        try {
            final Schedule schedule = scheduleRegistry.get(scheduleId);
            final List<Job> jobs = schedule.getJobs(time);
            return ResponseEntity.ok(jobs);
        } catch (ScheduleNotFoundException e) {
            return e.getResponseEntity();
        }
    }

    @GetMapping(value = "/{schedule_id}/next", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getNextJob(@PathVariable("schedule_id") Integer scheduleId, @RequestParam(name = "time") Integer time) {
        try {
            final Schedule schedule = scheduleRegistry.get(scheduleId);
            final ScheduledJob nextJob = schedule.getNextJob(time).orElse(null);
            return ResponseEntity.ok(nextJob);
        } catch (ScheduleNotFoundException e) {
            return e.getResponseEntity();
        }
    }

    @GetMapping(value = "/{schedule_id}/max", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMaxCost(@PathVariable("schedule_id") Integer scheduleId) {
        try {
            final Schedule schedule = scheduleRegistry.get(scheduleId);
            final int maximumCost = schedule.getMaximumCost();
            return ResponseEntity.ok(maximumCost);
        } catch (ScheduleNotFoundException e) {
            return e.getResponseEntity();
        }
    }

    @Data
    @AllArgsConstructor
    static class ScheduleOutputDto {

        private ArrayList<List<Integer>> scheduleTable;

        static ScheduleOutputDto fromSchedule(Schedule schedule) {
            ArrayList<List<Integer>> scheduleTable = new ArrayList<>();
            for(List<JobTimeFrame> jobs : schedule.getScheduleTable()) {
                final List<Integer> ids = jobs.stream().map(JobTimeFrame::getJobId).collect(Collectors.toList());
                scheduleTable.add(ids);
            }
            return new ScheduleOutputDto(scheduleTable);
        }
    }
}
