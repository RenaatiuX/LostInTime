package com.ren.lostintime.common.worldgen;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.worldgen.fossil.FossilEra;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class LITPlacedFeatures {

    public static final Map<FossilEra, ResourceKey<PlacedFeature>> PLACED_KEYS = new EnumMap<>(FossilEra.class);


    public static void bootstrap(BootstapContext<PlacedFeature>  context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        for (FossilEra era : FossilEra.values()) {
            ResourceKey<PlacedFeature> key = ResourceKey.create(Registries.PLACED_FEATURE,
                            new ResourceLocation(LostInTime.MODID, "fossil/" + era.name().toLowerCase()));

            PLACED_KEYS.put(era, key);

            context.register(key, new PlacedFeature(configured.getOrThrow(LITConfiguredFeatures.FOSSIL_KEYS.get(era)),
                    List.of(CountPlacement.of(era.count), InSquarePlacement.spread(), HeightRangePlacement.uniform(
                                    VerticalAnchor.absolute(era.minY),
                                    VerticalAnchor.absolute(era.maxY)), BiomeFilter.biome())));
        }

    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(LostInTime.MODID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> configured, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configured, List.copyOf(modifiers)));
    }
}
