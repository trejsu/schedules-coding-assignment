package com.schedules.model;

import lombok.Value;

@Value
public class JobTimeFrame {

    int jobId;
    boolean start;

    public static JobTimeFrame start(int id) {
        return new JobTimeFrame(id, true);
    }

    public static JobTimeFrame next(int id) {
        return new JobTimeFrame(id, false);
    }
}
