package org.makarov.system.manager;

import org.makarov.system.Service;
import org.makarov.system.entity.Task;
import org.makarov.system.util.Callback;
import org.makarov.system.util.ExponentialDistribution;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TaskManager {

    private Queue<Task> taskQueue;

    private ExponentialDistribution distribution;

    private int taskCount;

    private List<Service> services;

    private double currentTime = 0;

    private List<Double> percents;

    public TaskManager(int taskCount, double lambda, double u) {
        this.taskCount = taskCount;
        this.distribution = new ExponentialDistribution(lambda, u);
        this.taskQueue = tasksInit(currentTime);
        this.services = new ArrayList<>();
    }

    public void addService(Service service) {
        services.add(service);
    }

    private Queue<Task> tasksInit(double time) {
        Queue<Task> tasks = new PriorityQueue<>(taskCount);

        for (int i = 0; i < taskCount; i++) {
            time += distribution.getForArrive();
            Task task = new Task(time);
            tasks.add(task);
        }

        return tasks;
    }

    public void run(Callback callback) {
        while (isContinueWorking()) {
            double arriveTaskTime = Double.MAX_VALUE;

            Task task = taskQueue.peek();
            if (task != null) {
                arriveTaskTime = task.getArriveTime();
            }

            Map<Double, Service> serviceEventsTime = getServiceEventsTime();
            double serviceEvent = Collections.min(serviceEventsTime.keySet());
            currentTime = Math.min(arriveTaskTime, serviceEvent);

            if (arriveTaskTime < serviceEvent) {
                Service service = chooseService();
                service.addTask(taskQueue.poll());
                service.doIteration();
            } else {
                Service service = serviceEventsTime.get(serviceEvent);
                service.doIteration();
            }
        }

        Queue<Task> allResults = new ArrayDeque<>();
        for (Service service : services) {
            allResults.addAll(service.getResultQueue());
        }

        callback.call(allResults, currentTime);
    }

    private boolean isContinueWorking() {
        return !taskQueue.isEmpty() || services.stream()
                .anyMatch((service -> service.getNextEventTime() != Double.MAX_VALUE));
    }

    private Map<Double, Service> getServiceEventsTime() {
        return services.stream()
                .collect(Collectors.toMap(Service::getNextEventTime, Function.identity(), (key, value) -> key));
    }

    private Service chooseService() {
        if (percents == null) {
            this.percents = new ArrayList<>(services.size());
            double currentPercent = 0;
            for (Service service : services) {
                currentPercent += service.getPercent();
                percents.add(currentPercent);
            }

            if (currentPercent > 1.0) {
                throw new UnsupportedOperationException("Incorrect random services configure! Full random: " + currentPercent);
            }
        }

        double random = Math.random();
        for (int i = 0; i < percents.size(); i++) {
            if (random < percents.get(i)) {
                return services.get(i);
            }
        }

        throw new UnsupportedOperationException("Service can't be choose. Random value: " + random);
    }
}
