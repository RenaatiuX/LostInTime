package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.function.Consumer;

public class ModLootParamSets {

    public static final LootContextParam<Float> DODO_GOLDEN_FOOD_MULTIPLIER = create("dodo_golden_multiplier");

    public static final LootContextParamSet DODO_PECK = register("dodo_peck", builder -> {
        builder.required(LootContextParams.BLOCK_STATE).required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(DODO_GOLDEN_FOOD_MULTIPLIER);
    });


    public static LootContextParamSet register(String name, Consumer<LootContextParamSet.Builder> builder){
        return LootContextParamSets.register(new ResourceLocation(LostInTime.MODID, name).toString(), builder);
    }

    public static <T> LootContextParam<T> create(String name){
        return new LootContextParam<>(new ResourceLocation(LostInTime.MODID, name));
    }
}
