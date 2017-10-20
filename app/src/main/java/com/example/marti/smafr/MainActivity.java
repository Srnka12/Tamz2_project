package com.example.marti.smafr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity implements DatePicker.OnDateChangedListener{

    EditText editTxtJmeno;
    DatePicker datum;
    TextView editTxtKusy;

    String globalDatum;
    byte[] globalImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTxtJmeno = (EditText)findViewById(R.id.editTxtJmeno);
        editTxtKusy = (TextView)findViewById(R.id.editTxtKusy);

        datum = (DatePicker)findViewById(R.id.datum);
        datum.init(2017, 0, 1, this);


    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //Log.d("month", " " + monthOfYear); //pro výpis do konzole
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        globalDatum = format.format(calendar.getTime());

        //myText.setText(names[globalMonth]);
        //myImage.setImageResource(zodiacSymbols[globalMonth]);
    }

    public void FotoClick(View v)
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1);
    }

    public void VlozeniClick(View v)
    {
        Produkt produkt = new Produkt(editTxtJmeno.getText().toString(), globalDatum, Integer.parseInt(editTxtKusy.getText().toString()), globalImg);

        DatabaseHelper db = new DatabaseHelper(this);
        db.insertProdukt(produkt);

        Log.d("jmeno", " " + editTxtJmeno.getText());
        Log.d("datum", " " + globalDatum);
        Log.d("kusu", " " + editTxtKusy.getText());
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
/*
    public Bitmap getImage(int i){

        String qu = "select img  from table where feedid=" + i ;
        Cursor cur = db.rawQuery(qu, null);

        if (cur.moveToFirst()){
            byte[] imgByte = cur.getBlob(0);
            cur.close();
            return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        }
        if (cur != null && !cur.isClosed()) {
            cur.close();
        }

        return null ;
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            if(requestCode == 1)
            {
                Bitmap cameraImages = (Bitmap) data.getExtras().get("data");
                globalImg = getBitmapAsByteArray(cameraImages);
            }
        }
    }
}
