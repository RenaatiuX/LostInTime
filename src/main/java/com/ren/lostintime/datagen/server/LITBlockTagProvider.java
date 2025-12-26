package com.ren.lostintime.datagen.server;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.BlockInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class LITBlockTagProvider extends BlockTagsProvider {

    public LITBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, LostInTime.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(BlockTags.SAPLINGS)
                .add(BlockInit.MANGO_SAPLING.get());

        tag(BlockTags.LEAVES)
                .add(BlockInit.MANGO_LEAVES.get())
                .add(BlockInit.MANGO_FRUIT_LEAVES.get());

        tag(BlockTags.LOGS)
                .add(BlockInit.MANGO_LOG.get());

        tag(BlockTags.LOGS_THAT_BURN)
                .add(BlockInit.MANGO_LOG.get());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(BlockInit.MANGO_LEAVES.get())
                .add(BlockInit.MANGO_FRUIT_LEAVES.get());

        tag(LITTags.Blocks.DODO_SOILS)
                .add(Blocks.GRASS_BLOCK)
                .add(Blocks.DIRT)
                .add(Blocks.COARSE_DIRT)
                .add(Blocks.PODZOL)
                .add(Blocks.MYCELIUM)
                .add(Blocks.SAND)
                .add(Blocks.GRAVEL);

    }
}
