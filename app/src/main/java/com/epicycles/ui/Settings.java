package com.epicycles.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.epicycles.R;
import com.epicycles.utils.PreferencesManager;

import android.content.SharedPreferences;

public class Settings extends AppCompatActivity {

    private Switch fpsToggle;
    private EditText fpsInput;

    private SeekBar speedSlider;
    private TextView speedLabel;

    private SeekBar precisionSlider;
    private TextView precisionLabel;

    private static final String PREF_SHOW_FPS = "show_fps";
    private static final String PREF_FPS_VALUE = "fps_value";

    private static final String PREF_SPEED_VALUE = "speed_value";
    private static final String PREF_SPEED_TEXT = "speed_text";

    private static final String PREF_PRECISION_VALUE = "precision_value";
    private static final String PREF_PRECISION_TEXT = "precision_text";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        prefs = PreferencesManager.get(this);

        initViews();
        setupFPSInput();
        setupToggle();
        setupSpeedSlider();
        setupPrecisionSlider();
        setupWindowUI();
        loadSavedValues();
    }

    private void initViews() {
        fpsToggle = findViewById(R.id.switchFPS);
        fpsInput = findViewById(R.id.EditTextFPS);

        speedSlider = findViewById(R.id.speedBar);
        speedLabel = findViewById(R.id.speedText);

        precisionSlider = findViewById(R.id.precisionBar);
        precisionLabel = findViewById(R.id.precisionText);
    }

    private void setupFPSInput() {
        fpsInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        fpsInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(9) });

        fpsInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable == null || editable.toString().isEmpty()) return;

                try {
                    int fps = Integer.parseInt(editable.toString());
                    prefs.edit().putInt(PREF_FPS_VALUE, fps).apply();
                } catch (Exception e) {
                    Log.e("Settings", "Invalid FPS");
                }
            }
        });
    }

    private void setupToggle() {
        fpsToggle.setOnCheckedChangeListener((CompoundButton button, boolean isChecked) -> {
            prefs.edit().putBoolean(PREF_SHOW_FPS, isChecked).apply();
        });
    }

    private void setupSpeedSlider() {
        speedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedLabel.setText(progress + "/100");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int speed = seekBar.getProgress();
                prefs.edit()
                        .putInt(PREF_SPEED_VALUE, speed)
                        .putString(PREF_SPEED_TEXT, speed + "/100")
                        .apply();
            }
        });
    }

    private void setupPrecisionSlider() {
        precisionSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                precisionLabel.setText(progress + "/10");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int precision = seekBar.getProgress();
                prefs.edit()
                        .putInt(PREF_PRECISION_VALUE, precision)
                        .putString(PREF_PRECISION_TEXT, precision + "/10")
                        .apply();
            }
        });
    }

    private void setupWindowUI() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));
        window.setNavigationBarColor(getResources().getColor(android.R.color.black));
        window.getDecorView().setSystemUiVisibility(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
        );
    }

    private void loadSavedValues() {
        boolean showFPS = prefs.getBoolean(PREF_SHOW_FPS, true);
        int fpsValue = prefs.getInt(PREF_FPS_VALUE, 60);
        int speedValue = prefs.getInt(PREF_SPEED_VALUE, 50);
        int precisionValue = prefs.getInt(PREF_PRECISION_VALUE, 10);

        fpsToggle.setChecked(showFPS);
        fpsInput.setText(String.valueOf(fpsValue));

        speedSlider.setProgress(speedValue);
        speedLabel.setText(speedValue + "/100");

        precisionSlider.setProgress(precisionValue);
        precisionLabel.setText(precisionValue + "/10");
    }
}
