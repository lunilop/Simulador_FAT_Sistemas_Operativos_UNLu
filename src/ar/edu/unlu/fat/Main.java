package ar.edu.unlu.fat;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimuladorFAT simulador = new SimuladorFAT();
            VentanaPrincipal ventana = new VentanaPrincipal(simulador);
            ventana.setVisible(true);
        });
    }
}
