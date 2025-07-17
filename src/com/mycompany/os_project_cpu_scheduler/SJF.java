/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.os_project_cpu_scheduler;

import java.util.*;

public class SJF implements Scheduler {
    private List<String> ganttChart = new ArrayList<>();

    @Override
    public void schedule(List<Process> processes) {
        List<Process> ready = new ArrayList<>();
        int time = 0, completed = 0, n = processes.size();
        boolean[] done = new boolean[n];

        while (completed < n) {
            for (int i = 0; i < n; i++) {
                if (!done[i] && processes.get(i).arrivalTime <= time) {
                    ready.add(processes.get(i));
                }
            }
            if (ready.isEmpty()) {
                time++;
                continue;
            }
            ready.sort(Comparator.comparingInt(p -> p.burstTime));
            Process p = ready.get(0);
            p.startTime = Math.max(time, p.arrivalTime);
            p.completionTime = p.startTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.responseTime = p.startTime - p.arrivalTime;
            time = p.completionTime;
            ganttChart.add("| " + p.id + " ");
            done[processes.indexOf(p)] = true;
            ready.clear();
            completed++;
        }
    }
    


