
function generateProcessInputs() {
  const method = document.getElementById("inputMethod").value;
  const count = parseInt(document.getElementById("numProcesses").value);
  const container = document.getElementById("processInputs");
  container.innerHTML = "";
  for (let i = 0; i < count; i++) {
    const id = `P${i + 1}`;
    let arrival, burst;
    if (method === "random") {
      arrival = Math.floor(Math.random() * 5);
      burst = Math.floor(Math.random() * 8) + 1;
    }
    container.innerHTML += `
      <div class="process-group">
        <label>${id} - Arrival Time:</label>
        <input type="number" value="${arrival ?? ''}" class="arrival" />
        <label>${id} - Burst Time:</label>
        <input type="number" value="${burst ?? ''}" class="burst" />
      </div>
    `;
  }
}

document.getElementById("algorithm").addEventListener("change", function () {
  const algo = this.value;
  document.getElementById("quantumInput").style.display =
    algo === "rr" || algo === "mlfq" ? "block" : "none";
});

function submitData() {
  const arrivals = Array.from(document.querySelectorAll(".arrival")).map(input => parseInt(input.value));
  const bursts = Array.from(document.querySelectorAll(".burst")).map(input => parseInt(input.value));
  const algorithm = document.getElementById("algorithm").value;
  const quantum = parseInt(document.getElementById("quantum")?.value || 0);
  let quantums = [];
  if (algorithm === "mlfq") {
    // For demo, use 4 quantums, or prompt user for more inputs if needed
    quantums = [quantum || 2, quantum || 4, quantum || 8, quantum || 16];
  }
  const processes = arrivals.map((arrival, i) =>
    window.createProcess(`P${i + 1}`, arrival, bursts[i])
  );
  const result = window.runScheduler(processes, algorithm, quantum, quantums);
  const avg = window.getAverages(result.processes);
  // Build metrics table
  let metrics = `<table><tr><th>PID</th><th>Arrival</th><th>Burst</th><th>Completion</th><th>Turnaround</th><th>Response</th></tr>`;
  for (let p of result.processes) {
    metrics += `<tr><td>${p.id}</td><td>${p.arrivalTime}</td><td>${p.burstTime}</td><td>${p.completionTime}</td><td>${p.turnaroundTime}</td><td>${p.responseTime}</td></tr>`;
  }
  metrics += "</table>";
  // Build colored Gantt chart
  const colorMap = {};
  const colors = [
    '#5ecdf8', '#81dcff', '#f8b05e', '#f85e7a', '#a95ef8', '#5ef8a9', '#f85e5e', '#f8e25e', '#5ef8e2', '#e25ef8'
  ];
  let colorIdx = 0;
  for (let p of result.processes) {
    if (!colorMap[p.id]) {
      colorMap[p.id] = colors[colorIdx % colors.length];
      colorIdx++;
    }
  }
  // Parse gantt blocks
  let ganttHtml = '<div style="display:flex;align-items:center;">';
  for (let block of result.gantt) {
    // Extract process id and time range
    const match = block.match(/\|\s*(P\d+)[^\(]*\((\d+)-(\d+)[^\)]*\)/);
    if (match) {
      const pid = match[1];
      const start = match[2];
      const end = match[3];
      ganttHtml += `<div style="background:${colorMap[pid]};color:#0c1b3c;padding:8px 12px;margin-right:2px;border-radius:6px;min-width:60px;text-align:center;">
        ${pid}<br><span style='font-size:0.9em;'>${start}-${end}</span>
      </div>`;
    } else {
      ganttHtml += `<div style="padding:8px 12px;">${block}</div>`;
    }
  }
  ganttHtml += '<div style="padding:8px 12px;">|</div></div>';
  const outputDiv = document.getElementById("output");
  outputDiv.innerHTML = `
    <h3>Gantt Chart</h3>
    ${ganttHtml}
    <h3>Process Metrics</h3>
    ${metrics}
    <h3>Averages</h3>
    <p>Average Turnaround Time: ${avg.avgTurnaround.toFixed(2)}</p>
    <p>Average Response Time: ${avg.avgResponse.toFixed(2)}</p>
  `;
}
