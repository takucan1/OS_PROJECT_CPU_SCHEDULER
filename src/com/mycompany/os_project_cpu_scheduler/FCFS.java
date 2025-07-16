/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.os_project_cpu_scheduler;

/**
 *
 * @author Takucan
 */
import java.util.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FCFS implements Scheduler {
    private List<String> ganttChart = new ArrayList<>();

    @Override
    public void schedule(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int time = 0;
        for (Process p : processes) {
            if (time < p.arrivalTime) time = p.arrivalTime;
            p.startTime = time;
            p.completionTime = time + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.responseTime = p.startTime - p.arrivalTime;
            time += p.burstTime;
            ganttChart.add("| " + p.id + " ");
        }
    }

    @Override
    public List<String> getGanttChart() {
        return ganttChart;
    }

    @Override
    public void printMetrics(List<Process> processes) {
        System.out.printf("%-10s%-15s%-15s%-15s%-20s%-15s\n",
                "Process", "Arrival Time", "Burst Time", "Completion", "Turnaround Time", "Response Time");
        for (Process p : processes) {
            System.out.printf("%-10s%-15d%-15d%-15d%-20d%-15d\n",
                    p.id, p.arrivalTime, p.burstTime, p.completionTime, p.turnaroundTime, p.responseTime);
        }

        System.out.println("\nAverage Turnaround Time: " + getAverageTurnaroundTime(processes));
        System.out.println("Average Response Time: " + getAverageResponseTime(processes));
    }

    @Override
    public double getAverageTurnaroundTime(List<Process> processes) {
        int total = 0;
        for (Process p : processes) {
            total += p.turnaroundTime;
        }
        return (double) total / processes.size();
    }

    @Override
    public double getAverageResponseTime(List<Process> processes) {
        int total = 0;
        for (Process p : processes) {
            total += p.responseTime;
        }
        return (double) total / processes.size();
    }
}


