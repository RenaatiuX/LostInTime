package com.ren.lostintime.client.screen.book;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public abstract class Page {

    public final String name;

    public Page(String name) {
        this.name = name;
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, Font font, PrehistoricBookScreen screen);
}
