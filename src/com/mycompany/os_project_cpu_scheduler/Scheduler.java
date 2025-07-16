/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.os_project_cpu_scheduler;

/**
 *
 * @author Aledon & Tipactipac
 */
import java.util.List;

public interface Scheduler {
    void schedule(List<Process> processes);
    List<String> getGanttChart();
    void printMetrics(List<Process> processes);
    double getAverageTurnaroundTime(List<Process> processes);
    double getAverageResponseTime(List<Process> processes);
    
   
}
