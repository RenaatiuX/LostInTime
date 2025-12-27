package com.ren.lostintime.datagen;

import com.ren.lostintime.datagen.client.LITBlockStateProvider;
import com.ren.lostintime.datagen.client.LITItemsModelProvider;
import com.ren.lostintime.datagen.client.LITLanguageProvider;
import com.ren.lostintime.datagen.server.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class DataGatherer {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

        //TAG
        LITBlockTagProvider blockTag = gen.addProvider(event.includeServer(), new LITBlockTagProvider(output, provider, helper));
        gen.addProvider(event.includeServer(), new LITItemTagProvider(output, provider, blockTag.contentsGetter(), helper));

        //SERVER
        gen.addProvider(event.includeServer(), new LITWorldGenProvider(output, provider));
        gen.addProvider(event.includeServer(), new LITRecipeProvider(output));
        gen.addProvider(event.includeServer(), new CuriosTagProvider(output, helper, provider));
        gen.addProvider(event.includeServer(), new LITLootTableProvider(output));

        //CLIENT
        gen.addProvider(event.includeClient(), new LITItemsModelProvider(output, helper));
        gen.addProvider(event.includeClient(), new LITBlockStateProvider(output, helper));
        gen.addProvider(event.includeClient(), new LITLanguageProvider(output));
    }
}
