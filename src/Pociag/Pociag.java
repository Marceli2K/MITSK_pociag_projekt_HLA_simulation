package Pociag;

import Pasazer.Pasazer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stanislaw on 08.05.2018.
 */
public class Pociag {

    private static List<Wagon> wagonList;

    Pociag(int iloscWagon) {
        this.wagonList = new ArrayList<>();
        for (int i = 0; i < iloscWagon; i++) {
            this.wagonList.add(new Wagon());
        }
    }

    private int available;
    private int max;
    private static Pociag instance = null;


    static void setInstance(int iloscWagon)
    {
        if(instance==null) instance = new Pociag(iloscWagon);
    }
    static public Pociag getInstance()
    {
        if(instance==null) instance = new Pociag(4);
        return instance;
    }


    public static boolean registerPasazer(Pasazer pasazer) {
        boolean seated = false;
        for (Wagon wagon : wagonList){
            if(!seated) {
                seated = wagon.registerPasazer(pasazer);
            }
        }
        if(!seated) {
            int x = 0;//get random inf form 0 to wagonList.size()
            seated = wagonList.get(x).registerPasazerWagon(pasazer);
        }
        return seated;
    }
    public int getWagonListSize(){
        return wagonList.size();
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

    public boolean addTo(int count)
    {
        if(this.available+count<=this.max) {
            this.available += count;
            System.out.println("Pociag: I just got for " + count + ". Now I have " + this.available + " products");
            return true;
        }
        else
        {
            System.out.println("Pociag: I have no left space for " + count + " products");
            return false;
        }
    }

    public boolean getFrom(int count)
    {
        if(available-count>=0) {
            this.available-=count;
            System.out.println("Pociag: I just given " + count + ". Now I have " + this.available + " products");
            return true;
        }
        else
        {
            System.out.println("Pociag: I have no left products to give");
            return false;
        }
    }
}
