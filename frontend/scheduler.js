// --- CPU Scheduling Algorithms in JavaScript ---

// Helper: Process object factory
function createProcess(id, arrivalTime, burstTime) {
  return {
    id,
    arrivalTime,
    burstTime,
    completionTime: 0,
    turnaroundTime: 0,
    responseTime: 0,
    remainingTime: burstTime,
    started: false
  };
}

// FCFS Scheduler
function fcfsScheduler(processes) {
  let time = 0;
  let gantt = [];
  processes.sort((a, b) => a.arrivalTime - b.arrivalTime);
  for (let p of processes) {
    time = Math.max(time, p.arrivalTime);
    p.completionTime = time + p.burstTime;
    p.turnaroundTime = p.completionTime - p.arrivalTime;
    p.responseTime = time - p.arrivalTime;
    gantt.push(`| ${p.id} (${time}-${p.completionTime}) `);
    time = p.completionTime;
  }
  return { processes, gantt };
}

// SJF Scheduler (non-preemptive)
function sjfScheduler(processes) {
  let time = 0;
  let gantt = [];
  let ready = [];
  let done = [];
  let procs = processes.map(p => ({ ...p }));
  while (done.length < procs.length) {
    ready = procs.filter(p => !p.completionTime && p.arrivalTime <= time);
    if (ready.length === 0) {
      time++;
      continue;
    }
    ready.sort((a, b) => a.burstTime - b.burstTime);
    let p = ready[0];
    p.responseTime = time - p.arrivalTime;
    p.completionTime = time + p.burstTime;
    p.turnaroundTime = p.completionTime - p.arrivalTime;
    gantt.push(`| ${p.id} (${time}-${p.completionTime}) `);
    time = p.completionTime;
    done.push(p);
  }
  return { processes: done, gantt };
}

// SRTF Scheduler (preemptive SJF)
function srtfScheduler(processes) {
  let time = 0;
  let gantt = [];
  let procs = processes.map(p => ({ ...p }));
  let lastId = null;
  let completed = 0;
  while (completed < procs.length) {
    let ready = procs.filter(p => p.arrivalTime <= time && p.remainingTime > 0);
    if (ready.length === 0) {
      time++;
      lastId = null;
      continue;
    }
    ready.sort((a, b) => a.remainingTime - b.remainingTime);
    let p = ready[0];
    if (!p.started) {
      p.responseTime = time - p.arrivalTime;
      p.started = true;
    }
    if (lastId !== p.id) {
      gantt.push(`| ${p.id} (${time}-`);
      lastId = p.id;
    }
    p.remainingTime--;
    time++;
    if (p.remainingTime === 0) {
      p.completionTime = time;
      p.turnaroundTime = p.completionTime - p.arrivalTime;
      completed++;
      gantt[gantt.length - 1] += `${time}) `;
    }
  }
  return { processes: procs, gantt };
}

// Round Robin Scheduler
function rrScheduler(processes, quantum) {
  let time = 0;
  let gantt = [];
  let queue = [];
  let procs = processes.map(p => ({ ...p }));
  let arrived = [];
  let completed = 0;
  while (completed < procs.length) {
    // Add newly arrived processes
    for (let p of procs) {
      if (p.arrivalTime <= time && !arrived.includes(p.id) && p.remainingTime > 0) {
        queue.push(p);
        arrived.push(p.id);
      }
    }
    if (queue.length === 0) {
      time++;
      continue;
    }
    let p = queue.shift();
    if (!p.started) {
      p.responseTime = time - p.arrivalTime;
      p.started = true;
    }
    let runTime = Math.min(quantum, p.remainingTime);
    gantt.push(`| ${p.id} (${time}-${time + runTime}) `);
    p.remainingTime -= runTime;
    time += runTime;
    // Add newly arrived processes during this quantum
    for (let q of procs) {
      if (q.arrivalTime > time - runTime && q.arrivalTime <= time && !arrived.includes(q.id) && q.remainingTime > 0) {
        queue.push(q);
        arrived.push(q.id);
      }
    }
    if (p.remainingTime > 0) {
      queue.push(p);
    } else {
      p.completionTime = time;
      p.turnaroundTime = p.completionTime - p.arrivalTime;
      completed++;
    }
  }
  return { processes: procs, gantt };
}

// MLFQ Scheduler (simple 4-level)
function mlfqScheduler(processes, quantums) {
  let time = 0;
  let gantt = [];
  let procs = processes.map(p => ({ ...p }));
  let queues = [[], [], [], []];
  let completed = 0;
  let arrived = [];
  while (completed < procs.length) {
    // Add newly arrived processes to Q0
    for (let p of procs) {
      if (p.arrivalTime <= time && !arrived.includes(p.id) && p.remainingTime > 0) {
        queues[0].push(p);
        arrived.push(p.id);
      }
    }
    let found = false;
    for (let q = 0; q < 4; q++) {
      if (queues[q].length > 0) {
        let p = queues[q].shift();
        if (!p.started) {
          p.responseTime = time - p.arrivalTime;
          p.started = true;
        }
        let runTime = Math.min(quantums[q], p.remainingTime);
        gantt.push(`| ${p.id} (${time}-${time + runTime})[Q${q}] `);
        p.remainingTime -= runTime;
        time += runTime;
        // Add newly arrived processes during this quantum
        for (let x of procs) {
          if (x.arrivalTime > time - runTime && x.arrivalTime <= time && !arrived.includes(x.id) && x.remainingTime > 0) {
            queues[0].push(x);
            arrived.push(x.id);
          }
        }
        if (p.remainingTime > 0) {
          if (q < 3) queues[q + 1].push(p);
          else queues[3].push(p);
        } else {
          p.completionTime = time;
          p.turnaroundTime = p.completionTime - p.arrivalTime;
          completed++;
        }
        found = true;
        break;
      }
    }
    if (!found) time++;
  }
  return { processes: procs, gantt };
}

// --- Exported function to run selected algorithm ---
function runScheduler(processes, algorithm, quantum, quantums) {
  switch (algorithm) {
    case "fcfs":
      return fcfsScheduler(processes);
    case "sjf":
      return sjfScheduler(processes);
    case "srtf":
      return srtfScheduler(processes);
    case "rr":
      return rrScheduler(processes, quantum);
    case "mlfq":
      return mlfqScheduler(processes, quantums);
    default:
      return fcfsScheduler(processes);
  }
}

// --- Helper for averages ---
function getAverages(processes) {
  const n = processes.length;
  const avgTurnaround = processes.reduce((a, p) => a + p.turnaroundTime, 0) / n;
  const avgResponse = processes.reduce((a, p) => a + p.responseTime, 0) / n;
  return { avgTurnaround, avgResponse };
}

// --- Export for use in script.js ---
window.createProcess = createProcess;
window.runScheduler = runScheduler;
window.getAverages = getAverages;
