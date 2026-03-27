package com.ren.lostintime.client.screen.book;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TextPageComponent extends PageComponent {

    private final Component text;
    private float scale;
    private final int color;
    private final boolean dropShadow;
    private final boolean centered;

    public TextPageComponent(Component text, int color, boolean dropShadow, boolean centered) {
        this.text = text;
        this.color = color;
        this.dropShadow = dropShadow;
        this.centered = centered;
        updateBounds();
    }

    public TextPageComponent(Component text) {
        this(text, 0x000000, false, false);
    }

    @Override
    public void updateBounds() {
        var font = Minecraft.getInstance().font;
        int textWidth = font.width(this.text);
        int textHeight = font.lineHeight;

        // Auto-calculate missing bounds based on the text size if they are not explicitly set (0)
        int effectiveWidth = this.width > 0 ? this.width : textWidth;
        int effectiveHeight = this.height > 0 ? this.height : textHeight;
        // Calculate scale to fit inside the effective bounds
        float scaleX = (float) effectiveWidth / textWidth;
        float scaleY = (float) effectiveHeight / textHeight;
        scale = Math.min(2.0f, Math.min(scaleX, scaleY));

        // Optionally cap scale so short text doesn't become gigantic unless explicitly forced by bounds
        if (this.width == 0 && this.height == 0) {
            scale = 1.0F; // Default 1:1 if no bounds provided
        }

        this.width = effectiveWidth;
        this.height = effectiveHeight;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, PrehistoricBookScreen screen) {

        int textWidth = font.width(this.text);

        graphics.pose().pushPose();

        float drawX = this.x;
        float drawY = this.y;

        if (this.centered) {
            drawX += this.width / 2.0F;
            drawY += this.height / 2.0F - (font.lineHeight * scale) / 2.0F;
            graphics.pose().translate(drawX, drawY, 0);
            graphics.pose().scale(scale, scale, 1.0F);
            graphics.drawString(font, this.text, -textWidth / 2, 0, this.color, this.dropShadow);
        } else {
            graphics.pose().translate(drawX, drawY, 0);
            graphics.pose().scale(scale, scale, 1.0F);
            graphics.drawString(font, this.text, 0, 0, this.color, this.dropShadow);
        }

        graphics.pose().popPose();
    }
}
