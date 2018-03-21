package org.makarov.system.entity;

import org.makarov.system.util.ExponentialDistribution;

public class Device {

    private Task currentTask;

    private double resolveTime;

    private final ExponentialDistribution exponentialDistribution;

    public Device(ExponentialDistribution exponentialDistribution) {
        this.exponentialDistribution = exponentialDistribution;
    }

    public void processTask(Task task) {
        this.currentTask = task;
        this.resolveTime = this.exponentialDistribution.getForExecution();
    }

    public Task resolveTask() {
        if (this.currentTask != null) {
            Task task = this.currentTask;
            task.setEndTime(getEndTime());
            clear();

            return task;
        }

        return null;
    }

    private void clear() {
        this.currentTask = null;
        this.resolveTime = 0;
    }

    public boolean isFinish(double currentTime) {
        return this.currentTask == null || getEndTime() <= currentTime;
    }

    public double getEndTime() {
        if (this.currentTask == null) {
            return Double.MAX_VALUE;
        }

        return this.currentTask.getServiceTime() + this.resolveTime;
    }
}
