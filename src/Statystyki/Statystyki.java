package Statystyki;

import Pasazer.Pasazer;

import java.util.Random;

/**
 * Created by Stanislaw on 08.05.2018.
 */
public class Statystyki {
    private Random random;
    private static Statystyki instance = null;
    protected int countOfCheckedPassenger = 0;
    protected int countOfPassengerWithoutBilet = 0;
    protected int countOfPassengerWITHBilet = 0;

    public Statystyki() {
        random = new Random();
    }


    static public Statystyki getInstanceKonduktor() {
        if (instance == null) instance = new Statystyki();
        return instance;
    }
}
