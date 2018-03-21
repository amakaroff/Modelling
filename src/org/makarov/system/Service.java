package org.makarov.system;

import org.makarov.system.entity.Device;
import org.makarov.system.entity.Task;
import org.makarov.system.util.ExponentialDistribution;

import java.util.*;

public class Service {

    private double currentTime;

    private final int count;

    private final int queueSize;

    private final int deviceCount;

    private ExponentialDistribution exponentialDistribution;

    private Queue<Task> tasks;

    private Queue<Task> serviceQueue;

    private List<Device> devices;

    private Queue<Task> resultQueue;

    public Service(int count, int queueSize, int deviceCount, double lambda, double u) {
        this.currentTime = 0;

        this.count = count;
        this.queueSize = queueSize;
        this.deviceCount = deviceCount;

        this.exponentialDistribution = new ExponentialDistribution(lambda, u);

        this.tasks = taskInit(currentTime);
        this.serviceQueue = queueServiceInit();
        this.devices = deviceInit();
        this.resultQueue = resultQueueInit();
    }

    private Queue<Task> taskInit(double time) {
        Queue<Task> tasks = new ArrayDeque<>(count);

        for (int i = 0; i < count; i++) {
            time += exponentialDistribution.getForArrive();
            Task task = new Task(time);
            tasks.add(task);
        }

        return tasks;
    }

    private List<Device> deviceInit() {
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < deviceCount; i++) {
            devices.add(new Device(exponentialDistribution));
        }

        return devices;
    }

    private Queue<Task> queueServiceInit() {
        return new ArrayDeque<>(queueSize);
    }

    private Queue<Task> resultQueueInit() {
        return new ArrayDeque<>(count);
    }

    public void run(Callback callback) {
        while (isContinueWorking()) {
            Task task = tasks.peek();

            double taskTime = getTaskArriveTime(task);
            Map<Double, Device> deviceEndTimes = createDeviceWorkTimeMap();
            double minDeviceEndTime = Collections.min(deviceEndTimes.keySet());

            currentTime = Math.min(taskTime, minDeviceEndTime);
            if (taskTime < minDeviceEndTime) {
                processArriveTaskEvent();
            } else {
                Device device = deviceEndTimes.get(minDeviceEndTime);
                processDeviceResolveTaskEvent(device);
            }
        }

        callback.call(resultQueue, currentTime);
    }

    private boolean isContinueWorking() {
        if (!tasks.isEmpty() || !serviceQueue.isEmpty()) {
            return true;
        }

        for (Device device : devices) {
            if (device.getEndTime() != Double.MAX_VALUE) {
                return true;
            }
        }

        return false;
    }

    private double getTaskArriveTime(Task task) {
        return task == null ? Double.MAX_VALUE : task.getBeginTime();
    }

    private Map<Double, Device> createDeviceWorkTimeMap() {
        Map<Double, Device> deviceEndTimes = new HashMap<>();
        for (Device device : devices) {
            deviceEndTimes.put(device.getEndTime(), device);
        }

        return deviceEndTimes;
    }

    private void processArriveTaskEvent() {
        Task task = tasks.poll();
        processCheckingServiceQueue(task);

        for (Device device : devices) {
            task = serviceQueue.peek();

            if (task != null && device.isFinish(currentTime)) {
                resolveOldTask(device);
                processNewTask(device, task);
            }
        }
    }

    private void processCheckingServiceQueue(Task task) {
        if (serviceQueue.size() == queueSize) {
            task.refuse();
            resultQueue.add(task);
        } else {
            serviceQueue.add(task);
        }
    }

    private void processDeviceResolveTaskEvent(Device device) {
        Task task = serviceQueue.peek();

        resolveOldTask(device);
        processNewTask(device, task);
    }

    private void resolveOldTask(Device device) {
        Task resolveTask = device.resolveTask();
        if (resolveTask != null) {
            resultQueue.add(resolveTask);
        }
    }

    private void processNewTask(Device device, Task task) {
        if (task != null) {
            task.setServiceTime(currentTime);
            device.processTask(task);
            serviceQueue.remove();
        }
    }
}
