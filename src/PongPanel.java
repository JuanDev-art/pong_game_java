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

    //Variable para la velocidad de la pala del rival.
    int rivalSpeed = 10;

    //Creo dos variables booleanas para saber si las teclas se están pulsando.
    boolean movingUp = true;
    boolean movingDown = true;

    //Variables para control de puntuaciones y reinicio de rondas pulsando una tecla.
    //boolean para pausar entre rondas.
    boolean waitingForServe = true;
    int playerScore = 0;
    int rivalScore = 0;

    //Constructor.
    public PongPanel(){
        this.setFocusable(true); //Permite que el panel reciba eventos de teclado.
        this.addKeyListener(this); //Le dice que escuche teclas en este panel.
        //Coloco la pelota en el centro desde el principio.
        resetBall();
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

        //Mostramos los puntos de los jugadores.
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Jugador: " + playerScore, 50 , 50);
        g.drawString("Rival: " +  rivalScore, PANEL_WIDTH - 200, 50);

        if (waitingForServe){

            g.drawString("Pulsa ESPACIO para continuar", PANEL_WIDTH / 2 - 180, PANEL_HEIGHT / 2);

        }

    }

    //Creo el método run(). Es obligatorio al implementar Runnable.
    //En este método irá el bucle del juego.
    @Override
    public void run(){

        //Este bucle se llama una vez por cada "frame" del juego.
        while (true){

            if (waitingForServe){
                repaint();
                try{

                    Thread.sleep(16);

                }catch (InterruptedException e){

                    e.printStackTrace();

                }
                //Salto el resto del bucle.
                continue;

            }

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

            //Si la pelota sale por la derecha, punto para el jugador.
            if(ballX > PANEL_WIDTH){

                playerScore ++; //Se le suma 1.(Punto).
                resetBall();
                waitingForServe = true;

            }

            //Si la pelota sale por la izquierda, punto para el rival.

            if(ballX < 0){

                rivalScore++; //Se le suma 1 al rival. (Punto).
                resetBall();
                waitingForServe = true;


            }

            //Creamos la clase Rectangle con la pelota y la pala para determinar si se están tocando, para ello
            //usamos .intersects. Lo meto dentro del bucle siempre uso la posición actualizada de la pelota.
            Rectangle ball = new Rectangle(ballX, ballY, ballSize, ballSize);
            Rectangle paddle = new Rectangle(playerX, playerY, playerWidth, playerHeight);

            //Creo la clase Rectangle también con la pala del rival.
            Rectangle rivalPaddle = new Rectangle(rivalX,rivalY,rivalWidth,rivalHeight);

            /*
            //Condiciones, para que la bola choque con los bordes de la pantalla.
            if (ballX <= 0 || ballX >= PANEL_WIDTH - ballSize){
                ballSpeedX *= -1;
            }

            */

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

                //Empujar la pelota fuera de la pala para evitar múltiples colisiones.
                ballX = playerX + playerWidth + 1;
            }

            //Limitar la pala del rival dentro de los bordes de la pantalla.
            if (rivalY < 0)rivalY = 0;
            if (rivalY>PANEL_HEIGHT - rivalHeight)rivalY = PANEL_HEIGHT - rivalHeight;

            //Colisión de la pelota con la pala del rival cuando se toquen.
            if (ball.intersects(rivalPaddle)){
                ballSpeedX *= -1;
                int rivalPaddleCenter = rivalY + rivalHeight/2;
                int ballCenter = ballY + ballSize/2;
                int difference = ballCenter - rivalPaddleCenter;
                ballSpeedY = difference/5;
            }

            //ANTIGUO
            //Movimiento de la pala del rival. Hago que el rival sólo siga la pelota si va hacia él.
            /*
            if(ballSpeedX > 0){

                if(ballY + ballSize / 2 < rivalY + rivalHeight / 2){

                    rivalY -= rivalSpeed;

                } else if (ballY + ballSize /2 > rivalY + rivalHeight / 2) {

                    rivalY += rivalSpeed;
                    
                }

            }
            */

            //Movimiento suave e inteligente de la pala del rival.
            if (ballSpeedX > 0){
                int ballCenterY = ballY + ballSize / 2;
                int rivalCenterY = rivalY + rivalHeight / 2;
                int difference = ballCenterY - rivalCenterY;

                if(Math.abs(difference)>5){
                    rivalY += difference / 8;
                }
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

    //Creo el método resetBall() para resetear la pelota cuando se marque un punto.
    private void resetBall(){
        ballX = PANEL_WIDTH / 2 - ballSize / 2;
        ballY = PANEL_HEIGHT / 2 - ballSize / 2;

        //Detengo la pelota mientras se espera.
        ballSpeedX = 0;
        ballSpeedY = 0;
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

        //Si se detecta espacio empieza la nueva ronda de juego.
        if(e.getKeyCode() == KeyEvent.VK_SPACE && waitingForServe){
            waitingForServe = false;

            //Relanzamos la pelota aleatoriamente hacia un lado.
            ballSpeedX = Math.random() < 0.5 ? 2 : -2;
            ballSpeedY = (int) (Math.random() * 5 - 2); //Entre -2 y 2.

        }

    }
}
