package Agents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class IMCAgent {
    private JFrame frame;
    private JTextField patientNameField;
    private JTextField weightField;
    private JTextField heightField;
    private JTextArea resultArea;
    
    private final String group = "230.0.0.0";
    private final int port = 4444;

    public IMCAgent() {
        frame = new JFrame("Agente IMC");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        JLabel nameLabel = new JLabel("Nome do Paciente:");
        patientNameField = new JTextField(20);
        JLabel weightLabel = new JLabel("Peso (kg):");
        weightField = new JTextField(20);
        JLabel heightLabel = new JLabel("Altura (m):");
        heightField = new JTextField(20);

        inputPanel.add(nameLabel);
        inputPanel.add(patientNameField);
        inputPanel.add(weightLabel);
        inputPanel.add(weightField);
        inputPanel.add(heightLabel);
        inputPanel.add(heightField);

        JPanel buttonPanel = new JPanel();
        JButton calculateButton = new JButton("Calcular IMC");
        calculateButton.addActionListener((ActionEvent e) -> {
            calculateIMC();
        });

        JButton listenButton = new JButton("Iniciar Escuta");
        listenButton.addActionListener((ActionEvent e) -> {
            startListeningForPatientInfo();
        });

        buttonPanel.add(calculateButton);
        buttonPanel.add(listenButton);

        resultArea = new JTextArea(6, 20);
        resultArea.setEditable(false);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(resultArea, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void calculateIMC() {
        String patientName = patientNameField.getText();
        double weight = Double.parseDouble(weightField.getText());
        double height = Double.parseDouble(heightField.getText());
        double imc = weight / (height * height);

        String result = "Paciente: " + patientName + "\nIMC: " + imc;

        double evidence = calculateEvidence(imc);
        result += "\nGrau de Evidência: " + evidence;

        resultArea.setText(result);

        // Envie o grau de evidência por broadcast
        sendEvidenceByBroadcast(evidence);
    }

    private double calculateEvidence(double imc) {
        if (imc >= 25 && imc <= 40) {
            return (imc - 25) / (40 - 25);
        } else if (imc > 40) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    private void sendEvidenceByBroadcast(double evidence) {
        try {
            InetAddress group = InetAddress.getByName(this.group);
            MulticastSocket socket = new MulticastSocket(port);
            socket.joinGroup(group);
            String evidenceMessage = String.valueOf(evidence);
            byte[] data = evidenceMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void startListeningForPatientInfo() {
        new Thread(() -> {
            try {
                InetAddress group = InetAddress.getByName(this.group);
                MulticastSocket socket = new MulticastSocket(port);
                socket.joinGroup(group);
                
                byte[] data = new byte[1024];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    socket.receive(packet);
                    String info = new String(packet.getData(), 0, packet.getLength());

                    // Processar as informações do paciente recebidas por broadcast
                    System.out.println(info);
                    String[] parts = info.split(",");
                    String patientName = parts[0];
                    double weight = Double.parseDouble(parts[1]);
                    double height = Double.parseDouble(parts[2]);
                    double imc = weight / (height * height);

                    String result = "Paciente: " + patientName + "\nIMC: " + imc;

                    double evidence = calculateEvidence(imc);
                    result += "\nGrau de Evidência: " + evidence;

                    resultArea.setText(result);
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IMCAgent());
    }
}

