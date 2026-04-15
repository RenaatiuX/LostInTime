package com.ren.lostintime.client.screen.book.util;

/**
 * Represents the width and height of a UI component.
 */
public record Dimension(DimensionValue width, DimensionValue height) {

    public static Dimension of(DimensionValue width, DimensionValue height) {
        return new Dimension(width, height);
    }

    /**
     * Creates a Dimension with absolute pixel values for both width and height.
     */
    public static Dimension absolute(float width, float height) {
        return new Dimension(DimensionValue.px(width), DimensionValue.px(height));
    }

    /**
     * Creates a Dimension that fills the remaining available space in both directions.
     */
    public static Dimension fill() {
        return new Dimension(DimensionValue.fill(), DimensionValue.fill());
    }
    
    public int resolveWidth(int parentWidth, int availableWidth) {
        return width.resolve(parentWidth, availableWidth);
    }
    
    public int resolveHeight(int parentHeight, int availableHeight) {
        return height.resolve(parentHeight, availableHeight);
    }
}