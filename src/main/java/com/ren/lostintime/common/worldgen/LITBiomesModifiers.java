package com.ren.lostintime.common.worldgen;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.worldgen.fossil.FossilEra;
import com.ren.lostintime.datagen.server.LITTags;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LITBiomesModifiers {

    public static final ResourceKey<BiomeModifier> ADD_FOSSILS = registerKey("add_fossils");
    public static final ResourceKey<BiomeModifier> ADD_RED_ALGAE = registerKey("add_red_algae");
    public static final ResourceKey<BiomeModifier> DODO_SPAWN = registerKey("dodo_spawn");
    public static final ResourceKey<BiomeModifier> ANOMALOCARIS_SPAWN = registerKey("anomalocaris_spawn");
    public static final ResourceKey<BiomeModifier> ENDOCERAS_SPAWN = registerKey("endoceras_spawn");
    public static final ResourceKey<BiomeModifier> HYLONOMUS_SPAWN = registerKey("hylonomus_spawn");
    public static final ResourceKey<BiomeModifier> DAEODON_SPAWN = registerKey("daeodon_spawn");

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var placed = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        HolderSet<PlacedFeature> fossils =
                HolderSet.direct(Arrays.stream(FossilEra.values())
                        .map(era -> placed.getOrThrow(LITPlacedFeatures.FOSSIL_PLACED.get(era))).toList());

        context.register(ADD_FOSSILS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD), fossils, GenerationStep.Decoration.UNDERGROUND_ORES));

        //PLANTS
        context.register(ADD_RED_ALGAE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                // Llamamos a un tag específico que crearemos en JSON
                biomes.getOrThrow(LITTags.Biomes.HAS_RED_ALGAE),
                HolderSet.direct(placed.getOrThrow(LITPlacedFeatures.RED_ALGAE_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        //SPAWN
        context.register(DODO_SPAWN, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(LITTags.Biomes.DODO_CAN_SPAWN), List.of(
                new MobSpawnSettings.SpawnerData(EntityInit.DODO.get(), 20, 1, 4)
        )));
        context.register(ANOMALOCARIS_SPAWN, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(LITTags.Biomes.ANOMALOCARIS_CAN_SPAWN), List.of(
                new MobSpawnSettings.SpawnerData(EntityInit.ANOMALOCARIS.get(), 15, 1, 3)
        )));
        context.register(ENDOCERAS_SPAWN, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(LITTags.Biomes.ENDOCERAS_CAN_SPAWN), List.of(
                new MobSpawnSettings.SpawnerData(EntityInit.ENDOCERAS.get(), 10, 1, 2)
        )));
        context.register(HYLONOMUS_SPAWN, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(LITTags.Biomes.HYLONOMUS_CAN_SPAWN), List.of(
                new MobSpawnSettings.SpawnerData(EntityInit.HYLONOMUS.get(), 15, 1, 3)
        )));
        context.register(DAEODON_SPAWN, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.SAVANNA)), List.of(
                        new MobSpawnSettings.SpawnerData(EntityInit.DAEODON.get(), 8, 1, 2)
        )));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Objects.requireNonNull(ResourceLocation.tryBuild(LostInTime.MODID, name)));
    }
}
