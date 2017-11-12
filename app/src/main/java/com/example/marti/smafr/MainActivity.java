package com.example.marti.smafr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity implements DatePicker.OnDateChangedListener{

    EditText editTxtJmeno;
    DatePicker datum;
    TextView editTxtKusy;

    String globalDatum;
    Bitmap globalImg;

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
        Produkt produkt = new Produkt(editTxtJmeno.getText().toString(), globalDatum,
                Integer.parseInt(editTxtKusy.getText().toString()), globalImg);

        DatabaseHelper db = new DatabaseHelper(this);
        db.insertProdukt(produkt);

        Log.d("jmeno", " " + editTxtJmeno.getText());
        Log.d("datum", " " + globalDatum);
        Log.d("kusu", " " + editTxtKusy.getText());
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
                globalImg = cameraImages;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.seznam)
        {
            //Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent (this, SeznamActivity.class);
            startActivity(intent);
        }

        if(id == R.id.vlozeni)
        {
            //Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(this, Main4Activity.class);
            //startAactivity(intent);
            //Activita zacina s request kodem 333
            //startActivityForResult(intent, 333);
        }

        return super.onOptionsItemSelected(item);
    }
}
