package com.ren.lostintime.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TransfiguratorRecipeBuilder implements RecipeBuilder {

    private final ItemStack result;
    private final TransfiguratorRecipe.Type type;
    private Ingredient input = Ingredient.EMPTY;
    private Ingredient nutrient = Ingredient.of(ItemInit.UNIVERSAL_NUTRIENT.get());
    private final List<TransfiguratorRecipe.WeightedItem> failedResults = new ArrayList<>();
    private int processingTime = 200;
    private String group;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

    public TransfiguratorRecipeBuilder(ItemStack result, TransfiguratorRecipe.Type type) {
        this.result = result;
        this.type = type;
    }

    public static TransfiguratorRecipeBuilder transfigurator(ItemLike result, TransfiguratorRecipe.Type type) {
        return new TransfiguratorRecipeBuilder(new ItemStack(result), type);
    }

    public static TransfiguratorRecipeBuilder transfigurator(ItemLike result, int count, TransfiguratorRecipe.Type type) {
        return new TransfiguratorRecipeBuilder(new ItemStack(result, count), type);
    }

    public TransfiguratorRecipeBuilder input(Ingredient input) {
        this.input = input;
        return this;
    }

    public TransfiguratorRecipeBuilder input(ItemLike input) {
        return input(Ingredient.of(input));
    }

    public TransfiguratorRecipeBuilder input(TagKey<Item> input) {
        return input(Ingredient.of(input));
    }

    public TransfiguratorRecipeBuilder nutrient(Ingredient nutrient) {
        this.nutrient = nutrient;
        return this;
    }

    public TransfiguratorRecipeBuilder nutrient(ItemLike nutrient) {
        return nutrient(Ingredient.of(nutrient));
    }

    public TransfiguratorRecipeBuilder nutrient(TagKey<Item> nutrient) {
        return nutrient(Ingredient.of(nutrient));
    }

    public TransfiguratorRecipeBuilder addFailedResult(ItemStack stack, int weight) {
        this.failedResults.add(new TransfiguratorRecipe.WeightedItem(stack, weight));
        return this;
    }

    public TransfiguratorRecipeBuilder addFailedResult(ItemLike item, int weight) {
        return addFailedResult(new ItemStack(item), weight);
    }

    public TransfiguratorRecipeBuilder addFailedResult(ItemLike item, int count, int weight) {
        return addFailedResult(new ItemStack(item, count), weight);
    }

    public TransfiguratorRecipeBuilder processingTime(int processingTime) {
        this.processingTime = processingTime;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        save(pFinishedRecipeConsumer, getRecipeId());
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, String pRecipeId) {
        ResourceLocation resourceLocation = getRecipeId();
        ResourceLocation resourceLocation2 = ResourceLocation.parse(pRecipeId);
        if (resourceLocation2.equals(resourceLocation)) {
            throw new IllegalStateException("Recipe " + pRecipeId + " should remove its 'save' argument as it is equal to default one");
        } else {
            save(pFinishedRecipeConsumer, resourceLocation2);
        }
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        if (advancement.getCriteria().isEmpty()) {
            LostInTime.LOGGER.warn("No way of obtaining recipe {}", pRecipeId);
        }
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, group, input, nutrient, failedResults, result, processingTime, type, advancement));
    }

    protected ResourceLocation getRecipeId() {
        return ResourceLocation.fromNamespaceAndPath(
                LostInTime.MODID, "transfigurator/" + ForgeRegistries.ITEMS.getKey(result.getItem()).getPath());
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final Ingredient input;
        private final Ingredient nutrient;
        private final List<TransfiguratorRecipe.WeightedItem> failedResults;
        private final ItemStack result;
        private final int processingTime;
        private final TransfiguratorRecipe.Type type;
        private final Advancement.Builder advancement;

        public Result(ResourceLocation id, String group, Ingredient input, Ingredient nutrient, List<TransfiguratorRecipe.WeightedItem> failedResults, ItemStack result, int processingTime, TransfiguratorRecipe.Type type, Advancement.Builder advancement) {
            this.id = id;
            this.group = group;
            this.input = input;
            this.nutrient = nutrient;
            this.failedResults = failedResults;
            this.result = result;
            this.processingTime = processingTime;
            this.type = type;
            this.advancement = advancement;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            if (group != null) {
                pJson.addProperty("group", group);
            }
            pJson.add("input", input.toJson());
            pJson.add("nutrient", nutrient.toJson());
            
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", ForgeRegistries.ITEMS.getKey(result.getItem()).toString());
            if (result.getCount() > 1) {
                resultObj.addProperty("count", result.getCount());
            }
            pJson.add("result", resultObj);
            pJson.addProperty("processing_time", processingTime);
            pJson.addProperty("processingType", type.getSerializedName());

            if (!failedResults.isEmpty()) {
                JsonArray failedArray = new JsonArray();
                for (TransfiguratorRecipe.WeightedItem item : failedResults) {
                    JsonObject obj = new JsonObject();
                    JsonObject itemObj = new JsonObject();
                    itemObj.addProperty("item", ForgeRegistries.ITEMS.getKey(item.stack().getItem()).toString());
                    if (item.stack().getCount() > 1) {
                        itemObj.addProperty("count", item.stack().getCount());
                    }
                    obj.add("item", itemObj);
                    obj.addProperty("weight", item.weight());
                    failedArray.add(obj);
                }
                pJson.add("failed_results", failedArray);
            }
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return TransfiguratorRecipe.SERIALIZER;
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            return advancement.serializeToJson();
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "recipes/" + id.getPath());
        }
    }
}
