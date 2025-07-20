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

## Sample Screen Shots

FCFS
<img width="744" height="763" alt="image" src="https://github.com/user-attachments/assets/d35a75d7-5700-49bc-8e99-b9894dd1fd80" />
<img width="732" height="748" alt="image" src="https://github.com/user-attachments/assets/f9efc648-afa8-4f22-a5e6-41791f93002a" />

=============================================================================


SJF
<img width="735" height="771" alt="image" src="https://github.com/user-attachments/assets/1f0572a3-a377-48ca-b8c3-afefd4e07a21" />
<img width="733" height="734" alt="image" src="https://github.com/user-attachments/assets/d2950fa7-3a9c-4639-bad5-c0230c03a597" />


=============================================================================



SRTF
<img width="733" height="772" alt="image" src="https://github.com/user-attachments/assets/63fbd47f-cedb-4a07-a137-f50c53288a51" />
<img width="730" height="755" alt="image" src="https://github.com/user-attachments/assets/787fb08d-d565-4153-8785-988ef71932dc" />



=============================================================================




MLFQ
<img width="733" height="781" alt="image" src="https://github.com/user-attachments/assets/e32b4cef-f894-436f-81b8-587ff46f8364" />
<img width="1159" height="860" alt="image" src="https://github.com/user-attachments/assets/1268de8d-2344-406c-984e-49d87fbba02b" />


=============================================================================



## Export Results into Files
<img width="337" height="118" alt="image" src="https://github.com/user-attachments/assets/0ea71d13-e484-4e21-be30-8305493c9e16" />


=============================================================================



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
