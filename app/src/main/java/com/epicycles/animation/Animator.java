package com.epicycles.animation;

import android.content.SharedPreferences;

import com.epicycles.fourier.FourierComponent;
import com.epicycles.ui.DrawingView;
import com.epicycles.utils.PreferencesManager;
import java.util.List;

public class Animator implements Runnable {

    public double delta = 0;
    public int FPS;
    public double drawInterval;
    public int drawCount;
    public long timer;
    public Thread drawThread;
    DrawingView panel;
    List<FourierComponent> components;
    private double speed;
    private double TOTAL_ANIMATION_DURATION = 5000; // Completes one cycle in 5 seconds
    private double timeStepIncrement;
    private final double TIME_STEP_LIMIT = 2 * Math.PI;



    public Animator(List<FourierComponent> components, DrawingView panel) {
        this.components = components;
        this.panel = panel;

        SharedPreferences prefs = PreferencesManager.get(panel.getContext());

        this.speed = prefs.getInt("speed_value", 50);
        this.FPS = prefs.getInt("fps_value", 60);
        // ---------------------------------------------------------

        drawInterval = 1000000000.0 / FPS;

        double totalCycle = 2 * Math.PI;
        this.timeStepIncrement = speed * ((totalCycle) / (TOTAL_ANIMATION_DURATION * FPS));

        drawThread = new Thread(this);
        startDrawThread();
    }



    public void startDrawThread() {
        drawThread.start();
        panel.setComponents(components);
    }



    public void run() {
        long lastTime = System.nanoTime();
        long currentTime;

        while (drawThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {

                panel.timeStep += timeStepIncrement;

                if (panel.timeStep >= TIME_STEP_LIMIT) {
                    panel.timeStep = 0;
                    panel.drawPoints.clear();

                    if (panel.trailBitmap != null && panel.trailCanvas != null) {
                        panel.trailBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
                    }
                }

                panel.invalidate();
                drawCount++;
                delta--;
            }

            if (timer >= 1000000000) {
                panel.FPS = drawCount;
                drawCount = 0;
                timer = 0;
            }
        }
    }
}
