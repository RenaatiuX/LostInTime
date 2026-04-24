package com.ren.lostintime.client.screen.book.components;

import com.mojang.datafixers.util.Pair;
import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.Inset;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class StackedImageComponent extends PageComponent{


    protected List<Pair<ResourceLocation, Point>> images = new LinkedList<>();

    private final int totalTextureHeight;
    private final int totalTextureWidth;
    protected final int imageWidth, imageHeight;
    protected final boolean centered;

    protected float additionalScale = 1f;

    public StackedImageComponent(int imageWidth, int imageHeight, boolean centered) {
        this(imageWidth, imageHeight,256, 256, centered);
    }

    public StackedImageComponent(int imageWidth, int imageHeight, int totalTextureWidth, int totalTextureHeight, boolean centered) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.totalTextureHeight = totalTextureHeight;
        this.totalTextureWidth = totalTextureWidth;
        this.centered = centered;
    }

   

    public StackedImageComponent addImage(ResourceLocation image, int u, int v){
        this.images.add(Pair.of(image, new Point(u, v)));
        return this;
    }

    
    public StackedImageComponent additionalScale(float scale) {
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

        for (Pair<ResourceLocation, Point> imageEntry : this.images) {
            ResourceLocation tex = imageEntry.getFirst();
            Point uv = imageEntry.getSecond();
            graphics.blit(tex, 0, 0, uv.x, uv.y, imageWidth, imageHeight, totalTextureWidth, totalTextureHeight);
        }

        graphics.pose().popPose();
    }
}
