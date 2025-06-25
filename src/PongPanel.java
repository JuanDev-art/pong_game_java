import javax.swing.*;
import java.awt.*;

public class PongPanel extends JPanel implements Runnable {

    //Declaramos las variables para la posición de la pelota.
    int ballX = 100;
    int ballY = 100;
    int ballSize = 20;

    //Decalramos las variables para controlar la dirección del movimiento.
    int ballSpeedX = 2;
    int ballSpeedY = 2;

    //Declaramos el ancho y el alto de la ventana con final, para que no cambien nunca.
    final int PANEL_WIDTH = 800;
    final int PANEL_HEIGHT = 600;

    //Creo el método paintComponent() y lo sobrescribo.
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        //Dibujamos la pelota con fillOval.
        g.fillOval(ballX, ballY, ballSize, ballSize);

    }

    //Creo el método run(). Es obligatorio al implementar Runnable.
    //En este método irá el bucle del juego.
    @Override
    public void run(){

        //Este bucle se llama una vez por cada "frame" del juego.
        while (true){

            //Con esto la pelota en cada frame cambiará su movimiento.
            ballX += ballSpeedX;
            ballY += ballSpeedY;

            //Condiciones, para que la bola choque con los bordes de la pantalla.
            if (ballX <= 0 || ballX >= PANEL_WIDTH - ballSize){
                ballSpeedX *= -1;
            }

            if (ballY <= 0 || ballY >= PANEL_HEIGHT - ballSize){
                ballSpeedY *= -1;
            }

            //Volver a dibujar el panel.
            repaint();

            try{
                Thread.sleep(16); //Aproximadamente 60 frames por segundo.
            }catch (InterruptedException e){

                e.printStackTrace();

            }
        }

    }
}
