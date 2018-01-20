package com.schedules.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Value
public class Schedule {

    final ArrayList<List<JobTimeFrame>> scheduleTable;

    @JsonIgnore
    final Map<Integer, Job> jobs;

    @JsonIgnore
    final int [] costs;


    public int getCost(int time) {
        return isTimeValid(time) ? costs[time] : 0;
    }

    public List<Job> getJobs(int time) {
        return isTimeValid(time) ? scheduleTable.get(time).stream()
                .map(jobTimeFrame -> jobs.get(jobTimeFrame.getJobId()))
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
                .map(job -> ScheduledJob.fromJob(jobs.get(job.getJobId()), nextJobTime));
    }

    @JsonIgnore
    public int getMaximumCost() {
        return Arrays.stream(costs).max().orElse(0);
    }

    private boolean isTimeValid(int time) {
        return time >= 0 && time < scheduleTable.size();
    }

}
