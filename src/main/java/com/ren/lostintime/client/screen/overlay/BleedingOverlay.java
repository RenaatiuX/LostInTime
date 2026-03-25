package com.ren.lostintime.client.screen.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.MobEffectInit;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class BleedingOverlay {

    private static final ResourceLocation BLOOD_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/gui/blood_overlay.png");

    public static final IGuiOverlay HUD_BLEEDING = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player != null && !player.isSpectator() && player.hasEffect(MobEffectInit.BLEEDING.get())) {

            int duration = player.tickCount % 40;

            float alpha = 0.15F;

            if (duration < 6) {
                alpha += 0.7F * ((6 - duration) / 6.0F);
            } else if (duration > 8 && duration < 14) {
                alpha += 0.4F * ((14 - duration) / 6.0F);
            }

            RenderSystem.enableBlend();

            guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);

            guiGraphics.blit(BLOOD_TEXTURE, 0, 0, -90, 0.0F, 0.0F, screenWidth, screenHeight, screenWidth, screenHeight);

            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

            RenderSystem.disableBlend();
        }
    };

}
