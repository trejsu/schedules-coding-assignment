package com.schedules.scheduler;


import com.schedules.model.Job;
import com.schedules.model.JobTimeFrame;
import com.schedules.model.Schedule;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Service
public class Scheduler {

    public Schedule createSchedule(List<Job> inputJobs) {
        final int scheduleDuration = findMaxPeriod(inputJobs);
        ArrayList<List<JobTimeFrame>> schedule = fillWithEmptyLists(scheduleDuration);
        inputJobs.forEach(scheduleJob(scheduleDuration, schedule));
        return new Schedule(schedule, mapJobsToIds(inputJobs));
    }

    private Consumer<Job> scheduleJob(int scheduleDuration, ArrayList<List<JobTimeFrame>> schedule) {
        return job -> {
            final int period = job.getPeriod();
            int occurrences = scheduleDuration / period;
            for (int jobStart = 0, occurrence = 0; occurrence < occurrences; jobStart += period, occurrence++) {
                addJobToSchedule(schedule, job, jobStart);
            }
        };
    }

    private void addJobToSchedule(ArrayList<List<JobTimeFrame>> schedule, Job job, int jobStart) {
        for (int jobFrame = 0; jobFrame < job.getDuration(); jobFrame++) {
            boolean start = (jobFrame == 0);
            final JobTimeFrame jobTimeFrame = new JobTimeFrame(job.getId(), start);
            schedule.get(jobStart + jobFrame).add(jobTimeFrame);
        }
    }

    private int findMaxPeriod(List<Job> inputJobs) {
        return inputJobs.stream().mapToInt(Job::getPeriod).max().orElse(0);
    }

    private Map<Integer, Job> mapJobsToIds(List<Job> inputJobs) {
        return inputJobs.stream().collect(Collectors.toMap(Job::getId, identity()));
    }

    private ArrayList<List<JobTimeFrame>> fillWithEmptyLists(int size) {
        ArrayList<List<JobTimeFrame>> result = new ArrayList<>();
        for (int i=0; i<size; i++) {
            result.add(new ArrayList<>());
        }
        return result;
    }

}
