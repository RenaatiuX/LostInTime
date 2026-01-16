package com.ren.lostintime.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ren.lostintime.common.init.RecipeInit;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IdentificationRecipe implements Recipe<Container> {

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation id;
    protected final Ingredient input;
    private final NavigableMap<Double, ItemStack> weightedOutputs;

    protected IdentificationRecipe(ResourceLocation resourceLocation, Ingredient input, NavigableMap<Double, ItemStack> weightedOutputs) {
        this.id = resourceLocation;
        this.input = input;
        this.weightedOutputs = weightedOutputs;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonNullList = NonNullList.create();
        nonNullList.add(input);
        return nonNullList;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            if (matches(pContainer, i)) {
                return true;
            }
        }
        return false;
    }

    private boolean matches(Container container, int slot) {
        ItemStack itemStack = container.getItem(slot);
        if (itemStack.isEmpty()) return false;
        return input.test(itemStack);
    }

    @Override
    public @NotNull ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    public ItemStack getRandomOutput(RandomSource random){
        return weightedOutputs.higherEntry(random.nextDouble() * weightedOutputs.lastKey()).getValue().copy();
    }

    public Collection<ItemStack> allPossibleOutputs(){
        return weightedOutputs.values();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
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
        return RecipeInit.IDENTIFICATION_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<IdentificationRecipe> {

        @Override
        public IdentificationRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            JsonElement jsonelement = GsonHelper.isArrayNode(pSerializedRecipe, "input") ? GsonHelper.getAsJsonArray(pSerializedRecipe,
                    "input") : GsonHelper.getAsJsonObject(pSerializedRecipe, "input");
            Ingredient input = Ingredient.fromJson(jsonelement);
            NavigableMap<Double, ItemStack> outputs = weightedItemsFromJson(GsonHelper.getAsJsonArray(pSerializedRecipe, "outputs"));
            return new IdentificationRecipe(pRecipeId, input, outputs);
        }

        @Override
        public @Nullable IdentificationRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            NavigableMap<Double, ItemStack> outputs = new TreeMap<>();
            int outputSize = pBuffer.readVarInt();
            for (int i = 0; i < outputSize; i++) {
                outputs.put(pBuffer.readDouble(), pBuffer.readItem());
            }
            return new IdentificationRecipe(pRecipeId, input, outputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, IdentificationRecipe pRecipe) {
            pRecipe.input.toNetwork(pBuffer);
            pBuffer.writeVarInt(pRecipe.weightedOutputs.size());
            for (Map.Entry<Double, ItemStack> output : pRecipe.weightedOutputs.entrySet()) {
                pBuffer.writeDouble(output.getKey());
                pBuffer.writeItem(output.getValue());
            }
        }

        private static NavigableMap<Double, ItemStack> weightedItemsFromJson(JsonArray outputsArray) {
            NavigableMap<Double, ItemStack> items = new TreeMap<>();
            double total = 0;
            for (int i = 0; i < outputsArray.size(); ++i) {
                JsonObject object = outputsArray.get(i).getAsJsonObject();
                ItemStack item = ShapedRecipe.itemStackFromJson(object);
                if (item.isEmpty()) continue;
                total += GsonHelper.getAsDouble(object, "weight");
                items.put(total, item);
            }
            return items;
        }
    }
}
