import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PongPanel extends JPanel implements Runnable, KeyListener {

    //Declaramos las variables para la posición de la pelota.
    int ballX = 100;
    int ballY = 100;
    int ballSize = 20;

    //Declaramos las variables para controlar la dirección del movimiento.
    int ballSpeedX = 2;
    int ballSpeedY = 2;

    //Declaramos el ancho y el alto de la ventana con final, para que no cambien nunca.
    final int PANEL_WIDTH = 800;
    final int PANEL_HEIGHT = 600;

    //Declaramos las variables para la pala.
    int playerX = 50; //Posición horizontal de la pala.
    int playerY = 250; //Posición vertical.
    int playerWidth = 20; //Ancho de la pala.
    int playerHeight = 100; //Alto de la pala.


    //Constructor.
    public PongPanel(){
        this.setFocusable(true); //Permite que el panel reciba eventos de teclado.
        this.addKeyListener(this); //Le dice que escuche teclas en este panel.
    }

    //Creo el método paintComponent() y lo sobrescribo.
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        //Dibujamos la pelota con fillOval.
        g.fillOval(ballX, ballY, ballSize, ballSize);

        //Dibujamos la pala con fillRect.
        g.fillRect(playerX, playerY, playerWidth, playerHeight);

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

            //Creamos la clase Rectangle con la pelota y la pala para determinar si se están tocando, para ello
            //usamos .intersects. Lo meto dentro del bucle siempre uso la posición actualizada de la pelota.
            Rectangle ball = new Rectangle(ballX, ballY, ballSize, ballSize);
            Rectangle paddle = new Rectangle(playerX, playerY, playerWidth, playerHeight);

            //Condiciones, para que la bola choque con los bordes de la pantalla.
            if (ballX <= 0 || ballX >= PANEL_WIDTH - ballSize){
                ballSpeedX *= -1;
            }

            if (ballY <= 0 || ballY >= PANEL_HEIGHT - ballSize){
                ballSpeedY *= -1;
            }

            //Limitar la pala dentro del panel.
            if (playerY < 0)playerY = 0;
            if (playerY>PANEL_HEIGHT - playerHeight)playerY = PANEL_HEIGHT - playerHeight;


            //Colisión de la pelota con la pala cuando se toquen.
            if (ball.intersects(paddle)){
                ballSpeedX *= -1;
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

    //Implementamos los métodos obligatorios de KeyListener.
    @Override
    public void keyTyped (KeyEvent e){

    }

    @Override
    public void keyReleased(KeyEvent e){

    }

    @Override
    public void keyPressed(KeyEvent e){

        if (e.getKeyCode()== KeyEvent.VK_W){
            playerY -= 10;

        }

        if (e.getKeyCode()== KeyEvent.VK_S){
            playerY += 10;
        }

    }
}
