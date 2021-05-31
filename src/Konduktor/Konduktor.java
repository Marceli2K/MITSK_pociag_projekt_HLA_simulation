package Konduktor;

import Pasazer.Pasazer;

import java.util.List;
import java.util.Random;

/**
 * Created by Stanislaw on 08.05.2018.
 */
public class Konduktor {
    int timeToNext;
    private Random random;
    private List<Pasazer> wagonList;

    public Konduktor() {
        random = new Random();
        timeToNext = generateTimeToNext();
    }

    public int wsiadanie() {
        int czasPoprzeniejStacji = 0;
        timeToNext = generateTimeToNext();
        int count = random.nextInt(4) + 1;
        System.out.println("Wsiadlo  " + count + " nowych pasazerow. Nastepny przystanek za: " + timeToNext);
        return count;
    }

    public int getTimeToNext() {
        return timeToNext;
    }

    private int generateTimeToNext() {
        return random.nextInt(10) + 1;
    }
}
