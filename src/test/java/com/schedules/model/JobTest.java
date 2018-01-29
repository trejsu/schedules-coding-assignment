package com.schedules.model;


import com.schedules.exception.JobValidationException;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Theories.class)
public class JobTest {

    @Test
    public void validJobShouldPassValidation() {
        final Job job = Job.builder().id(0).period(5).duration(3).cost(2).build();

        final Runnable validate = job::validate;

        assertDoesNotThrow(validate);
    }

    @Test
    public void jobWithDurationGreaterThanPeriodShouldNotPassValidation() {
        final Job job = Job.builder().id(0).period(5).duration(6).cost(2).build();

        final Runnable validate = job::validate;

        assertThrow(validate, JobValidationException.class);
    }

    @DataPoint
    public static Job negativePeriodAndDuration = Job.builder().id(0).period(-1).duration(-5).cost(5).build();
    @DataPoint
    public static Job negativeDuration = Job.builder().id(0).period(5).duration(-5).cost(5).build();
    @DataPoint
    public static Job negativeCost = Job.builder().id(0).period(5).duration(5).cost(-5).build();
    @DataPoint
    public static Job negativeDurationAndCost = Job.builder().id(0).period(5).duration(-5).cost(-5).build();
    @DataPoint
    public static Job allNegative = Job.builder().id(0).period(-5).duration(-5).cost(-5).build();

    @Theory
    public void jobWithNegativeParametersShouldNotPassValidation(Job job) {
        final Runnable validate = job::validate;

        assertThrow(validate, JobValidationException.class);
    }

    private <T> void assertThrow(Runnable action, Class<T> exceptionClass) {
        try {
            action.run();
            fail("Should have thrown an " + exceptionClass + ".");
        } catch (Exception e) {
            assertTrue(exceptionClass.isInstance(e));
        }
    }


    private void assertDoesNotThrow(Runnable action){
        try {
            action.run();
        } catch (Exception ex) {
            fail("Expected action not to throw, but it did.");
        }
    }

}
