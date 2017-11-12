package com.example.marti.smafr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class SeznamActivity extends Activity {

    ListView lv;
    Context context;
    Seznam seznam;
    List<Produkt> produkty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_seznam);

        context = this;

        DatabaseHelper db = new DatabaseHelper(context);
        produkty = db.getAllProdukty();

        for(int i = 0; i < produkty.size(); i++)
        {
            Log.d("Produkt", "Pr: " + produkty.get(i).jmeno + produkty.get(i).datum);
        }

        lv = (ListView)findViewById(R.id.listView1);

        seznam = new Seznam(context, R.layout.list_entry_seznam, produkty);

        lv.setAdapter(seznam);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Produkt produkt = (Produkt)adapterView.getItemAtPosition(i);
                //Toast.makeText(getApplicationContext(), "mena: " + entry.mena, Toast.LENGTH_SHORT);
                Intent intent = new Intent(SeznamActivity.this, SeznamPodrobnosti.class);
                intent.putExtra("id", produkt.id);
                intent.putExtra("jmeno", produkt.jmeno);
                intent.putExtra("datum", produkt.datum);
                intent.putExtra("kusy", produkt.kusy);

                //preneseni Bitmapy
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                produkt.obrazek.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                intent.putExtra("obrazek", byteArray);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        DatabaseHelper db = new DatabaseHelper(context);
        produkty = db.getAllProdukty();

        seznam = new Seznam(context, R.layout.list_entry_seznam, produkty);
        lv.setAdapter(seznam);
    }
}
