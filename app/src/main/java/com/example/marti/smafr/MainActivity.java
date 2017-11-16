package com.example.marti.smafr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

public class MainActivity extends Activity implements DatePicker.OnDateChangedListener{

    EditText editTxtJmeno;
    DatePicker datum;
    NumberPicker np;
    ImageView imgNahled;

    String globalDatum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTxtJmeno = (EditText)findViewById(R.id.editTxtJmeno);

        np = (NumberPicker) findViewById(R.id.npKusy);
        np.setMinValue(1);
        np.setMaxValue(20);
        np.setWrapSelectorWheel(true);

        datum = (DatePicker)findViewById(R.id.datum);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        globalDatum = df.format(c.getTime());

        StringTokenizer tokens = new StringTokenizer(globalDatum, "/");
        int aktualniDen = Integer.parseInt(tokens.nextToken());
        int aktualniMesic = Integer.parseInt(tokens.nextToken());
        int aktualniRok = Integer.parseInt(tokens.nextToken());

        datum.init(aktualniRok, (aktualniMesic - 1), aktualniDen, this);
        //globalDatum = "01" + "/" + "01" + "/" + "2017";

        imgNahled = (ImageView) findViewById(R.id.imgNahled);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //Log.d("month", " " + monthOfYear); //pro výpis do konzole
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        globalDatum = format.format(calendar.getTime());
    }

    public void FotoClick(View v)
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1);
    }

    public void VlozeniClick(View v)
    {
        //ziskani Bitmpa z ImageView
        BitmapDrawable drawable = (BitmapDrawable) imgNahled.getDrawable();
        Bitmap globalImg = drawable.getBitmap();

        Produkt produkt = new Produkt(editTxtJmeno.getText().toString(), globalDatum,
                np.getValue(), globalImg);

        //vlozeni produktu
        DatabaseHelper db = new DatabaseHelper(this);
        db.insertProdukt(produkt);

        Log.d("jmeno", " " + editTxtJmeno.getText());
        Log.d("datum", " " + globalDatum);
        Log.d("kusu", " " + np.getValue());

        ObnoveniActivity();
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
                imgNahled.setImageBitmap(cameraImages);
            }
        }
    }

    private void ObnoveniActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        MainActivity.this.finish();
        startActivity(intent);
    }

    private void DialogOtevreni() {

        //vytvoreni dialogu
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Odstranit");

        alertDialogBuilder
                .setMessage("Opravdu chcete smazat všechny produkty?")
                .setCancelable(false)
                .setPositiveButton("ano", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteDatabase("produktManager");
                    }
                })
                .setNegativeButton("ne", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ObnoveniActivity();
                    }
                });

        alertDialogBuilder.create();
        alertDialogBuilder.show();
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

        if(id == R.id.smazani)
        {
            DialogOtevreni();
        }

        return super.onOptionsItemSelected(item);
    }
}
