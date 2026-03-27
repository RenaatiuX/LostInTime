package com.ren.lostintime.client.screen.book;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

public abstract class Page{

    public int x;
    public int y;
    public int width;
    public int height;

    public void setBounds(Rectangle rect) {
        setBounds(rect.x, rect.y, rect.width, rect.height);
    }

    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        updateBounds();
    }

    public void updateBounds(){}

    public boolean isInside(int x, int y) {
        return getBounds().contains(x, y);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean onClick(int mouseX, int mouseY, int button){
        return false;
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, PrehistoricBookScreen screen);
}
