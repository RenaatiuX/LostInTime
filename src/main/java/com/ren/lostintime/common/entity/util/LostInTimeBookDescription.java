package com.ren.lostintime.common.entity.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

    ResourceLocation icon();

    default Component getDisplayName(){
        return getEntityType().getDescription();
    }


}
