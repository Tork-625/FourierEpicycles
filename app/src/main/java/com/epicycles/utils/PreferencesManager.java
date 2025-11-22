package com.epicycles.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }
}
