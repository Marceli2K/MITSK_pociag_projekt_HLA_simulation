
import java.awt.*;

import javax.swing.*;

import static java.lang.Thread.sleep;

public class Rectangles extends JPanel {
    static JLabel t1;
    static JLabel t2, t3, x1, x2, x3;
    JPanel stat = new JPanel();

    Rectangles() {

    }

    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        setBackground(Color.WHITE);

        int rectX = 370;
        int width = 370;
        int height = 370;
        setForeground(Color.BLACK);
//            rysuj wagony
        for (int i = 0; i < 5; i++) {
            g.fillRect(i * rectX +
                    5 + i, 1, width, height);
        }
//            rysuj linie miedzy wagonami(korytarzem) a przedzialami
        for (int i = 1; i < 6; i++) {
            g.setColor(Color.gray);
            g.drawLine(i + 5 * i, 280, i * 370, 280);
        }
//            rysuj przedzialy
        for (int j = 1; j < 5; j++) {
            for (int i = 1; i < 5; i++) {
                g.setColor(Color.gray);
                g.drawLine(i * 76 + rectX * (j - 1), 1, i * 76 + rectX * (j - 1), 280);
            }
        }
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.paintComponent(g);
    }

    public static void rep() {


    }

    public static void main(String[] args) {
        Rectangles rects = new Rectangles();
        JFrame frame = new JFrame("Rectangles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(rects);
        frame.setSize(1505, 600);
        frame.setLocationRelativeTo(null);
        Container cp = frame.getContentPane();
        Container cp2 = frame.getContentPane();
            cp.setSize(220, 220);
            JLabel x1 = new JLabel(String.valueOf("Prawdopodobieństwo przejchania bez biletu"));
            x1.setSize(111, 111);
            cp2.add(x1);

            frame.setVisible(true);
            rep();


    }
}

   
    
    
    