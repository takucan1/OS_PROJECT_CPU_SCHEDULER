/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.os_project_cpu_scheduler;

/**
 *
 * @author Aledon & Tipactipac
 */
import java.util.*;

public class GanttChart {
    public static class Interval {
        String processId;
        int startTime;
        int endTime;
        String annotation; // e.g., "(Q0)" for MLFQ, or "" for others

        public Interval(String processId, int startTime, String annotation) {
            this.processId = processId;
            this.startTime = startTime;
            this.annotation = annotation == null ? "" : annotation;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        public String getLabel() {
            return processId + annotation;
        }
    }