package com.ren.lostintime.client.screen.book;

/**
 * Represents a dynamic layout dimension.
 * Percentages should be provided as a float from 0.0 to 1.0 (e.g., 0.5f for 50%).
 */
public record LayoutValue(float value, Unit unit) {

    public enum Unit {
        PIXEL,
        PERCENT_COMPONENT,
        PERCENT_PARENT
    }

    public static final LayoutValue ZERO = px(0);

    public static LayoutValue px(float pixels) {
        return new LayoutValue(pixels, Unit.PIXEL);
    }

    public static LayoutValue percentOfComponent(float percent) {
        return new LayoutValue(percent, Unit.PERCENT_COMPONENT);
    }

    public static LayoutValue percentOfParent(float percent) {
        return new LayoutValue(percent, Unit.PERCENT_PARENT);
    }

    /**
     * Calculates the absolute pixel value based on the layout context.
     *
     * @param componentSize The width or height of the component.
     * @param parentSize      The width or height of the page.
     * @return The resolved absolute pixel value.
     */
    public int resolve(int componentSize, int parentSize) {
        return switch (unit) {
            case PIXEL -> Math.round(value);
            case PERCENT_COMPONENT -> Math.round(componentSize * value);
            case PERCENT_PARENT -> Math.round(parentSize * value);
        };
    }
}