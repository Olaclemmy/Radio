package org.oucho.radio2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.oucho.radio2.R;
import org.oucho.radio2.radio.RadioKeys;

import java.util.ArrayList;

public class Radio implements RadioKeys {
    private final String url;
    private final String name;
    private final byte[] img;

    public Radio(String url, String name, byte[] img) {
        this.url = url;
        this.name = name;
        this.img = img;
    }

    public static ArrayList<Radio> getRadios(Context context) {
        RadiosDatabase radiosDatabase = new RadiosDatabase(context);
        SQLiteDatabase db = radiosDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT url, name, image FROM " + TABLE_NAME + " ORDER BY NAME COLLATE NOCASE", null);
        ArrayList<Radio> radios = new ArrayList<>();
        while (cursor.moveToNext()) {
            Radio radio = new Radio(cursor.getString(0), cursor.getString(1), cursor.getBlob(2));
            radios.add(radio);
        }
        db.close();
        cursor.close();
        return radios;
    }


    public static void addNewRadio(Context context, Radio radio) {

        Log.d("Radio", "loading: " + radio);

        RadiosDatabase radiosDatabase = new RadiosDatabase(context);
        ContentValues values = new ContentValues();
        values.put("url", radio.url);
        values.put("name", radio.name);
        values.put("image", radio.img);

        try (SQLiteDatabase db = radiosDatabase.getWritableDatabase()) {
            db.insertOrThrow(TABLE_NAME, null, values);

            String text = context.getResources().getString(R.string.addRadio_fromApp, radio.getTitle());
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

            String text = context.getResources().getString(R.string.addRadio_error);
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

            Log.d("Radio", "Error: " + e);
        }
    }

    public static void deleteRadio(Context context, Radio radio) {
        RadiosDatabase radiosDatabase = new RadiosDatabase(context);
        SQLiteDatabase db = radiosDatabase.getWritableDatabase();
        db.delete(TABLE_NAME, "url = '" + radio.getUrl() + "'", null);
        db.close();
    }

    public String getTitle() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public byte[] getLogo() {
        return img;
    }

}
