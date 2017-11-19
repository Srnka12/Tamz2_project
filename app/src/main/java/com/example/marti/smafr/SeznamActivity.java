package com.example.marti.smafr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SeznamActivity extends Activity {

    ListView lv;
    Context context;
    EditText editPole;
    ImageView buttonPotvrzeni;
    Seznam seznam;
    List<Produkt> produkty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_seznam);

        context = this;

        editPole = (EditText) findViewById(R.id.editPole);
        editPole.setEnabled(false);

        //nacteni pole produktu z DB
        DatabaseHelper db = new DatabaseHelper(context);
        produkty = db.getAllProdukty();

        if(getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();

            int key = extras.getInt("key");

            if(key == 1)
            {
                editPole.setHint("Název");
                editPole.setInputType(InputType.TYPE_CLASS_TEXT);
                editPole.setEnabled(true);
            }
            else if(key == 2)
            {
                editPole.setHint("Datum");
                editPole.setInputType(InputType.TYPE_CLASS_DATETIME);
                editPole.setEnabled(true);
            }
            else if(key == 3)
            {
                String text = extras.getString("string");
                List<Produkt> newProdukty = new ArrayList<Produkt>();
                int position = 0;

                for(int i = 0; i < produkty.size(); i++)
                {
                    if(produkty.get(i).jmeno.equals(text))
                    {
                        newProdukty.add(position, produkty.get(i));
                        position++;
                    }
                }

                produkty.clear();
                produkty = newProdukty;
            }
            else if (key == 4)
            {
                String text = extras.getString("string");
                List<Produkt> newProdukty = new ArrayList<Produkt>();
                int position = 0;

                for(int i = 0; i < produkty.size(); i++)
                {
                    if(produkty.get(i).datum.equals(text))
                    {
                        newProdukty.add(position, produkty.get(i));
                        position++;
                    }
                }

                produkty.clear();
                produkty = newProdukty;
            }
            else
            {
                List<Integer> produktyId;
                produktyId = (List<Integer>) extras.getIntegerArrayList("produktySeznam");

                List<Produkt> newProdukty = new ArrayList<Produkt>();
                int position = 0;

                for(int i = 0; i < produktyId.size(); i++)
                {
                    for(int j = 0; j < produkty.size(); j++)
                    {
                        if(produkty.get(j).id == produktyId.get(i))
                        {
                            newProdukty.add(position, produkty.get(j));
                            position++;
                            break;
                        }
                    }
                }

                produkty.clear();
                produkty = newProdukty;
            }
        }

        //vypis hodnot z pole produkty
        /*
        for(int i = 0; i < produkty.size(); i++)
        {
            Log.d("Produkt", "Pr: " + produkty.get(i).jmeno + produkty.get(i).datum);
        }*/

        buttonPotvrzeni = (ImageView) findViewById(R.id.buttonPotvrzeni);
        lv = (ListView)findViewById(R.id.listView1);

        seznam = new Seznam(context, R.layout.list_entry_seznam, produkty);

        lv.setAdapter(seznam);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Produkt produkt = (Produkt)adapterView.getItemAtPosition(i);
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

        buttonPotvrzeni.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                    {
                        //Toast.makeText(getApplicationContext(), "Action_Down", Toast.LENGTH_SHORT).show();
                        buttonPotvrzeni.getDrawable().setColorFilter(Color.argb(120, 120, 0, 255), PorterDuff.Mode.SRC_ATOP); //podbarvi obrazek
                        buttonPotvrzeni.invalidate(); //projeveni obarveni
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        buttonPotvrzeni.getDrawable().clearColorFilter(); //smazani podbarveni
                        buttonPotvrzeni.invalidate(); //projev
                        String t = Integer.toString(editPole.getInputType());
                        if(editPole.getInputType() == 1)
                        {
                            //Toast.makeText(getApplicationContext(), "Nazev", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SeznamActivity.this, SeznamActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("key", 3);
                            bundle.putString("string", editPole.getText().toString());
                            intent.putExtras(bundle);
                            SeznamActivity.this.finish();
                            startActivity(intent);
                        }
                        else if(editPole.getInputType() == 4)
                        {
                            //Toast.makeText(getApplicationContext(), "Datum", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SeznamActivity.this, SeznamActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("key", 4);
                            bundle.putString("string", editPole.getText().toString());
                            intent.putExtras(bundle);
                            SeznamActivity.this.finish();
                            startActivity(intent);
                        }

                        //Toast.makeText(getApplicationContext(), "Action_UP", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                return true;
            }
        });
    }
/*
    @Override
    public void onResume(){
        super.onResume();
        DatabaseHelper db = new DatabaseHelper(context);
        produkty = db.getAllProdukty();

        seznam = new Seznam(context, R.layout.list_entry_seznam, produkty);
        lv.setAdapter(seznam);
    }
*/
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.seznam_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.vyhledani_nazev)
        {
            //Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SeznamActivity.this, SeznamActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("key", 1);
            intent.putExtras(bundle);
            SeznamActivity.this.finish();
            startActivity(intent);
        }

        if(id == R.id.vyhledani_datum)
        {
            Intent intent = new Intent(SeznamActivity.this, SeznamActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("key", 2);
            intent.putExtras(bundle);
            SeznamActivity.this.finish();
            startActivity(intent);
        }

        if(id == R.id.obnoveni)
        {
            Intent intent = new Intent(SeznamActivity.this, SeznamActivity.class);
            SeznamActivity.this.finish();
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
