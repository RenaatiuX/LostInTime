package com.ren.lostintime.client.screen.book;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ImageComponent extends PageComponent{

    protected final ResourceLocation texture;
    protected int imageWidth, imageHeight, totalTextureWidth, totalTextureHeight;

    protected float additionalScale = 1f;

    public ImageComponent(ResourceLocation texture, int imageWidth, int imageHeight) {
        this(texture, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    /**
     *
     * @param texture the texture
     * @param imageWidth the width of the image to be drawn from the texture
     * @param imageHeight the height of the image to be drawn from the texture
     * @param totalTextureWidth the total width of the whole texture file
     * @param totalTextureHeight the total height of the whole texture file
     */
    public ImageComponent(ResourceLocation texture, int imageWidth, int imageHeight, int totalTextureWidth, int totalTextureHeight) {
        this.texture = texture;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.totalTextureHeight = totalTextureHeight;
        this.totalTextureWidth = totalTextureWidth;
    }

    public ImageComponent additionalScale(float scale){
        this.additionalScale = Mth.clamp(scale, 0, 1);
        return this;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, PrehistoricBookScreen screen) {
        // Ensure we don't divide by zero
        if (this.imageWidth <= 0 || this.imageHeight <= 0 || this.width <= 0 || this.height <= 0) {
            return;
        }

        // Calculate aspect ratios
        float scaleX = (float) this.width / this.imageHeight;
        float scaleY = (float) this.height / (float) this.imageHeight;

        float scale = Math.min(scaleX, scaleY) * additionalScale;

        int newWidth = Math.round(this.imageWidth * scale);
        int newHeight = Math.round(this.imageHeight * scale);

        int xOffset = this.x + (this.width - newWidth) / 2;
        int yOffset = this.y + (this.height - newHeight) / 2;


        graphics.pose().pushPose();
        graphics.pose().translate(xOffset, yOffset, 0);
        graphics.pose().scale(scale, scale, scale);

        // Blit the texture with the new scaled dimensions, drawing the specified image region
        graphics.blit(texture, 0, 0, 0, 0, imageWidth, imageHeight, totalTextureWidth, totalTextureHeight);
        graphics.pose().popPose();
    }
}
