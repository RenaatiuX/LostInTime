package com.ren.lostintime.common.worldgen;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.worldgen.fossil.FossilEra;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;

public class LITBiomesModifiers {

    public static final ResourceKey<BiomeModifier> ADD_FOSSILS = registerKey("add_fossils");

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var placed = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        HolderSet<PlacedFeature> fossils =
                HolderSet.direct(Arrays.stream(FossilEra.values())
                        .map(era -> placed.getOrThrow(LITPlacedFeatures.PLACED_KEYS.get(era))).toList());

        context.register(ADD_FOSSILS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD), fossils, GenerationStep.Decoration.UNDERGROUND_ORES));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(LostInTime.MODID, name));
    }
}
