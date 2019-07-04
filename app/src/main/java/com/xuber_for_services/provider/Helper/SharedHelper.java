package com.xuber_for_services.provider.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedHelper {

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public static final String PREFS_NAME = "JustDail";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    public static void putKey(Context context, String Key, String Value) {
        sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.commit();

    }


    public static void putKYC(Context context, Boolean kyc) {

        sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //editor.putString(Key, Value);
     //   editor.putString(kyc, false);
        editor.commit();

    }
    public static String getKey(Context contextGetKey, String Key) {
        sharedPreferences = contextGetKey.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        String Value = sharedPreferences.getString(Key, "");
        return Value;

    }

}
