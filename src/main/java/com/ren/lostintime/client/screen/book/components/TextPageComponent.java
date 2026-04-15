package com.ren.lostintime.client.screen.book.components;

import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class TextPageComponent extends PageComponent {

    private final Component text;
    private final int color;
    private final boolean centered;
    private List<FormattedCharSequence> cachedLines;

    public TextPageComponent(Component text, int color, boolean centered) {
        this.text = text;
        this.color = color;
        this.centered = centered;
    }

    @Override
    public void updateBounds() {
        Font font = Minecraft.getInstance().font;
        if (this.width > 0) {
            this.cachedLines = font.split(this.text, this.width);
            this.height = this.cachedLines.size() * font.lineHeight;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, PrehistoricBookScreen screen) {
        if (this.cachedLines == null || this.cachedLines.isEmpty()) return;

        int currentY = this.y;
        for (FormattedCharSequence line : this.cachedLines) {
            int drawX = this.x;
            if (this.centered) {
                drawX += (this.width - font.width(line)) / 2;
            }
            graphics.drawString(font, line, drawX, currentY, this.color, false);
            currentY += font.lineHeight;
        }
    }
}
