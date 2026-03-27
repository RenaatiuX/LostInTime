package com.ren.lostintime.common.entity.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.awt.*;
import java.util.List;

@AutoRegisterCapability
public interface LostInTimeBookDescription {


    EntityType<?> getEntityType();

    TimePeriod getPeriod();

    List<ItemStack> drops();

    default Component getDisplayName(){
        return getEntityType().getDescription();
    }
}
