package com.schedules.model;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Value
public class Schedule {

    ArrayList<List<Integer>> schedule;
    Map<Integer, Job> jobs;

    public int getCost(int time) {
        return schedule.get(time)
                .stream()
                .map(jobs::get)
                .mapToInt(Job::getCost)
                .sum();
    }

    public List<Job> getJobs(int time) {
        return schedule.get(time)
                .stream()
                .map(jobs::get)
                .collect(toList());
    }

    public ScheduledJob getNextJob(int time) {
        int nextNonEmptyTime = time + 1;
        while(schedule.get(nextNonEmptyTime).isEmpty()) {
            nextNonEmptyTime++;
        }
        final Job nextJobToRun = jobs.get(schedule.get(nextNonEmptyTime).get(0));
        return ScheduledJob.fromJob(nextJobToRun, nextNonEmptyTime);
    }

    public int getMaximumCost() {
        return schedule
                .stream()
                .mapToInt(id -> id
                        .stream()
                        .map(jobs::get)
                        .mapToInt(Job::getCost)
                        .sum())
                .max()
                .orElse(0);
    }

}
