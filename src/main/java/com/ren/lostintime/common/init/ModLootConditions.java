package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.loot.RandomChanceWithGoldenMultiplier;
import com.ren.lostintime.common.loot.RandomTagEntryLoot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModLootConditions {

    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(BuiltInRegistries.LOOT_CONDITION_TYPE.key(), LostInTime.MODID);
    public static final DeferredRegister<LootPoolEntryType> LOOT_POOL_ENTRIES = DeferredRegister.create(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE.key(), LostInTime.MODID);


    public static final RegistryObject<LootItemConditionType> RANDOM_CHANCE_GOLDEN = LOOT_CONDITIONS.register("random_chance_golden", () -> new LootItemConditionType(new RandomChanceWithGoldenMultiplier.Serializer()));
    public static final RegistryObject<LootPoolEntryType> RANDOM_TAG_ENTRY = LOOT_POOL_ENTRIES.register("random_tag_entry", () -> new LootPoolEntryType(new RandomTagEntryLoot.Serializer()));

}
