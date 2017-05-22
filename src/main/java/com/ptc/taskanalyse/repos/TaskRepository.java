package com.ptc.taskanalyse.repos;

import com.ptc.taskanalyse.models.Task;
import com.ptc.taskanalyse.models.TaskDurationInfo;

import java.util.Map;

/**
 * Created by asasmaz on 21/05/2017.
 */
public interface TaskRepository {

    void setPerformed(int id, double duration);

    void setPerformedWithAvgDuration(int id, double duration, double newAvgDuration);

    TaskDurationInfo getDurationInfo(int taskId);

    double getAverageDuration(int taskId);

    Task get(int id);

    Map<Integer, Task> getAll();

    boolean exists(int id);
}
