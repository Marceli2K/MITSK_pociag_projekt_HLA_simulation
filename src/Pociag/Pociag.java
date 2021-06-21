package Pociag;

import Konduktor.Konduktor;
import Pasazer.Pasazer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Pociag {
    int tmp = 999999999;
    private List<Wagon> wagonList; //lista wagonow w pociagu
    protected static List<Konduktor> konduktorList = new ArrayList<>();
    protected int passengerWithBilet = 0;
    protected int passengerWithoutBilet = 0;
    int index = 0;
    double smallest;
    Pociag(int iloscWagon) {
        wagonList = new ArrayList<>();
        for (int i = 0; i < iloscWagon; i++) {
            Konduktor konduktor = new Konduktor();
            konduktorList.add(konduktor);
            wagonList.add(new Wagon());
        }
    }

    private int available;
    private static Pociag instance = null;


    static void setInstance(int iloscWagon) {
        if (instance == null) instance = new Pociag(iloscWagon);
    }

    static public Pociag getInstance() {
        if (instance == null) instance = new Pociag(4);
        return instance;
    }


    public boolean registerPassenger(Pasazer pasazer) {
        pasazer.setPasazerBilet(pasazer);
        if (pasazer.getPasazerBilet()) {
            passengerWithBilet = passengerWithBilet + 1;
        } else {
            passengerWithoutBilet = passengerWithoutBilet + 1;
        }
        boolean seated = false;


        int w1, w2, w3, w4;
        w1 = wagonList.get(0).getPasazerowieWagonListSize() + wagonList.get(0).getWagonPassengerSeated();
        w2 = wagonList.get(1).getPasazerowieWagonListSize() + wagonList.get(1).getWagonPassengerSeated();
        w3 = wagonList.get(2).getPasazerowieWagonListSize() + wagonList.get(2).getWagonPassengerSeated();
        w4 = wagonList.get(3).getPasazerowieWagonListSize() + wagonList.get(3).getWagonPassengerSeated();

        this.smallest = w1;
        this.index = 0;
        if (this.smallest >= w2) {
            this.smallest = w2;
            this.index = 1;
        }

        if (this.smallest >= w3) {
            this.smallest = w3;
            this.index = 2;
        }

        if (this.smallest >= w4) {
            this.smallest = w4;
            this.index = 3;
        }

        pasazer.setNR_WagonNR(this.index+1);
        seated = wagonList.get(this.index).registerPasazer(pasazer);
//      dodawanie pasazerow do miejsc stojacych
        if (!seated) {
            int x = 0;//get random inf form 0 to wagonList.size()
            seated = wagonList.get(index).registerPasazerWagon(pasazer);
        }
        return seated;
    }


    public List<Wagon> getWagonList() {
        return wagonList;
    }

    public int getPasazerowieWagonListSizeFromPociag(int x) {
        return wagonList.get(x).getPasazerowieWagonListSize();
    }

    public int getAvailable() {
        return available;
    }

    public List<Konduktor> getKonduktorList() {
        return konduktorList;
    }

    public int getAllPassengerSeated() {
        int AllPassengerSeated = 0;
        for (Wagon wagon : wagonList) {
            AllPassengerSeated = AllPassengerSeated + wagon.getWagonPassengerSeated();
        }
        return AllPassengerSeated;

    }

}
