package com.ren.lostintime.client.screen.book;

public record Inset(LayoutValue top, LayoutValue bottom, LayoutValue left, LayoutValue right) {

    public static final Inset ZERO = Inset.all(LayoutValue.px(0));

    /**
     * Applies the same LayoutValue to all 4 sides.
     */
    public static Inset all(LayoutValue value) {
        return new Inset(value, value, value, value);
    }

    /**
     * Applies symmetric vertical and horizontal LayoutValues.
     */
    public static Inset symmetric(LayoutValue vertical, LayoutValue horizontal) {
        return new Inset(vertical, vertical, horizontal, horizontal);
    }

    /**
     * Creates an Inset with specific top, bottom, left, and right LayoutValues.
     */
    public static Inset of(LayoutValue top, LayoutValue bottom, LayoutValue left, LayoutValue right) {
        return new Inset(top, bottom, left, right);
    }

    // --- Resolution Helpers ---

    public int resolveTop(int componentHeight, int parentHeight) {
        return top.resolve(componentHeight, parentHeight);
    }

    public int resolveBottom(int componentHeight, int parentHeight) {
        return bottom.resolve(componentHeight, parentHeight);
    }

    public int resolveLeft(int componentWidth, int parentWidth) {
        return left.resolve(componentWidth, parentWidth);
    }

    public int resolveRight(int componentWidth, int parentWidth) {
        return right.resolve(componentWidth, parentWidth);
    }
    
    public int resolveVerticalTotal(int componentHeight, int parentHeight) {
        return resolveTop(componentHeight, parentHeight) + resolveBottom(componentHeight, parentHeight);
    }

    public int resolveHorizontalTotal(int componentWidth, int parentWidth) {
        return resolveLeft(componentWidth, parentWidth) + resolveRight(componentWidth, parentWidth);
    }
}