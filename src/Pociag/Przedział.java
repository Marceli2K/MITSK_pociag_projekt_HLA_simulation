package Pociag;

import Pasazer.Pasazer;

import java.util.ArrayList;
import java.util.List;

public class Przedział {
    protected List<Pasazer> pasazerList;
    private int maxPasazer;

    Przedział() {
        this.pasazerList = new ArrayList<>();
        this.maxPasazer = 6;
    }

    public int getMaxPasazer() {
        return maxPasazer;
    }

    public int getSizePasazerList() {
        return pasazerList.size();
    }

    public boolean registerPasazerList(Pasazer pasazer) {
        if (getSizePasazerList() <= getMaxPasazer()) {
            return pasazerList.add(pasazer);
        } else {
            return false;
        }
    }
    protected List<Pasazer> getPasazerowiePrzedzialList() {
        return pasazerList;
    }

}
