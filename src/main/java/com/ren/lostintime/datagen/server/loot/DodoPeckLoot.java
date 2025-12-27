package com.ren.lostintime.datagen.server.loot;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.ItemInit;
import com.ren.lostintime.datagen.server.LITTags;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public class DodoPeckLoot implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> pOutput) {
        pOutput.accept(new ResourceLocation(LostInTime.MODID, "dodo/pecking"),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.STICK).setWeight(25))
                                .add(LootItem.lootTableItem(Items.BONE).setWeight(15))
                                .add(LootItem.lootTableItem(Items.WHEAT_SEEDS).setWeight(10))
                                .add(LootItem.lootTableItem(Items.IRON_NUGGET).setWeight(5))
                                .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(5))
                                .add(LootItem.lootTableItem(Items.LEATHER_BOOTS).setWeight(5))
                                .add(LootItem.lootTableItem(Items.EMERALD).setWeight(3))
                                .add(LootItem.lootTableItem(Items.NAME_TAG).setWeight(2))
                                .add(LootItem.lootTableItem(Items.IRON_SWORD).setWeight(1))
                                .add(TagEntry.expandTag(LITTags.Items.UNIDENTIFIED_FOSSIL).setWeight(1))
                                .add(LootItem.lootTableItem(Items.EMERALD_BLOCK).setWeight(1))
                        ));
    }
}
