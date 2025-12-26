package com.ren.lostintime.datagen.server.loot;

import net.minecraft.data.loot.packs.VanillaEntityLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class EntityLoot extends VanillaEntityLoot {

    protected Set<EntityType<?>> knowEntities = new HashSet<>();

    @Override
    public void generate() {

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
