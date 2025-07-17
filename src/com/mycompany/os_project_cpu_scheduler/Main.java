
package com.mycompany.os_project_cpu_scheduler;



import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Process> processes = new ArrayList<>();
        Scheduler scheduler = null;

        System.out.println("Select input method: [1] Manual [2] Random");
        int inputChoice = sc.nextInt();

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        if (inputChoice == 1) {
            for (int i = 0; i < n; i++) {
                System.out.print("Enter Process ID: ");
                String id = sc.next();
                System.out.print("Enter Arrival Time: ");
                int arrival = sc.nextInt();
                System.out.print("Enter Burst Time: ");
                int burst = sc.nextInt();
                processes.add(new Process(id, arrival, burst));
            }
        } else {
            Random rand = new Random();
            for (int i = 0; i < n; i++) {
                String id = "P" + (i + 1);
                int arrival = rand.nextInt(5); // e.g., 0-4
                int burst = rand.nextInt(8) + 1; // e.g., 1-8
                processes.add(new Process(id, arrival, burst));
                System.out.println("Generated: " + id + " Arrival: " + arrival + " Burst: " + burst);
            }
        }

        System.out.println("\nSelect scheduling algorithm:");
        System.out.println("[1] FCFS [2] SJF [3] SRTF [4] Round Robin [5] MLFQ");
        int algo = sc.nextInt();

        if (algo == 1) {
            scheduler = new FCFS();
        } else if (algo == 2) {
            scheduler = new SJF();
        } else if (algo == 3) {
            scheduler = new SRTF();
        } else if (algo == 4) {
            System.out.print("Enter time quantum for Round Robin: ");
            int quantum = sc.nextInt();
            scheduler = new RoundRobin(quantum);
        } else if (algo == 5) {
            int[] quantums = new int[4];
            for (int i = 0; i < 4; i++) {
                System.out.print("Enter time quantum for Q" + i + ": ");
                quantums[i] = sc.nextInt();
            }
            scheduler = new MLFQ(quantums);
        } else {
            System.out.println("Invalid selection.");
            sc.close();
            return;
        }

        // Sort by arrival time for consistency
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        scheduler.schedule(processes);

        // --- Gantt Chart ---
        System.out.println("\n--- GANTT CHART ---");
        List<String> gantt = scheduler.getGanttChart();
        for (String s : gantt) System.out.print(s);
        System.out.println("|");

        // --- Process Metrics ---
        System.out.println("\n--- PROCESS METRICS ---");
        System.out.printf("| %-4s | %-7s | %-5s | %-10s | %-11s | %-9s |\n", "PID", "Arrival", "Burst", "Completion", "Turnaround", "Response");
        System.out.println("|------|---------|-------|------------|------------|----------|");
        for (Process p : processes) {
            System.out.printf("| %-4s | %-7d | %-5d | %-10d | %-11d | %-9d |\n",
                    p.id, p.arrivalTime, p.burstTime, p.completionTime, p.turnaroundTime, p.responseTime);
        }

        double avgTurnaround = scheduler.getAverageTurnaroundTime(processes);
        double avgResponse = scheduler.getAverageResponseTime(processes);

        System.out.printf("\nAverage Turnaround Time: %.2f\n", avgTurnaround);
        System.out.printf("Average Response Time: %.2f\n", avgResponse);

        sc.close();
    }
}

