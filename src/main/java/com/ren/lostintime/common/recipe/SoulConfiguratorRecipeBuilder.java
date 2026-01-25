package com.ren.lostintime.common.recipe;

import com.google.gson.JsonObject;
import com.ren.lostintime.LostInTime;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.awt.event.ItemListener;
import java.util.function.Consumer;

public class SoulConfiguratorRecipeBuilder implements RecipeBuilder {

    public static SoulConfiguratorRecipeBuilder recipe(ItemLike successResult, ItemLike failResult) {
        return recipe(new ItemStack(successResult), new ItemStack(failResult));
    }

    public static SoulConfiguratorRecipeBuilder recipe(ItemLike successResult) {
        return recipe(new ItemStack(successResult));
    }

    public static SoulConfiguratorRecipeBuilder recipe(ItemStack successResult) {
        return new SoulConfiguratorRecipeBuilder(successResult, ItemStack.EMPTY);
    }

    public static SoulConfiguratorRecipeBuilder recipe(ItemStack successResult, ItemStack failResult) {
        return new SoulConfiguratorRecipeBuilder(successResult, failResult);
    }

    //aka fossil
    private Ingredient precedent;
    private Ingredient aspect;
    private Ingredient bindingMaterial;
    private final ItemStack successResult;
    private final ItemStack failResult;
    private String group;
    private float chance = 0.5F;
    private int requiredSoulFuel = 100;
    private int processTime = 200;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

    public SoulConfiguratorRecipeBuilder(ItemStack successResult, ItemStack failResult) {
        this.successResult = successResult;
        this.failResult = failResult;
    }

    public SoulConfiguratorRecipeBuilder bindingMaterial(Ingredient bindingMaterial) {
        this.bindingMaterial = bindingMaterial;
        return this;
    }

    public SoulConfiguratorRecipeBuilder bindingMaterial(ItemLike... bindingMaterial) {
        return bindingMaterial(Ingredient.of(bindingMaterial));
    }

    public SoulConfiguratorRecipeBuilder bindingMaterial(TagKey<Item> bindingMaterial) {
        return bindingMaterial(Ingredient.of(bindingMaterial));
    }

    public SoulConfiguratorRecipeBuilder fossil(Ingredient precedent) {
        this.precedent = precedent;
        return this;
    }

    public SoulConfiguratorRecipeBuilder fossil(ItemLike... precedent) {
        return fossil(Ingredient.of(precedent));
    }

    public SoulConfiguratorRecipeBuilder fossil(TagKey<Item> precedent) {
        return fossil(Ingredient.of(precedent));
    }

    public SoulConfiguratorRecipeBuilder aspect(Ingredient aspect) {
        this.aspect = aspect;
        return this;
    }

    public SoulConfiguratorRecipeBuilder aspect(ItemLike... aspect) {
        return aspect(Ingredient.of(aspect));
    }

    public SoulConfiguratorRecipeBuilder aspect(TagKey<Item> aspect) {
        return aspect(Ingredient.of(aspect));
    }


    public SoulConfiguratorRecipeBuilder fuel(int requiredFuel) {
        this.requiredSoulFuel = Math.max(1, requiredFuel);
        return this;
    }

    public SoulConfiguratorRecipeBuilder processTime(int processTime) {
        this.processTime = Math.max(1, processTime);
        return this;
    }

    public SoulConfiguratorRecipeBuilder chance(float chance) {
        this.chance = Mth.clamp(chance, 0f, 1f);
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
        return successResult.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        pRecipeId = pRecipeId.withPrefix("soul_configurator/");
        if (precedent == null) {
            throw new IllegalArgumentException(String.format("Soul configuration recipe %s has null precedent(fossil)", pRecipeId));
        }
        if (aspect == null) {
            throw new IllegalArgumentException(String.format("Soul configuration recipe %s has null aspect", pRecipeId));
        }

        if (bindingMaterial == null) {
            throw new IllegalArgumentException(String.format("Soul configuration recipe %s has null bindingMaterial", pRecipeId));
        }
        if (advancement.getCriteria().isEmpty()) {
            LostInTime.LOGGER.warn("No way of obtaining recipe through advancements {}", pRecipeId);
        }
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, this));
    }

    public static class Result implements FinishedRecipe {

        private final ResourceLocation id;
        private final SoulConfiguratorRecipeBuilder data;

        public Result(ResourceLocation id, SoulConfiguratorRecipeBuilder data) {
            this.id = id;
            this.data = data;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            if (data.group != null) {
                pJson.addProperty("group", data.group);
            }
            pJson.add("precedent_matter", data.precedent.toJson());
            pJson.add("aspect", data.aspect.toJson());
            pJson.add("coherence_medium", data.bindingMaterial.toJson());


            pJson.add("success_result", fromItemStack(data.successResult));
            if (!data.failResult.isEmpty())
                pJson.add("fail_result", fromItemStack(data.failResult));

            if (data.chance != 0.5f)
                pJson.addProperty("chance", data.chance);
            if (data.requiredSoulFuel != 100) {
                pJson.addProperty("requiredSoulFuel", data.requiredSoulFuel);
            }
            if (data.processTime != 200) {
                pJson.addProperty("processTime", data.processTime);
            }
        }

        public static JsonObject fromItemStack(ItemStack stack) {
            JsonObject stackJson = new JsonObject();
            stackJson.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
            if (stack.getCount() > 1) stackJson.addProperty("count", stack.getCount());
            return stackJson;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return SoulConfiguratorRecipe.SERIALIZER;
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            if (data.advancement.getCriteria().isEmpty())
                return null;
            return data.advancement.serializeToJson();
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            if (data.advancement.getCriteria().isEmpty())
                return null;
            return new ResourceLocation(id.getNamespace(),
                    "recipes/soul_extractor/" + id.getPath());
        }
    }
}
