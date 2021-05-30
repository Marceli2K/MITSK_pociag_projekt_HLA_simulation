package Pociag;

import Pasazer.Pasazer;

import java.util.ArrayList;
import java.util.List;

public class Wagon {
    private List<Pasazer> pasazerList;
    private List<Przedział> przedziałList;
    private int iloscPrzedzial;
    Wagon(){
        this.pasazerList = new ArrayList<>();
        this.przedziałList = new ArrayList<>();
        this.iloscPrzedzial = 4;
        for (int i = 0; i < 4; i++) {
            this.przedziałList.add(new Przedział());
        }
    }

    public boolean registerPasazer(Pasazer pasazer) {
        boolean seated = false;
        for (Przedział przedział : przedziałList){
            if(!seated) {
                seated = przedział.registerPasazerList(pasazer);
            }
        }
        return seated;
    }
    public boolean registerPasazerWagon(Pasazer pasazer) {
         return pasazerList.add(pasazer);
    }

}
