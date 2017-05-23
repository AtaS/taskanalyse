package com.ptc.taskanalyse.controllers;

import com.ptc.taskanalyse.exceptions.ResourceNotFoundException;
import com.ptc.taskanalyse.models.BooleanResponse;
import com.ptc.taskanalyse.models.SimpleResponse;
import com.ptc.taskanalyse.models.Task;
import com.ptc.taskanalyse.services.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TasksController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TaskService taskService;

    /**
     * Get all the tasks endpoint
     * @return array of task objects
     */
    @GetMapping("/")
    public Map<Integer, Task> getAll() {
        return taskService.getAll();
    }

    /**
     * Get a specific task endpoint
     * @param taskId
     * @return
     */
    @GetMapping("/{taskId}")
    public Task get(@PathVariable int taskId) {
        return taskService.get(taskId);
    }

    /**
     * Mark a task as performed
     */
    @PostMapping("/{taskId}/perform")
    @CacheEvict(value = "avgTime", key = "#taskId")
    public ResponseEntity<Object> perform(@PathVariable int taskId, @RequestParam Double duration) {

        // Check if task service exists
        if (!taskService.exists(taskId)) {
            logger.warn("Non-existing TaskId is queried in /tasks/taskId/perform endpoint", taskId, duration);
            throw new ResourceNotFoundException();
        }

        try {
            taskService.setPerformed(taskId, duration);
        } catch (Exception ex) {
            logger.error("Exception in /tasks/taskId/perform endpoint", ex);
            return new ResponseEntity<>(new BooleanResponse("error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new BooleanResponse(true), HttpStatus.OK);
    }

    /**
     * Get an average running duration of a task
     * @param taskId
     * @return
     */
    @GetMapping("/{taskId}/average")
    @Cacheable(value = "avgTime", key = "#taskId")
    public SimpleResponse averageTime(@PathVariable int taskId) {
        return new SimpleResponse(taskService.getAverageDuration(taskId));
    }

    /**
     * Subscribe to a task's completion and get notified
     * @param taskId
     * @param url
     * @return
     */
    @PostMapping("/{taskId}/subscribe")
    public SimpleResponse subscribe(@PathVariable int taskId, @RequestParam String url) {
        taskService.subscribe(taskId, url);
        return new SimpleResponse("OK");
    }

}
