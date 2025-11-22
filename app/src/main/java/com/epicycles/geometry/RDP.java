package com.epicycles.geometry;

import android.graphics.Point;
import java.util.ArrayList;
import java.util.List;

public class RDP {

    public static List<Point> simplify(List<Point> points, double epsilon) {
        if (points.size() < 2) {
            return new ArrayList<>(points);
        }

        double maxDistance = 0;
        int index = 0;
        for (int i = 1; i < points.size() - 1; i++) {
            double distance = perpendicularDistance(points.get(i), points.get(0), points.get(points.size() - 1));
            if (distance > maxDistance) {
                maxDistance = distance;
                index = i;
            }
        }

        if (maxDistance > epsilon) {
            List<Point> left = simplify(points.subList(0, index + 1), epsilon);
            List<Point> right = simplify(points.subList(index, points.size()), epsilon);

            List<Point> result = new ArrayList<>(left);
            result.remove(result.size() - 1);
            result.addAll(right);

            return result;
        } else {
            List<Point> result = new ArrayList<>();
            result.add(points.get(0));
            result.add(points.get(points.size() - 1));
            return result;
        }
    }



    private static double perpendicularDistance(Point point, Point lineStart, Point lineEnd) {
        double dx = lineEnd.x - lineStart.x;
        double dy = lineEnd.y - lineStart.y;

        if (dx == 0 && dy == 0) {
            return Math.sqrt(Math.pow(point.x - lineStart.x, 2) + Math.pow(point.y - lineStart.y, 2));
        }

        double t = ((point.x - lineStart.x) * dx + (point.y - lineStart.y) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));

        double closestX = lineStart.x + t * dx;
        double closestY = lineStart.y + t * dy;

        return Math.sqrt(Math.pow(point.x - closestX, 2) + Math.pow(point.y - closestY, 2));
    }
}
