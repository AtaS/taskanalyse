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

    //Map avg duration of tasks as {taskId, duration in miliseconds}
    private Map<Integer, Double> avgDurations = new HashMap<>();

    //Map of completed task's duration as {taskId, duration in milliseconds}
    private Map<Integer, List<Double>> durationsMap = new HashMap<>();


    public MemoryTaskRepository(@Autowired ObjectMapper jacksonObjectMapper) {
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    private Map<Integer, Set<String>> subscribersMap = new HashMap<>();

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
        if (!durationsMap.containsKey(id))
            durationsMap.put(id, new ArrayList<>());

        durationsMap.get(id).add(duration);
    }

    @Override
    public void setPerformedWithAvgDuration(int taskId, double duration, double newAvgDuration) {
        setPerformed(taskId, duration);
        updateAverageDuration(taskId, newAvgDuration);
    }

    @Override
    public TaskDurationInfo getDurationInfo(int taskId) {

        TaskDurationInfo info = new TaskDurationInfo();

        try {
            // completed durations for this task
            long completedDurations = !durationsMap.containsKey(taskId) ? 0 : durationsMap.get(taskId).size();
            int duration = Math.toIntExact(completedDurations);
            info.setNumberOfFinishedTasks(duration);
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

    @Override
    public Task get(int id) {
        return getTaskMap().get(id);
    }

    @Override
    public Map<Integer, Task> getAll() {
        if (taskMap == null)
            readTasks();

        return taskMap;
    }

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
