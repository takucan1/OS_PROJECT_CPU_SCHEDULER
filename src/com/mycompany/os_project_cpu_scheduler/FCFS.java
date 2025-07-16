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
