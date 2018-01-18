package com.schedules.model;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class ScheduleTest {

    @Test
    public void shouldReturnProperlyCalculatedCostInGivenTime() {
        final Schedule schedule = getSchedule();
        final int time = 2;

        final int cost = schedule.getCost(time);

        assertThat(cost, is(5));
    }

    @Test
    public void shouldReturnZeroCostWhenNoJobsAreRunning() {
        final Schedule schedule = getSchedule();
        final int time = 4;

        final int cost = schedule.getCost(time);

        assertThat(cost, is(0));
    }

    @Test
    public void shouldReturnZeroCostWhenOutOfBoundsTimeGiven() {
        final Schedule schedule = getSchedule();
        final int time = 10;

        final int cost = schedule.getCost(time);

        assertThat(cost, is(0));
    }

    @Test
    public void shouldListAllJobsRunningInGivenTime() {
        final Schedule schedule = getSchedule();
        final int time = 0;

        final List<Job> jobs = schedule.getJobs(time);

        List<Integer> ids = jobs.stream().map(Job::getId).collect(toList());
        assertThat(ids, containsInAnyOrder(0, 3));
    }

    @Test
    public void shouldReturnEmptyListWhenNoJobsAreRunning() {
        final Schedule schedule = getSchedule();
        final int time = 4;

        final List<Job> jobs = schedule.getJobs(time);

        List<Integer> ids = jobs.stream().map(Job::getId).collect(toList());
        assertThat(ids, empty());
    }

    @Test
    public void shouldReturnEmptyListWhenOutOfBoundsTimeGiven() {
        final Schedule schedule = getSchedule();
        final int time = 10;

        final List<Job> jobs = schedule.getJobs(time);

        List<Integer> ids = jobs.stream().map(Job::getId).collect(toList());
        assertThat(ids, empty());
    }

    @Test
    public void shouldReturnNextJobWhenOneIsAvailable() {
        final Schedule schedule = getSchedule();
        final int time = 1;

        final Optional<ScheduledJob> nextJob = schedule.getNextJob(time);

        assertThat(nextJob.isPresent(), is(true));
        assertThat(nextJob.get().getId(), is(3));
        assertThat(nextJob.get().getStart(), is(5));
    }

    @Test
    public void shouldReturnEmptyNextJobWhenNoNextJobIsAvailable() {
        final Schedule schedule = getSchedule();
        final int time = 8;

        final Optional<ScheduledJob> nextJob = schedule.getNextJob(time);

        assertThat(nextJob.isPresent(), is(false));
    }

    @Test
    public void shouldReturnProperlyCalculatedMaximumCost() {
        final Schedule schedule = getSchedule();

        final int maximumCost = schedule.getMaximumCost();

        assertThat(maximumCost, is(6));
    }

    private Schedule getSchedule() {
        List<Job> jobs = asList(
                Job.builder().id(0).period(10).duration(4).cost(2).build(),
                Job.builder().id(1).period(5).duration(2).cost(3).build(),
                Job.builder().id(2).period(10).duration(2).cost(2).build(),
                Job.builder().id(3).period(5).duration(1).cost(4).build()
        );

        ArrayList<List<JobTimeFrame>> scheduleTimeline = new ArrayList<>();
        scheduleTimeline.add(asList(new JobTimeFrame(0, true), new JobTimeFrame(3, true)));
        scheduleTimeline.add(asList(new JobTimeFrame(0, false), new JobTimeFrame(1, true)));
        scheduleTimeline.add(asList(new JobTimeFrame(0, false), new JobTimeFrame(1, false)));
        scheduleTimeline.add(singletonList(new JobTimeFrame(0, false)));
        scheduleTimeline.add(emptyList());
        scheduleTimeline.add(singletonList(new JobTimeFrame(3, true)));
        scheduleTimeline.add(singletonList(new JobTimeFrame(1, true)));
        scheduleTimeline.add(singletonList(new JobTimeFrame(1, false)));
        scheduleTimeline.add(singletonList(new JobTimeFrame(2, true)));
        scheduleTimeline.add(singletonList(new JobTimeFrame(2, false)));

        final Map<Integer, Job> jobsWithIds = jobs.stream().collect(Collectors.toMap(Job::getId, identity()));

        return new Schedule(scheduleTimeline, jobsWithIds);
    }
}

