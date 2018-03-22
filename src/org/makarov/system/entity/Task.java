package org.makarov.system.entity;

public class Task implements Comparable<Task> {

    private double arriveTime;

    private double beginResolveTime;

    private double endResolveTime;

    private boolean isRefuse = false;

    public Task(double arriveTime) {
        this.arriveTime = arriveTime;
    }

    public double getArriveTime() {
        return arriveTime;
    }

    public void setBeginResolveTime(double beginResolveTime) {
        this.beginResolveTime = beginResolveTime;
    }

    public double getBeginResolveTime() {
        return beginResolveTime;
    }

    public void setEndResolveTime(double endResolveTime) {
        this.endResolveTime = endResolveTime;
    }

    public double getEndResolveTime() {
        return endResolveTime;
    }

    public void refuse() {
        isRefuse = true;
        beginResolveTime = Double.POSITIVE_INFINITY;
        endResolveTime = Double.POSITIVE_INFINITY;
    }

    public boolean isRefuse() {
        return isRefuse;
    }

    @Override
    public int compareTo(Task task) {
        return Double.compare(this.getArriveTime(), task.getArriveTime());
    }
}
