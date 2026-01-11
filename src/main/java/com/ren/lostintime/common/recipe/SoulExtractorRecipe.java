package com.ren.lostintime.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ren.lostintime.common.blockentity.SoulExtractorRecipeContainer;
import com.ren.lostintime.common.init.RecipeInit;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SoulExtractorRecipe implements Recipe<SoulExtractorRecipeContainer> {

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation id;
    private final NonNullList<Ingredient> inputs;
    private final Ingredient soulSource;
    private final Ingredient catalyst;
    private final ItemStack result;
    private final float chance;
    private final int residueOnSuccess;

    public SoulExtractorRecipe(ResourceLocation id, NonNullList<Ingredient> inputs, Ingredient soulSource,
                               Ingredient catalyst, ItemStack result, float chance, int residueOnSuccess) {
        this.id = id;
        this.inputs = inputs;
        this.soulSource = soulSource;
        this.catalyst = catalyst;
        this.result = result;
        this.chance = chance;
        this.residueOnSuccess = residueOnSuccess;
    }

    @Override
    public boolean matches(SoulExtractorRecipeContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide) return false;
        if (pContainer.getAmountInputSlots() < inputs.size()) return false;
        var list = new ArrayList<ItemStack>();
        //cleanse all empty stacks
        for (int i = 0; i < pContainer.getAmountInputSlots(); i++) {
            var stack = pContainer.getInput().getStackInSlot(i);
            if (!stack.isEmpty())
                list.add(stack);
        }
        //only the allowed amount of inputs is allowed inside the input inventory
        if (list.size() != inputs.size()) return false;
        Set<Integer> indicesBlacklisted = new TreeSet<>();
        for (Ingredient ingredient : inputs) {
            for (int i = 0; i < list.size(); i++) {
                if (indicesBlacklisted.contains(i)) continue;
                var stack = list.get(i);
                if (stack.isEmpty()) continue;
                if (ingredient.test(stack)) {
                    indicesBlacklisted.add(i);
                    break;
                }
            }
            if (indicesBlacklisted.size() != inputs.size()) return false;
        }
        if (!soulSource.test(pContainer.getSoulSource())) {
            return false;
        }
        return (catalyst.isEmpty() && pContainer.getCatalyst().isEmpty()) || catalyst.test(pContainer.getCatalyst());
    }

    @Override
    public ItemStack assemble(SoulExtractorRecipeContainer pContainer, RegistryAccess pRegistryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
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
        return RecipeInit.SOUL_EXTRACTOR_RECIPE.get();
    }

    public float getChance() {
        return chance;
    }

    public int getResidueOnSuccess() {
        return residueOnSuccess;
    }

    public NonNullList<Ingredient> getInputs() {
        return inputs;
    }

    public Ingredient getSoulSource() {
        return soulSource;
    }

    public Ingredient getCatalyst() {
        return catalyst;
    }

    public static class Serializer implements RecipeSerializer<SoulExtractorRecipe> {

        @Override
        public SoulExtractorRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            NonNullList<Ingredient> inputs = NonNullList.create();
            JsonArray inputsArray = GsonHelper.getAsJsonArray(pSerializedRecipe, "inputs", new JsonArray());
            for (JsonElement element : inputsArray) {
                inputs.add(Ingredient.fromJson(element));
            }
            if (!pSerializedRecipe.has("soul_source")) {
                throw new JsonParseException("Soul Extractor recipe requires a soul_source");
            }
            Ingredient soulSource = Ingredient.fromJson(
                    GsonHelper.getAsJsonObject(pSerializedRecipe, "soul_source"));
            Ingredient catalyst = Ingredient.EMPTY;
            if (pSerializedRecipe.has("catalyst")) {
                catalyst = Ingredient.fromJson(
                        GsonHelper.getAsJsonObject(pSerializedRecipe, "catalyst"));
            }
            ItemStack result = ShapedRecipe.itemStackFromJson(
                    GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));
            float chance = GsonHelper.getAsFloat(pSerializedRecipe, "chance", 1.0F);
            int residueOnSuccess = GsonHelper.getAsInt(pSerializedRecipe, "residue_on_success", 0);

            return new SoulExtractorRecipe(pRecipeId, inputs, soulSource, catalyst, result, chance, residueOnSuccess);
        }

        @Override
        public @Nullable SoulExtractorRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int inputCount = pBuffer.readVarInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(inputCount, Ingredient.EMPTY);
            for (int i = 0; i < inputCount; i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
            }
            Ingredient soulSource = Ingredient.fromNetwork(pBuffer);
            Ingredient catalyst = Ingredient.fromNetwork(pBuffer);
            ItemStack result = pBuffer.readItem();
            float chance = pBuffer.readFloat();
            int residueOnSuccess = pBuffer.readVarInt();
            return new SoulExtractorRecipe(pRecipeId, inputs, soulSource, catalyst, result, chance, residueOnSuccess);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, SoulExtractorRecipe pRecipe) {
            pBuffer.writeVarInt(pRecipe.getInputs().size());
            for (Ingredient ingredient : pRecipe.getInputs()) {
                ingredient.toNetwork(pBuffer);
            }
            //u can access even private fields directly in here, use it!
            pRecipe.getSoulSource().toNetwork(pBuffer);
            pRecipe.getCatalyst().toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeFloat(pRecipe.getChance());
            pBuffer.writeVarInt(pRecipe.getResidueOnSuccess());
        }
    }
}
