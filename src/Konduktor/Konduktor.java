package Konduktor;

import Pasazer.Pasazer;
import Pociag.Pociag;

import java.util.List;
import java.util.Random;

/**
 * Created by Stanislaw on 08.05.2018.
 */
public class Konduktor {
    private Random random;
    private static Konduktor instance = null;

    public Konduktor() {
        random = new Random();
    }

    public int numberofKonduktors() {
        int count = random.nextInt(4) + 1;
        System.out.println("Liczba konduktorów w pociągu  " + count);
        return count;
    }

    public void checkBilets(Pasazer pasazer) {
        if (pasazer.checked == false) {
            if (pasazer.getPasazerBilet()) {
                System.out.println("Pasazer o id : " + pasazer.getPasazerID() + " ma bilet  ");
            } else {
                System.out.println("Pasazer o id : " + pasazer.getPasazerID() + " nie ma biletu  ");
            }
        } else {
            System.out.println("Pasazer o id : " + pasazer.getPasazerID() + " ma juz sprawdzony bilet  ");
        }

    }

    static void setInstance() {
        if (instance == null) instance = new Konduktor();
    }

    static public Konduktor getInstanceKonduktor() {
        if (instance == null) instance = new Konduktor();
        return instance;
    }
}
