package com.ren.lostintime.client.screen.book;

import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CreaturePage extends Page {

    public final EntityType<? extends LivingEntity> entityType;
    public final ItemStack drop;
    public final String description;

    private LivingEntity cachedEntity;

    public CreaturePage(String name, EntityType<? extends LivingEntity> entityType, ItemStack drop, String description) {
        super(name);
        this.entityType = entityType;
        this.drop = drop;
        this.description = description;
    }

    public LivingEntity getEntityType(Level level) {
        if (this.cachedEntity == null && level != null) {
            this.cachedEntity = this.entityType.create(level);
        }
        return this.cachedEntity;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, Font font, PrehistoricBookScreen screen) {
        LivingEntity entity = getEntityType(Minecraft.getInstance().level);
        if (entity != null) {
            graphics.drawCenteredString(font, this.name, x + 64, y + 15, 0x8B7D6B);
            graphics.fill(x + 20, y + 26, x + 108, y + 27, 0x558B7D6B);

            int entityRenderX = x + 64;
            int entityRenderY = y + 110;
            float lookX = (float)entityRenderX - mouseX;
            float lookY = (float)(entityRenderY - 30) - mouseY;

            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, entityRenderX, entityRenderY, 20, lookX, lookY, entity);
            Lighting.setupFor3DItems();

            graphics.drawString(font, Component.literal("Drops:"), x + 25, y + 140, 0x555555, false);
            graphics.renderItem(this.drop, x + 65, y + 137);

            if (mouseX >= x + 65 && mouseX <= x + 65 + 16 && mouseY >= y + 137 && mouseY <= y + 137 + 16) {
                screen.setHoveredItem(this.drop);
            }

            graphics.drawString(font, this.description, x + 140, y + 35, 0x555555, false);
        }
    }
}
