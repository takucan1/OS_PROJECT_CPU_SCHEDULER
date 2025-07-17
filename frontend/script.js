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

  const outputDiv = document.getElementById("output");
  let time = 0;
  let gantt = "";

  let metrics = `<table><tr><th>PID</th><th>Arrival</th><th>Burst</th><th>Completion</th></tr>`;

  for (let i = 0; i < arrivals.length; i++) {
    time = Math.max(time, arrivals[i]);
    const completion = time + bursts[i];
    gantt += `| P${i + 1} (${time}-${completion}) `;
    time = completion;
    metrics += `<tr><td>P${i + 1}</td><td>${arrivals[i]}</td><td>${bursts[i]}</td><td>${completion}</td></tr>`;
  }

  metrics += "</table>";

  outputDiv.innerHTML = `
    <h3>Gantt Chart</h3>
    <p>${gantt}|</p>
    <h3>Process Metrics</h3>
    ${metrics}
  `;
}
