package com.example.marti.smafr;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by marti on 16. 11. 2017.
 */

public class NotifyService extends IntentService {
    DatabaseHelper db;
    List<Produkt> produkty;
    List<Integer> produktyId = new ArrayList<Integer>();

    public NotifyService() {
        super("NotifyService");
    }

    public NotifyService(String name) {
        super(name);
    }

    private String AktualniDatum()
    {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        showNotification();
    }

    private void showNotification() {

        String aktualniDatum = AktualniDatum();

        StringTokenizer tokens = new StringTokenizer(aktualniDatum, "/");
        int aktualniDen = Integer.parseInt(tokens.nextToken());
        int aktualniMesic = Integer.parseInt(tokens.nextToken());
        int aktualniRok = Integer.parseInt(tokens.nextToken());

        db = new DatabaseHelper(getApplicationContext());
        produkty = db.getAllProdukty();
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

        //pretypovani Listu na ArrayList
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
}
