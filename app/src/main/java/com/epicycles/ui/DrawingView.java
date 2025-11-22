package com.epicycles.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.epicycles.fourier.FourierComponent;
import com.epicycles.input.SketchHandler;
import com.epicycles.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawingView extends View {
    public int screenHeight;
    public int screenWidth;
    public List<Point> points;
    public List<Point> drawPoints;
    public Bitmap trailBitmap;
    public Canvas trailCanvas;
    public enum Mode { DRAW, ANIMATE }
    public Mode mode = Mode.DRAW;
    public int FPS;
    public Point previousPoint = null;
    public boolean isDrawing = false;
    public double timeStep = 0;
    public int panelCenterX;
    public int panelCenterY;
    public double[][] epicycle_info;
    public List<Point> simplifiedPoints = new ArrayList<>();
    private Paint trailPaint;
    private Paint fadePaint;
    private SketchHandler mouseH;
    private boolean displayFPS;
    private List<Integer> colorList;


    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SharedPreferences prefs = PreferencesManager.get(context);
        displayFPS = prefs.getBoolean("show_fps", true);

        setBackgroundColor(Color.TRANSPARENT);
        initScreenSize(context);
        initTouchHandler();
        initColorList();
        initPaints();

        invalidate();
    }



    private void initScreenSize(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        if (display != null) {
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
            panelCenterX = screenWidth / 2;
            panelCenterY = screenHeight / 2;
        }
    }



    private void initTouchHandler() {
        points = new ArrayList<>();
        mouseH = new SketchHandler(points, this);
        setOnTouchListener(mouseH);
        setFocusable(true);
    }



    private void initColorList() {
        int[] solidColors = new int[]{
                Color.MAGENTA, Color.RED,
                Color.rgb(255, 20, 147),
                Color.rgb(255, 99, 71),
                Color.rgb(255, 255, 0),
                Color.rgb(0, 255, 255),
                Color.rgb(255, 0, 255),
                Color.rgb(255, 105, 180),
                Color.rgb(255, 69, 0),
                Color.rgb(32, 178, 170),
                Color.rgb(244, 164, 96),
                Color.rgb(255, 228, 181),
                Color.rgb(173, 216, 230),
                Color.rgb(255, 228, 225),
        };

        colorList = new ArrayList<>();
        for (int c : solidColors) colorList.add(c);
        Collections.shuffle(colorList);
    }



    private void initPaints() {
        fadePaint = new Paint();
        fadePaint.setColor(Color.BLACK);
        fadePaint.setAlpha(15);
        fadePaint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_OUT));

        trailPaint = new Paint();
        trailPaint.setColor(Color.WHITE);
        trailPaint.setStrokeWidth(10);
        trailPaint.setStyle(Paint.Style.STROKE);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        trailBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        trailCanvas = new Canvas(trailBitmap);
    }



    public void setComponents(List<FourierComponent> components) {
        epicycle_info = new double[components.size()][3];

        int i = 0;
        for (FourierComponent fc : components) {
            epicycle_info[i][0] = fc.getAmplitude();
            epicycle_info[i][1] = fc.getPhase();
            epicycle_info[i][2] = fc.frequency;
            i++;
        }

        drawPoints = new ArrayList<>();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.rgb(64, 64, 64));
        drawBackgroundGrid(canvas);

        if (mode == Mode.DRAW) {
            drawDrawMode(canvas);
        } else if (mode == Mode.ANIMATE) {
            drawAnimationMode(canvas);
        }

        invalidate();
    }



    private void drawBackgroundGrid(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(0.5f);

        int gridSize = 80;

        for (int x = 0; x <= getWidth(); x += gridSize)
            canvas.drawLine(x, 0, x, screenHeight * 2, paint);

        for (int y = 0; y <= getHeight(); y += gridSize)
            canvas.drawLine(0, y, screenWidth * 2, y, paint);
    }



    private void drawDrawMode(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(25);

        if (!isDrawing) {
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(100);
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("You can Draw Here!", screenWidth / 2, screenHeight / 2, textPaint);
        }

        for (Point p : simplifiedPoints)
            canvas.drawCircle(p.x, p.y, 4, paint);

        for (Point p : points)
            canvas.drawCircle(p.x, p.y, 4, paint);
    }



    private void drawAnimationMode(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (displayFPS) drawFPS(canvas);

        float prevX = panelCenterX;
        float prevY = panelCenterY;

        paint.setColor(Color.rgb(255, 127, 80));
        canvas.drawCircle(prevX, prevY, 40, paint);

        for (int i = 1; i < epicycle_info.length; i++) {
            double amp = epicycle_info[i][0];
            double phase = epicycle_info[i][1];
            double freq = epicycle_info[i][2];

            double angle = (2 * Math.PI * freq * timeStep) + phase;

            float x = (float) (prevX + amp * Math.cos(angle));
            float y = (float) (prevY + amp * Math.sin(angle));

            paint.setStrokeWidth(6);
            paint.setColor(Color.GREEN);
            canvas.drawLine(prevX, prevY, x, y, paint);

            paint.setStrokeWidth(7);
            paint.setColor(colorList.get(i % colorList.size()));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(prevX, prevY, (float) amp / 2, paint);

            prevX = x;
            prevY = y;
        }

        Point curr = new Point((int) prevX, (int) prevY);

        paint.setStrokeWidth(10);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        if (trailBitmap != null)
            canvas.drawBitmap(trailBitmap, 0, 0, null);

        if (previousPoint != null)
            addLine(curr.x, curr.y, paint);

        addPoint(curr.x, curr.y, paint);
        previousPoint = curr;
    }



    private void drawFPS(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(Color.YELLOW);
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("FPS: " + FPS, 50, 120, textPaint);
    }



    public void addPoint(int x, int y, Paint paint) {
        if (trailCanvas != null) {
            paint.setStyle(Paint.Style.FILL);
            trailCanvas.drawCircle(x, y, 5, paint);
        }
    }
    public void addLine(int x, int y, Paint paint) {
        if (trailCanvas != null) {
            paint.setStrokeWidth(14);
            trailCanvas.drawLine(previousPoint.x, previousPoint.y, x, y, paint);
        }
    }
}
