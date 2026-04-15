package com.ren.lostintime.client.screen.book.components;

import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.Inset;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class LinePageComponent extends PageComponent{

    public enum Orientation{
        VERTICAL,
        HORIZONTAL
    }
    private final Orientation orientation;
    private final int color;

    public LinePageComponent(Orientation orientation, int color) {
        this.orientation = orientation;
        this.color = color;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, Inset inset, Dimension dimension, PrehistoricBookScreen screen) {
        var bounds = getBounds();
        graphics.fill((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getMaxX(), (int) bounds.getMaxY(), color);
    }
}
