package com.example.marti.smafr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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

    /*
    //notifikace
    DatabaseHelper db1;
    List<Produkt> produkty;
    List<Integer> produktyId = new ArrayList<Integer>();
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seznam_podrobnosti);

        //preneseni hodnot z activity
        Intent intent = getIntent();
        globalId = intent.getIntExtra("id", 0);

        //nastaveni textView
        uprJmeno = (TextView) findViewById(R.id.editTxtJmenoU);
        uprJmeno.setText(intent.getStringExtra("jmeno"));

        //rozparsovani stringu
        globalDatum = intent.getStringExtra("datum");
        StringTokenizer tokens = new StringTokenizer(globalDatum, "/");
        globalDate = Integer.parseInt(tokens.nextToken());
        globalMonth = Integer.parseInt(tokens.nextToken());
        globalYear = Integer.parseInt(tokens.nextToken());

        //inicializace DatePickeru
        myDate = (DatePicker)findViewById(R.id.datumU);
        myDate.init(globalYear, (globalMonth - 1), globalDate, this);

        //inicializace NumberPickeru
        np = (NumberPicker) findViewById(R.id.npKusyU);
        np.setMinValue(1);
        np.setMaxValue(20);
        np.setWrapSelectorWheel(true);
        np.setValue(intent.getIntExtra("kusy", 0));

        //preneseni pole bytu a prevedeni na Bitmapu
        img = (ImageView) findViewById(R.id.imgObrazekU);
        byte[] byteArray = intent.getByteArrayExtra("obrazek");
        obrazek = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        img.setImageBitmap(obrazek);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
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
                img.setImageBitmap(obrazek);
            }
        }
    }

    public void UpraveniClick(View v)
    {
        Produkt produkt = new Produkt(globalId, uprJmeno.getText().toString(), globalDatum,
                np.getValue(), obrazek);

        //aktualizace produktu
        DatabaseHelper db = new DatabaseHelper(this);
        db.updateProdukt(produkt);

        //smazani zaznamu v sipce zpet
        Intent intent = new Intent(this, SeznamActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void SmazaniProduktu(Produkt produkt)
    {
        DatabaseHelper db = new DatabaseHelper(this);
        db.deleteProdukt(produkt);
    }

    private void OtevreniSeznamActivity() {
        //smazani zaznamu v sipce zpet
        Intent intent = new Intent(this, SeznamActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void ObnoveniActivity(Produkt produkt)
    {
        Intent intent = new Intent(SeznamPodrobnosti.this, SeznamPodrobnosti.class);

        //preneseni hodnot produktu k znovuzobrazeni
        Bundle bundle = new Bundle();
        bundle.putInt("id", produkt.id);
        bundle.putString("jmeno", produkt.jmeno);
        bundle.putString("datum", produkt.datum);
        bundle.putInt("kusy", produkt.kusy);

        //preneseni Bitmapy k znovuzobrazeni
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        produkt.obrazek.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        bundle.putByteArray("obrazek", byteArray); ;

        intent.putExtras(bundle);
        SeznamPodrobnosti.this.finish();
        startActivity(intent);
    }

    private void DialogOtevreni(){

        final Produkt produkt = new Produkt(globalId, uprJmeno.getText().toString(), globalDatum,
                np.getValue(), obrazek);

        //vytvoreni dialogu
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Odstranit");

        alertDialogBuilder
                .setMessage("Opravdu chcete smazat tento produkt?")
                .setCancelable(false)
                .setPositiveButton("ano", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SmazaniProduktu(produkt);
                        OtevreniSeznamActivity();
                    }
                })
                .setNegativeButton("ne", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ObnoveniActivity(produkt);
                    }
                });

        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }

    public void SmazaniClick(View v){
        DialogOtevreni();
    }
/*
    private String AktualniDatum()
    {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public void NotifikaceClick(View v) {
        showNotification();
    }

    private void showNotification() {

        String aktualniDatum = AktualniDatum();

        StringTokenizer tokens = new StringTokenizer(aktualniDatum, "/");
        int aktualniDen = Integer.parseInt(tokens.nextToken());
        int aktualniMesic = Integer.parseInt(tokens.nextToken());
        int aktualniRok = Integer.parseInt(tokens.nextToken());

        db1 = new DatabaseHelper(getApplicationContext());
        produkty = db1.getAllProdukty();
        int position = 0;

        for(int i = 0; i < produkty.size(); i++)
        {
            StringTokenizer tokens1 = new StringTokenizer(produkty.get(i).datum, "/");
            int den = Integer.parseInt(tokens1.nextToken());
            int mesic = Integer.parseInt(tokens1.nextToken());
            int rok = Integer.parseInt(tokens1.nextToken());

            if(rok < aktualniRok)
            {
                produktyId.add(position, produkty.get(i).id);
                position++;
            }
            else if (rok == aktualniRok)
            {
                if(mesic < aktualniMesic)
                {
                    produktyId.add(position, produkty.get(i).id);
                    position++;
                }
                else if(mesic == aktualniMesic && den <= aktualniDen + 3)
                {
                    produktyId.add(position, produkty.get(i).id);
                    position++;
                }
            }
        }

        int velikost = produktyId.size();
        //ContentValues cv = new ContentValues();
        //cv.put("velikost", velikost);

        Intent resultIntent = new Intent(getApplicationContext(), SeznamActivity.class);

        //prenos hodnot do dalsi aktivity s danym klicem pro synchronizaci
        Bundle bundle = new Bundle();
        bundle.putInt("key", 5);

        ArrayList<Integer> produktySeznam = new ArrayList<Integer>(produktyId);

        //prenos listu produktu do dalsi aktivity
        bundle.putIntegerArrayList("produktySeznam", produktySeznam);
        //bundle.put("seznamProduktyJmena", produktyExpirace);

        resultIntent.putExtras(bundle);

        //resultIntent.putExtra("seznam", produktyExpiraceJmena);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext()).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Zkontroluj ledničku!")
                .setContentText("Produktů s blížící se (uplynulou) dobou expirace: " + velikost)
                .setContentIntent(resultPendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, mBuilder.build());
    }
    */
}
