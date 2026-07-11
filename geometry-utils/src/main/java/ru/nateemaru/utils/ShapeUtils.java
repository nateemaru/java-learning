package ru.nateemaru.utils;

import ru.nateemaru.library.Shape;

import java.util.Comparator;

public final class ShapeUtils {

    private ShapeUtils() {
    }

    public static int compareByArea(Shape first, Shape second) {
        validateShapes(first, second);

        return Double.compare(
                first.calculateArea(),
                second.calculateArea()
        );
    }

    public static int compareByPerimeter(Shape first, Shape second) {
        validateShapes(first, second);

        return Double.compare(
                first.calculatePerimeter(),
                second.calculatePerimeter()
        );
    }

    public static Shape findLargerByArea(Shape first, Shape second) {
        return compareByArea(first, second) >= 0 ? first : second;
    }

    public static Shape findLargerByPerimeter(Shape first, Shape second) {
        return compareByPerimeter(first, second) >= 0 ? first : second;
    }

    public static final Comparator<Shape> BY_AREA =
            Comparator.comparingDouble(Shape::calculateArea);

    public static final Comparator<Shape> BY_PERIMETER =
            Comparator.comparingDouble(Shape::calculatePerimeter);

    private static void validateShapes(Shape first, Shape second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Shapes must not be null");
        }
    }
}
