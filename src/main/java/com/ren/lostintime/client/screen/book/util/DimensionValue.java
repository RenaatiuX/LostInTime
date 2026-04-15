package com.ren.lostintime.client.screen.book.util;

/**
 * Represents a single dimension value (width or height) which can be absolute, 
 * a percentage of the parent, or a fill value to take up remaining space.
 */
public record DimensionValue(float value, Unit unit) {

    public enum Unit {
        NONE,
        PIXEL,
        PERCENT,
        FILL
    }

    /**
     *
     * @return a dimension which will prevent the dimension from setting this value
     */
    public static DimensionValue none() {
        return new DimensionValue(0, Unit.NONE);
    }

    public static DimensionValue px(float pixels) {
        return new DimensionValue(pixels, Unit.PIXEL);
    }

    /**
     * @param percent A value between 0.0 and 1.0 (e.g. 0.5f for 50%)
     */
    public static DimensionValue percent(float percent) {
        return new DimensionValue(percent, Unit.PERCENT);
    }

    /**
     * Instructs the component to fill the remaining available space.
     */
    public static DimensionValue fill() {
        return new DimensionValue(1.0f, Unit.FILL);
    }

    /**
     * Resolves the dimension into absolute pixels based on the context.
     *
     * @param parentSize     The total size of the parent (used for percentages).
     * @param availableSpace The remaining space in the parent (used for fill).
     */
    public int resolve(int parentSize, int availableSpace) {
        return switch (unit) {
            case PIXEL -> Math.round(value);
            case PERCENT -> Math.round(parentSize * value);
            case FILL -> availableSpace;
            case NONE -> 0;
        };
    }
}