package com.example.marti.smafr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by marti on 17. 10. 2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "produktManager";
    private static final String TABLE_PRODUKTY = "produkty";
    private static final String KEY_ID = "id";
    private static final String KEY_JMENO = "jmeno";
    private static final String KEY_DATUM = "datum";
    private static final String KEY_KUSY = "kusy";
    private static final String KEY_OBRAZEK = "obrazek";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUKTY_TABLE = "CREATE TABLE " + TABLE_PRODUKTY + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_JMENO + " TEXT, " + KEY_DATUM + " DATE, " + KEY_KUSY + " INTEGER, " + KEY_OBRAZEK + " BLOB" + ")";
        db.execSQL(CREATE_PRODUKTY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUKTY);

        onCreate(db);
    }

    void insertProdukt(Produkt produkt)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_JMENO, produkt.getJmeno());
        values.put(KEY_DATUM, produkt.getDatum());
        values.put(KEY_KUSY, produkt.getKusy());
        values.put(KEY_OBRAZEK, getBitmapAsByteArray(produkt.getObrazek()));

        db.insert(TABLE_PRODUKTY, null, values);
        db.close();
    }

    Produkt getProdukt(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRODUKTY, new String[]{KEY_ID, KEY_JMENO, KEY_DATUM, KEY_KUSY,
                KEY_OBRAZEK}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        Produkt produkt = new Produkt(Integer.getInteger(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                Integer.getInteger(cursor.getString(3)), BitmapFactory.decodeByteArray(cursor.getBlob(4), 0, cursor.getBlob(4).length));

        return produkt;
    }

    public List<Produkt> getAllProdukty()
    {
        List<Produkt> produktList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PRODUKTY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst())
        {
            do {
                Produkt produkt = new Produkt();
                produkt.setId(cursor.getInt(0));
                produkt.setJmeno(cursor.getString(1));
                produkt.setDatum(cursor.getString(2));
                produkt.setKusy(cursor.getInt(3));

                byte[] imgByte = cursor.getBlob(4);
                produkt.setObrazek(BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length));

                produktList.add(produkt);
            }
            while(cursor.moveToNext());
        }

        return produktList;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public int updateProdukt(Produkt produkt)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_JMENO, produkt.getJmeno());
        values.put(KEY_DATUM, produkt.getDatum());
        values.put(KEY_KUSY, produkt.getKusy());
        values.put(KEY_OBRAZEK, getBitmapAsByteArray(produkt.getObrazek()));
        return db.update(TABLE_PRODUKTY, values, KEY_ID + "=?", new String[]{String.valueOf(produkt.getId())});
    }

    public void deleteProdukt(Produkt produkt)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUKTY, KEY_ID + "=?", new String[]{String.valueOf(produkt.getId())});
        db.close();
    }

    public int getProduktCount()
    {
        String countQuery = "SELECT * FROM " + TABLE_PRODUKTY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }
}
