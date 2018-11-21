package org.oucho.radio2.utils;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.webkit.URLUtil;
import android.net.Uri;

import org.oucho.radio2.radio.RadioService;
import org.oucho.radio2.net.HttpGetter;

public class Playlist extends AsyncTask<Void, Void, String> {

    private static final int max_ttl = 10;

    private static final int NONE    = 0;
    private static final int M3U     = 1;
    private static final int PLS     = 2;

    @SuppressLint("StaticFieldLeak")
    private final RadioService player;
    private final String start_url;
    private int then = 0;

    //private static Random random = null;

    private static final String url_regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    private static Pattern url_pattern = null;

    public Playlist(RadioService a_player, String a_url) {
        super();
        player = a_player;
        start_url = a_url;
        then = Counter.now();
    }

    public Playlist start() {

        return (Playlist) executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected String doInBackground(Void... args) {

        String url = start_url;
        int ttl = max_ttl;
        int type = NONE;

        if ( url != null && url.length() != 0 && URLUtil.isValidUrl(url) ) {
            type = playlistType(url);
        } else {
            url = null;
        }

        if (url != null && type != NONE ) {
            ttl -= 1;
            url = selectUrlFromPlaylist(url,type);
        }

        if (ttl  == 0)
            url = null;

        return url;
    }

    protected void onPostExecute(String url) {

        if ( url != null && player != null && ! isCancelled() && Counter.still(then) )
            player.playLaunch(url);
    }

    private static String filter(String line, int type) {

        switch (type) {

            case M3U:
                return line.indexOf('#') == 0 ? "" : line;

            case PLS:
                if ( line.startsWith("File") && 0 < line.indexOf('=') )
                    return line;
                return "";

            default:
                return line;
        }
    }


    private String selectUrlFromPlaylist(String url, int type) {

        List<String> lines = HttpGetter.httpGet(url);

        for (int i=0; i<lines.size(); i+= 1) {

            String line = lines.get(i);
            line = filter(line.trim(),type);
            lines.set(i, line);
        }

        @SuppressWarnings("unchecked")
        List<String> links = selectUrlsFromList(lines);
        if ( links.size() == 0 )
            return null;

        //  for (int i=0; i<links.size(); i+= 1)

        // if ( random == null )
        //    random = new Random();

        // return links.get(random.nextInt(links.size()));

        return links.get(0);
    }



    private static ArrayList selectUrlsFromList(List<String> lines) {

        ArrayList links = new ArrayList<>();

        if ( url_pattern == null )
            url_pattern = Pattern.compile(url_regex);

        for (int i=0; i<lines.size(); i+=1)  {

            String line = lines.get(i);

            if ( 0 < line.length() ) {
                Matcher matcher = url_pattern.matcher(line);

                if ( matcher.find() ) {

                    String link = matcher.group();

                    if (link.startsWith("(") && link.endsWith(")"))
                        link = link.substring(1, link.length() - 1);

                    //noinspection unchecked
                    links.add(link);
                }
            }
        }

        return links;
    }

    private static Uri parseUri(String url) {
        return Uri.parse(url);
    }

    private static boolean isSuffix(String text, String suffix) {
        return text != null && text.endsWith(suffix) ;
    }

    private static boolean isSomeSuffix(String url, String suffix) {
        return isSuffix(url, suffix) || isSuffix(parseUri(url).getPath(), suffix);
    }

    private static int playlistType(String url) {

        String URL = url.toLowerCase();
        if ( isSomeSuffix(URL,".m3u"  ) ) return M3U;
        if ( isSomeSuffix(URL,".m3u8" ) ) return M3U;
        if ( isSomeSuffix(URL,".pls"  ) ) return PLS;
        return NONE;
    }

    public static boolean isPlaylistMimeType(String mime) {

        return mime != null && ("audio/x-scpls".equals(mime)
                || "audio/scpls".equals(mime)
                || "audio/x-mpegurl".equals(mime)
                || "audio/mpegurl".equals(mime)
                || "audio/mpeg-url".equals(mime)
                || "application/vnd.apple.mpegurl".equals(mime)
                || "application/x-winamp-playlist".equals(mime)
                || mime.contains("mpegurl")
                || mime.contains("mpeg-url")
                || mime.contains("scpls")
                || mime.indexOf("text/") == 0);

    }

}
