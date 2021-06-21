package GUI;
//*
//
//
// TODO
//  nalezy zaimplementowac rysowanie nowych pasazerow na liscie obiektow
//  ale oni znikaja wiec naleyz pamieta co tm zeby ich koordynaty umieszczac na liscie obiektow
//
// */

import java.awt.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.List;


import static org.portico.lrc.PorticoConstants.sleep;

public class DisplayGUI extends Canvas implements Runnable {
    protected List<Integer> listaPasazerowX = new ArrayList<>();
    protected List<Integer> listaPasazerowY = new ArrayList<>();
    static JLabel t1;
    static JLabel t2, t3, x1, x2, x3;
    static JFrame f;
    private static boolean drawOvalVariable = false;
    boolean variableDraw = true;
    int x, y = 0;


    public DisplayGUI() {
        f = new JFrame();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        t1 = new JLabel(String.valueOf("x"));
        t2 = new JLabel(String.valueOf("x"));
        t3 = new JLabel(String.valueOf("x"));
        x1 = new JLabel(String.valueOf("Prawdopodobieństwo przejchania bez biletu"));
        x2 = new JLabel(String.valueOf("Prawdopodobieństwo zajęcia miejsca"));
        x3 = new JLabel(String.valueOf("Czas symulacyjny"));
        Toolkit.getDefaultToolkit().setDynamicLayout(false);
        f.setSize(1505, 600);
        x1.setBounds(50, 450, 300, 30);
        x2.setBounds(450, 450, 300, 30);
        x3.setBounds(850, 450, 300, 30);
        f.add(t1);
        f.add(t2);
        f.add(x1);
        f.add(x2);
        f.add(x3);

    }


    public void paint(Graphics g) {
        x = x + 1;
        sleep((long) 0.0000001);
        if (true) {
            setBackground(Color.WHITE);
            this.variableDraw = false;
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


            g.setColor(Color.gray);
//            for (int i = 1; i < 60; i++) {
//                g.fillOval(24 * i, 19 * i, 13, 13);
//            }

            for (Integer x : listaPasazerowX) {
                for (Integer y : listaPasazerowY) {

                }
            }
            //RYSUJ PASAZERÓW
            for (int i = 0; i < listaPasazerowX.size(); i++) {
                g.fillOval(listaPasazerowX.get(i), listaPasazerowY.get(i), 11, 11);
            }

            drawOvalVariable = false;
        }


    }


    @Override
    public void run() {

    }

//    METODA AKTUALIZUJACA DANE STATYSTYCZNE
    public static void setStatistics(double probabilityWithoutBilet, double probabilitySeated, double federateTime, DisplayGUI m) {
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
//
//        f.setLayout(null);
        f.setVisible(true);
    }

//    METODA ODPOWIADAJACA ZA WYWOŁANIE RYSOWANIA NOWYCH PASAZERÓW
    public void drawOval(int x, int y) {
        drawOvalVariable = true;
        listaPasazerowY.add(y);
        listaPasazerowX.add(x);


    }
}
