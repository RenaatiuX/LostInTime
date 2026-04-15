package com.ren.lostintime.client.screen.book.components;

import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.Inset;
import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TitlePageComponent extends PageComponent {

    private final Component text;
    private float scale;
    private final int color;
    private final boolean dropShadow;
    private final boolean centered;
    private float maxScale = -1F;

    public TitlePageComponent(Component text, int color, boolean dropShadow, boolean centered) {
        this.text = text;
        this.color = color;
        this.dropShadow = dropShadow;
        this.centered = centered;
        updateBounds();
    }

    public TitlePageComponent(Component text) {
        this(text, 0xFF000000, false, true);
    }

    public TitlePageComponent maxScale(float scale){
        this.maxScale = scale;
        return this;
    }

    @Override
    public void updateBounds() {
        var font = Minecraft.getInstance().font;
        int textWidth = font.width(this.text);
        int textHeight = font.lineHeight;

        int effectiveWidth = this.width;
        int effectiveHeight = this.height;


        this.scale = Math.min((float)effectiveHeight / textHeight, (float)effectiveWidth / textWidth);

        if (maxScale > 0)
            scale = Math.min(maxScale, scale);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, Inset inset, Dimension dimension, PrehistoricBookScreen screen) {

        int textWidth = font.width(this.text);


        //graphics.renderOutline(this.getBounds().x, this.getBounds().y, this.getBounds().width, this.getBounds().height, 0xFF000000);

        graphics.pose().pushPose();

        float drawX = this.x;
        float drawY = this.y;

        int actualTextHeight = this.height;



        if (this.centered) {
            drawX += (this.width) / 2.0F;
            drawY += actualTextHeight / 2.0F;
            graphics.pose().translate(drawX, drawY + 1, 0);
            graphics.pose().scale(scale, scale, 1.0F);
            graphics.drawString(font, this.text, -textWidth / 2, -font.lineHeight / 2, this.color, this.dropShadow);
        } else {
            graphics.pose().translate(drawX, drawY + 1, 0);
            graphics.pose().scale(scale, scale, 1.0F);
            graphics.drawString(font, this.text, 0, 0, this.color, this.dropShadow);
        }

        graphics.pose().popPose();
    }
}
