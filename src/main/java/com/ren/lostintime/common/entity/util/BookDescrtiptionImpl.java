package com.ren.lostintime.common.entity.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class BookDescrtiptionImpl implements LostInTimeBookDescription {

    private final TimePeriod period;
    private final List<Supplier<? extends ItemLike>> drops;
    private final EntityType<?> entityType;
    private final Component descriptionOverride;
    private final ResourceLocation icon;

    @SafeVarargs
    public BookDescrtiptionImpl(TimePeriod period, EntityType<?> entityType, ResourceLocation icon, Supplier<? extends ItemLike>... drops) {
        this(period, entityType, null, icon, drops);
    }

    @SafeVarargs
    public BookDescrtiptionImpl(TimePeriod period, EntityType<?> entityType, Component descriptionOverride, ResourceLocation icon, Supplier<? extends ItemLike>... drops) {
        this.period = period;
        this.icon = icon;
        this.drops = Arrays.asList(drops);
        this.entityType = entityType;
        this.descriptionOverride = descriptionOverride;
    }

    @Override
    public EntityType<?> getEntityType() {
        return entityType;
    }

    @Override
    public TimePeriod getPeriod() {
        return period;
    }

    @Override
    public List<ItemStack> drops() {
        return drops.stream().map(Supplier::get).map(ItemStack::new).toList();
    }

    @Override
    public Component getDisplayName() {
        if (descriptionOverride == null)
            return LostInTimeBookDescription.super.getDisplayName();
        return descriptionOverride;
    }

    @Override
    public ResourceLocation icon() {
        return this.icon;
    }
}
