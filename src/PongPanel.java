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

    //Declaramos las variables de la pala del rival.
    int rivalX = 730; //Posición horizontal de la pala.
    int rivalY = 250; //Posición vertical.
    int rivalWidth = 20; //Ancho de la pala.
    int rivalHeight = 100; //Alto de la pala.

    //Creo una variable para la velocidad de la pala.
    int paddleSpeed = 25;

    //Creo dos variables booleanas para saber si las teclas se están pulsando.
    boolean movingUp = true;
    boolean movingDown = true;

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

        //Dibujamos la pala del rival.
        g.fillRect(rivalX, rivalY, rivalWidth, rivalHeight);

    }

    //Creo el método run(). Es obligatorio al implementar Runnable.
    //En este método irá el bucle del juego.
    @Override
    public void run(){

        //Este bucle se llama una vez por cada "frame" del juego.
        while (true){

            //Suavizado del movimiento de la pala. Para que no de saltos de pixels.
            if(movingUp){

                playerY -= paddleSpeed;

            }

            if(movingDown){

                playerY += paddleSpeed;
            }


            //Con esto la pelota en cada frame cambiará su movimiento.
            ballX += ballSpeedX;
            ballY += ballSpeedY;

            //Creamos la clase Rectangle con la pelota y la pala para determinar si se están tocando, para ello
            //usamos .intersects. Lo meto dentro del bucle siempre uso la posición actualizada de la pelota.
            Rectangle ball = new Rectangle(ballX, ballY, ballSize, ballSize);
            Rectangle paddle = new Rectangle(playerX, playerY, playerWidth, playerHeight);

            //Creo la clase Rectangle también con la pala del rival.
            Rectangle rivalPaddle = new Rectangle(rivalX,rivalY,rivalWidth,rivalHeight);

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
                int paddleCenter = playerY + playerHeight/2;
                int ballCenter = ballY + ballSize/2;
                int difference = ballCenter - paddleCenter;
                ballSpeedY = difference/5;
            }

            //Movimiento automático de la pala del rival.
            if (ballY + ballSize /2 < rivalY + rivalHeight /2){
                rivalY -= 2; //Sube

            }else if (ballY + ballSize/2 > rivalY + rivalHeight/2){
                rivalY +=2; //Baja

            }

            //Limitar la pala del rival dentro de los bordes de la pantalla.
            if (rivalY < 0)rivalY = 0;
            if (rivalY>PANEL_HEIGHT - rivalHeight)rivalY = PANEL_HEIGHT - rivalHeight;

            //Colisión de la pelota con la pala del rival cuando se toquen.
            if (ball.intersects(rivalPaddle)){
                ballSpeedX *= -1;
                int rivalPaddleCenter = rivalY + rivalHeight/2;
                int ballCenter = rivalY + ballSize/2;
                int difference = ballCenter - rivalPaddleCenter;
                ballSpeedY = difference/5;
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

        //Liberamos las banderas booleanas(movingUp = true && movingDown = true) cuando se pulse la tecla.
        if (e.getKeyCode()== KeyEvent.VK_W){
            movingUp = false;

        }

        if (e.getKeyCode()== KeyEvent.VK_S){
            movingDown = false;
        }

    }

    @Override
    public void keyPressed(KeyEvent e){

        //Activamos las banderas booleanas(movingUp = true && movingDown = true) cuando se pulse la tecla.

        if (e.getKeyCode()== KeyEvent.VK_W){
            movingUp = true;

        }

        if (e.getKeyCode()== KeyEvent.VK_S){
            movingDown = true;
        }

    }
}
