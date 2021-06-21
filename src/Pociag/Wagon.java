package Pociag;

import Pasazer.Pasazer;

import java.util.ArrayList;
import java.util.List;

public class Wagon {
    protected List<Pasazer> pasazerList; //lista pasazerow stojących w wagonie
    private static Wagon instance = null;
    private final List<Przedział> przedziałList;
    private final int iloscPrzedzial;
    public List<Pasazer> trainPassengerList; //lista wszytskich pasazerow w wagonie
    int xVariable = 0;

    Wagon() {
        this.pasazerList = new ArrayList<>();
        this.przedziałList = new ArrayList<>();
        this.iloscPrzedzial = 5;
        this.trainPassengerList = new ArrayList<Pasazer>();
        for (int i = 0; i < 5; i++) {
            this.przedziałList.add(new Przedział());
        }
    }

    public boolean registerPasazer(Pasazer pasazer) {

        boolean seated = false;
        for (Przedział przedzial : przedziałList) {
//            System.out.println("pasazerowie przedzial list: "+ przedzial.getSizePasazerList());
            if (!seated) {
                seated = przedzial.registerPasazerList(pasazer);
                if (seated){
                    pasazer.setSeated();
                    int tmp = 999999999;
                    int index = 0;
                    int siz = pasazerList.size();
                    if (tmp > siz) {
                        tmp = siz;
                        index = przedziałList.indexOf(przedzial);
                        pasazer.setPrzedzialNR(index+1);

                    }
                    trainPassengerListRegister(pasazer);


                }
            }
        }
        return seated;
    }


    public boolean registerPasazerWagon(Pasazer pasazer) {
        trainPassengerListRegister(pasazer);
        return pasazerList.add(pasazer);
    }

    public boolean trainPassengerListRegister(Pasazer pasazer){
        return trainPassengerList.add(pasazer);
    }
    public int getWagonPassengerSeated(){
        int countSeatedPassenger= 0 ;
        for(Pasazer pasazer: trainPassengerList){
            if(pasazer.getSeated()){
                 countSeatedPassenger +=1;
            }
        }

        return countSeatedPassenger;
    }

    public int getPasazerowieWagonListSize() {
        return pasazerList.size();
    }



    protected List<Pasazer> getPasazerowieWagonList() {
        return pasazerList;
    }

    static void setInstance(int iloscWagon) {
        if (instance == null) instance = new Wagon();
    }

    static public Wagon getInstance() {
        if (instance == null) instance = new Wagon();
        return instance;
    }

    public List<Pasazer> getListPassengerInWagon() {
        return trainPassengerList;
    }

    public int getListPassengerInWagonSize() {
        return trainPassengerList.size();
    }
}
