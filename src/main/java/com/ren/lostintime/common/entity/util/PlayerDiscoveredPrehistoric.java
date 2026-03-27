package com.ren.lostintime.common.entity.util;

import com.ren.lostintime.common.init.CapabilityInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Set;

@AutoRegisterCapability
public interface PlayerDiscoveredPrehistoric extends INBTSerializable<CompoundTag> {


    Set<TimePeriod> discoveredTimePeriods();

    void discoverTimePeriod(TimePeriod period);

    default boolean canDiscover(Entity entity){
        return entity.getCapability(CapabilityInit.ENTITY_DESCRIPTION_CAPABILITY).isPresent();
    }

    void discoverEntity(Entity entity);

    Set<LostInTimeBookDescription> discoveredEntities();

    void reset();


}
