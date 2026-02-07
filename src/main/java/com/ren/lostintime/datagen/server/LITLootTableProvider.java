package com.ren.lostintime.datagen.server;

import com.ren.lostintime.common.init.ModLootParamSets;
import com.ren.lostintime.datagen.server.loot.DodoPeckLoot;
import com.ren.lostintime.datagen.server.loot.ModBlockLoot;
import com.ren.lostintime.datagen.server.loot.ModEntityLoot;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class LITLootTableProvider extends LootTableProvider {

    public LITLootTableProvider(PackOutput output) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(ModEntityLoot::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(DodoPeckLoot::new, ModLootParamSets.DODO_PECK),
                new SubProviderEntry(ModBlockLoot::new, LootContextParamSets.BLOCK)));
    }
}
