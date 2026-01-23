package com.ren.lostintime.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SoulConfiguratorFuelRecipeBuilder {

    public static SoulConfiguratorFuelRecipeBuilder fuelRecipe(ItemLike input, int fuelValue){
        return new SoulConfiguratorFuelRecipeBuilder(Ingredient.of(input), fuelValue);
    }

    public static SoulConfiguratorFuelRecipeBuilder fuelRecipe(TagKey<Item> input, int fuelValue){
        return new SoulConfiguratorFuelRecipeBuilder(Ingredient.of(input), fuelValue);
    }

    protected Ingredient input;
    protected int fuelValue = 300;

    private SoulConfiguratorFuelRecipeBuilder(Ingredient input, int fuelValue) {
        this.fuelValue = Math.max(1, fuelValue);
        this.input = input;
    }

    public void save(Consumer<FinishedRecipe> writer, ResourceLocation id){
        writer.accept(new Result(this, id.withPrefix("soul_configurator_fuel/")));
    }

    private static class Result implements FinishedRecipe{

        private final SoulConfiguratorFuelRecipeBuilder data;

        public Result(SoulConfiguratorFuelRecipeBuilder data, ResourceLocation id) {
            this.data = data;
            this.id = id;
        }

        private final ResourceLocation id;

        @Override
        public void serializeRecipeData(JsonObject pJson) {
           pJson.add("input", data.input.toJson());
           if (data.fuelValue != 300){
               pJson.addProperty("fuelValue", data.fuelValue);
           }
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return SoulConfiguratorFuelRecipe.SERIALIZER;
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
