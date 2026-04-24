package com.ren.lostintime.client.screen.book.components;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.DiscoveredLocation;
import com.ren.lostintime.client.screen.book.util.Inset;
import com.ren.lostintime.client.screen.book.util.MapDrawUtils;
import com.ren.lostintime.client.util.ScreenRenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.time.chrono.MinguoEra;
import java.util.List;
import java.util.Optional;

public class MapPageComponent extends PageComponent {

    public static final ResourceLocation MAP_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/gui/book/atlas.png");
    protected static final int imageWidth = 768, imageHeight = 690;

    protected List<DiscoveredLocation> locations;

    public MapPageComponent(ResourceLocation csvLocations) {
        this.locations = MapDrawUtils.loadLocations(csvLocations);
    }


    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, Inset inset, Dimension dimension, PrehistoricBookScreen screen) {
        float scaleX = (float) this.width / imageWidth;
        float scaleY = (float) this.height / (float) imageHeight;

        float scale = Math.min(scaleX, scaleY);

        int newWidth = Math.round(imageWidth * scale);
        int newHeight = Math.round(imageHeight * scale);

        float drawX = this.x;
        float drawY = this.y;


        drawX += (this.width - newWidth) / 2.0F;
        drawY += (this.height - newHeight) / 2.0F;


        graphics.pose().pushPose();
        graphics.pose().translate(drawX, drawY, 0);
        graphics.pose().scale(scale, scale, 1.0F);

        // Blit the texture with the new scaled dimensions, drawing the specified image region using u and v
        graphics.blit(MAP_TEXTURE, 0, 0, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        int size = 5;
        
        // Inverse the translation and scaling to get mouse coordinates in the unscaled texture space
        float unscaledMouseX = (mouseX - drawX) / scale;
        float unscaledMouseY = (mouseY - drawY) / scale;

        DiscoveredLocation tooltip = null;

        for (var location : this.locations) {
            var pixelCoordinates = MapDrawUtils.projectRealWorldToMap(location.worldCoordinates().x, location.worldCoordinates().y, imageWidth, imageHeight);

            var rect = ScreenRenderingUtils.centered((int) pixelCoordinates[0], (int) pixelCoordinates[1], size);

            if (rect.contains(unscaledMouseX, unscaledMouseY)) {
                tooltip = location;
            }
            // System.out.printf("Mouse: [%s, %s]\n", unscaledMouseX, unscaledMouseY);

            //graphics.fill(Math.round(pixelCoordinates[0]) - size, Math.round(pixelCoordinates[1]) - size, Math.round(pixelCoordinates[0]) + size, Math.round(pixelCoordinates[1]) + size, 0xFF000000);

            ScreenRenderingUtils.fill(graphics, rect, 0xFFFFFFFF);
        }

        graphics.pose().popPose();
        if (tooltip != null)
            graphics.renderTooltip(Minecraft.getInstance().font, List.of(Component.literal(tooltip.name()), Component.literal(tooltip.city())), Optional.empty(), mouseX, mouseY);
    }
}
