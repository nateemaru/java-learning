package ru.nateemaru.utils;

public final class UnitConverter {
    private static final double CENTIMETERS_IN_METER = 100.0;
    private static final double MILLIMETERS_IN_METER = 1000.0;

    private UnitConverter() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated"
        );
    }

    public static double centimetersToMeters(double centimeters) {
        return centimeters / CENTIMETERS_IN_METER;
    }

    public static double metersToCentimeters(double meters) {
        return meters * CENTIMETERS_IN_METER;
    }

    public static double millimetersToMeters(double millimeters) {
        return millimeters / MILLIMETERS_IN_METER;
    }

    public static double metersToMillimeters(double meters) {
        return meters * MILLIMETERS_IN_METER;
    }

    public static double squareCentimetersToSquareMeters(
            double squareCentimeters
    ) {
        return squareCentimeters
                / (CENTIMETERS_IN_METER * CENTIMETERS_IN_METER);
    }

    public static double squareMetersToSquareCentimeters(
            double squareMeters
    ) {
        return squareMeters
                * CENTIMETERS_IN_METER
                * CENTIMETERS_IN_METER;
    }
}