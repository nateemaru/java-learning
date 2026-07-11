import ru.nateemaru.dimensional.Cube;
import ru.nateemaru.dimensional.Shape3D;
import ru.nateemaru.dimensional.Sphere;
import ru.nateemaru.library.Circle;
import ru.nateemaru.library.Rectangle;
import ru.nateemaru.library.Shape;
import ru.nateemaru.library.Triangle;
import ru.nateemaru.utils.ShapeUtils;
import ru.nateemaru.utils.UnitConverter;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        demonstrateTwoDimensionalShapes();
        demonstrateThreeDimensionalShapes();
        demonstrateUnitConverter();
    }

    private static void demonstrateTwoDimensionalShapes() {
        System.out.println("=== Two-dimensional shapes ===");

        Shape circle = new Circle(5);
        Shape rectangle = new Rectangle(4, 6);
        Shape triangle = new Triangle(3, 4, 5);

        List<Shape> shapes = new ArrayList<>(
                List.of(circle, rectangle, triangle)
        );

        for (Shape shape : shapes) {
            printShapeInfo(shape);
        }

        Shape larger = ShapeUtils.findLargerByArea(circle, rectangle);

        System.out.printf(
                "%nLarger between circle and rectangle: %s, area: %.2f%n",
                larger.getClass().getSimpleName(),
                larger.calculateArea()
        );

        shapes.sort(ShapeUtils.BY_AREA.reversed());

        System.out.println("\nShapes sorted by area:");

        for (Shape shape : shapes) {
            System.out.printf(
                    "%s: %.2f%n",
                    shape.getClass().getSimpleName(),
                    shape.calculateArea()
            );
        }
    }

    private static void demonstrateThreeDimensionalShapes() {
        System.out.println("\n=== Three-dimensional shapes ===");

        Shape3D cube = new Cube(4);
        Shape3D sphere = new Sphere(3);

        List<Shape3D> shapes = List.of(cube, sphere);

        for (Shape3D shape : shapes) {
            System.out.printf(
                    "%s:%n  surface area: %.2f%n  volume: %.2f%n",
                    shape.getClass().getSimpleName(),
                    shape.calculateSurfaceArea(),
                    shape.calculateVolume()
            );
        }
    }

    private static void demonstrateUnitConverter() {
        System.out.println("\n=== Unit conversion ===");

        double centimeters = 250;
        double meters = UnitConverter.centimetersToMeters(centimeters);

        System.out.printf(
                "%.2f centimeters = %.2f meters%n",
                centimeters,
                meters
        );

        double squareCentimeters = 25_000;
        double squareMeters =
                UnitConverter.squareCentimetersToSquareMeters(
                        squareCentimeters
                );

        System.out.printf(
                "%.2f square centimeters = %.2f square meters%n",
                squareCentimeters,
                squareMeters
        );
    }

    private static void printShapeInfo(Shape shape) {
        System.out.printf(
                "%s:%n  area: %.2f%n  perimeter: %.2f%n",
                shape.getClass().getSimpleName(),
                shape.calculateArea(),
                shape.calculatePerimeter()
        );
    }
}
