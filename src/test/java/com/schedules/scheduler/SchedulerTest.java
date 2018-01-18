package com.schedules.scheduler;


import com.schedules.model.Job;
import com.schedules.model.Schedule;
import com.schedules.model.ScheduledJob;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class SchedulerTest {

    @Test
    public void shouldCreateScheduleWithAllJobsStartingAtTimeZero() {
        final Scheduler scheduler = new Scheduler();
        final List<Job> inputJobs = getJobs();
        final int time = 0;

        final Schedule schedule = scheduler.createSchedule(inputJobs);

        assertThat(schedule.getCost(time), is(11));
        assertThat(schedule.getJobs(time), hasSize(4));
        assertThat(schedule.getMaximumCost(), is(11));
    }

    @Test
    public void shouldCreateScheduleWithProperlyScheduledJobs() {
        final Scheduler scheduler = new Scheduler();
        final List<Job> inputJobs = getJobs();
        final int time = 1;

        final Schedule schedule = scheduler.createSchedule(inputJobs);

        assertThat(schedule.getSchedule().get(time), hasSize(3));
    }

    private List<Job> getJobs() {
        return asList(
                    Job.builder().id(0).period(10).duration(4).cost(2).build(),
                    Job.builder().id(1).period(5).duration(2).cost(3).build(),
                    Job.builder().id(2).period(10).duration(2).cost(2).build(),
                    Job.builder().id(3).period(5).duration(1).cost(4).build()
            );
    }
}
