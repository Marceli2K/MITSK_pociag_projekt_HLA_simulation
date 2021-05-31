package Pasazer;

import org.portico.impl.hla1516e.types.encoding.*;
import hla.rti1516e.encoding.DecoderException;
import org.portico.impl.hla1516e.types.encoding.HLA1516eInteger64BE;

import java.util.Random;

/**
 * Created by Stanislaw on 08.05.2018.
 */
public class Pasazer extends HLA1516eFixedRecord {
    private static long prawdPosiadaniaBiletu;
    private boolean bilet;
    private long pasazerID;

    //    prawdopodobienstwo posiadania biletu przez nowego pasazera
    public Pasazer(int pasazerID) {

        int x = randomBilet();
        this.bilet = (x == 0 ? false : true);
        this.pasazerID = pasazerID;
        add(new HLA1516eInteger64BE(this.pasazerID));
        add(new HLA1516eBoolean(this.bilet));
    }

    public static int randomBilet() {
        Random random = new Random();
        long p = random.nextLong();
        if (p < prawdPosiadaniaBiletu) {
            return 1;
        } else {
            return 0;
        }
    }

    public void decode(byte[] bytes) throws DecoderException {
        super.decode(bytes);
        this.pasazerID = ((HLA1516eInteger64BE) get(0)).getValue();
        this.bilet = ((HLA1516eBoolean) get(1)).getValue();

    }

    public long getPasazerID() {
        return pasazerID;
    }

    public boolean getPasazerBilet() {
        return bilet;
    }


//    public double getArrivalTime() {
//        return arrivalTime;
//    }

}
