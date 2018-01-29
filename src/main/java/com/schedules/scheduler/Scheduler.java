package com.schedules.scheduler;


import com.schedules.model.Job;
import com.schedules.model.JobTimeFrame;
import com.schedules.model.Schedule;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Arrays.copyOfRange;
import static java.util.function.Function.identity;

@Service
public class Scheduler {

    public Schedule createSchedule(List<Job> inputJobs) {
        final int scheduleDuration = findMaxPeriod(inputJobs);
        ArrayList<List<JobTimeFrame>> schedule = fillWithEmptyLists(scheduleDuration);
        inputJobs.sort(Comparator.comparingInt(Job::getCost).reversed());
        int [] costs = new int[scheduleDuration];
        inputJobs.forEach(scheduleOptimally(scheduleDuration, schedule, costs));
        return new Schedule(schedule, mapJobsToIds(inputJobs), costs);
    }

    private Consumer<Job> scheduleOptimally(int scheduleDuration, ArrayList<List<JobTimeFrame>> schedule, int [] costs) {
        return job -> {
            final int period = job.getPeriod();
            int occurrences = scheduleDuration / period;
            for (int occurrence = 0; occurrence < occurrences; occurrence++) {
                int jobStart = findIndexWithMinimalCost(occurrence, period, job.getDuration(), costs);
                addJobToSchedule(schedule, job, jobStart, costs);
            }
        };
    }

    private int findIndexWithMinimalCost(int occurrence, int period, int duration, int [] costs) {
        final int offset = occurrence * period;
        final int[] array = copyOfRange(costs, offset, offset + period);
        return getIndexOfSubsetWithMinSum(array, duration) + offset;
    }

    int getIndexOfSubsetWithMinSum(int [] array, int subsetLength) {
        int resultIndex = 0;

        int minSum = 0;
        for (int i = 0; i < subsetLength; i++) {
            minSum += array[i];
        }

        int currentSum = minSum;

        for (int i = subsetLength; i < array.length; i++) {
            currentSum += array[i] - array[i - subsetLength];

            if (currentSum < minSum) {
                minSum = currentSum;
                resultIndex = i - subsetLength + 1;
            }
        }
        return resultIndex;
    }

    void addJobToSchedule(ArrayList<List<JobTimeFrame>> schedule, Job job, int jobStart, int [] costs) {
        final int duration = job.getDuration();
        final int id = job.getId();
        final int cost = job.getCost();
        for (int jobFrame = 0; jobFrame < duration; jobFrame++) {
            boolean start = (jobFrame == 0);
            final JobTimeFrame jobTimeFrame = new JobTimeFrame(id, start);
            schedule.get(jobStart + jobFrame).add(jobTimeFrame);
            costs[jobStart + jobFrame] += cost;
        }
    }

    int findMaxPeriod(List<Job> inputJobs) {
        return inputJobs.stream().mapToInt(Job::getPeriod).max().orElse(0);
    }

    Map<Integer, Job> mapJobsToIds(List<Job> inputJobs) {
        return inputJobs.stream().collect(Collectors.toMap(Job::getId, identity()));
    }

    ArrayList<List<JobTimeFrame>> fillWithEmptyLists(int size) {
        ArrayList<List<JobTimeFrame>> result = new ArrayList<>();
        for (int i=0; i<size; i++) {
            result.add(new ArrayList<>());
        }
        return result;
    }

}
