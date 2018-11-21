package org.oucho.radio2.tunein.loaders;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;

import org.oucho.radio2.radio.RadioKeys;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


public class TuneInLoader extends BaseLoader<List<String>> implements RadioKeys {

    private static final String TAG = "TuneInLoader";
    private final String urlRadioTime;

    public TuneInLoader(Context context, String url) {
        super(context);
        this.urlRadioTime = url;
    }

    @Override
    public List<String> loadInBackground() {

        List<String> liste = null;

        String langue = Locale.getDefault().getLanguage();
        String pays = Locale.getDefault().getCountry();

        try {

            URL url = new URL(urlRadioTime);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept-Language", langue + "-" + pays);
            conn.connect();

            InputStream stream = conn.getInputStream();

            liste = parse(convertStreamToString(stream));

            stream.close();

        } catch (SocketTimeoutException e) {

            Intent error = new Intent();
            error.setAction(INTENT_ERROR);
            error.putExtra("error", "TimeoutException " + e);
            getContext().sendBroadcast(error);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }


    private List<String> parse(String string) {

        // Parse header
        if (!string.contains("<opml version=\"1\">"))
            return null;

        String header = string;
        header = header.substring(header.indexOf("<head>") + 6, header.indexOf("</head>"));

        String titre;
        String status = header.substring(header.indexOf("<status>") + 8, header.indexOf("</status>"));

        if (status.equals("200") && header.contains("<title>")) {
            titre = header.substring(header.indexOf("<title>") + 7, header.indexOf("</title>"));
            Intent intent = new Intent();
            intent.setAction(INTENT_TITRE);
            intent.putExtra("titre", titre);
            getContext().sendBroadcast(intent);
        }

        if (status.equals("400")) {
            Log.e(TAG, "Error: " + header.substring(header.indexOf("<fault>") + 7, header.indexOf("</fault>")));
            Log.e(TAG, "Error code: " + header.substring(header.indexOf("<fault_code>") + 12, header.indexOf("</fault_code>")));
            Intent intent = new Intent();
            intent.setAction("org.oucho.radio2.INTENT_TITRE");
            intent.putExtra("Titre", "Error");
            getContext().sendBroadcast(intent);
            return null;
        }

        // Parse body
        List<String> liste = new ArrayList<>();

        String body = string;
        body = body.substring(body.indexOf("<body>") + 6, body.indexOf("</body>"));
        body = body.substring(2, body.length() - 1);

        final String[] lines = body.split("\r\n|\r|\n");

        for (String line : lines) {
            String type = line.replace("<outline ", "").replace("/>", "").replace("\n", "");
            liste.add(Html.fromHtml(type).toString());
        }

        return liste;
    }

    private static String convertStreamToString(InputStream is) {
        Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
