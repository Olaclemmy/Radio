/*
 * Radio - Internet radio for android
 * Copyright (C) 2017  Old-Geek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.oucho.radio2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.System;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.oucho.radio2.update.UpdateChecker;
import org.oucho.radio2.db.Radio;
import org.oucho.radio2.db.RadiosDatabase;
import org.oucho.radio2.dialog.AboutDialog;
import org.oucho.radio2.dialog.FilePickerDialog;
import org.oucho.radio2.dialog.PermissionDialog;
import org.oucho.radio2.radio.RadioAdapter;
import org.oucho.radio2.radio.RadioKeys;
import org.oucho.radio2.radio.RadioService;
import org.oucho.radio2.tunein.TuneInFragment;
import org.oucho.radio2.tunein.adapters.BaseAdapter.OnItemClickListener;
import org.oucho.radio2.utils.ImageFactory;
import org.oucho.radio2.utils.LogoCache;
import org.oucho.radio2.utils.State;
import org.oucho.radio2.utils.VolumeTimer;
import org.oucho.radio2.view.CustomLayoutManager;
import org.oucho.radio2.view.SeekArc;
import org.oucho.radio2.xml.DatabaseSave;
import org.oucho.radio2.xml.ReadXML;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements RadioKeys, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Context mContext;
    private ActionBar actionBar;
    private SharedPreferences preferences;

    private String radio_name;
    private String playing_state;
    private String import_export_radio_list;
    private final String app_music = "org.oucho.musicplayer";

    private boolean isFocusedSearch;
    private boolean showBitrate = false;
    private boolean mpd_app_is_installed = false;
    private boolean music_app_is_installed = false;
    private static boolean running;
    private static boolean bHome = false;

    private final Handler handlerScroll = new Handler();
    private final Handler handlerBitrate = new Handler();

    private View edit_radio_view;
    private RecyclerView mRecyclerView;
    private NavigationView mNavigationView;

    private TextView error0;
    private TextView error1;
    private TextView viewSleepTimer;
    private EditText editText;
    private ImageButton home;
    private ImageButton img_play;
    private ImageButton img_pause;

    private Bitmap logoRadio;
    private VolumeTimer volumeTimer;

    private CountDownTimer sleepTimerCounter;
    private DrawerLayout mDrawerLayout;
    private PlayerReceiver playerReceiver;
    private ScheduledFuture scheduledFuture;

    private RadioAdapter mAdapter;
    private VolumeControl niveau_Volume;
    private RelativeLayout searchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int mUIFlag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getWindow().getDecorView().setSystemUiVisibility(mUIFlag);
        }

        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(PREF_FILE, MODE_PRIVATE);

        int textColor = ContextCompat.getColor(mContext, R.color.colorPrimary);
        String title = mContext.getString(R.string.app_name);

        ColorDrawable colorDrawable = new ColorDrawable(textColor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(title);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.inflateMenu(R.menu.navigation);
        mNavigationView.setNavigationItemSelectedListener(this);

        music_app_is_installed = checkIfAppIsInstalled(app_music);

        String app_mpd = "org.oucho.mpdclient";
        mpd_app_is_installed = checkIfAppIsInstalled(app_mpd);

        setNavigationMenu();

        niveau_Volume = new VolumeControl(this, new Handler());
        getContentResolver().registerContentObserver(System.CONTENT_URI, true, niveau_Volume);

        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_STATE);
        filter.addAction(INTENT_TITRE);
        filter.addAction(INTENT_ERROR);
        filter.addAction(INTENT_HOME);

        registerReceiver(playerReceiver, filter);

        error0 = findViewById(R.id.error0);
        error1 = findViewById(R.id.error1);

        editText = findViewById(R.id.search_radio);
        searchLayout = findViewById(R.id.search_layout);

        volumeTimer = new VolumeTimer();

        mAdapter = new RadioAdapter();
        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new CustomLayoutManager(this));

        mAdapter.setOnItemClickListener(mOnItemClickListener);

        mRecyclerView.setAdapter(mAdapter);

        img_play = findViewById(R.id.play_radio);
        img_pause = findViewById(R.id.pause_radio);

        home = findViewById(R.id.home_button);

        home.setOnClickListener(this);

        this.findViewById(R.id.add_radio).setOnClickListener(this);
        this.findViewById(R.id.stop_radio).setOnClickListener(this);
        this.findViewById(R.id.play_radio).setOnClickListener(this);
        this.findViewById(R.id.pause_radio).setOnClickListener(this);

        this.findViewById(R.id.search_button).setOnClickListener(this);

        showCurrentBitRate();
        showBitrate = true;

        search();
        volume();
        State.getState(mContext);

        UpdateChecker.checkForSnack(this);
    }


    private void setNavigationMenu() {

        Menu navigatioMenu = mNavigationView.getMenu();

        if (music_app_is_installed) {
            navigatioMenu.setGroupVisible(R.id.add_music, true);
            navigatioMenu.setGroupVisible(R.id.haut_default, false);
        } else {
            navigatioMenu.setGroupVisible(R.id.add_music, false);
            navigatioMenu.setGroupVisible(R.id.haut_default, true);
        }
    }

    private void setHomeButton(Boolean value) {

        bHome = value;

        if (value) {
            home.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_home_grey_600_24dp));
        } else {
            home.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_list_grey_50_24dp));
        }
    }


    private boolean checkIfAppIsInstalled(String packagename) {
        PackageManager packageManager = getPackageManager();

        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


   /* **********************************************************************************************
    * Pause / résume / etc.
    * *********************************************************************************************/

    @Override
    protected void onPause() {
        super.onPause();

        if (showBitrate) {
            stopBitrate();
            showBitrate = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!showBitrate) {
            showCurrentBitRate();
            showBitrate = true;
        }

        if (State.isStopped()) {
            TextView status = findViewById(R.id.etat);
            playing_state = "Stop";
            assert status != null;
            status.setText(playing_state);
        }

        if (running)
            showTimeEcran();

        updateListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(niveau_Volume);

        if (showBitrate)
            stopBitrate();

        try {
            unregisterReceiver(playerReceiver);
        } catch (IllegalArgumentException ignore) {}
    }


    private void exit() {
        getContentResolver().unregisterContentObserver(niveau_Volume);

        if (showBitrate)
            stopBitrate();

        cancelSleepTimer();

        Intent player = new Intent(this, RadioService.class);
        player.putExtra("action", ACTION_STOP);
        startService(player);

        unregisterReceiver(playerReceiver);

        LogoCache.get().clear();

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.set_timer:
                if (!running) {
                    showDatePicker();
                } else {
                    showTimerInfo();
                }
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

         mDrawerLayout.closeDrawer(GravityCompat.START);

        switch (menuItem.getItemId()) {
            case R.id.nav_music:
                Intent music = getPackageManager().getLaunchIntentForPackage(app_music);
                startActivity(music);
                break;
            case R.id.nav_export:
            case R.id.nav_export0:
                exporter();
                break;
            case R.id.nav_import:
            case R.id.nav_import0:
                importer();
                break;
            case R.id.nav_update:
                UpdateChecker.checkForDialog(this);
                break;
            case R.id.nav_help:
                about();
                break;
            case R.id.nav_exit:
                exit();
                break;
            default:
                break;
        }
        return true;
    }


    private void loadSearch() {

        Fragment fragment = TuneInFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_in_bottom);
        ft.replace(R.id.content_main, fragment);
        ft.commit();

        Animation animFadeIn = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
        animFadeIn.setDuration(400);
        searchLayout.setAnimation(animFadeIn);
        searchLayout.setVisibility(View.VISIBLE);
    }

    private void search() {

        editText.setOnFocusChangeListener(focusListener);
        editText.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                setSearch();

                return true;
            }
            return false;
        });

    }

    private void setSearch() {

        String textSearch = editText.getText().toString();


        if (!textSearch.equals("")) {
            Intent search = new Intent();
            search.setAction(INTENT_SEARCH);
            search.putExtra("search", textSearch);
            sendBroadcast(search);

            View view = getCurrentFocus();

            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

    }

    private final View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            isFocusedSearch = hasFocus;
        }
    };


    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        if (isFocusedSearch) {

            Intent focus = new Intent();
            focus.setAction(INTENT_FOCUS);
            sendBroadcast(focus);

        } else {

            moveTaskToBack(true);
        }
    }

    /* **********************************************************************************************
    *
    * Broadcast receiver
    *
    * *********************************************************************************************/

    private class PlayerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String receiveIntent = intent.getAction();

            if (INTENT_HOME.equals(receiveIntent)) {
                boolean home = intent.getBooleanExtra("setButton", false);
                setHomeButton(home);
            }

            if (INTENT_ERROR.equals(receiveIntent)) {

                String erreur = intent.getStringExtra("error");

                error0.setVisibility(View.VISIBLE);
                error1.setVisibility(View.VISIBLE);
                error1.setText(erreur);
            }

            if (INTENT_TITRE.equals(receiveIntent)) {

                String titre = intent.getStringExtra("titre");

                if (titre == null)
                    return;

                actionBar.setTitle(Html.fromHtml(titre));

                if (titre.equals(getResources().getString(R.string.app_name))) {
                    error0.setVisibility(View.GONE);
                    error1.setVisibility(View.GONE);
                    error1.setText("");

                    Animation animFadeOut = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out);
                    animFadeOut.setDuration(400);
                    searchLayout.setAnimation(animFadeOut);
                    searchLayout.setVisibility(View.GONE);

                    updateListView();
                }
            }

            if (INTENT_STATE.equals(receiveIntent)) {

                TextView player_status = findViewById(R.id.etat);

                boolean closeApplication = intent.getBooleanExtra(ACTION_QUIT, false);
                playing_state = intent.getStringExtra("state");
                radio_name = intent.getStringExtra("name");

                if (closeApplication) {
                    exit();
                }


                // Traduction du texte
                String locale_string;
                if ("Play".equals(playing_state)) {
                    locale_string = context.getResources().getString(R.string.play);
                    img_play.setVisibility(View.INVISIBLE);
                    img_pause.setVisibility(View.VISIBLE);
                } else if ("Loading...".equals(playing_state)) {
                    locale_string = context.getResources().getString(R.string.loading);
                } else if ("Disconnected".equals(playing_state)){
                    locale_string = context.getResources().getString(R.string.disconnected);
                } else if ("Completed".equals(playing_state)){
                    locale_string = context.getResources().getString(R.string.completed);
                } else if ("Pause".equals(playing_state)){
                    locale_string = playing_state;
                    img_play.setVisibility(View.VISIBLE);
                    img_pause.setVisibility(View.INVISIBLE);
                } else if ("Stop".equals(playing_state)){
                    locale_string = playing_state;
                    img_play.setVisibility(View.VISIBLE);
                    img_pause.setVisibility(View.INVISIBLE);
                } else {
                    locale_string = playing_state;
                }

                player_status.setText(locale_string);

                updateRadioName();
                updatePlayPauseIcon();
                updateListView();

            }
        }
    }



   /* *********************************
    * Affiche le nom de la radio active
    * *********************************/

    private void updateRadioName() {

        TextView StationTextView = findViewById(R.id.station);

        if (radio_name == null)
            radio_name = preferences.getString("name", "");

        StationTextView.setText(radio_name);
    }


   /* ****************************
    * Changement d'état play/pause
    * ****************************/

    @SuppressWarnings("ConstantConditions")
    private void updatePlayPauseIcon() {
        ImageView img_equalizer = findViewById(R.id.icon_equalizer);

        if (State.isPlaying() || State.isPaused()) {
            img_equalizer.setBackground(getDrawable(R.drawable.ic_equalizer1));
        } else {
            img_equalizer.setBackground(getDrawable(R.drawable.ic_equalizer0));
        }
    }


   /* **********************************************************************************************
    * Gestion des clicks sur l'interface
    * *********************************************************************************************/

    @Override
    public void onClick(View v) {

        Intent action_player = new Intent(this, RadioService.class);


        switch (v.getId()) {
            case R.id.stop_radio:
                action_player.putExtra("action", ACTION_STOP);
                startService(action_player);
                break;
            case R.id.play_radio:
                switch (playing_state) {
                    case "Stop":
                        action_player.putExtra("action", ACTION_PLAY);
                        startService(action_player);
                        break;
                    case "Pause":
                        action_player.putExtra("action", ACTION_RESTART);
                        startService(action_player);
                        break;
                    default:
                        break;
                }
                break;

            case R.id.pause_radio:

                switch (playing_state) {
                    case "Play":
                        action_player.putExtra("action", ACTION_PAUSE);
                        startService(action_player);
                        break;
                    case "Pause":
                        action_player.putExtra("action", ACTION_RESTART);
                        startService(action_player);
                        break;
                    default:
                        break;
                }
                break;

            case R.id.add_radio:
                popupAddRadio(v);
                break;

            case R.id.search_button:
                setSearch();
                break;

            case R.id.home_button:

                Intent home = new Intent();
                home.setAction(INTENT_HOME);

                if (!bHome) {

                    home.putExtra("go", "back");
                } else {

                    home.putExtra("go", "home");
                }
                sendBroadcast(home);

                break;

            default:
                break;
        }
    }


    private void popupAddRadio(final View v) {

        final PopupMenu popup = new PopupMenu(MainActivity.this, v);

        popup.getMenuInflater().inflate(R.menu.add_radio, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {
                case R.id.add_browse:
                    loadSearch();
                    break;
                case R.id.add_manual:
                    editRadio(null);
                    break;
                default:
                    break;
            }

            return true;
        });

        popup.show();
    }

    private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(int position, View view) {
            Radio radio = mAdapter.getItem(position);

            switch (view.getId()) {
                case R.id.fond:
                    if ( !(State.isPlaying() && radio.getUrl().equals(RadioService.getUrl())) )
                        play(radio);
                    break;
                case R.id.buttonMenu:
                    popupRadioItem(view, radio);
                    break;
                default:
                    break;
            }
        }
    };


    private void popupRadioItem(final View v, Radio radio) {

        final PopupMenu popup = new PopupMenu(MainActivity.this, v);

        if (mpd_app_is_installed) {
            popup.getMenuInflater().inflate(R.menu.contextmenu_editdelete_mpd, popup.getMenu());
        } else {
            popup.getMenuInflater().inflate(R.menu.contextmenu_editdelete, popup.getMenu());
        }
        popup.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {
                case R.id.menu_edit:
                    editRadio(radio);
                    break;
                case R.id.menu_delete:
                    deleteRadio(radio);
                    break;
                case R.id.menu_add_mpd:
                    String radio_name = radio.getTitle();
                    String radio_url = radio.getUrl();
                    sendRadioToMpdApp(radio_name, radio_url);
                    break;
                default:
                    break;
            }

            return true;
        });

        popup.show();
    }


    private void sendRadioToMpdApp(String radio_name, String radio_url) {
        Intent sendRadioToMpd = new Intent();
        sendRadioToMpd.setAction("org.oucho.MPDclient.ADD_RADIO");
        sendRadioToMpd.putExtra("name", radio_name);
        sendRadioToMpd.putExtra("url", radio_url);
        mContext.sendBroadcast(sendRadioToMpd);
    }


    private void updateListView() {
        ArrayList<Radio> radioList = new ArrayList<>();
        radioList.addAll(Radio.getRadios(mContext));

        mAdapter.setData(radioList);

        handlerScroll.removeCallbacksAndMessages(null);

        handlerScroll.postDelayed(() -> {

            String url = RadioService.getUrl();

            if (RadioService.getUrl() == null) {
                url = preferences.getString("url", null);
            }

            for (int i = 0; i < radioList.size(); i++) {

                if (radioList.get(i).getUrl().equals(url))
                    mRecyclerView.smoothScrollToPosition(i);
            }

        }, 250);



    }


       /* **********************************************************************************************
    * Ajout ou édition d'une radio
    * *********************************************************************************************/

    @SuppressLint("InflateParams")
    private void editRadio(final Radio oldRadio) {

        logoRadio = null;

        AlertDialog.Builder edit_radio_dialog = new AlertDialog.Builder(this);

        int title = oldRadio == null ? R.string.addRadio : R.string.edit;

        edit_radio_dialog.setTitle(getResources().getString(title));

        edit_radio_view = getLayoutInflater().inflate(R.layout.layout_editwebradio, null);
        edit_radio_dialog.setView(edit_radio_view);

        final EditText editTextUrl = edit_radio_view.findViewById(R.id.editTextUrl);
        final EditText editTextName = edit_radio_view.findViewById(R.id.editTextName);
        final ImageView editLogo = edit_radio_view.findViewById(R.id.logo);
        final TextView text = edit_radio_view.findViewById(R.id.texte);

        editLogo.setOnClickListener(v -> addImg());

        if(oldRadio != null) {
            editTextUrl.setText(oldRadio.getUrl());
            editTextName.setText(oldRadio.getTitle());

            if (oldRadio.getLogo() != null ) {
                editLogo.setImageBitmap(logoRadio);
                editLogo.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                text.setVisibility(View.INVISIBLE);
                editLogo.setImageBitmap(ImageFactory.getImage(oldRadio.getLogo()));
                logoRadio = ImageFactory.getImage(oldRadio.getLogo());
            }
        }

        edit_radio_dialog.setPositiveButton(R.string.ok, (dialog, id) -> {
            String radio_url = editTextUrl.getText().toString();
            String radio_name = editTextName.getText().toString();
            byte[] radio_logo = null;

            if (logoRadio != null) {
                radio_logo = ImageFactory.getBytes(logoRadio);
            }

            if("".equals(radio_url) || "http://".equals(radio_url)) {
                Toast.makeText(mContext, R.string.errorInvalidURL, Toast.LENGTH_SHORT).show();
                return;
            }

            if("".equals(radio_name))
                radio_name = radio_url;

            if(oldRadio != null) {
                Radio.deleteRadio(mContext, oldRadio);
            }

            Radio newRadio = new Radio(radio_url, radio_name, radio_logo);
            Radio.addNewRadio(mContext, newRadio);
            updateListView();
        });

        edit_radio_dialog.setNegativeButton(R.string.cancel, (dialog, id) -> updateListView());


        AlertDialog dialog = edit_radio_dialog.create();
        //noinspection ConstantConditions
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

    }


   /* **********************************************************************************************
    * Volume observer
    * *********************************************************************************************/

    private class VolumeControl extends ContentObserver {
        private int previousVolume;
        private final Context context;

        private VolumeControl(Context context, Handler handler) {
            super(handler);
            this.context = context;

            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            assert audioManager != null;
            previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            volume();

            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            assert audio != null;
            int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

            int delta = previousVolume - currentVolume;

            if (delta > 0) {
                previousVolume = currentVolume;
            }
            else if(delta < 0) {
                previousVolume = currentVolume;
            }
        }
    }


    /* ******************************************
     * Gestion de l'affichage de l'icon de volumeTimer
     * ******************************************/

    private void volume() {

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        ImageView play = findViewById(R.id.icon_volume);

        if (currentVolume == 0) {
            assert play != null;
            play.setBackground(getDrawable(R.drawable.volume0));
        } else if (currentVolume < 4) {
            assert play != null;
            play.setBackground(getDrawable(R.drawable.volume1));
        } else if (currentVolume < 7) {
            assert play != null;
            play.setBackground(getDrawable(R.drawable.volume2));
        } else if (currentVolume < 10) {
            assert play != null;
            play.setBackground(getDrawable(R.drawable.volume3));
        } else if (currentVolume < 13) {
            assert play != null;
            play.setBackground(getDrawable(R.drawable.volume4));
        } else if (currentVolume < 16) {
            assert play != null;
            play.setBackground(getDrawable(R.drawable.volume5));
        }
    }



   /* **********************************************************************************************
    * Lecture de la radio
    * *********************************************************************************************/

    private void play(Radio radio) {

        String url = radio.getUrl();
        String name = radio.getTitle();
        byte[] logo = radio.getLogo();

        SharedPreferences.Editor edit = preferences.edit();

        Intent player = new Intent(this, RadioService.class);

        player.putExtra("action", ACTION_PLAY);
        player.putExtra("url", url);
        player.putExtra("name", name);
        startService(player);

        if (ImageFactory.byteToString(logo) != null) {

            String encodedImage = ImageFactory.byteToString(logo);

            Intent intent = new Intent();
            intent.setAction(INTENT_UPDATENOTIF);
            intent.putExtra("name", radio_name);
            intent.putExtra("state", "Play");
            intent.putExtra("logo", encodedImage);
            sendBroadcast(intent);

            edit.putString("image_data",encodedImage);
            edit.apply();

        } else {

            String encodedImage = ImageFactory.drawableResourceToBitmap(mContext, R.drawable.ic_radio_blue_grey_600_36dp);

            Intent intent = new Intent();
            intent.setAction(INTENT_UPDATENOTIF);
            intent.putExtra("name", radio_name);
            intent.putExtra("state", "Play");
            intent.putExtra("logo", encodedImage);
            sendBroadcast(intent);

            edit.remove("image_data");
            edit.apply();
        }
    }



   /* **********************************************************************************************
    * Suppression de la radio
    * *********************************************************************************************/

    private void deleteRadio(final Radio radio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.deleteRadioConfirm) + radio.getTitle());
        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            Radio.deleteRadio(mContext, radio);
            LogoCache.get().invalidate(radio.getUrl());
            updateListView();
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }




        /* *********************************************************************************************
     *
     * Sauvegarde/restauration/importation des radios
     *
     * ********************************************************************************************/

    private void exporter() {

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            checkWritePermission();
            import_export_radio_list = "exporter";
        } else {

            RadiosDatabase radiosDatabase = new RadiosDatabase(mContext);

            String Destination = Environment.getExternalStorageDirectory().toString() + "/Radio";

            File newRep = new File(Destination);

            boolean folderExisted = newRep.exists() || newRep.mkdir();

            try {

                if (!folderExisted) {
                    throw new IOException("Unable to create path");
                }
            } catch (IOException e) {
                Log.e("MainActivity", "Error: " + e);
            }

            String path = Destination + "/" + RadiosDatabase.DB_NAME + ".xml";

            DatabaseSave databaseSave = new DatabaseSave(radiosDatabase.getReadableDatabase(), path);
            databaseSave.exportData();

            Toast.makeText(mContext, getString(R.string.exporter), Toast.LENGTH_SHORT).show();


        }
    }



   /* **********************************************************************************************
    * Get showBitrate
    * *********************************************************************************************/

    private void showCurrentBitRate() {
        handlerBitrate.postDelayed(new Runnable() {

            public void run() {
                bitRate();
                handlerBitrate.postDelayed(this, 2000);
            }
        }, 1);
    }


    private void bitRate() {
        final int uid = android.os.Process.myUid();
        final long received = TrafficStats.getUidRxBytes(uid) / 1024;

        handlerBitrate.postDelayed(() -> {
            long current = TrafficStats.getUidRxBytes(uid) / 1024;
            long total = current - received;
            long ByteToBit = total * 8;
            TextView BitRate = findViewById(R.id.bitrate);

            if (ByteToBit <= 1024 ) {
                String bitrate = String.valueOf(ByteToBit);
                assert BitRate != null;
                BitRate.setText(bitrate + " Kb/s");
            } else {
                long megaBit = ByteToBit / 1024;
                String bitrate = String.valueOf(megaBit);
                assert BitRate != null;
                BitRate.setText(bitrate + " Mb/s");
            }
        }, 1000);
    }


    private void stopBitrate() {

        if (showBitrate) {
            handlerBitrate.removeCallbacksAndMessages(null);
            showBitrate = false;
        }
    }

   /* **********************************************************************************************
    * Sleep Timer
    * *********************************************************************************************/

    private void showDatePicker() {

        final String start = getString(R.string.start);
        final String cancel = getString(R.string.cancel);
        final SeekArc mSeekArc;
        final TextView mSeekArcProgress;

        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.date_picker_dialog, null);
        mSeekArc = view.findViewById(R.id.seekArc);
        mSeekArcProgress = view.findViewById(R.id.seekArcProgress);

        mSeekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch() {
                // vide, obligatoire
            }

            @Override
            public void onStartTrackingTouch() {
                // vide, obligatoire
            }

            @Override
            public void onProgressChanged(int progress) {
                String minute;

                if (progress <= 1){
                    minute = "minute";
                } else {
                    minute = "minutes";
                }

                String temps = String.valueOf(progress) + " " + minute;

                mSeekArcProgress.setText(temps);
            }
        });



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(start, (dialog, which) -> {

            int mins = mSeekArc.getProgress();
            startTimer(mins);
        });

        builder.setNegativeButton(cancel, (dialog, which) -> {
            // This constructor is intentionally empty, pourquoi ? parce que !
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


   /* ***********
    * Start timer
    * ***********/

    private void startTimer(final int minutes) {

        final String impossible = getString(R.string.impossible);
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final int delay = (minutes * 60) * 1000;

        if (delay == 0) {
            Toast.makeText(this, impossible, Toast.LENGTH_LONG).show();
            return;
        }

        scheduledFuture = scheduler.schedule(new StopTimer(), delay, TimeUnit.MILLISECONDS);

        RadioService.timerOnOff(true);
        running = true;
        State.getState(mContext);
        showTimeEcran();
        volumeTimer.volumeDown(scheduledFuture, delay);
    }


    public static class StopTimer implements Runnable {

        public void run() {

            running = false;

            Intent player = new Intent(RadioApplication.getInstance(), RadioService.class);
            player.putExtra("action", "stop");
            RadioApplication.getInstance().startService(player);

            RadioService.timerOnOff(false);
            State.getState(RadioApplication.getInstance());

        }
    }


   /* ***************************************
    * Afficher temps restant dans AlertDialog
    * ***************************************/

    private void showTimerInfo() {
        final String continuer = getString(R.string.continuer);
        final String cancelTimer = getString(R.string.cancel_timer);

        if (scheduledFuture.getDelay(TimeUnit.MILLISECONDS) < 0) {
            cancelSleepTimer();
            return;
        }

        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.timer_info_dialog, null);

        final TextView timeLeft = view.findViewById(R.id.time_left);

        final AlertDialog timerDialog = new AlertDialog.Builder(this).setPositiveButton(continuer, (dialog, which) -> dialog.dismiss()).setNegativeButton(cancelTimer, (dialog, which) -> cancelSleepTimer()).setView(view).create();

        new CountDownTimer(scheduledFuture.getDelay(TimeUnit.MILLISECONDS), 1000) {
            @Override
            public void onTick(long seconds) {

                long secondes = seconds;
                secondes = secondes / 1000;

                String textTemps = String.format(getString(R.string.timer_info),  ((secondes % 3600) / 60), ((secondes % 3600) % 60));

                timeLeft.setText(textTemps);
            }

            @Override
            public void onFinish() {
                timerDialog.dismiss();
            }
        }.start();

        timerDialog.show();
    }


   /* ********************************
    * Afficher temps restant à l'écran
    * ********************************/

    private void showTimeEcran() {

        viewSleepTimer = findViewById(R.id.sleep_timer);

        assert viewSleepTimer != null;
        viewSleepTimer.setVisibility(View.VISIBLE);

        try {
            sleepTimerCounter = new CountDownTimer(scheduledFuture.getDelay(TimeUnit.MILLISECONDS), 1000) {
                @Override
                public void onTick(long seconds) {

                    long secondes = seconds;

                    secondes = secondes / 1000;

                    String textTemps = "zZz " + String.format(getString(R.string.timer_info), ((secondes % 3600) / 60), ((secondes % 3600) % 60));

                    viewSleepTimer.setText(textTemps);
                }

                @Override
                public void onFinish() {
                    viewSleepTimer.setVisibility(View.INVISIBLE);
                }

            }.start();
        } catch (RuntimeException e) {
            running = false;
        }

    }


   /* ****************
    * Annuler le timer
    * ****************/

    private void cancelSleepTimer() {
        if (running) {
            scheduledFuture.cancel(true);
            sleepTimerCounter.cancel();
            sleepTimerCounter = null;

            volumeTimer.getVolumeTimer().cancel();
            volumeTimer.setVolume(1.0f);
        }

        running = false;

        RadioService.timerOnOff(false);
        State.getState(mContext);

        viewSleepTimer = findViewById(R.id.sleep_timer);
        assert viewSleepTimer != null;
        viewSleepTimer.setVisibility(View.INVISIBLE);

        scheduledFuture = null;
    }



    private void importer() {

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            checkWritePermission();
            import_export_radio_list = "importer";
        } else {

            new Handler().post(() -> FilePickerDialog.with(getSupportFragmentManager())
                    .type("xml")
                    .onFileSelected(path -> {

                        File file = new  File(path);

                        if(file.exists()){

                            ReadXML readXML = new ReadXML();
                            String XMLdata = readXML.readFile(file.getPath());
                            readXML.read(mContext, XMLdata);

                            updateListView();
                        }
                    }).show());



        }
    }


        /* **********************************************************************************************
    * Changer le logo de la radio
    * *********************************************************************************************/

    private void addImg() {

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            checkWritePermission();
            import_export_radio_list = "image";
        } else {

            FilePickerDialog.with(getSupportFragmentManager())
                    .type("image")
                    .onFileSelected(path -> {

                        File file = new  File(path);

                        if(file.exists()){

                            try {
                                logoRadio = ImageFactory.resize(BitmapFactory.decodeFile(file.getAbsolutePath()));

                                final ImageView logo = edit_radio_view.findViewById(R.id.logo);
                                final TextView text = edit_radio_view.findViewById(R.id.texte);

                                logo.setImageBitmap(logoRadio);
                                logo.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                                text.setVisibility(View.INVISIBLE);
                            } catch (NullPointerException ignored) {}

                        }
                    }).show();
        }
    }


    /***********************************************************************************************
     * AboutDialog dialog
     **********************************************************************************************/

    private void about() {
        AboutDialog dialog = new AboutDialog();
        dialog.show(getSupportFragmentManager(), "about");
    }

    private void checkWritePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            PermissionDialog permission = new PermissionDialog();
            permission.check(mContext, MainActivity.this);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {

            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResult == PackageManager.PERMISSION_GRANTED) {

                    switch (import_export_radio_list) {
                        case "importer":
                            importer();
                            break;
                        case "exporter":
                            exporter();
                            break;
                        case "image":
                            addImg();
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

}
