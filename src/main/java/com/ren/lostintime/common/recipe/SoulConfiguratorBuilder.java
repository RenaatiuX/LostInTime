package com.ren.lostintime.common.recipe;

import com.google.gson.JsonObject;
import com.ren.lostintime.LostInTime;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SoulConfiguratorBuilder implements RecipeBuilder {

    private final Ingredient precedent;
    private final Ingredient lapisLazuli;
    private final Ingredient aspect;
    private final Ingredient coherence;
    private final Ingredient soulPowder;
    private final ItemStack resultA;
    private final ItemStack resultB;
    private String group;
    private float chance = 0.5F;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

    public SoulConfiguratorBuilder(Ingredient precedent, Ingredient lapisLazuli, Ingredient aspect, Ingredient coherence, Ingredient soulPowder,
                                   ItemStack resultA, ItemStack resultB) {
        this.precedent = precedent;
        this.lapisLazuli = lapisLazuli;
        this.aspect = aspect;
        this.coherence = coherence;
        this.soulPowder = soulPowder;
        this.resultA = resultA;
        this.resultB = resultB;
    }

    public SoulConfiguratorBuilder chance(float chance) {
        this.chance = chance;
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
        return resultA.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        if (advancement.getCriteria().isEmpty()) {
            LostInTime.LOGGER.warn("No way of obtaining recipe {}", pRecipeId);
        }
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, group, precedent, lapisLazuli, aspect, coherence, soulPowder,
                resultA, resultB, chance, advancement));
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

    //Hmm
    private ResourceLocation getRecipeId() {
        return new ResourceLocation(
                LostInTime.MODID, "soul_configurator/" + ForgeRegistries.ITEMS.getKey(resultA.getItem()).getPath());
    }

    public static class Result implements FinishedRecipe {

        private final ResourceLocation id;
        private final String group;
        private final Ingredient precedent;
        private final Ingredient lapisLazuli;
        private final Ingredient aspect;
        private final Ingredient coherence;
        private final Ingredient soulPowder;
        private final ItemStack resultA;
        private final ItemStack resultB;
        private final float chance;
        private final Advancement.Builder criteria;

        public Result(ResourceLocation id, String group, Ingredient precedent, Ingredient lapisLazuli, Ingredient aspect, Ingredient coherence,
                      Ingredient soulPowder, ItemStack resultA, ItemStack resultB, float chance, Advancement.Builder criteria) {
            this.id = id;
            this.group = group;
            this.precedent = precedent;
            this.lapisLazuli = lapisLazuli;
            this.aspect = aspect;
            this.coherence = coherence;
            this.soulPowder = soulPowder;
            this.resultA = resultA;
            this.resultB = resultB;
            this.chance = chance;
            this.criteria = criteria;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            if (group != null) {
                pJson.addProperty("group", group);
            }
            pJson.add("precedent_matter", precedent.toJson());
            pJson.add("lapis_lazuli", lapisLazuli.toJson());
            pJson.add("aspect", aspect.toJson());
            pJson.add("coherence_medium", coherence.toJson());
            pJson.add("soul_powder", soulPowder.toJson());

            JsonObject resultAJson = new JsonObject();
            resultAJson.addProperty("item", ForgeRegistries.ITEMS.getKey(resultA.getItem()).toString());
            if (resultA.getCount() > 1) resultAJson.addProperty("count", resultA.getCount());
            pJson.add("result_a", resultAJson);

            JsonObject resultBJson = new JsonObject();
            resultBJson.addProperty("item", ForgeRegistries.ITEMS.getKey(resultB.getItem()).toString());
            if (resultB.getCount() > 1) resultBJson.addProperty("count", resultB.getCount());
            pJson.add("result_b", resultBJson);

            pJson.addProperty("return_chance", chance);
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
