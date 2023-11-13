package Agents;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class SedentaryLifestyleAgent {
    private JFrame frame;
    private JTextField patientNameField;
    private JLabel physicalActivitiesPerWeekLabel = new JLabel("Atividades Físicas por Semana:");
    private JTextField physicalActivitiesPerWeekField = new JTextField(20);
    private JTextArea resultArea;
    
    private final String group = "230.0.0.0";
    private final int port = 4444;

    public SedentaryLifestyleAgent() {
        frame = new JFrame("Agente de Sedentarismo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        JLabel nameLabel = new JLabel("Nome do Paciente:");
        patientNameField = new JTextField(20);

        inputPanel.add(nameLabel);
        inputPanel.add(patientNameField);
        inputPanel.add(physicalActivitiesPerWeekLabel);
        inputPanel.add(physicalActivitiesPerWeekField);

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
        int physicalActivitiesPerWeek = Integer.parseInt(physicalActivitiesPerWeekField.getText());
        double sedentaryLifestyleDegree = 0.0;
        
        if(physicalActivitiesPerWeek < 1)
            sedentaryLifestyleDegree = 1;
        else if(physicalActivitiesPerWeek < 2)
            sedentaryLifestyleDegree = 0.75;
        else if(physicalActivitiesPerWeek < 3)
            sedentaryLifestyleDegree = 0.50;
        else if(physicalActivitiesPerWeek < 4)
            sedentaryLifestyleDegree = 0.25;
        
        String result = "Paciente: " + patientName + "\n";
        result += "Grau de Evidência: " + sedentaryLifestyleDegree + "\n";

        resultArea.setText(result);

        // Envie ambos os graus de evidência por broadcast
        sendEvidenceByBroadcast(sedentaryLifestyleDegree);
    }

    private void startListeningForPressureInfo() {
        // Código de escuta para receber informações de outros agentes
        // ...
    }

    private void sendEvidenceByBroadcast(double sedentaryLifestyleDegree) {
        try {
            InetAddress group = InetAddress.getByName(this.group);
            MulticastSocket socket = new MulticastSocket(port);
            socket.joinGroup(group);
            String evidenceMessage = Double.toString(sedentaryLifestyleDegree);
            byte[] data = evidenceMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SedentaryLifestyleAgent());
    }
}

