import javax.swing.*;
import java.awt.*;

public class PongPanel extends JPanel implements Runnable {
    public static void main(String[] args) {

    }

    //Creo el método paintComponent() y lo sobrescribo.
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.drawRect(50, 50, 100, 100); // (x, y , ancho, alto).
    }

    //Creo el método run(). Es obligatorio al implementar Runnable.
    //En este método irá el bucle del juego.
    @Override
    public void run(){

    }
}
