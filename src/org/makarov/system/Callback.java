package org.makarov.system;

import org.makarov.system.entity.Task;

import java.util.Queue;

public interface Callback {

    void call(Queue<Task> resultTasks, double endTime);
}
