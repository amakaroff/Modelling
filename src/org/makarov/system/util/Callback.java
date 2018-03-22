package org.makarov.system.util;

import org.makarov.system.entity.Task;

import java.util.Queue;

@FunctionalInterface
public interface Callback {

    void call(Queue<Task> result, double finishTime);
}
