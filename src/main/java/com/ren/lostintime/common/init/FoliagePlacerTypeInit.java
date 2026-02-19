package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.worldgen.feature.foliageplacers.AraucarioxylonFoliagePlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class FoliagePlacerTypeInit {

    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACER =
            DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, LostInTime.MODID);

    public static final RegistryObject<FoliagePlacerType<AraucarioxylonFoliagePlacer>> ARAUCARIOXYLON_FOLIAGE_PLACER =
            FOLIAGE_PLACER.register("araucarioxylon_foliage_placer",
                    () -> new FoliagePlacerType<>(AraucarioxylonFoliagePlacer.CODEC));


}
