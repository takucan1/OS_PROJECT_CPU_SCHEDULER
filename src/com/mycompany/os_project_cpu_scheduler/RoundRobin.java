/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.os_project_cpu_scheduler;

import java.util.*;

public class RoundRobin implements Scheduler {
    private List<String> ganttChart = new ArrayList<>();
    private int quantum;

    public RoundRobin(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public void schedule(List<Process> processes) {
        Queue<Process> queue = new LinkedList<>();
        int time = 0, completed = 0, n = processes.size();
        int[] remaining = new int[n];
        boolean[] started = new boolean[n];
        for (int i = 0; i < n; i++) remaining[i] = processes.get(i).burstTime;
        Set<Process> arrived = new HashSet<>();

        while (completed < n) {
            for (Process p : processes) {
                if (p.arrivalTime <= time && !arrived.contains(p)) {
                    queue.offer(p);
                    arrived.add(p);
                }
            }

            if (queue.isEmpty()) {
                time++;
                continue;
            }

            Process p = queue.poll();
            int idx = processes.indexOf(p);
            if (!started[idx]) {
                p.startTime = time;
                started[idx] = true;
            }

            ganttChart.add("| " + p.id + " ");

            int exec = Math.min(quantum, remaining[idx]);
            time += exec;
            remaining[idx] -= exec;

            for (Process np : processes) {
                if (np.arrivalTime > p.arrivalTime && np.arrivalTime <= time && !arrived.contains(np)) {
                    queue.offer(np);
                    arrived.add(np);
                }
            }

            if (remaining[idx] > 0) {
                queue.offer(p);
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

