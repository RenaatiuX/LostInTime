package com.ren.lostintime.client.screen.book;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CreaturePage {

    public final String name;
    public final EntityType<? extends LivingEntity> entityType;
    public final ItemStack drop;
    public final String description;

    private LivingEntity cachedEntity;

    public CreaturePage(String name, EntityType<? extends LivingEntity> entityType, ItemStack drop, String description) {
        this.name = name;
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
}
