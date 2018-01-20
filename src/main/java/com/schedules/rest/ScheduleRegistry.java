package com.schedules.rest;

import com.schedules.exception.ScheduleNotFoundException;
import com.schedules.model.Schedule;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Service
public class ScheduleRegistry {

    private static final Map<Integer, Schedule> schedules = new HashMap<>();
    private static Integer nextKey = 0;


    public Integer add(Schedule schedule) {
        schedules.put(nextKey, schedule);
        return nextKey++;
    }

    public Schedule get(Integer scheduleId) {
        final Schedule schedule = schedules.get(scheduleId);
        return ofNullable(schedule).orElseThrow(() -> new ScheduleNotFoundException("Requested schedule does not exist"));
    }
}
