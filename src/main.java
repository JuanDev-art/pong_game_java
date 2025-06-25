import javax.swing.JFrame;

public class main {
    public static void main(String[] args) {

        //Creo un objeto JFrame.
        JFrame window = new JFrame();

        //LLamamos a los métodos.

        //setSize(); (800x600)
        window.setSize(800, 600);

        //setTitle();
        window.setTitle("Pong's King");

        //setDefaultCloseOperation(); cuando se pulse "x" se cerrará el programa por completo.
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //setVisible(); para que la ventana se muestre.
        window.setVisible(true);

        PongPanel panel = new PongPanel();

        window.add(panel);

    }


}
