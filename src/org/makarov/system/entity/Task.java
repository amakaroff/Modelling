package org.makarov.system.entity;

public class Task implements Comparable<Task> {

    private double beginTime;

    private double serviceTime;

    private double endTime;

    private boolean isRefuse = false;

    public Task(double beginTime) {
        this.beginTime = beginTime;
    }

    public double getBeginTime() {
        return beginTime;
    }

    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void refuse() {
        isRefuse = true;
        serviceTime = Double.POSITIVE_INFINITY;
        endTime = Double.POSITIVE_INFINITY;
    }

    public boolean isRefuse() {
        return isRefuse;
    }

    @Override
    public int compareTo(Task task) {
        return Double.compare(this.getBeginTime(), task.getBeginTime());
    }
}
