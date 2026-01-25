package com.ren.lostintime.client.screen;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.menu.SoulConfiguratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class SoulConfiguratorScreen extends AbstractContainerScreen<SoulConfiguratorMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LostInTime.MODID, "textures/gui/soul_configurator.png");

    public SoulConfiguratorScreen(SoulConfiguratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 172;
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

        //fuel
        float progress = menu.getContainerData().get(0);

        int height = Math.round((progress / 100f) * 13f);
        int inverseHeight = 13 - height;

        pGuiGraphics.blit(TEXTURE, x + 77, y + 40 + inverseHeight, 176, inverseHeight, 13, height);

        float processingProgress = menu.getContainerData().get(1);
        if (processingProgress > 0) {

            int firstWidth = (int) Mth.map(Mth.clamp(processingProgress, 0f, 40f), 0f, 40f, 0, 32f);
            pGuiGraphics.blit(TEXTURE, x + 67, y + 32, 176, 14, firstWidth, 6);

            if (processingProgress >= 40){

                int secondWidth = (int) Mth.map(Mth.clamp(processingProgress, 40f, 80f), 40f, 80f, 0, 33f);
                pGuiGraphics.blit(TEXTURE, x + 117, y + 32, 176, 20, secondWidth, 6);
            }
            if (processingProgress >= 80){

                int thirdHeight = (int) Mth.map(Mth.clamp(processingProgress, 80f, 100f), 80f, 100f, 0, 13);
                pGuiGraphics.blit(TEXTURE, x + 144, y + 38, 203, 26, 6, thirdHeight);
            }


        }
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
    }
}
