package com.schedules.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.schedules.exception.JobValidationException;
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

    public void validate() {
        durationShorterOrEqualPeriod();
        periodDurationAndCostPositive();
    }

    private void durationShorterOrEqualPeriod() {
        if (duration > period) {
            throw new JobValidationException("Duration of the job cannot be greater than its period.");
        }
    }

    private void periodDurationAndCostPositive() {
        if (period <= 0 || duration <= 0 || cost <= 0) {
            throw new JobValidationException("Period, duration and cost has to be greater than 0.");
        }
    }

}
