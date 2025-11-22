package com.epicycles.input;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import com.epicycles.animation.Animator;
import com.epicycles.fourier.EpicycleCalculator;
import com.epicycles.fourier.FourierComponent;
import com.epicycles.geometry.RDP;
import com.epicycles.ui.DrawingView;
import com.epicycles.utils.PreferencesManager;
import java.util.List;

public class SketchHandler implements View.OnTouchListener {

    private final List<Point> points;
    private final DrawingView panel;
    public Animator animate;

    private List<FourierComponent> components;

    private final double epsilon;

    public SketchHandler(List<Point> points, DrawingView panel) {
        this.points = points;
        this.panel = panel;

        SharedPreferences prefs = PreferencesManager.get(panel.getContext());

        int factor = prefs.getInt("precision_value", 10) * 10;
        if (factor > 99) factor = 99;

        this.epsilon = (100 - factor);
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_MOVE:
                panel.isDrawing = true;
                handleDrag(new Point((int) event.getX(), (int) event.getY()));
                return true;

            case MotionEvent.ACTION_UP:
                handleRelease();
                v.setOnTouchListener(null);
                return true;
        }
        return false;
    }



    private void handleDrag(Point currentPoint) {
        if (!points.isEmpty()) {
            Point last = points.get(points.size() - 1);
            interpolatePoints(last, currentPoint);
        }

        addRawPoint(currentPoint);
        panel.invalidate();
    }



    private void interpolatePoints(Point last, Point current) {
        int dx = current.x - last.x;
        int dy = current.y - last.y;

        int distance = (int) Math.sqrt(dx * dx + dy * dy);
        int step = 1;

        for (int i = 1; i <= distance / step; i++) {
            int x = last.x + (dx * i) / distance;
            int y = last.y + (dy * i) / distance;
            points.add(new Point(x, y));
        }
    }



    private void addRawPoint(Point current) {
        points.add(current);
    }



    private void handleRelease() {
        List<Point> simplified = simplifyCurve(points);
        points.clear();

        List<Point> looped = makeLoop(simplified);
        List<Point> normalized = normalizePoints(looped);

        List<FourierComponent> comps = computeFourier(normalized);
        beginAnimation(comps);
    }



    private List<Point> simplifyCurve(List<Point> pts) {
        return RDP.simplify(pts, epsilon);
    }



    private List<Point> makeLoop(List<Point> simplified) {
        List<Point> loop = new java.util.ArrayList<>(simplified);

        for (int i = simplified.size() - 1; i >= 0; i--) {
            loop.add(simplified.get(i));
        }
        return loop;
    }



    private List<Point> normalizePoints(List<Point> pts) {
        EpicycleCalculator cal = new EpicycleCalculator();
        return cal.normalize(panel.screenWidth, panel.screenHeight, pts);
    }



    private List<FourierComponent> computeFourier(List<Point> normalized) {
        EpicycleCalculator cal = new EpicycleCalculator();

        cal.toComplexNumbers(normalized);
        components = cal.computeDFT();

        return components;
    }



    private void beginAnimation(List<FourierComponent> comps) {
        panel.mode = DrawingView.Mode.ANIMATE;
        panel.setComponents(comps);
        panel.invalidate();

        animate = new Animator(comps, panel);
    }
}
