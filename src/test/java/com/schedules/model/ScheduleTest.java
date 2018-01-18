package com.schedules.model;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import static org.hamcrest.Matchers.isIn;

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
    public void shouldReturnNextJobToExecuteAfterGivenTime() {
        final Schedule schedule = getSchedule();
        final int time = 3;

        final ScheduledJob nextJob = schedule.getNextJob(time);

        assertThat(nextJob.getId(), is(3));
        assertThat(nextJob.getStart(), is(5));
    }

    @Test
    public void shouldReturnOneOfNextJobsToExecuteAfterGivenTime() {
        final Schedule schedule = getSchedule();
        final int time = 0;

        final ScheduledJob nextJob = schedule.getNextJob(time);

        assertThat(nextJob.getId(), isIn(asList(0, 1)));
        assertThat(nextJob.getStart(), is(1));
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

        ArrayList<List<Integer>> scheduleTimeline = new ArrayList<>();
        scheduleTimeline.add(asList(0, 3));
        scheduleTimeline.add(asList(0, 1));
        scheduleTimeline.add(asList(0, 1));
        scheduleTimeline.add(singletonList(0));
        scheduleTimeline.add(emptyList());
        scheduleTimeline.add(singletonList(3));
        scheduleTimeline.add(singletonList(1));
        scheduleTimeline.add(singletonList(1));
        scheduleTimeline.add(singletonList(2));
        scheduleTimeline.add(singletonList(2));

        final Map<Integer, Job> jobsWithIds = jobs.stream().collect(Collectors.toMap(Job::getId, identity()));

        return new Schedule(scheduleTimeline, jobsWithIds);
    }
}

