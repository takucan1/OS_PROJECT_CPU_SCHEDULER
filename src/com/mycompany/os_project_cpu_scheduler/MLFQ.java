/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.os_project_cpu_scheduler;

import java.util.*;

public class MLFQ implements Scheduler {
    private List<String> ganttChart = new ArrayList<>();
    private int[] quantums; // e.g., {2, 4, 8, Integer.MAX_VALUE}
    private int levels = 4;

    public MLFQ(int[] quantums) {
        this.quantums = quantums;
    }

    @Override
    public void schedule(List<Process> processes) {
        List<Queue<Process>> queues = new ArrayList<>();
        for (int i = 0; i < levels; i++) queues.add(new LinkedList<>());

        int time = 0, completed = 0, n = processes.size();
        int[] remaining = new int[n];
        int[] level = new int[n];
        boolean[] started = new boolean[n];
        for (int i = 0; i < n; i++) remaining[i] = processes.get(i).burstTime;

        Set<Process> arrived = new HashSet<>();

        while (completed < n) {
            for (Process p : processes) {
                if (p.arrivalTime <= time && !arrived.contains(p)) {
                    queues.get(0).offer(p);
                    level[processes.indexOf(p)] = 0;
                    arrived.add(p);
                }
            }

            int q = -1;
            for (int i = 0; i < levels; i++) {
                if (!queues.get(i).isEmpty()) {
                    q = i;
                    break;
                }
            }

            if (q == -1) {
                time++;
                continue;
            }

            Process p = queues.get(q).poll();
            int idx = processes.indexOf(p);
            if (!started[idx]) {
                p.startTime = time;
                started[idx] = true;
            }

            ganttChart.add("| " + p.id + "(Q" + q + ") ");

            int exec = Math.min(quantums[q], remaining[idx]);
            time += exec;
            remaining[idx] -= exec;

            for (Process np : processes) {
                if (np.arrivalTime > p.arrivalTime && np.arrivalTime <= time && !arrived.contains(np)) {
                    queues.get(0).offer(np);
                    level[processes.indexOf(np)] = 0;
                    arrived.add(np);
                }
            }

            if (remaining[idx] > 0) {
                if (q < levels - 1) {
                    queues.get(q + 1).offer(p);
                    level[idx] = q + 1;
                } else {
                    queues.get(q).offer(p);
                }
            } else {
                p.completionTime = time;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.responseTime = p.startTime - p.arrivalTime;
                completed++;
            }
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


