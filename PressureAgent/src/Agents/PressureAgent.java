package Agents;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class PressureAgent {
    private JFrame frame;
    private JTextField patientNameField;
    private JTextField systolicPressureField;
    private JTextField diastolicPressureField;
    private JTextArea resultArea;
    
    private final String group = "230.0.0.0";
    private final int port = 4444;

    public PressureAgent() {
        frame = new JFrame("Agente de Pressão Arterial");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        JLabel nameLabel = new JLabel("Nome do Paciente:");
        patientNameField = new JTextField(20);
        JLabel systolicPressureLabel = new JLabel("Pressão Sistólica (mmHg):");
        systolicPressureField = new JTextField(20);
        JLabel diastolicPressureLabel = new JLabel("Pressão Diastólica (mmHg):");
        diastolicPressureField = new JTextField(20);

        inputPanel.add(nameLabel);
        inputPanel.add(patientNameField);
        inputPanel.add(systolicPressureLabel);
        inputPanel.add(systolicPressureField);
        inputPanel.add(diastolicPressureLabel);
        inputPanel.add(diastolicPressureField);

        JPanel buttonPanel = new JPanel();
        JButton calculateButton = new JButton("Calcular Evidência");
        calculateButton.addActionListener((ActionEvent e) -> {
            calculateEvidence();
        });

        JButton listenButton = new JButton("Iniciar Escuta");
        listenButton.addActionListener((ActionEvent e) -> {
            startListeningForPressureInfo();
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

    private void calculateEvidence() {
        String patientName = patientNameField.getText();
        int systolicPressure = Integer.parseInt(systolicPressureField.getText());
        int diastolicPressure = Integer.parseInt(diastolicPressureField.getText());

        double systolicEvidence = calculateSystolicEvidence(systolicPressure);
        double diastolicEvidence = calculateDiastolicEvidence(diastolicPressure);

        String result = "Paciente: " + patientName + "\n";
        result += "Grau de Evidência (Sistólica): " + systolicEvidence + "\n";
        result += "Grau de Evidência (Diastólica): " + diastolicEvidence;

        resultArea.setText(result);

        // Envie ambos os graus de evidência por broadcast
        sendEvidenceByBroadcast(systolicEvidence, diastolicEvidence);
    }

    private double calculateSystolicEvidence(int systolicPressure) {
        if (systolicPressure >= 120 && systolicPressure <= 140) {
            return (double) (systolicPressure - 120) / (140 - 120);
        } else if (systolicPressure > 140) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    private double calculateDiastolicEvidence(int diastolicPressure) {
        if (diastolicPressure >= 80 && diastolicPressure <= 90) {
            return (double) (diastolicPressure - 80) / (90 - 80);
        } else if (diastolicPressure > 90) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    private void startListeningForPressureInfo() {
        // Código de escuta para receber informações de outros agentes
        // ...
    }

    private void sendEvidenceByBroadcast(double systolicEvidence, double diastolicEvidence) {
        try {
            InetAddress group = InetAddress.getByName(this.group);
            MulticastSocket socket = new MulticastSocket(port);
            socket.joinGroup(group);
            String evidenceMessage = systolicEvidence + "," + diastolicEvidence;
            byte[] data = evidenceMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PressureAgent());
    }
}

