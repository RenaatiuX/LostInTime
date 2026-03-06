package com.ren.lostintime.datagen.server.loot;

import com.ren.lostintime.common.init.BlockInit;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModBlockLoot extends BlockLootSubProvider {

    protected List<Block> block = new ArrayList<>();

    public ModBlockLoot() {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS);
    }

    @Override
    protected void generate() {
        dropSelf(BlockInit.CRETACEOUS_FOSSIL_BLOCK.get());
        dropSelf(BlockInit.IDENTIFICATION_TABLE.get());
        dropSelf(BlockInit.SOUL_EXTRACTOR.get());
        dropSelf(BlockInit.SOUL_CONFIGURATOR.get());
        dropSelf(BlockInit.TRANSFIGURATOR.get());
        add(BlockInit.ARAUCARIOXYLON_SIGN.get(), block ->
                createSingleItemTable(BlockInit.ARAUCARIOXYLON_SIGN.get()));
        add(BlockInit.ARAUCARIOXYLON_WALL_SIGN.get(), block ->
                createSingleItemTable(BlockInit.ARAUCARIOXYLON_SIGN.get()));
        add(BlockInit.ARAUCARIOXYLON_HANGING_SIGN.get(), block ->
                createSingleItemTable(BlockInit.ARAUCARIOXYLON_HANGING_SIGN.get()));
        add(BlockInit.ARAUCARIOXYLON_WALL_HANGING_SIGN.get(), block ->
                createSingleItemTable(BlockInit.ARAUCARIOXYLON_HANGING_SIGN.get()));
    }

    protected void dropSelfWithContents(Block block) {
        this.add(block, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(block.asItem()).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("inventory", "BlockEntityTag.inventory")))));
    }


    @Override
    protected void add(Block pBlock, LootTable.Builder pBuilder) {
        this.block.add(pBlock);
        super.add(pBlock, pBuilder);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return this.block;
    }
}
