package pong_game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

public class PongPanel extends JPanel implements Runnable, KeyListener {

    private final JFrame parentFrame;
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
    boolean movingUp = false;
    boolean movingDown = false;

    //Variables para control de puntuaciones y reinicio de rondas pulsando una tecla.
    //boolean para pausar entre rondas.
    boolean waitingForServe = true;
    int playerScore = 0;
    int rivalScore = 0;

    //Fondo animado con estrellas.
    int numStars = 100;
    int[] starX = new int[numStars];
    int[] starY = new int[numStars];

    //Variable de la fuente Orbitron.
    Font orbitronFont;

    //Variables de efecto de choque de la pelota con la pala.
    private boolean showImpactEffect = false;
    private int impactX = 0;
    private int impactY = 0;
    private long impactStartTime = 0;
    private final int IMPACT_DURATION = 100; // número en milisegundos.

    //Constructor.
    public PongPanel(JFrame parentFrame){
        this.parentFrame = parentFrame;
        setPreferredSize(new Dimension(800, 600));
        this.setFocusable(true); //Permite que el panel reciba eventos de teclado.
        this.addKeyListener(this); //Le dice que escuche teclas en este panel.
        //Coloco la pelota en el centro desde el principio.
        resetBall();

        //Genero posiciones iniciales aleatorias para las estrellas.
        for (int i = 0; i < numStars ; i++) {
            starX[i] = (int) (Math.random() * PANEL_WIDTH);
            starY[i] = (int) (Math.random() * PANEL_HEIGHT);
        }

        //Inicio de música de fondo.
        startBackgroundMusic("pong_game/loopBackground.wav");
    }

    //Creo el método playSound() para reproducir el sonido del juego.
    private void playSound(String soundFilename){
        try{
            URL soundURL = getClass().getResource(("/" + soundFilename));
            if (soundURL == null) {
                System.out.println("No se pudo encontrar el archivo de sonido " + soundFilename);
                return;
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();

        } catch(Exception e){
            e.printStackTrace();
        }

    }

    //Pongo música de fondo.
    private Clip backgroundMusic;

    private void startBackgroundMusic(String soundFileName) {
        try {

            URL soundURL = getClass().getResource("/" + soundFileName);
            if (soundURL == null) {
                System.out.println("No se pudo encontrar el archivo de música " + soundFileName);
                return;
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundURL);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInput);


            // Volumen: entre -80.0f (silencio) y 0.0f (volumen máximo original).
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f);//Hay que cambiar este valor para ajustar el volumen.



            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);


        }catch (Exception e) {
            e.printStackTrace();

        }

    }

    //Creo método para poder detener la música.
    private void stopBackgroundMusic() {
        if(backgroundMusic != null && backgroundMusic.isRunning()) {

            backgroundMusic.stop();
            backgroundMusic.close();

        }
    }




    //Creo el método paintComponent() y lo sobrescribo.
    @Override
    public void paintComponent(Graphics g){

        //Limpia el panel antes de dibujar.
        super.paintComponent(g);

        //Convertimos a Graphics2D.
        Graphics2D g2 = (Graphics2D) g;

        try{
            //Cargamos la fuente desde la carpeta "fonts".
            InputStream is = getClass().getResourceAsStream("/pong_game/resources/Orbitron-VariableFont.ttf");
            if (is == null) {
                System.out.println("No se encontró la fuente personalizada");
                g2.setFont(new Font("Consolas", Font.BOLD, 40));
            }else {
                Font orbitron = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(35f);
                g2.setFont(orbitron);
            }

        }catch (Exception e) {
            e.printStackTrace();

            //Si fallara la fuente usamos una por defecto.
            g2.setFont(new Font("Consolas",Font.BOLD, 48));

        }

        //Fondo degradado de azul a negro.
        GradientPaint gp = new GradientPaint(0, 0, Color.BLACK, 0, getHeight(), Color.BLUE);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        //Dibujar estrellas blancas.
        g.setColor(Color.WHITE);
        for (int i = 0 ; i < numStars; i++) {
            g.fillOval(starX[i], starY[i],2 ,2);
        }

        //Dibujo una línea central punteada.
        /*
        EXPLICACIÓN:

        1. int i = 0; → empezamos en la parte de arriba del campo (posición vertical i = 0).

        2. i < getHeight(); → mientras i esté dentro de la pantalla (por ejemplo, 600 píxeles de alto).

        3. i += 40; → cada vez que dibujamos un trozo, saltamos 40 píxeles hacia abajo.
        Eso hace que la línea sea punteada, con espacios entre los trozos.

        LO QUE SE DIBUJA DENTRO DEL BUCLE.
        Esto dibuja un rectángulo blanco (una rayita) en cada vuelta del bucle:

        > getWidth()/2 - 5 → lo pone en el centro horizontal del panel.

        > getWidth()/2 es la mitad del ancho total (el centro).

        > -5 es para ajustarlo al centro, porque el rectángulo mide 10 de ancho (así queda centrado).

        > i → es la posición vertical, que va cambiando con cada vuelta del bucle.

        > 0 → ancho del rectángulo (es una rayita finita).

        > 20 → alto del rectángulo (cada rayita es como una barrita vertical de 20 píxeles).


        */
        g.setColor(Color.WHITE);
        for (int y = 0 ; y < getHeight();y += 30) {
            g.fillRect(getWidth()/2 - 2, y, 4, 15);
        }

        //Dibujo los bordes del campo.
        //Línea superior.
        g.fillRect(0,0,getWidth(),5);
        //Línea inferior.
        g.fillRect(0,getHeight() - 5, getWidth(), 5);

        //Dibujo palas y pelota en blanco.
        //Pelota.
        g.fillOval(ballX, ballY, ballSize, ballSize);
        //Pala. Primero le cambio el color. Después la dibujo.
        g.setColor(Color.GREEN);
        g.fillRect(playerX, playerY, playerWidth, playerHeight);
        //Pala del rival. Primero le cambio el color. Después la dibujo.
        g.setColor(Color.RED);
        g.fillRect(rivalX, rivalY, rivalWidth, rivalHeight);

        //Puntuación.
        g.setFont(orbitronFont);
        g.setColor(Color.GREEN);
        g.drawString("Player: " + playerScore, 50, 50);
        g.setColor(Color.RED);
        g.drawString("Rival: " + rivalScore, PANEL_WIDTH - 200, 50);

        //Mensaje de espera.
        if (waitingForServe) {
            String mensaje = "Pulsa ESPACIO  para comenzar";
            g.setColor(Color.WHITE);
            g.setFont(orbitronFont);
            FontMetrics fm = g.getFontMetrics();
            int textoAncho = fm.stringWidth(mensaje);
            int x = (getWidth() - textoAncho) / 2;
            int y = getHeight() / 2;

            g.drawString(mensaje, x, y);
        }

        //Efecto de colisión de la pelota con la pala.
        if (showImpactEffect) {
            long elapsed = System.currentTimeMillis() - impactStartTime;
            if (elapsed < IMPACT_DURATION) {
                int size = 20;
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(impactX - size / 2, impactY - size / 2, size, size);
            }else {
                showImpactEffect = false;
            }
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
                playSound("pong_game/point.wav");
                resetBall();
                waitingForServe = true;

            }

            //Si la pelota sale por la izquierda, punto para el rival.

            if(ballX < 0){

                rivalScore++; //Se le suma 1 al rival. (Punto).
                playSound("pong_game/point.wav");
                resetBall();
                waitingForServe = true;


            }

            //Mover las estrellas hacia abajo.
            for (int i = 0; i < numStars; i++) {
                starY[i] += 1;

                //Si la estrella sale por abajo, la subimos arriba de nuevo.
                if (starY[i] > PANEL_HEIGHT) {
                    starY[i] = 0;
                    starX[i] = (int)(Math.random() * PANEL_WIDTH);
                }

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

                //Registrar impacto.
                showImpactEffect = true;
                impactX = ball.x + ball.width / 2;
                impactY = ball.y + ball.height / 2;
                impactStartTime = System.currentTimeMillis();

                //Empujar la pelota fuera de la pala para evitar múltiples colisiones.
                ballX = playerX + playerWidth + 1;

                //Sonido de la pala al colisionar con la pelota.
                playSound("pong_game/popPaddle.wav");
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

                //Registrar impacto.
                showImpactEffect = true;
                impactX = ball.x + ball.width / 2;
                impactY = ball.y + ball.height / 2;
                impactStartTime = System.currentTimeMillis();

                //Sonido de la pala al colisionar con la pelota.
                playSound("pong_game/popPaddle.wav");
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

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            stopBackgroundMusic(); //Detenemos la música.
            volverAlMenu(); //Volvemos al menú.

        }

    }

    private void volverAlMenu() {

        parentFrame.dispose();
    }

}
