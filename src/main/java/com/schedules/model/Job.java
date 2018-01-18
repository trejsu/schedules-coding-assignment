package com.schedules.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Job {

    int id;
    int period;
    int duration;
    int cost;

}
