package com.ren.lostintime.common.recipe;

import com.google.gson.JsonObject;
import com.ren.lostintime.common.init.RecipeInit;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulConfiguratorRecipe implements Recipe<Container> {

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation id;
    //aka fossil
    private final Ingredient precedentMatter;
    private final Ingredient aspect;
    //aka binding material
    private final Ingredient coherenceMedium;
    private final ItemStack successResult;
    private final ItemStack failResult;
    private final float chance;
    private final int requiredSoulFuel;
    private final int processTime;

    public SoulConfiguratorRecipe(ResourceLocation id, Ingredient precedentMatter, Ingredient aspect, Ingredient coherenceMedium, ItemStack successResult, ItemStack failResult, float chance, int requiredSoulFuel, int processTime) {
        this.id = id;
        this.precedentMatter = precedentMatter;
        this.aspect = aspect;
        this.coherenceMedium = coherenceMedium;
        this.successResult = successResult;
        this.failResult = failResult;
        this.chance = chance;
        this.requiredSoulFuel = requiredSoulFuel;
        this.processTime = processTime;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (pLevel.isClientSide()) return false;
        if (pContainer.getContainerSize() != 3) return false;

        return precedentMatter.test(pContainer.getItem(0)) && aspect.test(pContainer.getItem(1)) &&
                coherenceMedium.test(pContainer.getItem(2));
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        if (Math.random() < chance) {
            return successResult.copy();
        } else {
            return failResult.copy();
        }
    }

    public ItemStack getResult(Level level) {
        if (level.random.nextFloat() < chance) {
            return successResult.copy();
        }
        return failResult.copy();
    }

    public int getProcessTime() {
        return processTime;
    }

    public int getRequiredSoulFuel() {
        return requiredSoulFuel;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return successResult.copy();
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
        return RecipeInit.SOUL_CONFIGURATOR_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<SoulConfiguratorRecipe> {

        @Override
        public SoulConfiguratorRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient precedent = Ingredient.fromJson(pSerializedRecipe.get("precedent_matter"));
            Ingredient aspect = Ingredient.fromJson(pSerializedRecipe.get("aspect"));
            Ingredient coherence = Ingredient.fromJson(pSerializedRecipe.get("coherence_medium"));

            ItemStack successResult = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("success_result"));

            ItemStack failResult = pSerializedRecipe.has("fail_result") ? ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("fail_result")) : ItemStack.EMPTY;

            float chance = Mth.clamp(GsonHelper.getAsFloat(pSerializedRecipe, "chance", 0.5f), 0f, 1f);
            int requiredSoulFuel = Math.max(1, GsonHelper.getAsInt(pSerializedRecipe, "requiredSoulFuel", 100));
            int processTime = Math.max(1, GsonHelper.getAsInt(pSerializedRecipe, "processTime", 20 * 10));
            return new SoulConfiguratorRecipe(pRecipeId, precedent, aspect, coherence, successResult, failResult, chance, requiredSoulFuel, processTime);
        }

        @Override
        public @Nullable SoulConfiguratorRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient precedent = Ingredient.fromNetwork(pBuffer);
            Ingredient aspect = Ingredient.fromNetwork(pBuffer);
            Ingredient coherence = Ingredient.fromNetwork(pBuffer);
            ItemStack successResult = pBuffer.readItem();
            ItemStack failResult = pBuffer.readItem();
            float chance = pBuffer.readFloat();
            int requiredSoulFuel = pBuffer.readInt();
            int processTime = pBuffer.readInt();
            return new SoulConfiguratorRecipe(pRecipeId, precedent, aspect, coherence, successResult, failResult, chance, requiredSoulFuel, processTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, SoulConfiguratorRecipe pRecipe) {
            pRecipe.precedentMatter.toNetwork(pBuffer);
            pRecipe.aspect.toNetwork(pBuffer);
            pRecipe.coherenceMedium.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.successResult);
            pBuffer.writeItem(pRecipe.failResult);
            pBuffer.writeFloat(pRecipe.chance);
            pBuffer.writeInt(pRecipe.requiredSoulFuel);
            pBuffer.writeInt(pRecipe.processTime);
        }
    }
}
