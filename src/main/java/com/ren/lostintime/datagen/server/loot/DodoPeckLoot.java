package com.ren.lostintime.datagen.server.loot;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.ItemInit;
import com.ren.lostintime.common.init.ModLootTables;
import com.ren.lostintime.common.loot.RandomChanceWithGoldenMultiplier;
import com.ren.lostintime.common.loot.RandomTagEntryLoot;
import com.ren.lostintime.datagen.server.LITTags;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.Tags;

import java.util.function.BiConsumer;

public class DodoPeckLoot implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> pOutput) {
        pOutput.accept(ModLootTables.DODO_PECK_LOOT,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(UniformGenerator.between(1, 3))
                                .add(AlternativesEntry.alternatives(
                                        RandomTagEntryLoot.randomTagEntry(Tags.Items.MUSHROOMS).when(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.MUSHROOM_FIELDS))),
                                        LootItem.lootTableItem(Items.STICK)
                                )).when(LootItemRandomChanceCondition.randomChance(0.25f)))
                        .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.BONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).when(LootItemRandomChanceCondition.randomChance(0.15f))))
                        .withPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.15f)).setRolls(UniformGenerator.between(1, 3)).add(RandomTagEntryLoot.randomTagEntry(Tags.Items.SEEDS)))
                        .withPool(LootPool.lootPool().when(RandomChanceWithGoldenMultiplier.goldenChance(0.05f)).add(LootItem.lootTableItem(Items.IRON_NUGGET).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5)))))
                        .withPool(LootPool.lootPool().when(RandomChanceWithGoldenMultiplier.goldenChance(0.05f)).add(LootItem.lootTableItem(Items.GOLD_NUGGET).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))))
                        .withPool(LootPool.lootPool().when(RandomChanceWithGoldenMultiplier.goldenChance(0.05f)).add(LootItem.lootTableItem(Items.LEATHER_BOOTS).apply(EnchantRandomlyFunction.randomEnchantment()).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.01f, 1f)))))
                        .withPool(LootPool.lootPool().when(RandomChanceWithGoldenMultiplier.goldenChance(0.03f)).add(LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
                        .withPool(LootPool.lootPool().when(RandomChanceWithGoldenMultiplier.goldenChance(0.02f)).add(LootItem.lootTableItem(Items.NAME_TAG)))
                        .withPool(LootPool.lootPool().when(RandomChanceWithGoldenMultiplier.goldenChance(0.001f)).add(LootItem.lootTableItem(Items.EMERALD_BLOCK)))
                        .withPool(LootPool.lootPool().when(RandomChanceWithGoldenMultiplier.goldenChance(0.0025f)).add(LootItem.lootTableItem(Items.IRON_SWORD).apply(EnchantRandomlyFunction.randomEnchantment()).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.01f, 1f)))))
                        .withPool(LootPool.lootPool().when(RandomChanceWithGoldenMultiplier.goldenChance(0.0025f)).setRolls(UniformGenerator.between(1, 3)).add(RandomTagEntryLoot.randomTagEntry(LITTags.Items.UNIDENTIFIED_FOSSIL)))


                        );
    }
}
