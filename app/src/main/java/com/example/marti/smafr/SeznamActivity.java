package com.example.marti.smafr;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class SeznamActivity extends Activity {

    ListView lv;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_seznam);

        context = this;

        DatabaseHelper db = new DatabaseHelper(context);
        List<Produkt> produkty = db.getAllProdukty();

        for(int i = 0; i < produkty.size(); i++)
        {
            Log.d("Produkt", "Pr: " + produkty.get(i).jmeno + produkty.get(i).datum);
        }

        lv = (ListView)findViewById(R.id.listView1);

        //Log.d("Result","length = " + result.size());

        Seznam seznam = new Seznam(context, R.layout.list_entry_seznam, produkty);


        lv.setAdapter(seznam);
    }
}
