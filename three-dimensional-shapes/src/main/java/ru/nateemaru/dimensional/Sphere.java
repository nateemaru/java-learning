package ru.nateemaru.dimensional;

public class Sphere extends Shape3D {
    private final double radius;

    public Sphere(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException(
                    "Radius must be greater than zero"
            );
        }

        this.radius = radius;
    }

    @Override
    public double calculateSurfaceArea() {
        return 4 * Math.PI * radius * radius;
    }

    @Override
    public double calculateVolume() {
        return 4.0 / 3.0 * Math.PI * radius * radius * radius;
    }
}
