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
  const quantumInput = document.getElementById("quantumInput");
  const mlfqExtra = document.getElementById("mlfqExtra");

  if (algo === "rr" || algo === "mlfq") {
    quantumInput.style.display = "block";
  } else {
    quantumInput.style.display = "none";
  }

  if (algo === "mlfq") {
    mlfqExtra.style.display = "block";
  } else {
    mlfqExtra.style.display = "none";
  }
});

function submitData() {
  const arrivals = Array.from(document.querySelectorAll(".arrival")).map(input => parseInt(input.value));
  const bursts = Array.from(document.querySelectorAll(".burst")).map(input => parseInt(input.value));
  const algorithm = document.getElementById("algorithm").value;
  const quantum = parseInt(document.getElementById("quantum")?.value || 0);
  const allotment = parseInt(document.getElementById("allotment")?.value || 0);
  const contextSwitch = parseInt(document.getElementById("contextSwitch")?.value || 0);
  let quantums = [];

  if (algorithm === "mlfq") {
    quantums = [quantum || 2, (quantum || 2) * 2, (quantum || 2) * 4];
  }

  const processes = arrivals.map((arrival, i) =>
    window.createProcess(`P${i + 1}`, arrival, bursts[i])
  );

  // Direct execution (not step-by-step)
  const result = window.runScheduler(processes, algorithm, quantum, quantums, allotment, contextSwitch);

  // Color mapping for Gantt chart
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

  let ganttHtml = '<div style="display:flex;align-items:center;">';
  for (let block of result.gantt) {
    const match = block.match(/\|\s*(P\d+)(?:\s*\((\d+)-(\d+)(?:,Q(\d+))?\))?/);

    if (match) {
      const pid = match[1];
      const start = match[2];
      const end = match[3];
      const queue = match[4];

      ganttHtml += `<div style="background:${colorMap[pid]};color:#0c1b3c;padding:8px 12px;margin-right:2px;border-radius:6px;min-width:60px;text-align:center;">
        ${pid}${start && end ? `<br><span style='font-size:0.9em;'>${start}-${end}${queue !== undefined ? `<br>Q${queue}` : ''}</span>` : ''}
      </div>`;
    } else {
      ganttHtml += `<div style="padding:8px 12px;">${block}</div>`;
    }
  }
  ganttHtml += '<div style="padding:8px 12px;">|</div></div>';

  document.getElementById("output").innerHTML = `
    <h3>Gantt Chart</h3>
    ${ganttHtml}
    <h3>Process Metrics</h3>
    <table><tr><th>PID</th><th>Arrival</th><th>Burst</th><th>Completion</th><th>Turnaround</th><th>Response</th></tr>
      ${result.processes.map(p => `<tr><td>${p.id}</td><td>${p.arrivalTime}</td><td>${p.burstTime}</td><td>${p.completionTime}</td><td>${p.turnaroundTime}</td><td>${p.responseTime}</td></tr>`).join('')}
    </table>
  `;
  addLiveUpdate("Scheduler finished. Results updated.");
}

function fullReset() {
  document.getElementById("processInputs").innerHTML = "";
  document.getElementById("numProcesses").value = "";
  document.getElementById("inputMethod").value = "manual";
  document.getElementById("algorithm").value = "fcfs";
  document.getElementById("quantumInput").style.display = "none";
  document.getElementById("mlfqExtra").style.display = "none";
  document.getElementById("quantum").value = "";
  document.getElementById("allotment").value = "";
  document.getElementById("output").innerHTML = "<h2>Results</h2>";
}

// Live chat functions remain unchanged
function sendLiveChat() {
  const input = document.getElementById("liveChatInput");
  const box = document.getElementById("liveChatBox");
  const msg = input.value.trim();
  if (msg) {
    const time = new Date().toLocaleTimeString();
    box.innerHTML += `<div><b>You</b> <span style="color:#888;font-size:0.9em;">${time}</span>: ${msg}</div>`;
    input.value = "";
    box.scrollTop = box.scrollHeight;
  }
}

function addLiveUpdate(message) {
  const box = document.getElementById("liveChatBox");
  const time = new Date().toLocaleTimeString();
  box.innerHTML += `<div><b>System</b> <span style="color:#888;font-size:0.9em;">${time}</span>: ${message}</div>`;
  box.scrollTop = box.scrollHeight;
}

function exportResults() {
  const outputDiv = document.getElementById("output");
  const html = outputDiv.innerHTML;
  const tempDiv = document.createElement("div");
  tempDiv.innerHTML = html;
  let text = "";

  // Extract Gantt Chart
  const gantt = tempDiv.querySelector("div[style*='display:flex']");
  if (gantt) {
    text += "Gantt Chart:\n";
    text += gantt.innerText + "\n\n";
  }

  // Extract Process Metrics Table
  const table = tempDiv.querySelector("table");
  if (table) {
    text += "Process Metrics:\n";
    for (const row of table.rows) {
      text += Array.from(row.cells).map(cell => cell.innerText).join("\t") + "\n";
    }
    text += "\n";
  }

  // Download as text file
  const blob = new Blob([text], { type: "text/plain" });
  const a = document.createElement("a");
  a.href = URL.createObjectURL(blob);
  a.download = "cpu_scheduler_results.txt";
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
}