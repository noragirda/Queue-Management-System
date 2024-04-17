package org.example;

import javax.swing.*;
import java.awt.*;

public class QueueDisplayGUI extends JFrame {
    private JTextArea queueDisplay;
    private JLabel averageWaitingTimeLabel;
    private JLabel averageServiceTimeLabel;
    private JLabel peakHourLabel;
    private JLabel timerLabel;

    public QueueDisplayGUI() {
        setTitle("Queue Status");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeGUI();
    }

    private void initializeGUI() {
        queueDisplay = new JTextArea();
        queueDisplay.setEditable(false);
        queueDisplay.setFont(new Font("Monospaced", Font.PLAIN, 12));
        queueDisplay.setBackground(new Color(154, 228, 203));

        JScrollPane scrollPane = new JScrollPane(queueDisplay);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Queue Status"));
        scrollPane.setBackground(new Color(239, 191, 255));

        JPanel statisticsPanel = new JPanel(new GridLayout(4, 1));
        statisticsPanel.setBackground(new Color(101, 180, 200));

        timerLabel = new JLabel("Time: 0s");
        setLabelBackground(timerLabel, new Color(239, 191, 255));

        averageWaitingTimeLabel = new JLabel("Average Waiting Time: Calculating...");
        averageServiceTimeLabel = new JLabel("Average Service Time: Calculating...");
        peakHourLabel = new JLabel("Peak Hour: Calculating...");

        setLabelBackground(averageWaitingTimeLabel, new Color(255, 218, 185));
        setLabelBackground(averageServiceTimeLabel, new Color(255, 233, 213));
        setLabelBackground(peakHourLabel, new Color(255, 248, 220));

        statisticsPanel.add(timerLabel);
        statisticsPanel.add(averageWaitingTimeLabel);
        statisticsPanel.add(averageServiceTimeLabel);
        statisticsPanel.add(peakHourLabel);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(statisticsPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }

    private void setLabelBackground(JLabel label, Color color) {
        label.setOpaque(true);
        label.setBackground(color);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        label.setFont(new Font("Arial", Font.BOLD, 14));
    }

    public void updateQueueDisplay(String displayText) {
        SwingUtilities.invokeLater(() -> queueDisplay.setText(displayText));
    }

    public void updateTimer(int currentTime) {
        SwingUtilities.invokeLater(() -> timerLabel.setText("Time: " + currentTime + "s"));
    }

    public void closeDisplay() {
        SwingUtilities.invokeLater(() -> {
            setVisible(false);
            dispose();
        });
    }

    public void displayStatistics(double averageWaitingTime, double averageServiceTime, int peakHour) {
        SwingUtilities.invokeLater(() -> {
            averageWaitingTimeLabel.setText("Average Waiting Time: " + String.format("%.2f", averageWaitingTime));
            averageServiceTimeLabel.setText("Average Service Time: " + String.format("%.2f", averageServiceTime));
            peakHourLabel.setText("Peak Hour: " + peakHour);
        });
    }
}
