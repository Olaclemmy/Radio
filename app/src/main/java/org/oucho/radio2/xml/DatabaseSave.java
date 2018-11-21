package org.oucho.radio2.xml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import org.oucho.radio2.db.RadiosDatabase;


public class DatabaseSave {

    private final SQLiteDatabase mDb;
    private Exporter mExporter;


    public DatabaseSave(SQLiteDatabase db, String destXml) {
        mDb = db;

        try {
            saveToFile(destXml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToFile(String destXml) throws IOException {
        File myFile = new File(destXml);

        boolean myFileExisted = myFile.exists() || myFile.createNewFile();

        try {
            if (!myFileExisted) {
                throw new IOException("Unable to create file");
            }
        } catch (IOException e) {
            Log.e("DatabaseSave", "Error: " + e);
        }

        FileOutputStream fOut = new FileOutputStream(myFile);
        BufferedOutputStream bos = new BufferedOutputStream(fOut);

        mExporter = new Exporter(bos);
    }

    public void exportData() {


        try {
            mExporter.enTete();

            exportTable();

            mExporter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportTable() throws IOException {

        String sql = "select * from " + RadiosDatabase.TABLE_NAME;
        Cursor cur = mDb.rawQuery(sql, new String[0]);

        cur.moveToFirst();

        while (cur.getPosition() < cur.getCount()) {

            String encodedImage = null;
            String name = cur.getString(cur.getColumnIndexOrThrow("name"));
            String url = cur.getString(cur.getColumnIndexOrThrow("url"));

            byte[] logo = cur.getBlob(cur.getColumnIndexOrThrow("image"));

            mExporter.startRadio();

            if (logo != null) {
                encodedImage = Base64.encodeToString(logo, Base64.DEFAULT);
            }

            mExporter.addRadio(name, url, encodedImage);
            mExporter.endRadio();

            cur.moveToNext();
        }

        mExporter.fin();

        cur.close();
    }



    private static class Exporter {

        private static final String ENTETE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n" + "<map>" + "\n" ;
        private static final String START_RADIO = "<radio>" + "\n";
        private static final String END_RADIO = "</radio>" + "\n";
        private static final String FIN = "</map>";

        private final BufferedOutputStream mbufferos;

        private String stg;

        Exporter(BufferedOutputStream bos) {
            mbufferos = bos;
        }

        void enTete() throws IOException {

            stg = ENTETE;
            mbufferos.write(stg.getBytes());
        }

        void startRadio() throws IOException {

            stg = START_RADIO;
            mbufferos.write(stg.getBytes());
        }

        void addRadio(String name, String url, String image) throws IOException {

            String stg = "<name>" + name.replace("&", "&amp;") + "</name>"+ "\n"
                    + "<url>" + url + "</url>"+ "\n"
                    + "<image>" + image + "</image>" + "\n";

            mbufferos.write(stg.getBytes());
        }

        void endRadio() throws IOException {

            stg = END_RADIO;
            mbufferos.write(stg.getBytes());
        }

        void fin() throws IOException {

            stg = FIN;
            mbufferos.write(stg.getBytes());
        }

        void close() throws IOException {
            if (mbufferos != null) {
                mbufferos.close();
            }
        }
    }

}