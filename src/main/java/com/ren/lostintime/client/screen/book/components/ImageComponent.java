package com.ren.lostintime.client.screen.book.components;

import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.Inset;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ImageComponent extends PageComponent {

    protected final ResourceLocation texture;
    protected final int u, v;
    protected int imageWidth, imageHeight, totalTextureWidth, totalTextureHeight;

    protected float additionalScale = 1f;
    protected final boolean centered;

    public ImageComponent(ResourceLocation texture, int imageWidth, int imageHeight, boolean centered) {
        this(texture, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight, centered);
    }

    public ImageComponent(ResourceLocation texture, int imageWidth, int imageHeight, int totalTextureWidth, int totalTextureHeight, boolean centered) {
        this(texture, 0, 0, imageWidth, imageHeight, totalTextureWidth, totalTextureHeight, centered);
    }

    /**
     *
     * @param texture            the texture
     * @param u                  the x-coordinate of the image within the texture
     * @param v                  the y-coordinate of the image within the texture
     * @param imageWidth         the width of the image to be drawn from the texture
     * @param imageHeight        the height of the image to be drawn from the texture
     * @param totalTextureWidth  the total width of the whole texture file
     * @param totalTextureHeight the total height of the whole texture file
     * @param centered           whether to center the image within the component bounds
     */
    public ImageComponent(ResourceLocation texture, int u, int v, int imageWidth, int imageHeight, int totalTextureWidth, int totalTextureHeight, boolean centered) {
        this.texture = texture;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.totalTextureHeight = totalTextureHeight;
        this.totalTextureWidth = totalTextureWidth;
        this.centered = centered;
        this.u = u;
        this.v = v;
    }

    public ImageComponent additionalScale(float scale) {
        this.additionalScale = Math.max(0, scale);
        return this;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, Inset inset, Dimension dimension, PrehistoricBookScreen screen) {
        // Ensure we don't divide by zero
        if (this.imageWidth <= 0 || this.imageHeight <= 0 || this.width <= 0 || this.height <= 0) {
            return;
        }

        // Calculate aspect ratios
        float scaleX = (float) this.width / this.imageWidth;
        float scaleY = (float) this.height / (float) this.imageHeight;

        float scale = Math.min(scaleX, scaleY) * additionalScale;

        int newWidth = Math.round(this.imageWidth * scale);
        int newHeight = Math.round(this.imageHeight * scale);

        float drawX = this.x;
        float drawY = this.y;

        if (this.centered) {
            drawX += (this.width - newWidth) / 2.0F;
            drawY += (this.height - newHeight) / 2.0F;
        }

        graphics.pose().pushPose();
        graphics.pose().translate(drawX, drawY, 0);
        graphics.pose().scale(scale, scale, 1.0F);

        // Blit the texture with the new scaled dimensions, drawing the specified image region using u and v
        graphics.blit(texture, 0, 0, u, v, imageWidth, imageHeight, totalTextureWidth, totalTextureHeight);
        graphics.pose().popPose();
    }
}
