package Pasazer;

import org.portico.impl.hla1516e.types.encoding.*;
import hla.rti1516e.encoding.DecoderException;
import org.portico.impl.hla1516e.types.encoding.HLA1516eInteger64BE;
import org.portico.impl.hla1516e.types.encoding.HLA1516eBoolean;

import java.util.Random;

/**
 * Created by Stanislaw on 08.05.2018.
 */
public class Pasazer extends HLA1516eFixedRecord {
    private static long prawdPosiadaniaBiletu;
    public boolean checked;
    private boolean bilet;
    protected long pasazerID;
    private boolean tryToSit;
    protected boolean seated;
    protected long wagonNR =0;
    protected long  przedzialNR = -1;
    private static Pasazer instance = null;
    //    prawdopodobienstwo posiadania biletu przez nowego pasazera


    public Pasazer(int pasazerID) {

        checked = false;
        this.seated = false;
        this.bilet = false;
        this.pasazerID = pasazerID;
        tryToSit = false;

        add(new HLA1516eInteger64BE(this.pasazerID));
        add(new HLA1516eInteger64BE(this.wagonNR));
        add(new HLA1516eInteger64BE(this.przedzialNR));
        add(new HLA1516eBoolean(this.bilet));
        add(new HLA1516eBoolean(this.checked));
        add(new HLA1516eBoolean(this.tryToSit));
        add(new HLA1516eBoolean(this.seated));

    }

    public void decode(byte[] bytes) throws DecoderException {
        super.decode(bytes);
        this.pasazerID = ((HLA1516eInteger64BE) get(0)).getValue();
        this.wagonNR = ((HLA1516eInteger64BE) get(1)).getValue();
        this.przedzialNR = ((HLA1516eInteger64BE) get(2)).getValue();
        this.bilet = ((HLA1516eBoolean) get(3)).getValue();
        this.checked = ((HLA1516eBoolean) get(4)).getValue();
        this.tryToSit = ((HLA1516eBoolean) get(5)).getValue();
        this.seated = ((HLA1516eBoolean) get(6)).getValue();
    }

    public static int randomBilet() {
        Random random = new Random();
        long p = random.nextLong();
        if (p < prawdPosiadaniaBiletu) {
//            System.out.println("11111111111111111111111111111111111111111111111");
            return 1;
        } else {
//            System.out.println("00000000000000000000000000000000000000000000000000");
            return 0;
        }
    }

    public long getPasazerID() {
        return pasazerID;
    }

    public void setPasazerBilet(Pasazer pasazer){
        int x = randomBilet();
        pasazer.bilet = (x != 0);
    }
    public void setPrzedzialNR(int przedzial){
        this.przedzialNR = przedzial;
//        System.out.println("nr przedzialu ustalono na :"+przedzialNR);

    }
    public void setNR_WagonNR(int wagon){
        this.wagonNR = wagon;
//        System.out.println("nr wagonu ustalono na :"+wagonNR);
    }

    public int getPrzedzialNR(){
        return (int) this.przedzialNR;


    }
    public int getNR_WagonNR(){
        return (int) this.wagonNR;

    }


    public boolean getPasazerBilet() {
        return this.bilet;
    }

    protected boolean setTryToSit() {
        return this.tryToSit = true;
    }

    protected boolean getTryToSit() {
        return this.tryToSit;
    }

    public void setChecked() {
        this.checked = true;
    }
    public void setSeated() {
        this.seated = true;
    }
    public boolean getSeated() {
        return this.seated;
    }

}
