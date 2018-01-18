package com.schedules.model;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Value
public class Schedule {

    ArrayList<List<JobTimeFrame>> schedule;

    Map<Integer, Job> jobs;


    public int getCost(int time) {
        return schedule.get(time).stream()
                .map(getJobFromJobTimeFrame())
                .mapToInt(Job::getCost)
                .sum();
    }

    public List<Job> getJobs(int time) {
        return schedule.get(time).stream()
                .map(getJobFromJobTimeFrame())
                .collect(toList());
    }

    public Optional<ScheduledJob> getNextJob(int time) {
        JobTimeFrame jobTimeFrame = null;
        while(jobTimeFrame == null && time < schedule.size() - 1) {
            jobTimeFrame = schedule.get(++time).stream()
                    .filter(JobTimeFrame::isStart)
                    .findFirst()
                    .orElse(null);
        }
        final int nextJobTime = time;
        return ofNullable(jobTimeFrame)
                .map(j -> ScheduledJob.fromJob(jobs.get(j.getJobId()), nextJobTime));
    }

    public int getMaximumCost() {
        return schedule
                .stream()
                .mapToInt(jobsInTimeFrame -> jobsInTimeFrame
                        .stream()
                        .map(getJobFromJobTimeFrame())
                        .mapToInt(Job::getCost)
                        .sum())
                .max()
                .orElse(0);
    }

    private Function<JobTimeFrame, Job> getJobFromJobTimeFrame() {
        return jobTimeFrame -> jobs.get(jobTimeFrame.getJobId());
    }

}
