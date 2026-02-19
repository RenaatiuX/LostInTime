package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.worldgen.feature.trunkplacers.AraucarioxylonTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class TrunkPlacerTypeInit {

    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER =
            DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, LostInTime.MODID);

    public static final RegistryObject<TrunkPlacerType<AraucarioxylonTrunkPlacer>> ARAUCARIOXYLON_TRUNk_PLACER =
            TRUNK_PLACER.register("araucarioxylon_trunk_placer",
                    () -> new TrunkPlacerType<>(AraucarioxylonTrunkPlacer.CODEC));

}
