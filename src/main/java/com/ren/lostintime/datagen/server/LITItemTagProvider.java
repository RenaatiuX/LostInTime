package com.ren.lostintime.datagen.server;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.BlockInit;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class LITItemTagProvider extends ItemTagsProvider {

    public LITItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider,
                              CompletableFuture<TagLookup<Block>> pBlockTags,
                              @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, LostInTime.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ItemTags.SAPLINGS)
                .add(BlockInit.MANGO_SAPLING.get().asItem());

        tag(ItemTags.LEAVES)
                .add(BlockInit.MANGO_LEAVES.get().asItem())
                .add(BlockInit.MANGO_FRUIT_LEAVES.get().asItem());

        tag(ItemTags.LOGS_THAT_BURN)
                .add(BlockInit.MANGO_LOG.get().asItem());

        tag(ItemTags.LOGS)
                .add(BlockInit.MANGO_LOG.get().asItem());

        tag(LITTags.Items.SEEDS)
                .add(Items.WHEAT_SEEDS).add(Items.BEETROOT_SEEDS).add(Items.MELON_SEEDS).add(Items.PUMPKIN_SEEDS)
                .add(Items.TORCHFLOWER_SEEDS);

        tag(LITTags.Items.FRUITS)
                .add(Items.APPLE).add(Items.MELON_SLICE).add(Items.SWEET_BERRIES).add(Items.GLOW_BERRIES)
                .add(ItemInit.MANGO.get());

        tag(LITTags.Items.UNIDENTIFIED_FOSSIL)
                .add(ItemInit.CAMBRIAN_FOSSIL.get()).add(ItemInit.ORDOVICIAN_FOSSIL.get()).add(ItemInit.SILURIAN_FOSSIL.get())
                .add(ItemInit.CARBONIFEROUS_FOSSIL.get()).add(ItemInit.DEVONIAN_FOSSIL.get()).add(ItemInit.PERMIAN_FOSSIL.get())
                .add(ItemInit.TRIASSIC_FOSSIL.get()).add(ItemInit.JURASSIC_FOSSIL.get()).add(ItemInit.CRETACEOUS_FOSSIL.get())
                .add(ItemInit.PALEOGENE_FOSSIL.get()).add(ItemInit.NEOGENE_FOSSIL.get()).add(ItemInit.QUATERNARY_FOSSIL.get());

        tag(CuriosTags.Items.BELT_SLOT_TAG)
                .add(ItemInit.GOLDEN_EYE.get());

        //TODO this is the wrapper for dodo food cause in case someone wants to add something exclusively to the dod food, he can do it here or add seeds which he then can do in the seeds tag
        // seperate always food and general stuff like seeds, even tho they might contain the same items cause i never know what someone might want to add to this mod
        tag(LITTags.Items.DODO_FOOD).addTags(LITTags.Items.SEEDS);


    }


}
