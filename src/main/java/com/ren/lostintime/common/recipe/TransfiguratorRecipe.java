package com.ren.lostintime.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ren.lostintime.common.init.RecipeInit;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TransfiguratorRecipe implements Recipe<Container> {

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation id;
    private final Ingredient input;
    private final Ingredient nutrient;
    private final List<WeightedItem> failedResults;
    private final ItemStack result;
    private final int processingTime;

    public TransfiguratorRecipe(ResourceLocation id, Ingredient input, Ingredient nutrient, List<WeightedItem> failedResults, ItemStack result, int processingTime) {
        this.id = id;
        this.input = input;
        this.nutrient = nutrient;
        this.failedResults = failedResults;
        this.result = result;
        this.processingTime = processingTime;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        // Slot 0 is input, Slot 3 is nutrient
        return input.test(pContainer.getItem(0)) && nutrient.test(pContainer.getItem(1));
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeInit.TRANSFIGURATOR_RECIPE.get();
    }

    public Ingredient getInput() {
        return input;
    }

    public Ingredient getNutrient() {
        return nutrient;
    }

    public List<WeightedItem> getFailedResults() {
        return failedResults;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public ItemStack getFailedResult(RandomSource random) {
        if (failedResults.isEmpty()) return ItemStack.EMPTY;
        int totalWeight = failedResults.stream().mapToInt(WeightedItem::weight).sum();
        int pick = random.nextInt(totalWeight);
        int current = 0;
        for (WeightedItem item : failedResults) {
            current += item.weight();
            if (pick < current) {
                return item.stack().copy();
            }
        }
        return ItemStack.EMPTY;
    }

    public record WeightedItem(ItemStack stack, int weight) {
    }

    public static class Serializer implements RecipeSerializer<TransfiguratorRecipe> {

        @Override
        public TransfiguratorRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient input = Ingredient.fromJson(pSerializedRecipe.get("input"));
            Ingredient nutrient = Ingredient.fromJson(pSerializedRecipe.get("nutrient"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));
            int processingTime = GsonHelper.getAsInt(pSerializedRecipe, "processing_time", 200);
            
            List<WeightedItem> failedResults = new ArrayList<>();
            if (pSerializedRecipe.has("failed_results")) {
                JsonArray failedArray = GsonHelper.getAsJsonArray(pSerializedRecipe, "failed_results");
                for (JsonElement element : failedArray) {
                    JsonObject obj = element.getAsJsonObject();
                    ItemStack stack = ShapedRecipe.itemStackFromJson(obj.getAsJsonObject("item"));
                    int weight = GsonHelper.getAsInt(obj, "weight");
                    failedResults.add(new WeightedItem(stack, weight));
                }
            }

            return new TransfiguratorRecipe(pRecipeId, input, nutrient, failedResults, result, processingTime);
        }

        @Override
        public @Nullable TransfiguratorRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            Ingredient nutrient = Ingredient.fromNetwork(pBuffer);
            ItemStack result = pBuffer.readItem();
            int processingTime = pBuffer.readVarInt();
            
            int failedCount = pBuffer.readVarInt();
            List<WeightedItem> failedResults = new ArrayList<>(failedCount);
            for (int i = 0; i < failedCount; i++) {
                ItemStack stack = pBuffer.readItem();
                int weight = pBuffer.readVarInt();
                failedResults.add(new WeightedItem(stack, weight));
            }

            return new TransfiguratorRecipe(pRecipeId, input, nutrient, failedResults, result, processingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, TransfiguratorRecipe pRecipe) {
            pRecipe.input.toNetwork(pBuffer);
            pRecipe.nutrient.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeVarInt(pRecipe.processingTime);
            
            pBuffer.writeVarInt(pRecipe.failedResults.size());
            for (WeightedItem item : pRecipe.failedResults) {
                pBuffer.writeItem(item.stack());
                pBuffer.writeVarInt(item.weight());
            }
        }
    }
}
