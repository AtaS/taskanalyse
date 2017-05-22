package com.ptc.taskanalyse.services;

import com.ptc.taskanalyse.models.Task;
import com.ptc.taskanalyse.models.TaskDurationInfo;
import com.ptc.taskanalyse.repos.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by asasmaz on 20/05/2017.
 */
@Service
public class TaskService {

    TaskRepository taskRepository;

    public TaskService(@Autowired TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task get(int id) {
        return taskRepository.get(id);
    }

    public boolean exists(int id) {
        return taskRepository.exists(id);
    }

    /**
     * Set as task performed and update the average duration
     * @param id
     * @param duration
     */
    public void setPerformed(int id, double duration) {
        double newAvgDuration = recalculateAverageDuration(id, duration);
        taskRepository.setPerformedWithAvgDuration(id, duration, newAvgDuration);
    }

    /**
     * Lazy re-calculation of the average duration
     * @param taskId
     */
    public double recalculateAverageDuration(int taskId, double newDuration) {
        TaskDurationInfo info = taskRepository.getDurationInfo(taskId);

        double newAvgDuration =
                (info.getCurrentAvgDuration() * info.getNumOfFinishedTasks() + newDuration)
                        / (info.getNumOfFinishedTasks() + 1);

        return newAvgDuration;
    }


    public Double getAverageDuration(int id) {
        return taskRepository.getAverageDuration(id);
    }

    public Map<Integer, Task> getAll() {
        return taskRepository.getAll();
    }
}
