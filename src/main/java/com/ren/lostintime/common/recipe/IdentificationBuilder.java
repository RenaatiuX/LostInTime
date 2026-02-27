package com.ren.lostintime.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
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
    protected final Ingredient input;
    protected final ResourceLocation defaultId;
    protected final NavigableMap<ItemHolder, Double> weightedOutputs = new TreeMap<>();
    public double total;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

    public IdentificationBuilder(String modId, ItemLike itemInput) {
        this.modId = modId;
        this.input = Ingredient.of(itemInput);
        this.defaultId = ResourceLocation.fromNamespaceAndPath(modId, "identification/" + ForgeRegistries.ITEMS.getKey(itemInput.asItem()).getPath());
    }

    public IdentificationBuilder(String modId, TagKey<Item> tagInput) {
        this.modId = modId;
        this.input = Ingredient.of(tagInput);
        this.defaultId = ResourceLocation.fromNamespaceAndPath(modId, "identification/" + tagInput.location().getPath());
    }

    public IdentificationBuilder(String modId, Ingredient input, String name) {
        this.modId = modId;
        this.input = input;
        this.defaultId = ResourceLocation.fromNamespaceAndPath(modId, "identification/" + name);
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
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
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
        ResourceLocation resourceLocation2 = ResourceLocation.tryParse(pRecipeId);
        if (resourceLocation2 != null && resourceLocation2.equals(resourceLocation)) {
            throw new IllegalStateException("Recipe " + pRecipeId + " should remove its 'save' argument as it is equal to default one");
        } else {
            save(pFinishedRecipeConsumer, resourceLocation2);
        }
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, input, weightedOutputs, advancement));
    }

    protected ResourceLocation getRecipeId() {
        return defaultId;
    }

    public static class Result implements FinishedRecipe {

        private final ResourceLocation id;
        private final Ingredient ingredient;
        private final NavigableMap<ItemHolder, Double> weightedOutputs;
        private final Advancement.Builder advancement;

        protected Result(ResourceLocation id, Ingredient ingredient, NavigableMap<ItemHolder, Double> weightedOutputs, Advancement.Builder advancement) {
            this.id = id;
            this.ingredient = ingredient;
            this.weightedOutputs = weightedOutputs;
            this.advancement = advancement;
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
            return advancement.getCriteria().isEmpty() ? null : advancement.serializeToJson();
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return advancement.getCriteria().isEmpty() ? null : ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "recipes/" + id.getPath());
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
