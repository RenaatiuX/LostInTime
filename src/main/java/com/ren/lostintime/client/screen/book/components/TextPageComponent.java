package com.ren.lostintime.client.screen.book.components;

import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.Inset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class TextPageComponent extends PageComponent {

    private final Component text;
    private float scale = 1f;
    private final int color;
    private final boolean centered;
    private int maxHeight = -1;
    private List<FormattedCharSequence> cachedLines;

    public TextPageComponent(Component text, int color, boolean centered) {
        this.text = text;
        this.color = color;
        this.centered = centered;
    }

    public TextPageComponent setMaxHeight(int maxHeight){
        this.maxHeight = maxHeight;
        return this;
    }

    @Override
    public void updateBounds() {
        Font font = Minecraft.getInstance().font;
        if (this.width > 0) {
            this.cachedLines = font.split(this.text, this.width);
            var currentHeight = this.cachedLines.size() * font.lineHeight;
            if (maxHeight > 0 && currentHeight > maxHeight) {
                this.scale = (float)maxHeight / currentHeight;
            }
            this.height = Math.round(currentHeight * scale);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, Inset inset, Dimension dimension, PrehistoricBookScreen screen) {
        if (this.cachedLines == null || this.cachedLines.isEmpty()) return;

        int currentY = 0;
        for (FormattedCharSequence line : this.cachedLines) {
            int drawX = this.x;
            if (this.centered) {
                drawX += (this.width - font.width(line)) / 2;
            }
            graphics.pose().pushPose();
            graphics.pose().translate(drawX, this.y, 0);
            graphics.pose().scale(scale, scale, 1f);
            //current y gets scaled with the model matrix inside the pose stack
            graphics.drawString(font, line, 0, currentY, this.color, false);
            graphics.pose().popPose();
            currentY += font.lineHeight;
        }
    }
}
