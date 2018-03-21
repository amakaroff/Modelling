package org.makarov;

import org.makarov.system.Service;
import org.makarov.system.entity.Task;

public class Main {

    // A | S | k | B | Z  - обозначение

    // M | M | 2 | 2 | ∞  - мой вариант (3)

    // M - Пуассоновский поток требований
    // M - последовательность независимых, одинаково распределенных
    //     экспоненциально длительностей обслуживания на каждом приборе;
    // 2 - число мест ожидания в очереди
    // 2 - число обслуживающих приборов в системе
    // ∞ - указывает число источников требований

    private static final int COUNT = 1000;

    private static final int QUEUE_SIZE = 2;

    private static final int DEVICE_COUNT = 2;

    private static final double LAMBDA = 5;

    private static final int U = 5;

    public static void main(String[] args) {
        Service service = new Service(COUNT, QUEUE_SIZE, DEVICE_COUNT, LAMBDA, U);
        service.run((resultTasks, endTime) -> {
            int countRefused = 0;
            double timeWorking = 0;
            for (Task task : resultTasks) {
                if (task.isRefuse()) {
                    countRefused++;
                } else {
                    timeWorking += task.getEndTime() - task.getBeginTime();
                }
            }


            System.out.println("All service working time: " + endTime);
            System.out.println("Amount of refused: " + countRefused);
            System.out.println("All amount: " + resultTasks.size());
            System.out.println(String.format("Middle time of working: %.10f", (timeWorking / (resultTasks.size() - countRefused))));
        });
    }
}