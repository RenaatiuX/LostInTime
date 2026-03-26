package com.ren.lostintime.client.screen.book;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TitlePage extends Page {

    protected final Component title;
    protected final List<Component> description;

    public TitlePage(Component title, List<Component> description) {
        this.title = title.copy().withStyle(ChatFormatting.UNDERLINE);
        this.description = description;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, PrehistoricBookScreen screen) {
        int textWidth = font.width(this.title);
        
        // Calculate scale: fit within the page width with a small padding (e.g., 10 pixels total)
        float availableWidth = this.width - 10;
        float scale = availableWidth / textWidth;
        
        // Cap the maximum scale so short titles don't become too massive (e.g., max 2.0x scale)
        scale = Math.min(2.0F, scale);

        graphics.pose().pushPose();
        
        // Translate to the center of the page horizontally, and an offset from the top
        float centerX = this.x + this.width / 2.0F;
        float titleY = this.y + 20;
        
        graphics.pose().translate(centerX, titleY, 0);
        graphics.pose().scale(scale, scale, 1.0F);

        // Draw the string offset by half its width to center it around our translated (0, 0)
        // We set the dropShadow to false since books usually use flat text
        graphics.drawString(font, this.title, -textWidth / 2, 0, 0x8B0000, false);
        
        graphics.pose().popPose();

        // Render the description below the scaled title
        int currentY = this.y + 20 + (int)(font.lineHeight * scale) + 10;
        for (Component line : this.description) {
            graphics.drawString(font, line, this.x + 10, currentY, 0x555555, false);
            currentY += font.lineHeight + 2;
        }
    }
}