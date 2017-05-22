package com.ptc.taskanalyse.services;

import com.ptc.taskanalyse.logic.PublishLogic;
import com.ptc.taskanalyse.models.Task;
import com.ptc.taskanalyse.models.TaskDurationInfo;
import com.ptc.taskanalyse.repos.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by asasmaz on 20/05/2017.
 */
@Service
public class TaskService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        logger.info("Setting task performed", id);
        double newAvgDuration = recalculateAverageDuration(id, duration);

        Set<String> subscribers = taskRepository.getSubscribers(id);
        publishSubscribers(subscribers);

        taskRepository.setPerformedWithAvgDuration(id, duration, newAvgDuration);
    }

    /**
     * Lazy re-calculation of the average duration
     * @param taskId
     */
    public double recalculateAverageDuration(int taskId, double newDuration) {
        logger.info("Recalculating average duration", taskId);
        TaskDurationInfo info = taskRepository.getDurationInfo(taskId);

        double newAvgDuration =
                (info.getCurrentAvgDuration() * info.getNumOfFinishedTasks() + newDuration)
                        / (info.getNumOfFinishedTasks() + 1);

        return newAvgDuration;
    }


    /**
     * Get average duration for a task, pre-calculated.
     * @param id
     * @return
     */
    public Double getAverageDuration(int id) {
        logger.info("Getting average duration for Task ID " + id);
        return taskRepository.getAverageDuration(id);
    }

    public Map<Integer, Task> getAll() {
        return taskRepository.getAll();
    }

    /**
     * Subscribe to a task and get notified upon its completion
     * @param taskId
     * @param url fully qualified HTTP url
     */
    public void subscribe(int taskId, String url) {
        logger.info("Subscribing to the task", taskId);
        taskRepository.subscribe(taskId, url);
    }

    /**
     * Publish to subscribers, ping HTTP urls
     * @param subscribers
     */
    private void publishSubscribers(Set<String> subscribers) {
        PublishLogic publishLogic = new PublishLogic();
        publishLogic.publishToSubscribers(subscribers);
    }
}
