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
    protected int countOfCheckedPassenger = 0;
    protected int countOfPassengerWithoutBilet = 0;
    protected int countOfPassengerWITHBilet = 0;

    public Konduktor() {
        random = new Random();
    }

    public void checkBilet(Pasazer pasazer) {
        if (pasazer.checked == false) {
            System.out.println("czy pasazer ma bilet " + pasazer.getPasazerBilet());
            countOfCheckedPassenger += 1;
            if (pasazer.getPasazerBilet()) {
                System.out.println("Pasazer o id : " + pasazer.getPasazerID() + " ma bilet  ");
                countOfPassengerWithoutBilet += 1;
                pasazer.setChecked();
            } else {
                System.out.println("Pasazer o id : " + pasazer.getPasazerID() + " nie ma biletu  ");
                countOfPassengerWITHBilet += 1;
                pasazer.setChecked();
            }
        } else {
            System.out.println("Pasazer o id : " + pasazer.getPasazerID() + " ma juz sprawdzony bilet  ");
        }

    }

    static void setInstance() {
        if (instance == null) instance = new Konduktor();
    }

    public int getCountOfCheckedPassenger() {
        return countOfCheckedPassenger;
    }

    public int getCountOfPassengerWithoutBilet() {
        return countOfPassengerWithoutBilet;
    }

    public int getCountOfPassengerWITHBilet() {
        return countOfPassengerWITHBilet;
    }

    static public Konduktor getInstanceKonduktor() {
        if (instance == null) instance = new Konduktor();
        return instance;
    }
}
