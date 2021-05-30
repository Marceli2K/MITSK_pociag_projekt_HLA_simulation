package Pociag;

import Pasazer.Pasazer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stanislaw on 08.05.2018.
 */
public class Pociag {

    private static List<Wagon> wagonList; //lista wagonow w pociagu

    Pociag(int iloscWagon) {
        this.wagonList = new ArrayList<>();
        for (int i = 0; i < iloscWagon; i++) {
            this.wagonList.add(new Wagon());
        }
    }

    private int available;
    private int max;
    private static Pociag instance = null;


    static void setInstance(int iloscWagon) {
        if (instance == null) instance = new Pociag(iloscWagon);
    }

    static public Pociag getInstance() {
        if (instance == null) instance = new Pociag(4);
        return instance;
    }


    public static boolean registerPasazer(Pasazer pasazer) {
        boolean seated = false;
        int tmp=999999999;
        int index=0;
        for (Wagon wagon : wagonList) {
            int siz = wagon.pasazerList.size();
            if ( tmp> siz){
                tmp = siz;
                index = wagonList.indexOf(wagon);
            }
//            return Collections.min(wagonList.get(wagon).size())
        }
//            System.out.println("Numer obsługiwanego wagonu : " + wagonList.indexOf(wagon));
//            int actualElemnt = wagonList.indexOf(wagon);
            if (!seated) {
                System.out.println("ID pasazera :" + pasazer.getPasazerID());
                System.out.println("ID wagonu :" + index);
                seated = wagonList.get(index).registerPasazer(pasazer);
            }

//      dodawanie pasazerow do miejsc stojacych
        if (!seated) {
            int x = 0;//get random inf form 0 to wagonList.size()
            seated = wagonList.get(index).registerPasazerWagon(pasazer);
        }
        return seated;
    }

    public int getWagonListSize() {
        return wagonList.size();
    }

    public int getPasazerowieWagonListSizeFromPociag(int x) {
        return wagonList.get(x).getPasazerowieWagonListSize();
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }


}
