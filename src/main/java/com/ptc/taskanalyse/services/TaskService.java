package com.ptc.taskanalyse.services;

import com.ptc.taskanalyse.models.Task;
import com.ptc.taskanalyse.models.TaskDurationInfo;
import com.ptc.taskanalyse.repos.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        taskRepository.subscribe(taskId, url);
    }

    /**
     * Publish to subscribers, ping HTTP urls
     * @param subscribers
     */
    private void publishSubscribers(Set<String> subscribers) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        if (subscribers == null)
            return;

        for (String url : subscribers) {
            executorService.execute(() -> {
                try {
                    logger.info("Calling subscriber URL " + url);
                    URL subUrl = new URL(url);
                    URLConnection subConn = subUrl.openConnection();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    subConn.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        System.out.println(inputLine);
                    in.close();

                } catch (Exception e) {
                    logger.error("Couldn't ping subscriber url " + url, e);
                }
            });
            executorService.shutdown();
        }
    }
}
