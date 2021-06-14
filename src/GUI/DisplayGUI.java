package GUI;

import java.awt.*;
import javax.swing.*;

public class DisplayGUI extends Canvas implements Runnable {

    static JLabel t1;
    static JLabel t2, t3, x1, x2, x3;
    static JFrame f;

    public DisplayGUI() {
        f = new JFrame();
        t1 = new JLabel(String.valueOf("x"));
        t2 = new JLabel(String.valueOf("x"));
        t3 = new JLabel(String.valueOf("x"));
        x1 = new JLabel(String.valueOf("Prawdopodobieństwo przejchania bez biletu"));
        x2 = new JLabel(String.valueOf("Prawdopodobieństwo zajęcia miejsca"));
        x3 = new JLabel(String.valueOf("Czas symulacyjny"));
        Toolkit.getDefaultToolkit().setDynamicLayout(false);
        f.setSize(1880, 600);
        x1.setBounds(50, 450, 300, 30);
        x2.setBounds(450, 450, 300, 30);
        x3.setBounds(850, 450, 300, 30);
//        x1.setText(String.valueOf("Prawdopodobieństwo przejchania bez biletu"));
//        x2.setText(String.valueOf("Prawdopodobieństwo zajęcia miejsca"));
//        x3.setText(String.valueOf("Czas symulacyjny"));
        f.add(t1);
        f.add(t2);
        f.add(x1);
        f.add(x2);
        f.add(x3);
    }


    public void paint(Graphics g) {

        setBackground(Color.WHITE);
        int rectX = 370;
        int width = 370;
        int height = 370;
        setForeground(Color.BLACK);
        for (int i = 0; i < 5; i++) {
            g.fillRect(i * rectX +
                    5 + i, 1, width, height);
        }
        for (int i = 1; i < 6; i++) {
            g.setColor(Color.gray);
            g.drawLine(i + 5 * i, 280, i * 370, 280);
        }
        for (int j = 1; j < 6; j++) {
            for (int i = 1; i < 5; i++) {
                g.setColor(Color.gray);
                g.drawLine(i * 76 + rectX * (j - 1), 1, i * 76 + rectX * (j - 1), 280);
            }
        }

//        g.setColor(Color.gray);
//        for (int i = 1; i < 60; i++) {
//            g.fillOval(24 * i, 19 * i, 13, 13);
//        }

    }


    @Override
    public void run() {

    }

    public static void setStatistics(double probabilityWithoutBilet, double probabilitySeated,double federateTime, DisplayGUI m) {

        t1.setBounds(50, 500, 200, 30);
        t2.setBounds(450, 500, 200, 30);
        t3.setBounds(850, 500, 200, 30);
        t1.setText(String.valueOf(probabilityWithoutBilet));
        t2.setText(String.valueOf(probabilitySeated));
        t3.setText(String.valueOf(federateTime));
        f.add(t1);
        f.add(t2);
        f.add(t3);
        f.add(m);

//        f.setLayout(null);
        f.setVisible(true);
    }
}
