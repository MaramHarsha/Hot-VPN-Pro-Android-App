package com.harsha.hotvpnpro;
/*Made By Harsha*/
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preference {
    public SharedPreferences appSharedPrefs;
    public SharedPreferences.Editor prefsEditor;
    Context context;
    boolean isSync;

    public Preference(Context context) {
        this.context = context;
        this.appSharedPrefs = context.getSharedPreferences("FireVPNPro_pref", Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public boolean checkPreferenceSet(String key_value) {
        return appSharedPrefs.contains(key_value);
    }

    public boolean isBooleenPreference(String key_value) {
        return appSharedPrefs.getBoolean(key_value, false);
    }

    public void setBooleanpreference(String key_value, boolean defult_value) {
        this.prefsEditor.putBoolean(key_value, defult_value).commit();
    }

    public int getIntpreference(String key_value) {
        return appSharedPrefs.getInt(key_value, 0);
    }

    public void setIntpreference(String key_value, int defult_value) {
        this.prefsEditor.putInt(key_value, defult_value).commit();
    }


    public String getStringpreference(String key_value, String default_value) {
        return appSharedPrefs.getString(key_value, default_value);
    }

    public String getStringpreference(String key_value) {
        return appSharedPrefs.getString(key_value, "");
    }


    public void setStringpreference(String key_value, String defult_value) {
        this.prefsEditor.putString(key_value, defult_value).commit();
    }

    public long getLongpreference(String key_value) {
        return appSharedPrefs.getLong(key_value, -1);
    }


    public void setLongpreference(String key_value, Long defult_value) {
        this.prefsEditor.putLong(key_value, defult_value).commit();
    }

}
