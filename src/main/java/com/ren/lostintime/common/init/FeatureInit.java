package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.worldgen.feature.FossilFormationFeature;
import com.ren.lostintime.common.worldgen.feature.config.FossilFormationConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class FeatureInit {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, LostInTime.MODID);

    public static final RegistryObject<Feature<FossilFormationConfig>> FOSSIL_FORMATION = FEATURES.register(
            "fossil_formation", () -> new FossilFormationFeature(FossilFormationConfig.CODEC));
}
