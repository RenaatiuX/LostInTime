package com.ren.lostintime.common.init;

import com.google.common.collect.ImmutableSet;
import com.ren.lostintime.LostInTime;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VillagerInit {

    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES,
            LostInTime.MODID);

    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(
            ForgeRegistries.VILLAGER_PROFESSIONS, LostInTime.MODID);

    public static final RegistryObject<PoiType> IDENTIFICATION_POI = POI_TYPES.register("identification_poi",
            () -> new PoiType(ImmutableSet.copyOf(BlockInit.IDENTIFICATION_TABLE.get().getStateDefinition().getPossibleStates()),
                    1, 1));

    public static final RegistryObject<VillagerProfession> FOSSIL_MASTER = VILLAGER_PROFESSIONS.register("fossil_master",
            () -> new VillagerProfession("fossil_master", holder -> holder.get() == IDENTIFICATION_POI.get(),
                    holder -> holder.get() == IDENTIFICATION_POI.get(), ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_CARTOGRAPHER));
}
