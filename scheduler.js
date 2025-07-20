// --- CPU Scheduling Algorithms in JavaScript ---

// Helper: Process object factory
function createProcess(id, arrivalTime, burstTime) {
  return {
    id,
    arrivalTime: Number(arrivalTime),
    burstTime: Number(burstTime),
    completionTime: 0,
    turnaroundTime: 0,
    responseTime: 0,
    remainingTime: Number(burstTime),
    started: false
  };
}

// FCFS Scheduler
function fcfsScheduler(processes) {
  let time = 0;
  let gantt = [];
  let procs = processes.map(p => ({ ...p }));
  procs.sort((a, b) => a.arrivalTime - b.arrivalTime);
  for (let p of procs) {
    time = Math.max(time, p.arrivalTime);
    p.completionTime = time + p.burstTime;
    p.turnaroundTime = p.completionTime - p.arrivalTime;
    p.responseTime = time - p.arrivalTime;
    gantt.push(`| ${p.id} (${time}-${p.completionTime}) `);
    time = p.completionTime;
  }
  return { processes: procs, gantt };
}

function* fcfsSchedulerStep(processes) {
  let time = 0;
  let gantt = [];
  let procs = processes.map(p => ({ ...p }));
  procs.sort((a, b) => a.arrivalTime - b.arrivalTime);
  for (let p of procs) {
    time = Math.max(time, p.arrivalTime);
    p.completionTime = time + p.burstTime;
    p.turnaroundTime = p.completionTime - p.arrivalTime;
    p.responseTime = time - p.arrivalTime;
    gantt.push(`| ${p.id} (${time}-${p.completionTime}) `);
    time = p.completionTime;
    yield { processes: procs.map(x => ({ ...x })), gantt: [...gantt] };
  }
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
  let procs = processes.map(p => ({ ...p, remainingTime: p.burstTime }));
  let completed = 0;
  let lastProcess = null;

  while (completed < procs.length) {
    let available = procs.filter(p => p.arrivalTime <= time && p.remainingTime > 0);
    if (available.length > 0) {
      available.sort((a, b) => a.remainingTime - b.remainingTime);
      let current = available[0];
      if (current.remainingTime === current.burstTime) {
        current.responseTime = time - current.arrivalTime;
      }

      let start = time;
      time++;
      current.remainingTime--;

      if (lastProcess !== current.id) {
        gantt.push(`| ${current.id} (${start}-${time})`);
      } else {
        let last = gantt.pop();
        let match = last.match(/\|\s*(P\d+)\s*\((\d+)-(\d+)\)/);
        if (match && match[1] === current.id) {
          let newBlock = `| ${current.id} (${match[2]}-${time})`;
          gantt.push(newBlock);
        } else {
          gantt.push(last);
          gantt.push(`| ${current.id} (${start}-${time})`);
        }
      }

      if (current.remainingTime === 0) {
        current.completionTime = time;
        current.turnaroundTime = current.completionTime - current.arrivalTime;
        completed++;
      }

      lastProcess = current.id;
    } else {
      time++;
      lastProcess = null;
    }
  }

  return { processes: procs, gantt };
}

// Round Robin Scheduler
function rrScheduler(processes, quantum, contextSwitch = 0) {
  let time = 0;
  let gantt = [];
  let queue = [];
  let procs = processes.map(p => ({ ...p, remainingTime: p.burstTime, started: false }));
  let arrived = [];
  let completed = 0;
  let lastProcessId = null;

  while (completed < procs.length) {
    for (let p of procs) {
      if (p.arrivalTime <= time && !arrived.includes(p.id) && p.remainingTime > 0) {
        queue.push(p);
        arrived.push(p.id);
      }
    }

    if (queue.length === 0) {
      time++;
      lastProcessId = null;
      continue;
    }

    let p = queue.shift();
    if (!p.started) {
      p.responseTime = time - p.arrivalTime;
      p.started = true;
    }

    if (lastProcessId !== null && lastProcessId !== p.id && contextSwitch > 0) {
      gantt.push(`| CS (${time}-${time + contextSwitch}) `);
      time += contextSwitch;
    }

    let runTime = Math.min(quantum, p.remainingTime);
    gantt.push(`| ${p.id} (${time}-${time + runTime}) `);
    p.remainingTime -= runTime;
    time += runTime;

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

    lastProcessId = p.id;
  }
  return { processes: procs, gantt };
}

function mlfqScheduler(processes, quantums) {
  let time = 0;
  let gantt = [];
  let procs = processes.map(p => ({
    id: p.id,
    arrivalTime: Number(p.arrivalTime) || 0,
    burstTime: Number(p.burstTime) || 0,
    completionTime: 0,
    turnaroundTime: 0,
    responseTime: null,
    remainingTime: Number(p.burstTime) || 0,
    started: false
  }));

  let queues = [[], [], [], []];
  let completed = 0;
  let arrived = [];

  while (completed < procs.length) {
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

        let runTime = Math.min(Number(quantums[q]) || 1, p.remainingTime);
        let start = time;
        let end = time + runTime;

        gantt.push(`| ${p.id} (${start}-${end},Q${q})`);

        p.remainingTime -= runTime;
        time += runTime;

        for (let x of procs) {
          if (x.arrivalTime > start && x.arrivalTime <= time && !arrived.includes(x.id) && x.remainingTime > 0) {
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
          if (isNaN(p.responseTime)) {
            p.responseTime = p.turnaroundTime - p.burstTime;
          }
          completed++;
        }

        found = true;
        break;
      }
    }

    if (!found) {
      time++;
    }
  }

  // Post-processing: fix any lingering NaNs
  for (let p of procs) {
    p.completionTime = Number(p.completionTime) || time;
    p.turnaroundTime = Number(p.turnaroundTime) || (p.completionTime - p.arrivalTime);
    p.responseTime = Number(p.responseTime);
    if (isNaN(p.responseTime)) {
      p.responseTime = p.turnaroundTime - p.burstTime;
    }
  }

  return { processes: procs, gantt };
}


// --- Exported function to run selected algorithm ---
function runScheduler(processes, algorithm, quantum, quantums, allotment, contextSwitch) {
  switch (algorithm) {
    case "fcfs":
      return fcfsScheduler(processes);
    case "rr":
      return rrScheduler(processes, quantum, contextSwitch);
    case "mlfq":
      return mlfqScheduler(processes, quantums, contextSwitch, allotment);
    // ...other cases...
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

// --- Export for browser use ---
window.createProcess = createProcess;
window.runScheduler = runScheduler;
window.getAverages = getAverages;
window.runSchedulerStep = function(processes, algorithm, quantum, quantums) {
  switch (algorithm) {
    case "fcfs":
      return fcfsSchedulerStep(processes);
    // Add cases for other algorithms
    default:
      return fcfsSchedulerStep(processes);
  }
};