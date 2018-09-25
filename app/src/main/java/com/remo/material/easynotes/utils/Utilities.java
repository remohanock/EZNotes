package com.remo.material.easynotes.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.remo.material.easynotes.R;

public class Utilities {
    public static void savePreferenceID(Context context, long id){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.idPreference),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("ID",id);
        editor.apply();
    }

    public static long getSharedPreferenceID(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.idPreference),Context.MODE_PRIVATE);
        return sharedPreferences.getLong("ID",0);
    }


}
