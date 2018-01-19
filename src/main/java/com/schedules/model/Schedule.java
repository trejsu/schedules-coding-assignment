package com.schedules.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Value
public class Schedule {

    ArrayList<List<JobTimeFrame>> scheduleTable;

    @JsonIgnore
    Map<Integer, Job> jobs;


    public int getCost(int time) {
        return isTimeValid(time) ? scheduleTable.get(time).stream()
                .map(getJobFromJobTimeFrame())
                .mapToInt(Job::getCost)
                .sum()
                : 0;
    }

    public List<Job> getJobs(int time) {
        return isTimeValid(time) ? scheduleTable.get(time).stream()
                .map(getJobFromJobTimeFrame())
                .collect(toList())
                : emptyList();
    }

    public Optional<ScheduledJob> getNextJob(int time) {
        JobTimeFrame jobTimeFrame = null;
        while(jobTimeFrame == null && time < scheduleTable.size() - 1) {
            jobTimeFrame = scheduleTable.get(++time).stream()
                    .filter(JobTimeFrame::isStart)
                    .findFirst()
                    .orElse(null);
        }
        final int nextJobTime = time;
        return ofNullable(jobTimeFrame)
                .map(j -> ScheduledJob.fromJob(jobs.get(j.getJobId()), nextJobTime));
    }

    @JsonIgnore
    public int getMaximumCost() {
        return scheduleTable
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

    private boolean isTimeValid(int time) {
        return time >= 0 && time < scheduleTable.size();
    }

}
