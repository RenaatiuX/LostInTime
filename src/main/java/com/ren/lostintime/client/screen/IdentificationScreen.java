package com.ren.lostintime.client.screen;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.menu.IdentificationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class IdentificationScreen extends AbstractContainerScreen<IdentificationMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LostInTime.MODID, "textures/gui/identification.png");

    public IdentificationScreen(IdentificationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        int progress = Math.round(menu.getProgress() * 27F / IdentificationMenu.DURATION);
        progress = Mth.clamp(progress, 0, 27);
        if (progress > 0) {
            int inverseProgress = 27 - progress;
            pGuiGraphics.blit(TEXTURE, x + 56, y + 26 + inverseProgress, 176, inverseProgress, 25, progress);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
    }
}
