package com.example.marti.smafr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

public class SeznamPodrobnosti extends Activity implements DatePicker.OnDateChangedListener{

    NumberPicker np;
    TextView uprJmeno;
    DatePicker myDate;
    ImageView img;

    int globalId;
    int globalMonth;
    int globalDate;
    int globalYear;
    String globalDatum;
    Bitmap obrazek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seznam_podrobnosti);

        Intent intent = getIntent();
        globalId = intent.getIntExtra("id", 0);

        uprJmeno = (TextView) findViewById(R.id.editTxtJmenoU);
        uprJmeno.setText(intent.getStringExtra("jmeno"));

        //rozparsovani stringu
        globalDatum = intent.getStringExtra("datum");
        StringTokenizer tokens = new StringTokenizer(globalDatum, ".");
        globalDate = Integer.parseInt(tokens.nextToken());
        globalMonth = Integer.parseInt(tokens.nextToken());
        globalYear = Integer.parseInt(tokens.nextToken());

        myDate = (DatePicker)findViewById(R.id.datumU);
        myDate.init(globalYear, (globalMonth - 1), globalDate, this);

        np = (NumberPicker) findViewById(R.id.npKusyU);

        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        np.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        np.setMaxValue(20);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        np.setWrapSelectorWheel(true);

        np.setValue(intent.getIntExtra("kusy", 0));

        img = (ImageView) findViewById(R.id.imgObrazekU);
        byte[] byteArray = intent.getByteArrayExtra("obrazek");
        obrazek = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        img.setImageBitmap(obrazek);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        globalDatum = format.format(calendar.getTime());
    }

    public void FotoClickU(View v)
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            if(requestCode == 1)
            {
                Bitmap cameraImages = (Bitmap) data.getExtras().get("data");
                obrazek = cameraImages;
            }
        }
    }

    public void UpraveniClick(View v)
    {
        Produkt produkt = new Produkt(globalId, uprJmeno.getText().toString(), globalDatum,
                np.getValue(), obrazek);

        DatabaseHelper db = new DatabaseHelper(this);
        db.updateProdukt(produkt);

        Log.d("jmeno", " " + uprJmeno.getText());
        Log.d("datum", " " + globalDatum);
        Log.d("kusu", " " + np.getValue());

        Intent intent = new Intent(this, SeznamActivity.class);
        startActivity(intent);
    }

    public void SmazaniClick(View v)
    {
        Produkt produkt = new Produkt(globalId, uprJmeno.getText().toString(), globalDatum,
                np.getValue(), obrazek);

        DatabaseHelper db = new DatabaseHelper(this);
        db.deleteProdukt(produkt);

        Intent intent = new Intent(this, SeznamActivity.class);
        startActivity(intent);
    }
}
