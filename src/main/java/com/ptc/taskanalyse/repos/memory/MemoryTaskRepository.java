package com.ptc.taskanalyse.repos.memory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.taskanalyse.exceptions.InternalServerErrorException;
import com.ptc.taskanalyse.models.Task;
import com.ptc.taskanalyse.models.TaskDurationInfo;
import com.ptc.taskanalyse.repos.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by asasmaz on 20/05/2017.
 */
@Primary
@Repository
public class MemoryTaskRepository implements TaskRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ObjectMapper jacksonObjectMapper;

    //Map of tasks as {taskId, task}
    private Map<Integer, Task> taskMap;

    private Map<Integer, Double> avgDurations = new HashMap<>();

    //Map of completed task's duration as {taskId, duration in milliseconds}
    private Map<Integer, List<Double>> durationMap = new HashMap<>();

    private Map<Integer, Set<String>> subscribersMap = new HashMap<>();

    /**
     * Pass object mapper directly
     * @param jacksonObjectMapper
     */
    public MemoryTaskRepository(@Autowired ObjectMapper jacksonObjectMapper) {
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    /**
     * Read the tasks in tasksStub
     */
    private void readTasks() {
        String tasksStubJsonFileName = "static/tasksStub.json";
        try {
            File file = new ClassPathResource(tasksStubJsonFileName).getFile();

            taskMap = new HashMap<>();
            Task[] tasks = jacksonObjectMapper.readValue(file, Task[].class);

            //Add tasks into tasks map
            Arrays.stream(tasks).forEach(t -> taskMap.put(t.getId(), t));

        } catch (IOException e) {
            logger.error("Can't read file " + tasksStubJsonFileName, e);
        }
    }

    @Override
    public void setPerformed(int id, double duration) {
        if (!durationMap.containsKey(id))
            durationMap.put(id, new ArrayList<>());

        durationMap.get(id).add(duration);
    }

    /**
     * Two operations together as atomic.
     * @param taskId
     * @param duration
     * @param newAvgDuration
     */
    @Override
    public void setPerformedWithAvgDuration(int taskId, double duration, double newAvgDuration) {
        setPerformed(taskId, duration);
        updateAverageDuration(taskId, newAvgDuration);
    }

    /**
     * Get the info about current avg duration and how many tasks has made up that avg duration
     * @param taskId
     * @return TaskDurationInfo object
     */
    @Override
    public TaskDurationInfo getDurationInfo(int taskId) {

        TaskDurationInfo info = new TaskDurationInfo();

        try {
            info.setNumberOfFinishedTasks(
                    Math.toIntExact(
                            durationMap.entrySet().stream().filter(d -> d.getKey() == taskId).count()
                    )
            );
        } catch (ArithmeticException ex) {
            logger.error("Couldn't convert Long to Int, too many tasks? Time to use Long instead of Int", ex);
            throw new InternalServerErrorException(ex);
        }

        info.setCurrentAvgDuration(getAverageDuration(taskId));

        return info;
    }

    @Override
    public double getAverageDuration(int taskId) {
        Double avgDuration = avgDurations.get(taskId);
        return avgDuration == null ? -1 : avgDuration;
    }

    /**
     * Get a specific task
     * @param id
     * @return Task
     */
    @Override
    public Task get(int id) {
        return getTaskMap().get(id);
    }

    /**
     * Get all tasks
     * @return Map of all tasks as {id, task}
     */
    @Override
    public Map<Integer, Task> getAll() {
        if (taskMap == null)
            readTasks();

        return taskMap;
    }

    /**
     * Check if a task with a given ID exists
     * @param id
     * @return boolean
     */
    @Override
    public boolean exists(int id) {
        if (taskMap == null)
            readTasks();

        return taskMap.containsKey(id);
    }

    @Override
    public void subscribe(int taskId, String url) {
        if (!subscribersMap.containsKey(taskId))
            subscribersMap.put(taskId, new HashSet<String>());

        subscribersMap.get(taskId).add(url);
    }

    @Override
    public Set<String> getSubscribers(int id) {
        return subscribersMap.get(id);
    }


    private Map<Integer, Task> getTaskMap() {
        if (taskMap == null)
            readTasks();

        return taskMap;
    }

    private void updateAverageDuration(int taskId, double duration) {
        avgDurations.put(taskId, duration);
    }
}
