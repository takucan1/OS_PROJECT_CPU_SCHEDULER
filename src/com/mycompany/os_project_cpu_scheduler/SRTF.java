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

public class SRTF implements Scheduler {
    private List<String> ganttChart = new ArrayList<>();

    @Override
    public void schedule(List<Process> processes) {
        int n = processes.size(), completed = 0, time = 0, prev = -1;
        int[] remaining = new int[n];
        for (int i = 0; i < n; i++) remaining[i] = processes.get(i).burstTime;
        boolean[] started = new boolean[n];

        while (completed < n) {
            int idx = -1, minRem = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (processes.get(i).arrivalTime <= time && remaining[i] > 0 && remaining[i] < minRem) {
                    minRem = remaining[i];
                    idx = i;
                }
            }

            if (idx == -1) {
                time++;
                continue;
            }

            if (!started[idx]) {
                processes.get(idx).startTime = time;
                started[idx] = true;
            }

            if (prev != idx) ganttChart.add("| " + processes.get(idx).id + " ");

            remaining[idx]--;
            time++;

            if (remaining[idx] == 0) {
                completed++;
                processes.get(idx).completionTime = time;
                processes.get(idx).turnaroundTime = time - processes.get(idx).arrivalTime;
                processes.get(idx).responseTime = processes.get(idx).startTime - processes.get(idx).arrivalTime;
            }

            prev = idx;
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
