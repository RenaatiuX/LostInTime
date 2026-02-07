package com.ren.lostintime.datagen.server;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.BlockInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

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

        pickaxe(BlockInit.CRETACEOUS_FOSSIL_BLOCK);
    }

    public void pickaxe(Supplier<? extends Block>... blocks) {
        IntrinsicTagAppender<Block> tagAppender = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        Arrays.stream(blocks).map(Supplier::get).forEach(tagAppender::add);
    }

    public void stone(Supplier<? extends Block>... blocks) {
        IntrinsicTagAppender<Block> tagAppender = tag(BlockTags.NEEDS_STONE_TOOL);
        Arrays.stream(blocks).map(Supplier::get).forEach(tagAppender::add);
    }
}
