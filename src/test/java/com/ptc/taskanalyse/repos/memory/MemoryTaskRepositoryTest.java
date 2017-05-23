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
        Assert.assertEquals("There should be no finished tasks, yet", 0, numOfFinishedTasks, 0);

        repo.setPerformed(1, 5);

        numOfFinishedTasks = repo.getDurationInfo(1).getNumOfFinishedTasks();
        Assert.assertEquals("There should be 1 finished task now", 1, numOfFinishedTasks, 0);
    }

    @Test
    public void setPerformedWithAvgDuration() throws Exception {
        TaskService service = new TaskService(repo);
        TaskDurationInfo durInfo = repo.getDurationInfo(1);
        Assert.assertEquals(
                "There should be no finished tasks, yet", 0, durInfo.getNumOfFinishedTasks(), 0
        );
        Assert.assertEquals(
                "Average duration should not exist, i.e. should be -1",
                -1, durInfo.getCurrentAvgDuration(), 0
        );

        repo.setPerformedWithAvgDuration(1, 2, service.recalculateAverageDuration(1, 2));
        durInfo = repo.getDurationInfo(1);
        Assert.assertEquals(
                "There should be 1 finished task now", 1, durInfo.getNumOfFinishedTasks(), 0
        );
        Assert.assertEquals(
                "Average duration should be 3", 2, durInfo.getCurrentAvgDuration(),0
        );

        repo.setPerformedWithAvgDuration(1, 6, service.recalculateAverageDuration(1, 6));
        durInfo = repo.getDurationInfo(1);
        Assert.assertEquals(
                "There should be 2 finished tasks now", 2, durInfo.getNumOfFinishedTasks()
        );
        Assert.assertEquals(
                "Average duration should be 4", 4, durInfo.getCurrentAvgDuration(), 0
        );

        repo.setPerformedWithAvgDuration(1, 7, service.recalculateAverageDuration(1, 7));
        durInfo = repo.getDurationInfo(1);
        Assert.assertEquals(
                "There should be 3 finished tasks now", 3, durInfo.getNumOfFinishedTasks()
        );
        Assert.assertEquals(
                "Average duration should be 5", 5, durInfo.getCurrentAvgDuration(), 0
        );
    }

    @Test
    public void getDurationInfo() throws Exception {
        TaskService service = new TaskService(repo);
        repo.setPerformed(2, 5);
        repo.setPerformed(1, 5);
        TaskDurationInfo durInfo = repo.getDurationInfo(2);
        Assert.assertEquals("There should be 1 finished task", 1, durInfo.getNumOfFinishedTasks());
    }

    @Test
    public void getAverageDuration() throws Exception {
        TaskService service = new TaskService(repo);
        repo.setPerformedWithAvgDuration(2, 5, 5);
        repo.setPerformedWithAvgDuration(1, 10, 10);
        Assert.assertEquals("Avg duration should be the initial 5", 5,repo.getAverageDuration(2), 0);

        repo.setPerformedWithAvgDuration(2, 9, service.recalculateAverageDuration(2, 9));

        Assert.assertEquals("Avg duration should be 7 now", 7, repo.getAverageDuration(2), 0);
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