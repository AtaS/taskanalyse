package com.ptc.taskanalyse.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.taskanalyse.models.Task;
import com.ptc.taskanalyse.repos.memory.MemoryTaskRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * Created by asasmaz on 22/05/2017.
 */
public class TaskServiceTest {

    private TaskService taskService;

    @Before
    public void runBeforeTestMethod() {
        taskService = new TaskService(new MemoryTaskRepository(new ObjectMapper()));
    }


    @Test
    public void get() throws Exception {
        Task task = taskService.get(1);
        Assert.assertEquals("Task ID 1 check ID", 1, task.getId());
        Assert.assertEquals("Task ID 1 check name", "setupVM", task.getName());
    }

    @Test
    public void exists() throws Exception {
        Assert.assertTrue("Task ID 1 exists", taskService.exists(1));
        Assert.assertFalse("Task ID -1 doesn't exist", taskService.exists(-1));
    }

    @Test
    public void setPerformed() throws Exception {
        Double duration = taskService.getAverageDuration(2);
        Assert.assertEquals("Task ID 1 duration should be -1", -1, duration, 0);
        taskService.setPerformed(2, 5);
        duration = taskService.getAverageDuration(2);
        Assert.assertEquals("Task ID 1 duration should be 5", 5, duration, 0);
    }

    @Test
    public void recalculateAverageDuration() throws Exception {

        double duration = taskService.recalculateAverageDuration(1, 2);
        Assert.assertEquals("Duration should be 2", 2, duration, 0);

        duration = taskService.recalculateAverageDuration(1, 6);
        Assert.assertEquals("Duration should be 6", 6, duration, 0);

        taskService.setPerformed(1, 6);

        duration = taskService.recalculateAverageDuration(1, 10);
        Assert.assertEquals("Duration should be 6", 8, duration, 0);
    }

    @Test
    public void getAverageDuration() throws Exception {
        double duration = taskService.getAverageDuration(1);
        Assert.assertEquals("Initial avg duration should be -1", -1, duration, 0);

        taskService.setPerformed(1, 6);
        duration = taskService.getAverageDuration(1);
        Assert.assertEquals("Avg duration should be -1", 6, duration, 0);

        taskService.setPerformed(1, 10);
        duration = taskService.getAverageDuration(1);
        Assert.assertEquals("Avg duration should be -1", 8, duration, 0);
    }

    @Test
    public void getAll() throws Exception {
        Map<Integer, Task> tasks = taskService.getAll();
        Assert.assertEquals("Tasks size should be 5", tasks.size(), 5 );
    }

}