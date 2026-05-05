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
import net.minecraft.util.Mth;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class MapPageComponent extends PageComponent implements OneDimensionalNavigatableComponent{

    public static final ResourceLocation MAP_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/gui/book/atlas.png");
    public static final ResourceLocation MAP_FRAME_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/gui/book/atlas_frame.png");
    protected static final int mapImageWidth = 768, mapImageHeight = 590, frameWidth = 58, frameHeight = 58;

    protected List<DiscoveredLocation> locations;
    protected int navigationX = 0;

    public MapPageComponent(ResourceLocation csvLocations) {
        this.locations = MapDrawUtils.loadLocations(csvLocations);
    }


    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, Font font, Inset inset, Dimension dimension, PrehistoricBookScreen screen) {
        float scaleX = (float) this.width / frameWidth;
        float scaleY = (float) this.height / (float) frameHeight;

        float frameScale = Math.min(scaleX, scaleY);

        int frameWidth = Math.round(MapPageComponent.frameWidth * frameScale);
        int frameHeight = Math.round(MapPageComponent.frameHeight * frameScale);

        float frameX = this.x;
        float frameY = this.y;


        frameX += (this.width - frameWidth) / 2.0F;
        frameY += (this.height - frameHeight) / 2.0F;

        var mapFrameRect = new Rectangle(Mth.floor(frameX), Mth.floor(frameY), frameWidth, frameHeight);

        var mapFrameInsideRect = ScreenRenderingUtils.growRectangle(mapFrameRect, -5);



        graphics.pose().pushPose();
        var scissorRectangle = ScreenRenderingUtils.growRectangle(mapFrameRect, -1);
        ScreenRenderingUtils.scissor(graphics, scissorRectangle);

        //calculate the map y and x
        float mapScale = (float) mapFrameInsideRect.height / mapImageHeight;

        int scaledMapWidth = Math.round(mapScale * mapImageWidth);

        navigationX = Math.max((int)( -(scaledMapWidth - mapFrameInsideRect.width) / 2f), navigationX);
        navigationX = Math.min((int)((scaledMapWidth - mapFrameInsideRect.width) / 2f), navigationX);


        float mapX = mapFrameInsideRect.x - (scaledMapWidth - mapFrameInsideRect.width) / 2f + navigationX;

        graphics.pose().translate(mapX, mapFrameInsideRect.y, 0);
        graphics.pose().scale(mapScale, mapScale, 1f);

        graphics.blit(MAP_TEXTURE, 0, 0, 0, 0, mapImageWidth, mapImageHeight, mapImageWidth, mapImageHeight);


        int size = 5;

        // Inverse the translation and scaling to get mouse coordinates in the unscaled texture space
        float unscaledMouseX = (mouseX - mapX) / mapScale;
        float unscaledMouseY = (mouseY - mapFrameInsideRect.y) / mapScale;

        DiscoveredLocation tooltip = null;

        for (var location : this.locations) {
            var pixelCoordinates = MapDrawUtils.projectRealWorldToMap(location.worldCoordinates().x, location.worldCoordinates().y, mapImageWidth, mapImageHeight);

            var rect = ScreenRenderingUtils.centered((int) Math.round(pixelCoordinates[0]), (int) Math.round(pixelCoordinates[1]), size);

            if (scissorRectangle.contains(mouseX, mouseY) && rect.contains(unscaledMouseX, unscaledMouseY)) {
                tooltip = location;
            }
            ScreenRenderingUtils.fill(graphics, rect, 0xFFFFFFFF);
        }
        graphics.disableScissor();
        graphics.pose().popPose();


        graphics.pose().pushPose();
        graphics.pose().translate(frameX, frameY, 0);
        graphics.pose().scale(frameScale, frameScale, 1.0F);

        // Blit the texture with the new scaled dimensions, drawing the specified image region using u and v
        graphics.blit(MAP_FRAME_TEXTURE, 0, 0, 3, 3, MapPageComponent.frameWidth, MapPageComponent.frameHeight, 64, 64);



        graphics.pose().popPose();
        //graphics.renderOutline(mapFrameInsideRect.x, mapFrameInsideRect.y, mapFrameInsideRect.width, mapFrameInsideRect.height, 0xFF000000);
        if (tooltip != null)
            graphics.renderTooltip(Minecraft.getInstance().font, List.of(Component.literal(tooltip.name()), Component.literal(tooltip.city())), Optional.empty(), mouseX, mouseY);
    }

    @Override
    public void forward() {
        navigationX++;
    }

    @Override
    public void backward() {
        navigationX--;
    }
}
