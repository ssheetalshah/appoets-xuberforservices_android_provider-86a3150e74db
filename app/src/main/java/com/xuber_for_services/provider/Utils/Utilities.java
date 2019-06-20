package com.xuber_for_services.provider.Utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Freeware Sys on 4/8/2017.
 */

public class Utilities {

    boolean showLog = false;

    public void print(String tag, String message) {
        if(showLog){
            Log.v(tag,message);
        }
    }

    public void hideKeypad(Context context, View view){
        // Check if no view has focus:
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
