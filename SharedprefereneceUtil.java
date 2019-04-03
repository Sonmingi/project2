package com.example.user.jsouptest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by user on 2018-05-14.
 */

public class SharedprefereneceUtil {

    static final String PREF_NAME = "com.shared.pref";
    static Context mContext;

    public SharedprefereneceUtil(Context c) { mContext = c; }

    public void putSharedPreference(String key, int value) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getSharedPreference(String key, int value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        return pref.getInt(key, value);
    }

    public void putSharedPreference(String key, String value) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getSharedPreference(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        return pref.getString(key, value);
    }

    public static void putData
            (Context mContext, String key, String value)
    {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(mContext);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public static String getData
            (Context mContext, String key)
    {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(mContext);

        return prefs.getString(key, null);
    }


    public static void putDataBoolean
            (Context mContext, String key, Boolean value)
    {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(mContext);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(key, value);
        editor.commit();
    }

    public static Boolean getDataBoolean
            (Context mContext, String key)
    {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(mContext);

        return prefs.getBoolean(key, false);
    }

    public static void putDataInt
            (Context mContext, String key, int value)
    {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(mContext);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(key, value);
        editor.commit();
    }

    public static int getDataInt
            (Context mContext, String key)
    {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(mContext);

        return prefs.getInt(key, 100);
        // 100이 return 되면 오류.
    }
}