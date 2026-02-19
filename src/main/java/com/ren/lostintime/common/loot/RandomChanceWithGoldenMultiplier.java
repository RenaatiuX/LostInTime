package com.ren.lostintime.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.ren.lostintime.common.init.LootConditionsInit;
import com.ren.lostintime.common.init.LootParamSetsInit;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class RandomChanceWithGoldenMultiplier implements LootItemCondition {

    protected float probability;

    public RandomChanceWithGoldenMultiplier(float probability) {
        this.probability = probability;
    }

    @Override
    public LootItemConditionType getType() {
        return LootConditionsInit.RANDOM_CHANCE_GOLDEN.get();
    }

    @Override
    public boolean test(LootContext lootContext) {
        float chance = Mth.clamp(probability * lootContext.getParam(LootParamSetsInit.DODO_GOLDEN_FOOD_MULTIPLIER), 0f, 1f);
        return lootContext.getRandom().nextFloat() < chance;
    }

    public static LootItemCondition.Builder goldenChance(float chance){
        return () -> new RandomChanceWithGoldenMultiplier(chance);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<RandomChanceWithGoldenMultiplier> {


        @Override
        public void serialize(JsonObject jsonObject, RandomChanceWithGoldenMultiplier randomChanceWithGoldenMultiplier, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("chance", randomChanceWithGoldenMultiplier.probability);
        }

        @Override
        public RandomChanceWithGoldenMultiplier deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new RandomChanceWithGoldenMultiplier(GsonHelper.getAsFloat(jsonObject, "chance", 0.1f));
        }
    }
}
