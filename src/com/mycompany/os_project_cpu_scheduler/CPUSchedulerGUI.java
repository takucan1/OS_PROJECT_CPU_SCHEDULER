package com.mycompany.os_project_cpu_scheduler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;

public class CPUSchedulerGUI extends JFrame {
    private JComboBox<String> inputMethodCombo;
    private JTextField numProcessesField;
    private JTable processTable;
    private JComboBox<String> algorithmCombo;
    private JTextField quantumField;
    private JButton runButton;
    private JTextArea ganttTextArea;
    private JTable resultTable;
    private JLabel avgTATLabel;
    private JLabel avgRTLabel;

    public CPUSchedulerGUI() {
        setTitle("CPU Scheduler Visualizer");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel inputLabel = new JLabel("Input Method:");
        inputLabel.setBounds(20, 20, 100, 25);
        add(inputLabel);

        inputMethodCombo = new JComboBox<>(new String[]{"Manual", "Random"});
        inputMethodCombo.setBounds(120, 20, 100, 25);
        add(inputMethodCombo);

        JLabel numLabel = new JLabel("# Processes:");
        numLabel.setBounds(240, 20, 100, 25);
        add(numLabel);

        numProcessesField = new JTextField("5");
        numProcessesField.setBounds(320, 20, 50, 25);
        add(numProcessesField);

        JLabel algoLabel = new JLabel("Algorithm:");
        algoLabel.setBounds(400, 20, 80, 25);
        add(algoLabel);

        algorithmCombo = new JComboBox<>(new String[]{"FCFS", "SJF", "SRTF", "Round Robin", "MLFQ"});
        algorithmCombo.setBounds(480, 20, 120, 25);
        add(algorithmCombo);

        JLabel quantumLabel = new JLabel("Quantum(s):");
        quantumLabel.setBounds(620, 20, 80, 25);
        add(quantumLabel);

        quantumField = new JTextField("2,4,8,999");
        quantumField.setBounds(700, 20, 80, 25);
        add(quantumField);

        processTable = new JTable(new DefaultTableModel(new Object[]{"PID", "Arrival", "Burst"}, 5));
        JScrollPane tableScroll = new JScrollPane(processTable);
        tableScroll.setBounds(20, 60, 350, 120);
        add(tableScroll);

        runButton = new JButton("Run Scheduler");
        runButton.setBounds(400, 100, 160, 30);
        add(runButton);

        ganttTextArea = new JTextArea();
        ganttTextArea.setEditable(false);
        JScrollPane ganttScroll = new JScrollPane(ganttTextArea);
        ganttScroll.setBounds(20, 200, 760, 60);
        add(ganttScroll);

        resultTable = new JTable(new DefaultTableModel(new Object[]{"PID", "Arrival", "Burst", "Completion", "TAT", "RT"}, 0));
        JScrollPane resultScroll = new JScrollPane(resultTable);
        resultScroll.setBounds(20, 270, 760, 200);
        add(resultScroll);

        avgTATLabel = new JLabel("Avg TAT: ");
        avgTATLabel.setBounds(20, 480, 200, 25);
        add(avgTATLabel);

        avgRTLabel = new JLabel("Avg RT: ");
        avgRTLabel.setBounds(250, 480, 200, 25);
        add(avgRTLabel);

        runButton.addActionListener(e -> runScheduler());
    }

    private void runScheduler() {
        int num;
        try {
            num = Integer.parseInt(numProcessesField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number of processes.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) processTable.getModel();
        model.setRowCount(num); // ensure correct number of rows

        List<Process> processes = new ArrayList<>();

        if (inputMethodCombo.getSelectedItem().equals("Manual")) {
            for (int i = 0; i < num; i++) {
                Object pidObj = model.getValueAt(i, 0);
                Object atObj = model.getValueAt(i, 1);
                Object btObj = model.getValueAt(i, 2);

                if (pidObj == null || atObj == null || btObj == null ||
                    pidObj.toString().trim().isEmpty() ||
                    atObj.toString().trim().isEmpty() ||
                    btObj.toString().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All process fields must be filled in.");
                    return;
                }

                try {
                    String pid = pidObj.toString().trim();
                    int at = Integer.parseInt(atObj.toString().trim());
                    int bt = Integer.parseInt(btObj.toString().trim());
                    processes.add(new Process(pid, at, bt));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Arrival/Burst must be integers.");
                    return;
                }
            }
        } else {
            Random rand = new Random();
            processes.clear();
            for (int i = 0; i < num; i++) {
                String pid = "P" + (i + 1);
                int at = rand.nextInt(5);
                int bt = rand.nextInt(8) + 1;
                processes.add(new Process(pid, at, bt));

                model.setValueAt(pid, i, 0);
                model.setValueAt(at, i, 1);
                model.setValueAt(bt, i, 2);
            }
        }

        // Choose algorithm
        Scheduler scheduler;
        String selected = algorithmCombo.getSelectedItem().toString();

        try {
            switch (selected) {
                case "FCFS":
                    scheduler = new FCFS();
                    break;
                case "SJF":
                    scheduler = new SJF();
                    break;
                case "SRTF":
                    scheduler = new SRTF();
                    break;
                case "Round Robin":
                    int q = Integer.parseInt(quantumField.getText().trim());
                    scheduler = new RoundRobin(q);
                    break;
                case "MLFQ":
                    int[] qArr = Arrays.stream(quantumField.getText().trim().split(","))
                            .mapToInt(Integer::parseInt)
                            .toArray();
                    scheduler = new MLFQ(qArr);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Invalid scheduling algorithm.");
                    return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid quantum input.");
            return;
        }

        // Run and display
        scheduler.schedule(processes);
        ganttTextArea.setText(String.join("", scheduler.getGanttChart()) + "|");

        DefaultTableModel resultModel = (DefaultTableModel) resultTable.getModel();
        resultModel.setRowCount(0);
        for (Process p : processes) {
            resultModel.addRow(new Object[]{
                    p.id, p.arrivalTime, p.burstTime,
                    p.completionTime, p.turnaroundTime, p.responseTime
            });
        }

        avgTATLabel.setText("Avg TAT: " + scheduler.getAverageTurnaroundTime(processes));
        avgRTLabel.setText("Avg RT: " + scheduler.getAverageResponseTime(processes));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CPUSchedulerGUI().setVisible(true));
    }
}
