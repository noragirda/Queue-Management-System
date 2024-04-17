package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class SimulationGUI {
    private JFrame frame;
    private JTextField totalClientsField;
    private JTextField totalQueuesField;
    private JTextField simulationTimeField;
    private JTextField minArrivalTimeField;
    private JTextField maxArrivalTimeField;
    private JTextField minServiceTimeField;
    private JTextField maxServiceTimeField;
    private QueueDisplayGUI queueDisplayGUI;
    private JComboBox<String> strategyComboBox;
    private JTextArea queueDisplay;
    private SimulationManager simulationManager;
    private final Map<String, QueueAllocationStrategy> strategyMap;

    public SimulationGUI() {
        strategyMap = new HashMap<>();
        strategyMap.put("Shortest Queue", new ShortestQueueStrategy());
        strategyMap.put("Shortest Time", new ShortestTimeStrategy());
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Queue Simulation");
        frame.setSize(800, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(96, 170, 189));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(239, 191, 255));

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // 2 columns, variable rows
        inputPanel.setBackground(new Color(154, 228, 203));

        inputPanel.add(createLabel("Total Clients:"));
        totalClientsField = createTextField();
        inputPanel.add(totalClientsField);

        inputPanel.add(createLabel("Total Queues:"));
        totalQueuesField = createTextField();
        inputPanel.add(totalQueuesField);

        inputPanel.add(createLabel("Simulation Time:"));
        simulationTimeField = createTextField();
        inputPanel.add(simulationTimeField);

        inputPanel.add(createLabel("Minimal Arrival Time:"));
        minArrivalTimeField = createTextField();
        inputPanel.add(minArrivalTimeField);

        inputPanel.add(createLabel("Maximal Arrival Time:"));
        maxArrivalTimeField = createTextField();
        inputPanel.add(maxArrivalTimeField);

        inputPanel.add(createLabel("Minimal Service Time:"));
        minServiceTimeField = createTextField();
        inputPanel.add(minServiceTimeField);

        inputPanel.add(createLabel("Maximal Service Time:"));
        maxServiceTimeField = createTextField();
        inputPanel.add(maxServiceTimeField);

        inputPanel.add(createLabel("Strategy:"));
        strategyComboBox = new JComboBox<>(strategyMap.keySet().toArray(new String[0]));
        inputPanel.add(strategyComboBox);

        inputPanel.add(createLabel("      "));//added the spaces so that the button is on teh right side
        JButton startButton = new JButton("Start Simulation");
        styleButton(startButton);
        startButton.addActionListener(this::startSimulation);
        inputPanel.add(startButton);

        contentPanel.add(inputPanel);
        frame.add(contentPanel, BorderLayout.CENTER);

        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(10);
        textField.setBorder(BorderFactory.createLineBorder(new Color(150, 200, 250), 2));
        return textField;
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(100, 180, 220));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Tahoma", Font.BOLD, 12));
    }

    private void startSimulation(ActionEvent event) {
        try {
            int totalClients = Integer.parseInt(totalClientsField.getText());
            int totalQueues = Integer.parseInt(totalQueuesField.getText());
            int simulationTime = Integer.parseInt(simulationTimeField.getText());
            int minArrival = Integer.parseInt(minArrivalTimeField.getText());
            int maxArrival = Integer.parseInt(maxArrivalTimeField.getText());
            int minService = Integer.parseInt(minServiceTimeField.getText());
            int maxService = Integer.parseInt(maxServiceTimeField.getText());
            if (totalClients <= 0 || totalQueues <= 0 || simulationTime <= 0||minArrival<=0||maxArrival<=0||minService<=0||maxService<=0) {
                JOptionPane.showMessageDialog(frame, "Please enter positive values for all fields.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(minArrival>maxArrival||minService>maxService)
            {
                JOptionPane.showMessageDialog(frame, "Please enter valid values for arrival and service time.Minumum value should be less than maximum value.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String selectedStrategy = (String) strategyComboBox.getSelectedItem();
            QueueAllocationStrategy strategy = strategyMap.get(selectedStrategy);
            queueDisplayGUI= new QueueDisplayGUI();
            queueDisplayGUI.setVisible(true);
            simulationManager = new SimulationManager(
                    totalClients, totalQueues, simulationTime, strategy,
                    minArrival, maxArrival, minService, maxService, queueDisplayGUI);
            Thread simulationThread=new Thread(simulationManager);
            simulationThread.start();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter valid integers for all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimulationGUI::new);
    }
}
