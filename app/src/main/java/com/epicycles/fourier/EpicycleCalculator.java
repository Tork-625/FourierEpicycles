package com.epicycles.fourier;

import android.graphics.Point;
import com.epicycles.utils.ComplexNumber;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EpicycleCalculator {
    public List<ComplexNumber> complexPoints;

    public EpicycleCalculator(){
        this.complexPoints = new ArrayList<>();
    }



    public List<Point> normalize(int screenWidth, int screenHeight, List<Point> points) {
        int screenCenterX = screenWidth / 2;
        int screenCenterY = screenHeight / 2;

        // Calculating centroid
        double sumX = 0, sumY = 0;
        for (Point point : points) {
            sumX += point.x;
            sumY += point.y;
        }
        double centerX = sumX / points.size();
        double centerY = sumY / points.size();

        // Translating points to center (0, 0)
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            int normalizedX = (int) (point.x - centerX);
            int normalizedY = (int) (point.y - centerY);
            points.set(i, new Point(normalizedX, normalizedY));
        }

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            points.set(i, new Point(point.x + screenCenterX, point.y + screenCenterY));
        }

        return points;
    }



    public void toComplexNumbers(List<Point> points) {
        complexPoints.clear();
        for (Point point : points) {
            complexPoints.add(new ComplexNumber(point.x, point.y));
        }
    }



    public List<FourierComponent> computeDFT() {
        List<FourierComponent> components = new ArrayList<>();
        int N = complexPoints.size();

        for (int k = -N/2; k < N/2; k++) {
            ComplexNumber sum = new ComplexNumber(0, 0);
            for (int n = 0; n < N; n++) {
                double angle = (-2 * Math.PI * k * n) / N;
                ComplexNumber w = new ComplexNumber(Math.cos(angle), Math.sin(angle));
                sum = sum.add(complexPoints.get(n).multiply(w));
            }
            sum = sum.divide(N);
            components.add(new FourierComponent(k, sum));
        }

        components = sortByAmplitude(components);
        return components;
    }



    public List<FourierComponent> sortByAmplitude(List<FourierComponent> components) {
        Collections.sort(components, (a, b) -> Double.compare(b.getAmplitude(), a.getAmplitude()));
        return components;
    }
}
