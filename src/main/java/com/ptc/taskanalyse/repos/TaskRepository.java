package com.ptc.taskanalyse.repos;

import com.ptc.taskanalyse.models.Task;
import com.ptc.taskanalyse.models.TaskDurationInfo;

import java.util.Map;
import java.util.Set;

/**
 * Created by asasmaz on 21/05/2017.
 */
public interface TaskRepository {

    /**
     * Set a task as performed with duration
     * @param id
     * @param duration
     */
    void setPerformed(int id, double duration);

    /**
     * Two operations together as atomic.
     * @param id
     * @param duration
     * @param newAvgDuration
     */
    void setPerformedWithAvgDuration(int id, double duration, double newAvgDuration);

    /**
     * Get the info about current avg duration and how many tasks has made up that avg duration
     * @param taskId
     * @return TaskDurationInfo object
     */
    TaskDurationInfo getDurationInfo(int taskId);

    /**
     * Get average duration of the task, pre-calculated
     * @param taskId
     * @return
     */
    double getAverageDuration(int taskId);

    /**
     * Get a specific task
     * @param id
     * @return Task
     */
    Task get(int id);

    /**
     * Get all tasks
     * @return Map of all tasks as {id, task}
     */
    Map<Integer, Task> getAll();

    /**
     * Check if a task with a given ID exists
     * @param id
     * @return boolean
     */
    boolean exists(int id);

    /**
     * Subscribe to this given task
     * @param taskId
     * @param url
     */
    void subscribe(int taskId, String url);

    /**
     * Get all subscribers to this task
     * @param id
     * @return
     */
    Set<String> getSubscribers(int id);
}
