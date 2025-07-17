# CPU Scheduler Simulation (Web Version)

## Project Overview
This project is a web-based simulation of classic CPU scheduling algorithms. It allows users to input process data, select a scheduling algorithm, and visualize the results using a modern, interactive GUI. Supported algorithms include FCFS, SJF, SRTF, Round Robin, and MLFQ.

## How to Run the Simulation
1. **Open the App:**
   - Open `frontend/index.html` in any modern web browser (Chrome, Edge, Firefox, etc.).
2. **Input Data:**
   - Select input method (Manual or Random).
   - Enter the number of processes and click "Generate".
   - Fill in arrival and burst times for each process (or use random values).
3. **Choose Algorithm:**
   - Select a scheduling algorithm from the dropdown.
   - For Round Robin and MLFQ, enter the time quantum.
4. **Run Simulation:**
   - Click "Run Scheduler" to view the Gantt chart and process metrics.

## Scheduling Algorithms Implemented
- **FCFS (First-Come, First-Served):**
  - Processes are scheduled in the order they arrive.
- **SJF (Shortest Job First):**
  - Non-preemptive; the process with the shortest burst time is scheduled next.
- **SRTF (Shortest Remaining Time First):**
  - Preemptive SJF; always runs the process with the shortest remaining burst time.
- **Round Robin:**
  - Each process gets a fixed time quantum in a cyclic order.
- **MLFQ (Multi-Level Feedback Queue):**
  - Processes are scheduled using multiple queues with different time quantums, allowing dynamic priority changes.

## Sample Input & Expected Output
**Sample Input:**
- Processes: 3
- Arrival Times: 0, 2, 4
- Burst Times: 5, 3, 1
- Algorithm: FCFS

**Expected Output:**
- Gantt Chart: Colored blocks for P1 (0-5), P2 (5-8), P3 (8-9)
- Metrics Table:
  | PID | Arrival | Burst | Completion | Turnaround | Response |
  |-----|---------|-------|------------|------------|----------|
  | P1  |   0     |   5   |     5      |     5      |    0     |
  | P2  |   2     |   3   |     8      |     6      |    3     |
  | P3  |   4     |   1   |     9      |     5      |    4     |
- Average Turnaround Time: 5.33
- Average Response Time: 2.33

## Known Bugs, Limitations, or Incomplete Features
- MLFQ quantum values are currently set to default; user input for all levels is not yet supported.
- No persistent storage; all data is lost on page reload.
- No export or print functionality for results.
- Screenshots folder is not included by default; add your own for documentation.
- Only supports up to 10 unique process colors in the Gantt chart.


## Group contribution
- Aledon - Index.html, style, FCFS, RoundRobin, scheduler, script.
- Tipactipac - Scheduler, script, SJF,SRTF, MLFQ.
---
For questions, bug reports, or suggestions, please contact Takucan (2025).
