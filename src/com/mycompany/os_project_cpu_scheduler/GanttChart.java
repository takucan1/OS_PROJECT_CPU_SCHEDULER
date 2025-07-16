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
    
private List<Interval> intervals = new ArrayList<>();

    // Add execution at current time for a process (called per time unit)
    // This method merges consecutive time units of the same process
    public void addExecution(String processId, int time, String annotation) {
        if (intervals.isEmpty()) {
            intervals.add(new Interval(processId, time, annotation));
        } else {
            Interval last = intervals.get(intervals.size() - 1);
            if (last.processId.equals(processId) && last.annotation.equals(annotation)) {
                // Extend current interval
                last.setEndTime(time + 1);
            } else {
                // Close previous interval endTime if not set
                if (last.endTime == 0) last.setEndTime(time);
                // Start new interval
                intervals.add(new Interval(processId, time, annotation));
            }
        }
    }

    // Call this after all executions to close the last interval
    public void closeLastInterval(int endTime) {
        if (!intervals.isEmpty()) {
            Interval last = intervals.get(intervals.size() - 1);
            if (last.endTime == 0) {
                last.setEndTime(endTime);
            }
        }
    }
    
// Print the Gantt chart in console
    public void printChart() {
        // First line: process blocks
        StringBuilder line1 = new StringBuilder();
        // Second line: time markers
        StringBuilder line2 = new StringBuilder();

        for (Interval interval : intervals) {
            String label = interval.getLabel();
            int length = interval.endTime - interval.startTime;

            // Width of block: proportional to length, at least length 2 for visibility
            int blockWidth = Math.max(length * 2, label.length() + 2);

            // Center the label in the block
            int padding = blockWidth - label.length();
            int padLeft = padding / 2;
            int padRight = padding - padLeft;

            // Build block with spaces and label
            line1.append("|");
            for (int i = 0; i < padLeft; i++) line1.append(" ");
            line1.append(label);
            for (int i = 0; i < padRight; i++) line1.append(" ");

            // Append end separator for last block
            // We'll add '|' after all blocks
            //line1.append("|");

            // Time line: print start time, then spaces
            String startTimeStr = String.valueOf(interval.startTime);
            line2.append(startTimeStr);
            // Add spaces to align next time
            int spaceCount = blockWidth - startTimeStr.length();
            for (int i = 0; i < spaceCount; i++) line2.append(" ");
        }
        // Append last end time
        if (!intervals.isEmpty()) {
            Interval last = intervals.get(intervals.size() - 1);
            line2.append(last.endTime);
            line1.append("|");
        }

        System.out.println(line1.toString());
        System.out.println(line2.toString());
    }
}