package com.ren.lostintime.datagen.server;

import com.ren.lostintime.LostInTime;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class LITBiomeTags extends BiomeTagsProvider {
    public LITBiomeTags(PackOutput p_255800_, CompletableFuture<HolderLookup.Provider> p_256205_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_255800_, p_256205_, LostInTime.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(LITTags.Biomes.DODO_CAN_SPAWN).add(Biomes.BAMBOO_JUNGLE, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE);
        tag(LITTags.Biomes.ANOMALOCARIS_CAN_SPAWN).add(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN);
        tag(LITTags.Biomes.ENDOCERAS_CAN_SPAWN).add(Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.FROZEN_OCEAN);
        tag(LITTags.Biomes.HYLONOMUS_CAN_SPAWN)
                .add(Biomes.BAMBOO_JUNGLE, Biomes.CHERRY_GROVE, Biomes.DARK_FOREST)
                .addTag(BiomeTags.IS_TAIGA)
                .addTag(BiomeTags.IS_SAVANNA)
                .addTag(BiomeTags.IS_JUNGLE)
                .addTag(BiomeTags.IS_BEACH)
                .add(Biomes.DESERT, Biomes.BADLANDS)
                .add(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.FROZEN_RIVER);

        tag(LITTags.Biomes.HAS_RED_ALGAE)
                .add(Biomes.OCEAN)
                .add(Biomes.DEEP_OCEAN)
                .add(Biomes.COLD_OCEAN)
                .add(Biomes.DEEP_COLD_OCEAN)
                .add(Biomes.LUKEWARM_OCEAN)
                .add(Biomes.DEEP_LUKEWARM_OCEAN)
                .add(Biomes.WARM_OCEAN);
    }
}
