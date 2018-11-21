package org.oucho.radio2.radio;


public interface RadioKeys {

    String APPLICATION_NAME = "Radio";

    int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    String DB_NAME = "WebRadio";
    String TABLE_NAME =  "WebRadio";

    String PREF_FILE = "org.oucho.radio2_preferences";

    String ACTION_PLAY = "play";
    String ACTION_STOP = "stop";
    String ACTION_PAUSE = "pause";
    String ACTION_RESTART = "restart";
    String ACTION_QUIT = "quit";

    String USER_AGENT = "Radio/2.0 (Android 6+)";

    String INTENT_STATE = "org.oucho.radio2.INTENT_STATE";
    String INTENT_TITRE = "org.oucho.radio2.INTENT_TITRE";
    String INTENT_ERROR = "org.oucho.radio2.INTENT_ERROR";
    String INTENT_SEARCH = "org.oucho.radio2.INTENT_SEARCH";
    String INTENT_FOCUS = "org.oucho.radio2.INTENT_FOCUS";
    String INTENT_HOME = "org.oucho.radio2.INTENT_HOME";

    String HOME = "http://opml.radiotime.com";

    String INTENT_ADD_RADIO = "org.oucho.radio2.ADD_RADIO";
    String INTENT_UPDATENOTIF = "org.oucho.radio2.INTENT_STATE";
    String INTENT_CONTROL_STOP = "org.oucho.radio2.INTENT_STOP";
    String INTENT_CONTROL_PAUSE = "org.oucho.radio2.INTENT_PAUSE";
    String INTENT_CONTROL_RESTART = "org.oucho.radio2.INTENT_RESTART";


    String APK_DOWNLOAD_URL = "url";
    String APK_UPDATE_CONTENT = "updateMessage";
    String APK_VERSION_CODE = "versionCode";

    String UPDATE_TAG = "UpdateChecker";
    String URL_UPDATE = "http://oucho.free.fr/app_android/Radio/radio2.update";

    int TYPE_DIALOG = 1;
    int TYPE_SNACK = 2;

}
