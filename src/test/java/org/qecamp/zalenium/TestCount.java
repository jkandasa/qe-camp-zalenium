package org.qecamp.zalenium;

import java.util.concurrent.TimeUnit;

import lombok.Getter;

@Getter
public class TestCount {
    private int success = 0;
    private int failures = 0;
    private int skipped = 0;
    private long startTime = 0;
    private long endTime = 0;

    public void incrementSuccess() {
        success++;
    }

    public void incrementFailures() {
        failures++;
    }

    public void incrementSkipped() {
        skipped++;
    }

    public int getTotal() {
        return success + failures + skipped;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    private String timeTaken(long duration) {
        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        long milliseconds = 0;
        // update hours
        hours = TimeUnit.HOURS.convert(duration, TimeUnit.MILLISECONDS);
        duration = duration - (hours * 1000 * 60 * 60);
        // update minutes
        minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS);
        duration = duration - (minutes * 1000 * 60);
        // update seconds
        seconds = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS);
        duration = duration - (seconds * 1000);
        milliseconds = duration;
        return String.format("%02d:%02d:%02d.%d", hours, minutes, seconds, milliseconds);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder
                .append("total:").append(success + failures + skipped)
                .append(", failures:").append(failures)
                .append(", skipped:").append(skipped)
                .append(", success:").append(success)
                .append(", time_taken:[").append(timeTaken(endTime - startTime)).append("]");
        return builder.toString();
    }
}
