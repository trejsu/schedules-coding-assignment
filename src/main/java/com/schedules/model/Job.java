package com.schedules.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonPropertyOrder({ "id", "period", "duration", "cost" })
public class Job {

    int id;
    int period;
    int duration;
    int cost;

    public Job(@JsonProperty("id") int id,
               @JsonProperty("period") int period,
               @JsonProperty("duration") int duration,
               @JsonProperty("cost") int cost) {
        this.id = id;
        this.period = period;
        this.duration = duration;
        this.cost = cost;
    }

}
