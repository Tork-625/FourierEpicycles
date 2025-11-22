package com.epicycles.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.epicycles.R;
import com.epicycles.utils.WindowStyler;

public class ScreenBuilder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_title);

        WindowStyler.applyFullscreen(this);
        WindowStyler.setBlackNavigationBar(this);
    }

    public void bring_drawing_screen(View view) {
        startActivity(new Intent(this, DrawingActivity.class));
    }

    public void goSettings(View view) {
        startActivity(new Intent(this, Settings.class));
    }
}
