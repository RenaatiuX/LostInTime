package com.ren.lostintime.client.screen.book;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ImageComponent extends PageComponent{

    protected final ResourceLocation texture;
    protected int imageWidth, imageHeight, totalTextureWidth, totalTextureHeight;

    /**
     *
     * @param texture the texture
     * @param imageWidth the height which should be drawn
     * @param imageHeight the image width which should be drawn
     * @param totalTextureWidth the total width of the whole texture not just the part which is drawn on
     * @param totalTextureHeight the total width of the whole texture not just the part which is drawn on
     */
    public ImageComponent(ResourceLocation texture, int imageWidth, int imageHeight, int totalTextureWidth, int totalTextureHeight) {
        this.texture = texture;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.totalTextureHeight = totalTextureHeight;
        this.totalTextureWidth = totalTextureWidth;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, PrehistoricBookScreen screen) {


        if (this.width > 256 || this.height > 256) {
            graphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight, totalTextureWidth, totalTextureHeight);
        }else
            graphics.blit(texture, x, y, 0, 0, width, height);
    }
}
