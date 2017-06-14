package ca.dal.group5.jukefit.Preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by lockhart on 2017-06-14.
 */

public class PreferencesService {

    private static final String SHARED_NAME = "ca.dal.group5.jukefit";
    private static final String DEVICE_ID = "DEVICE_ID";

    private SharedPreferences prefs;

    public PreferencesService(Activity activity) {
        prefs = activity.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
    }

    public String getDeviceID() {
        if (!prefs.contains(DEVICE_ID)) {
            SharedPreferences.Editor prefs_editor = prefs.edit();
            prefs_editor.putString("DEVICE_ID", UUID.randomUUID().toString());
            prefs_editor.commit();
        }
        return prefs.getString(DEVICE_ID, null);
    }
}
