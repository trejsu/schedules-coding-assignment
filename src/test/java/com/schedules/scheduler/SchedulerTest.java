package com.schedules.scheduler;


import com.schedules.model.Job;
import com.schedules.model.JobTimeFrame;
import com.schedules.model.Schedule;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class SchedulerTest {

    @Test
    public void shouldCreateScheduleWithProperlyScheduledJobs() {
        final Scheduler scheduler = new Scheduler();
        final List<Job> inputJobs = getFirstExampleJobs();

        final Schedule schedule = scheduler.createSchedule(inputJobs);

        final ArrayList<List<JobTimeFrame>> scheduleTable = schedule.getScheduleTable();
        assertThat(mapToIds(scheduleTable, 0), containsInAnyOrder(3));
        assertThat(mapToIds(scheduleTable, 1), containsInAnyOrder(0, 1));
        assertThat(mapToIds(scheduleTable, 2), containsInAnyOrder(0, 1));
        assertThat(mapToIds(scheduleTable, 3), containsInAnyOrder(0));
        assertThat(mapToIds(scheduleTable, 4), containsInAnyOrder(0));
        assertThat(mapToIds(scheduleTable, 5), containsInAnyOrder(3));
        assertThat(mapToIds(scheduleTable, 6), containsInAnyOrder(1));
        assertThat(mapToIds(scheduleTable, 7), containsInAnyOrder(1));
        assertThat(mapToIds(scheduleTable, 8), containsInAnyOrder(2));
        assertThat(mapToIds(scheduleTable, 9), containsInAnyOrder(2));
    }

    @Test
    public void shouldProperlyFindMaxPeriod() {
        final Scheduler scheduler = new Scheduler();
        final List<Job> jobs = getSecondExampleJobs();
        final int actualMaxPeriod = 10;

        final int maxPeriod = scheduler.findMaxPeriod(jobs);

        assertThat(maxPeriod, equalTo(actualMaxPeriod));
    }

    @Test
    public void shouldReturnZeroMaxPeriodGivenEmptyInputJobs() {
        final Scheduler scheduler = new Scheduler();
        final List<Job> jobs = emptyList();
        final int actualMaxPeriod = 0;

        final int maxPeriod = scheduler.findMaxPeriod(jobs);

        assertThat(maxPeriod, equalTo(actualMaxPeriod));
    }

    @Test
    public void shouldCreateValidEmptyScheduleTable() {
        final Scheduler scheduler = new Scheduler();
        final int size = 10;

        final ArrayList<List<JobTimeFrame>> scheduleTable = scheduler.fillWithEmptyLists(size);

        assertThat(scheduleTable, hasSize(size));
        scheduleTable.forEach(Assert::assertNotNull);
    }

    @Test
    public void shouldLeaveEmptyScheduleTableGivenInvalidSize() {
        final Scheduler scheduler = new Scheduler();
        final int size = -999999999;

        final ArrayList<List<JobTimeFrame>> scheduleTable = scheduler.fillWithEmptyLists(size);

        assertThat(scheduleTable, hasSize(0));
    }

    @Test
    public void shouldReturnIndexOfSubsetWithMinimalSum() {
        final Scheduler scheduler = new Scheduler();
        int [] array = {1, 5, 4, 7, 8, 2, 4, 5, 4, 0, 2, 8, 5, 10};
        int subsetLength = 3;

        int indexWithMinimalSum = scheduler.getIndexOfSubsetWithMinSum(array, subsetLength);

        assertThat(indexWithMinimalSum, equalTo(8));
    }

    @Test
    public void shouldReturnFirstIndexGivenTheSameSums() {
        final Scheduler scheduler = new Scheduler();
        int [] array = new int[10];
        Arrays.fill(array, 5);
        int subsetLength = 3;

        int indexWithMinimalSum = scheduler.getIndexOfSubsetWithMinSum(array, subsetLength);

        assertThat(indexWithMinimalSum, equalTo(0));
    }

    @Test
    public void shouldProperlyAddJobToSchedule() {
        final Scheduler scheduler = new Scheduler();
        final int size = 10;
        final ArrayList<List<JobTimeFrame>> scheduleTable = scheduler.fillWithEmptyLists(size);
        final Job job = getSecondExampleJobs().get(2);
        final int jobStart = 2;
        int [] costs = new int[size];

        scheduler.addJobToSchedule(scheduleTable, job, jobStart, costs);

        assertThat(scheduleTable.get(jobStart), hasSize(1));
        assertThat(scheduleTable.get(jobStart + 1), hasSize(1));
        assertThat(costs[jobStart], equalTo(job.getCost()));
        assertThat(costs[jobStart + 1], equalTo(job.getCost()));
    }

    @Test
    public void shouldProperlyMapJobsToIds() {
        final Scheduler scheduler = new Scheduler();
        final List<Job> jobs = getFirstExampleJobs();

        final Map<Integer, Job> ids = scheduler.mapJobsToIds(jobs);

        assertThat(ids.get(0).getId(), equalTo(0));
        assertThat(ids.get(1).getId(), equalTo(1));
        assertThat(ids.get(2).getId(), equalTo(2));
        assertThat(ids.get(3).getId(), equalTo(3));
    }

    private List<Job> getFirstExampleJobs() {
        return asList(
                    Job.builder().id(0).period(10).duration(4).cost(2).build(),
                    Job.builder().id(1).period(5).duration(2).cost(3).build(),
                    Job.builder().id(2).period(10).duration(2).cost(2).build(),
                    Job.builder().id(3).period(5).duration(1).cost(4).build()
            );
    }

    private List<Job> getSecondExampleJobs() {
        return asList(
                Job.builder().id(0).period(3).duration(1).cost(1).build(),
                Job.builder().id(1).period(10).duration(4).cost(2).build(),
                Job.builder().id(2).period(5).duration(2).cost(3).build(),
                Job.builder().id(3).period(4).duration(2).cost(4).build()
        );
    }

    private List<Integer> mapToIds(ArrayList<List<JobTimeFrame>> scheduleTable, int index) {
        return scheduleTable.get(index).stream().map(JobTimeFrame::getJobId).collect(toList());
    }
}
