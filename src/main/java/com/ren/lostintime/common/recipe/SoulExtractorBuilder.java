package com.ren.lostintime.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ren.lostintime.LostInTime;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SoulExtractorBuilder implements RecipeBuilder {

    private final NonNullList<Ingredient> inputs  = NonNullList.create();
    private Ingredient soulSource = Ingredient.EMPTY;
    private Ingredient catalyst =  Ingredient.EMPTY;
    private final ItemStack result;
    private float chance = 1.0F;
    private int residueOnSuccess = 0;
    private String group;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

    public SoulExtractorBuilder(ItemStack result) {
        this.result = result;
    }

    public SoulExtractorBuilder addInput(Ingredient ingredient) {
        if (inputs.size() >= 3)
            throw new IllegalStateException("Soul Extractor supports max 3 inputs");
        this.inputs.add(ingredient);
        return this;
    }

    public SoulExtractorBuilder soulSource(Ingredient ingredient) {
        this.soulSource = ingredient;
        return this;
    }

    public SoulExtractorBuilder catalyst(Ingredient ingredient) {
        this.catalyst = ingredient;
        return this;
    }

    public SoulExtractorBuilder chance(float chance) {
        this.chance = chance;
        return this;
    }

    public SoulExtractorBuilder residueOnSuccess(int residue) {
        this.residueOnSuccess = residue;
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
        ResourceLocation resourceLocation2 = new ResourceLocation(pRecipeId);
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
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, group, inputs, soulSource, catalyst, result, chance,
                residueOnSuccess, advancement));
    }

    protected ResourceLocation getRecipeId() {
        return new ResourceLocation(
                LostInTime.MODID, "soul_extractor/" + ForgeRegistries.ITEMS.getKey(result.getItem()).getPath());
    }


    public static class Result implements FinishedRecipe {

        private final ResourceLocation id;
        private final String group;
        private final NonNullList<Ingredient> inputs;
        private final Ingredient soulSource;
        private final Ingredient catalyst;
        private final ItemStack result;
        private final float chance;
        private final int residueOnSuccess;
        private final Advancement.Builder criteria;

        public Result(ResourceLocation id, String group, NonNullList<Ingredient> inputs, Ingredient soulSource,
                Ingredient catalyst, ItemStack result, float chance, int residueOnSuccess, Advancement.Builder criteria) {
            this.id = id;
            this.group = group;
            this.inputs = inputs;
            this.soulSource = soulSource;
            this.catalyst = catalyst;
            this.result = result;
            this.chance = chance;
            this.residueOnSuccess = residueOnSuccess;
            this.criteria = criteria;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            if (group != null) {
                pJson.addProperty("group", group);
            }
            JsonArray inputsArray = new JsonArray();
            for (Ingredient ingredient : inputs) {
                inputsArray.add(ingredient.toJson());
            }
            pJson.add("inputs", inputsArray);
            pJson.add("soul_source", soulSource.toJson());
            if (!catalyst.isEmpty()) {
                pJson.add("catalyst", catalyst.toJson());
            }
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", ForgeRegistries.ITEMS.getKey(result.getItem()).toString());
            resultObj.addProperty("count", result.getCount());
            resultObj.addProperty("chance", chance);
            pJson.add("result", resultObj);
            if (residueOnSuccess > 0) {
                pJson.addProperty("residue_on_success", residueOnSuccess);
            }
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return SoulExtractorRecipe.SERIALIZER;
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            if (criteria.getCriteria().isEmpty())
                return null;
            return criteria.serializeToJson();
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            if (criteria.getCriteria().isEmpty())
                return null;
            return new ResourceLocation(id.getNamespace(),
                    "recipes/soul_extractor/" + id.getPath());
        }
    }
}
