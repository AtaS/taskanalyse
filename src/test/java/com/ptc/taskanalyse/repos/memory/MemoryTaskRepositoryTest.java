package com.ptc.taskanalyse.repos.memory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptc.taskanalyse.models.TaskDurationInfo;
import com.ptc.taskanalyse.repos.TaskRepository;
import com.ptc.taskanalyse.services.TaskService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by asasmaz on 22/05/2017.
 */
public class MemoryTaskRepositoryTest {

    private TaskRepository repo;

    @Before
    public void runBeforeTestMethod() {
        repo = new MemoryTaskRepository(new ObjectMapper());
    }

    @Test
    public void setPerformed() throws Exception {
        int numOfFinishedTasks = repo.getDurationInfo(1).getNumOfFinishedTasks();
        Assert.assertTrue("There should be no finished tasks, yet", numOfFinishedTasks == 0);

        repo.setPerformed(1, 5);

        numOfFinishedTasks = repo.getDurationInfo(1).getNumOfFinishedTasks();
        Assert.assertTrue("There should be 1 finished task now", numOfFinishedTasks == 1);
    }

    @Test
    public void setPerformedWithAvgDuration() throws Exception {
        TaskService service = new TaskService(repo);
        TaskDurationInfo durInfo = repo.getDurationInfo(1);
        Assert.assertTrue(
                "There should be no finished tasks, yet",
                durInfo.getNumOfFinishedTasks() == 0
        );
        Assert.assertTrue(
                "Average duration should not exist, i.e. should be -1",
                durInfo.getCurrentAvgDuration() == -1
        );

        repo.setPerformedWithAvgDuration(1, 2, service.recalculateAverageDuration(1, 2));
        durInfo = repo.getDurationInfo(1);
        Assert.assertTrue(
                "There should be 1 finished task now",
                durInfo.getNumOfFinishedTasks() == 1
        );
        Assert.assertTrue(
                "Average duration should be 3",
                durInfo.getCurrentAvgDuration() == 2
        );

        repo.setPerformedWithAvgDuration(1, 6, service.recalculateAverageDuration(1, 6));
        durInfo = repo.getDurationInfo(1);
        Assert.assertTrue(
                "There should be 1 finished task now",
                durInfo.getNumOfFinishedTasks() == 1
        );
        Assert.assertTrue(
                "Average duration should be 4",
                durInfo.getCurrentAvgDuration() == 4
        );
    }

    @Test
    public void getDurationInfo() throws Exception {
        TaskService service = new TaskService(repo);
        repo.setPerformed(2, 5);
        repo.setPerformed(1, 5);
        TaskDurationInfo durInfo = repo.getDurationInfo(2);
        Assert.assertTrue("There should be 1 finished task", durInfo.getNumOfFinishedTasks() == 1);
    }

    @Test
    public void getAverageDuration() throws Exception {
        TaskService service = new TaskService(repo);
        repo.setPerformedWithAvgDuration(2, 5, 5);
        repo.setPerformedWithAvgDuration(1, 10, 10);
        Assert.assertTrue("Avg duration should be the initial 5", repo.getAverageDuration(2) == 5);

        repo.setPerformedWithAvgDuration(2, 9, service.recalculateAverageDuration(2, 9));

        Assert.assertTrue("Avg duration should be 7 now", repo.getAverageDuration(2) == 7);
    }

    @Test
    public void get() throws Exception {
        Assert.assertTrue("Tasks should exist", repo.getAll().size() > 0);
    }

    @Test
    public void getAll() throws Exception {
        Assert.assertTrue("Tasks should exist", repo.getAll().size() > 0);
    }

    @Test
    public void exists() throws Exception {
        Assert.assertFalse("-1 task ID shouldn't exist", repo.exists(-1));
        Assert.assertTrue("1 task ID should exist", repo.exists(1));
    }

}