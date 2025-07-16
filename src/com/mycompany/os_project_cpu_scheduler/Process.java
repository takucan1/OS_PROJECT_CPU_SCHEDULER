/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.os_project_cpu_scheduler;

/**
 *
 * @author Aledon & Tipactipac
 */
public class Process {
    public String id;
    public int arrivalTime;
    public int burstTime;
    public int remainingTime;
    public int completionTime;
    public int turnaroundTime;
    public int responseTime;
    public int startTime = -1;
    public int queueLevel = -1; // For MLFQ

    public Process(String id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }
}
