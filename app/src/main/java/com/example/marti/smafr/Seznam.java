package com.example.marti.smafr;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by marti on 20. 10. 2017.
 */

public class Seznam extends ArrayAdapter<Produkt>{

    Context context;
    int layoutResourceId;
    List<Produkt> data = null;

    public Seznam(Context context, int layoutResourceId, List<Produkt> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ProduktHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ProduktHolder();
            holder.txtJmeno = (TextView)row.findViewById(R.id.txtJmeno);
            holder.txtDatum = (TextView)row.findViewById(R.id.txtDatum);
            holder.txtKusy = (TextView)row.findViewById(R.id.txtKusy);
            holder.imgObrazek = (ImageView)row.findViewById(R.id.imgObrazek);

            row.setTag(holder);
        }
        else
        {
            holder = (ProduktHolder)row.getTag();
        }

        Produkt produkt = data.get(position);

        holder.txtJmeno.setText(produkt.jmeno);
        holder.txtDatum.setText(produkt.datum);
        holder.txtKusy.setText(String.valueOf(produkt.kusy));

        holder.imgObrazek.setImageBitmap(produkt.obrazek);

        return row;
    }

    static class ProduktHolder //zastituje graficke prvky na jednom radku
    {
        TextView txtJmeno;
        TextView txtDatum;
        TextView txtKusy;
        ImageView imgObrazek;
    }
}
