package Pociag;

import Konduktor.Konduktor;
import Pasazer.Pasazer;

import java.util.ArrayList;
import java.util.List;


public class Pociag {

    private static List<Wagon> wagonList; //lista wagonow w pociagu
    protected static List<Konduktor> konduktorList = new ArrayList<>();
    protected int passengerWithBilet = 0;
    protected int passengerWithoutBilet = 0;

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


    public boolean registerPasazer(Pasazer pasazer) {
        pasazer.setPasazerBilet(pasazer);
        if (pasazer.getPasazerBilet()) {
            passengerWithBilet = passengerWithBilet + 1;
        } else {
            passengerWithoutBilet = passengerWithoutBilet + 1;
        }
        boolean seated = false;
        int tmp = 999999999;
        int index = 0;
        for (Wagon wagon : wagonList) {
            int siz = Wagon.pasazerList.size();
            if (tmp > siz) {
                tmp = siz;
                index = wagonList.indexOf(wagon);
            }
        }
//      DODAWANIE PASAZEROW DO MIEJSC SIEDZACYCH W PRZEDZIALACH
        if (!seated) {
            seated = wagonList.get(index).registerPasazer(pasazer);

        }

//      dodawanie pasazerow do miejsc stojacych
        if (!seated) {
            int x = 0;//get random inf form 0 to wagonList.size()
            seated = wagonList.get(index).registerPasazerWagon(pasazer);
        }
        return seated;
    }


    public static List<Wagon> getWagonList() {
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
