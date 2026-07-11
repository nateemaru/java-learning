package ru.nateemaru.library;

public class Triangle extends Shape {
    private final double a;
    private final double b;
    private final double c;

    public Triangle(double a, double b, double c) {
        if (a <= 0 || b <= 0 || c <= 0) {
            throw new IllegalArgumentException("The sides must be greater than zero");
        }

        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double calculateArea() {
        double semiPerimeter = calculatePerimeter() / 2;

        return Math.sqrt(
                semiPerimeter
                        * (semiPerimeter - a)
                        * (semiPerimeter - b)
                        * (semiPerimeter - c)
        );
    }

    @Override
    public double calculatePerimeter() {
        return a + b + c;
    }
}
