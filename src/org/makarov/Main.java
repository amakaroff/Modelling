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

    private static final int COUNT = 1000;      //общее колличество заданий

    private static final int QUEUE_SIZE = 2;    //размер очереди для ожидания

    private static final int DEVICE_COUNT = 2;  //количество обработчиков задач

    private static final double LAMBDA = 0.3;   //частота прихода новых заданий

    private static final double U = 0.3;        //скорость обработки заданий

    public static void main(String[] args) {
        Service service = new Service(COUNT, QUEUE_SIZE, DEVICE_COUNT, LAMBDA, U);

        service.run((result, finishTime) -> {
            int countRefused = 0;
            double timeWorking = 0;
            double queueWaiting = 0;
            for (Task task : result) {
                if (task.isRefuse()) {
                    countRefused++;
                } else {
                    timeWorking += task.getFinishResolveTime() - task.getArriveTime();
                    queueWaiting += task.getStartResolveTime() - task.getArriveTime();
                }
            }
            int countResolved = result.size() - countRefused;

            System.out.println("All service working time: " + finishTime);
            System.out.println("Amount of refused: " + countRefused);
            System.out.println("All amount: " + result.size());
            System.out.printf("Percent of refused tasks: %.2f%%\n", ((double) countRefused / result.size()) * 100);
            System.out.printf("Average time of working: %.10f\n", (timeWorking / countResolved));
            System.out.printf("Average time of waiting in service queue: %.10f\n", (queueWaiting / countResolved));
        });
    }
}