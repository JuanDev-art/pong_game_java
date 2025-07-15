package pong_game;
import menu_principal.MenuPrincipal;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {

        //Creo la ventana principal.
        JFrame window = new JFrame("Colection of games");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);
        window.setResizable(false);
        window.setLocationRelativeTo(null);

        //Muestro el menú principal.
        MenuPrincipal menu = new MenuPrincipal(window);
        window.setContentPane(menu);
        window.setVisible(true);

        //CÓDIGO ANTIGUO: Con él lanzapa sólamente el PongPanel();

        /* //Creo el panel del juego.
        PongPanel panel = new PongPanel();
        //Tamaño del panel
        panel.setPreferredSize(new java.awt.Dimension(800,600));

        //Creo un objeto JFrame.(la ventana).
        JFrame window = new JFrame("Pong's King");

        //LLamamos a los métodos.

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Añado el panel antes de mostrar la ventana.
        window.getContentPane().add(panel);
        //Ajusto el tamaño al contenido.
        window.pack();
        //Evito redimensionar.
        window.setResizable(false);
        //Centro en pantalla.
        window.setLocationRelativeTo(null);
        //Muestro la ventana correctamente.
        window.setVisible(true);



        //Iniciamos el hilo del juego. Que contiene el bucle principal.
        Thread gameThread = new Thread(panel);
        gameThread.start(); */

    }
}
