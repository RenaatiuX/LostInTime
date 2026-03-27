package com.ren.lostintime.client.screen.book;

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

    private Inset underlineInsets;
    private int underlineThickness, underlineColor;

    public TitlePageComponent(Component text, int color, boolean dropShadow, boolean centered) {
        this.text = text;
        this.color = color;
        this.dropShadow = dropShadow;
        this.centered = centered;
        updateBounds();
    }

    public TitlePageComponent(Component text) {
        this(text, 0x000000, false, true);
    }

    public TitlePageComponent addUnderline(int thickness, int color) {
        return addUnderline(thickness, color, Inset.ZERO);
    }

    /**
     *
     * @param thickness the thickness of the underline
     * @param color the hex code of the color the underline will have
     * @param insets will be more of a padding inside the y direction, when the height isn't set, when this component has autom height set, this will only work with absolute values
     */
    public TitlePageComponent addUnderline(int thickness, int color, Inset insets) {
        this.underlineThickness = thickness;
        this.underlineInsets = insets;
        this.underlineColor = color;
        return this;
    }

    public TitlePageComponent maxScale(float scale){
        this.maxScale = scale;
        return this;
    }


    public boolean hasUnderline(){
        return underlineInsets != null;
    }

    @Override
    public void updateBounds() {
        var font = Minecraft.getInstance().font;
        int textWidth = font.width(this.text);
        int textHeight = font.lineHeight;

        int effectiveWidth = this.width;
        int effectiveHeight = this.height;

        int heightOfText;

        // Auto-calculate missing bounds based on the text size if they are not explicitly set (0)
        boolean autoscaleHeight = this.height <= 0;
        boolean autoscaleWidth = this.width <= 0;

        if (autoscaleHeight && !autoscaleWidth){
            scale = (float)effectiveWidth / textWidth;
            heightOfText = (int) (textHeight * scale);
            if (hasUnderline()){
                int topOffset = underlineInsets.resolveTop(effectiveHeight, effectiveHeight);
                int botOffset = underlineInsets.resolveBottom(effectiveHeight, effectiveHeight);
                effectiveHeight = heightOfText + topOffset + botOffset + underlineThickness;

            }else {
                effectiveHeight = heightOfText;
            }
            this.height = effectiveHeight;
        }
        else if (autoscaleWidth && !autoscaleHeight){
            scale = (float)effectiveHeight / textHeight;
            effectiveWidth = (int) (textWidth * scale);
            this.width = effectiveWidth;
        }
        if (maxScale > 0)
            scale = Math.min(maxScale, scale);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, PrehistoricBookScreen screen) {

        int textWidth = font.width(this.text);

        graphics.pose().pushPose();

        float drawX = this.x;
        float drawY = this.y;

        //graphics.renderOutline(this.x, this.y, this.width, this.height, 0xFF0000FF);

        int actualTextHeight = this.height;
        if (hasUnderline()){
            int topOffset = underlineInsets.resolveTop(this.height, this.height);
            int botOffset = underlineInsets.resolveBottom(this.height, this.height);
            actualTextHeight -= topOffset + botOffset + underlineThickness;
        }



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

        if (hasUnderline()){
            int topOffset = underlineInsets.resolveTop(this.height, this.height);
            int lineStartY = this.y + Math.round(font.lineHeight * scale) + topOffset;
            int lineStartX = this.x + underlineInsets.resolveLeft(this.width, this.width);
            int lineEndX = this.x + this.width - underlineInsets.resolveRight(this.width, this.width);



            graphics.fill(lineStartX, lineStartY, lineEndX, lineStartY + underlineThickness, underlineColor);

        }
    }
}
