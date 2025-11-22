package com.epicycles.utils;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class WindowStyler {

    public static void applyFullscreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    public static void hideStatusBar(Activity activity) {
        Window window = activity.getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public static void setBlackNavigationBar(Activity activity) {
        activity.getWindow().setNavigationBarColor(
                activity.getResources().getColor(android.R.color.black)
        );
    }
}
