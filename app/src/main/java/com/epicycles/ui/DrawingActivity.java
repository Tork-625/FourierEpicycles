package com.epicycles.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.epicycles.utils.WindowStyler;

public class DrawingActivity extends AppCompatActivity {

    public static DrawingView panel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowStyler.applyFullscreen(this);
        WindowStyler.hideStatusBar(this);
        WindowStyler.setBlackNavigationBar(this);

        panel = new DrawingView(this, null);
        setContentView(panel);
    }
}
