package com.ptc.taskanalyse.models;

/**
 * Created by asasmaz on 22/05/2017.
 */
public class TaskDurationInfo {

    private int numberOfFinishedTasks;
    private double currentAvgDuration;

    public int getNumOfFinishedTasks() {
        return numberOfFinishedTasks;
    }

    public void setNumberOfFinishedTasks(int numberOfFinishedTasks) {
        this.numberOfFinishedTasks = numberOfFinishedTasks;
    }

    public double getCurrentAvgDuration() {
        return currentAvgDuration;
    }

    public void setCurrentAvgDuration(double currentAvgDuration) {
        this.currentAvgDuration = currentAvgDuration;
    }
}
