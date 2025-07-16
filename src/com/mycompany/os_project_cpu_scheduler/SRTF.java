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
}
