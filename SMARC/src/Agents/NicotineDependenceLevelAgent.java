import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NicotineDependenceLevelAgent extends JFrame {
    private JComboBox<String> pergunta1;
    private JComboBox<String> pergunta2;
    private JComboBox<String> pergunta3;
    private JComboBox<String> pergunta4;
    private JComboBox<String> pergunta5;
    private JComboBox<String> pergunta6;

    private int contadorPontos;
    private double grauEvidencia;

    public NicotineDependenceLevelAgent() {
        setTitle("Teste de Fagerström");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 2));

        pergunta1 = criarComboBox(new String[]{"Após 60 minutos", "Entre 31 e 60 minutos", "Entre 6 e 30 minutos", "Dentro de 5 minutos"});
        pergunta2 = criarComboBox(new String[]{"Não", "Sim"});
        pergunta3 = criarComboBox(new String[]{ "Outros além do primeiro da manhã", "O primeiro da manhã"});
        pergunta4 = criarComboBox(new String[]{"Menos de 10", "De 11 a 20", "De 21 a 30", "Mais de 30"});
        pergunta5 = criarComboBox(new String[]{"Não", "Sim"});
        pergunta6 = criarComboBox(new String[]{"Não", "Sim"});

        adicionarPergunta("Quanto tempo depois de acordar você fuma o primeiro cigarro?", pergunta1);
        adicionarPergunta("Você acha difícil não fumar em lugares proibidos (por exemplo, igreja, teatro, biblioteca)?", pergunta2);
        adicionarPergunta("Que cigarro do dia é o mais importante para você?", pergunta3);
        adicionarPergunta("Quantos cigarros você fuma por dia?", pergunta4);
        adicionarPergunta("Você fuma com mais frequência pela manhã?", pergunta5);
        adicionarPergunta("Você fuma mesmo doente o suficiente para estar na cama o dia todo?", pergunta6);

        JButton calcularButton = new JButton("Calcular");
        calcularButton.addActionListener((ActionEvent e) -> {
            calcularPontuacao();
        });

        add(calcularButton);
        setVisible(true);
    }

    private JComboBox<String> criarComboBox(String[] opcoes) {
        JComboBox<String> comboBox = new JComboBox<>(opcoes);
        return comboBox;
    }

    private void adicionarPergunta(String pergunta, JComboBox<String> comboBox) {
        add(new JLabel(pergunta));
        add(comboBox);
    }

    private void calcularPontuacao() {
        contadorPontos = 0;
        contadorPontos += pergunta1.getSelectedIndex();
        contadorPontos += pergunta2.getSelectedIndex();
        contadorPontos += pergunta3.getSelectedIndex();
        contadorPontos += pergunta4.getSelectedIndex();
        contadorPontos += pergunta5.getSelectedIndex();
        contadorPontos += pergunta6.getSelectedIndex();
        
        grauEvidencia = contadorPontos < 3 ? 0 : contadorPontos < 5 ? 0.25 : contadorPontos < 8 ? 0.5 : contadorPontos < 10 ? 0.75 : 1;

        exibirResultado();
    }

    private void exibirResultado() {
        JOptionPane.showMessageDialog(this, "Pontos obtivos no teste de Fangerstorm: " + contadorPontos + "\nGrau de Evidência Favorável: " + grauEvidencia, "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new NicotineDependenceLevelAgent();
        });
    }
}
