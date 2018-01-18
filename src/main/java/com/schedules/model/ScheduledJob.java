package com.schedules.model;

import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class ScheduledJob {

    int id;
    int period;
    int duration;
    int cost;
    int start;

    public static ScheduledJob fromJob(Job job, int start) {
        return builder()
                .id(job.getId())
                .period(job.getPeriod())
                .duration(job.getDuration())
                .cost(job.getCost())
                .start(start)
                .build();
    }
}
