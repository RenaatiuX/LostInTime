package com.ren.lostintime.datagen.server.loot;

import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.data.loot.packs.VanillaEntityLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class EntityLoot extends VanillaEntityLoot {

    protected Set<EntityType<?>> knowEntities = new HashSet<>();

    @Override
    public void generate() {
        add(EntityInit.DODO.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ItemInit.RAW_DODO.get()).apply(SmeltItemFunction.smelted()
                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));
    }

    @Override
    protected void add(EntityType<?> pEntityType, ResourceLocation pLootTableLocation, LootTable.Builder pBuilder) {
        this.knowEntities.add(pEntityType);
        super.add(pEntityType, pLootTableLocation, pBuilder);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return this.knowEntities.stream();
    }
}
