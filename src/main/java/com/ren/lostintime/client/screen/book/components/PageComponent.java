package com.ren.lostintime.client.screen.book.components;

import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

public abstract class PageComponent {

    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public PageComponent setX(int x) {
        this.x = x;
        updateBounds();
        return this;
    }

    public PageComponent setY(int y) {
        this.y = y;
        updateBounds();
        return this;
    }

    public PageComponent setWidth(int width) {
        this.width = width;
        updateBounds();
        return this;
    }

    public PageComponent setHeight(int height) {
        this.height = height;
        updateBounds();
        return this;
    }

    public PageComponent setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        updateBounds();
        return this;
    }

    public void updateBounds(){}

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, PrehistoricBookScreen screen);

}
