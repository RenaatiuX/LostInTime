package com.ren.lostintime.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Consumer;

public class IdentificationBuilder implements RecipeBuilder {

    protected final String modId;
    protected final ItemLike itemInput;
    protected final TagKey<Item> tagInput;
    protected final NavigableMap<ItemHolder, Double> weightedOutputs = new TreeMap<>();
    public double total;

    public IdentificationBuilder(String modId, ItemLike itemInput) {
        this.modId = modId;
        this.itemInput = itemInput;
        this.tagInput = null;
    }

    public IdentificationBuilder(String modId, TagKey<Item> tagInput) {
        this.modId = modId;
        this.itemInput = null;
        this.tagInput = tagInput;
    }

    public IdentificationBuilder addOutput(ItemLike itemLike, double weight) {
        return addOutput(itemLike, 1, 1, weight);
    }

    public IdentificationBuilder addOutput(ItemLike itemLike, int count, double weight) {
        return addOutput(itemLike, count, count, weight);
    }

    public IdentificationBuilder addOutput(ItemLike itemLike, int minCount, int maxCount, double weight) {
        total += weight;
        weightedOutputs.put(new ItemHolder(ForgeRegistries.ITEMS.getKey(itemLike.asItem()), minCount, maxCount), weight);
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return Items.CHICKEN;
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        save(pFinishedRecipeConsumer, getRecipeId());
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, String pRecipeId) {
        ResourceLocation resourceLocation = getRecipeId();
        ResourceLocation resourceLocation2 = new ResourceLocation(pRecipeId);
        if (resourceLocation2.equals(resourceLocation)) {
            throw new IllegalStateException("Recipe " + pRecipeId + " should remove its 'save' argument as it is equal to default one");
        } else {
            save(pFinishedRecipeConsumer, resourceLocation2);
        }
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, itemInput != null ? Ingredient.of(itemInput) :
                Ingredient.of(tagInput), weightedOutputs));
    }

    protected ResourceLocation getRecipeId() {
        if (itemInput != null) {
            return new ResourceLocation(modId, "identification/" + ForgeRegistries.ITEMS.getKey(itemInput.asItem()).getPath());
        } else if (tagInput != null) {
            return new ResourceLocation(modId, "identification/" + tagInput.location().getPath());
        }
        return ForgeRegistries.ITEMS.getKey(Items.ENDER_PEARL);
    }

    public static class Result implements FinishedRecipe {

        private final ResourceLocation id;
        private final Ingredient ingredient;
        private final NavigableMap<ItemHolder, Double> weightedOutputs;

        protected Result(ResourceLocation id, Ingredient ingredient, NavigableMap<ItemHolder, Double> weightedOutputs) {
            this.id = id;
            this.ingredient = ingredient;
            this.weightedOutputs = weightedOutputs;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            pJson.add("input", ingredient.toJson());

            JsonArray outputs = new JsonArray();
            for (Map.Entry<ItemHolder, Double> entry : weightedOutputs.entrySet()) {
                ItemHolder itemHolder = entry.getKey();
                JsonObject itemJson = new JsonObject();
                itemJson.addProperty("item", itemHolder.location.toString());
                if (itemHolder.minCount != itemHolder.maxCount) {
                    itemJson.addProperty("minCount", itemHolder.minCount);
                    itemJson.addProperty("maxCount", itemHolder.maxCount);
                } else if (itemHolder.minCount > 1) {
                    itemJson.addProperty("count", itemHolder.minCount);
                }
                itemJson.addProperty("weight", entry.getValue());
                outputs.add(itemJson);
            }
            pJson.add("outputs", outputs);
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return IdentificationRecipe.SERIALIZER;
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

    public record ItemHolder(ResourceLocation location, int minCount, int maxCount) implements Comparable<ItemHolder> {

        @Override
        public int compareTo(@NotNull ItemHolder o) {
            return location.getPath().compareTo(o.location.getPath());
        }

        public int getRandomCount(RandomSource random) {
            if (minCount == maxCount) return minCount;
            return random.nextInt(maxCount - minCount + 1) + minCount;
        }
    }
}
