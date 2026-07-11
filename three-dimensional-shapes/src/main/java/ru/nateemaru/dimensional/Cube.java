package ru.nateemaru.dimensional;

public class Cube extends Shape3D {
    private final double side;

    public Cube(double side) {
        if (side <= 0) {
            throw new IllegalArgumentException(
                    "Side must be greater than zero"
            );
        }

        this.side = side;
    }

    @Override
    public double calculateSurfaceArea() {
        return 6 * side * side;
    }

    @Override
    public double calculateVolume() {
        return side * side * side;
    }
}
