package com.example.marti.smafr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.StringTokenizer;

/**
 * Created by marti on 17. 10. 2017.
 */

public class Produkt {
    int id;
    String jmeno;
    String datum;
    int kusy;
    Bitmap obrazek;

    public Produkt()
    {

    }

    public Produkt(String jmeno, String datum, int kusy, Bitmap obrazek)
    {
        this.jmeno = jmeno;
        this.datum = datum;
        this.kusy = kusy;
        this.obrazek = obrazek;
    }

    public Produkt(int id, String jmeno, String datum, int kusy, Bitmap obrazek)
    {
        this.id = id;
        this.jmeno = jmeno;
        this.datum = datum;
        this.kusy = kusy;
        this.obrazek = obrazek;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getDatum()
    {
        //uprava pro vkladani do databaze
        StringTokenizer tokens = new StringTokenizer(datum, "/");
        int den = Integer.parseInt(tokens.nextToken());
        int mesic = Integer.parseInt(tokens.nextToken());
        int rok = Integer.parseInt(tokens.nextToken());

        datum = rok + "-" + mesic + "-" + den;
        return datum;
    }

    public void setDatum(String datum)
    {
        //uprava pro nahrani z databaze
        StringTokenizer tokens = new StringTokenizer(datum, "-");
        int rok = Integer.parseInt(tokens.nextToken());
        int mesic = Integer.parseInt(tokens.nextToken());
        int den = Integer.parseInt(tokens.nextToken());

        this.datum = den + "/" + mesic + "/" + rok;
    }

    public int getKusy() {
        return kusy;
    }

    public void setKusy(int kusy) {
        this.kusy = kusy;
    }

    public Bitmap getObrazek() {
        return obrazek;
    }

    public void setObrazek(Bitmap obrazek) {
        this.obrazek = obrazek;
    }
}
