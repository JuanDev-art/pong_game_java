package menu_principal;

import pong_game.PongPanel;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JPanel {

    public MenuPrincipal(JFrame window) {
        setLayout(new GridLayout(3, 1));

        JButton pongButton = new JButton("Jugar a Space Pong");
        pongButton.addActionListener(e -> {

            //Lanzamos el Space Pong.
            PongPanel panel = new PongPanel(window);
            window.setContentPane(panel);
            window.revalidate();
            window.repaint();
            window.pack();
            window.setLocationRelativeTo(null);
            panel.requestFocusInWindow();

            //Iniciamos el hilo del juego.
            Thread gameThread = new Thread(panel);
            gameThread.start();

        });

        JButton otherButton = new JButton("Otro juego (próximamente)");
        otherButton.setEnabled(false); //Implementado más adelante.

        JButton exit = new JButton("Salir");
        exit.addActionListener(e -> System.exit(0));

        add(pongButton);
        add(otherButton);
        add(exit);

    }
}
