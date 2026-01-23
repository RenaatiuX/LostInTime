package com.ren.lostintime.common.recipe;

import com.google.gson.JsonObject;
import com.ren.lostintime.common.init.RecipeInit;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SoulConfiguratorFuelRecipe implements Recipe<Container> {

    public static final Serializer SERIALIZER = new Serializer();

    private final Ingredient input;
    private final int fuelValue;
    private final ResourceLocation id;

    public SoulConfiguratorFuelRecipe(Ingredient input, int fuelValue, ResourceLocation id) {
        this.input = input;
        this.fuelValue = fuelValue;
        this.id = id;
    }

    public int getFuelValue() {
        return fuelValue;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (pContainer.getContainerSize() == 1){
            return input.test(pContainer.getItem(0));
        }
        return false;
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeInit.SOUL_CONFIGURATOR_FUEL_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<SoulConfiguratorFuelRecipe> {

        @Override
        public SoulConfiguratorFuelRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {

            var input = Ingredient.fromJson(pSerializedRecipe.get("input"));
            var value = GsonHelper.getAsInt(pSerializedRecipe, "fuelValue", 300);
            return new SoulConfiguratorFuelRecipe(input, value, pRecipeId);
        }

        @Override
        public @Nullable SoulConfiguratorFuelRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            var input = Ingredient.fromNetwork(pBuffer);
            var value = pBuffer.readInt();
            return new SoulConfiguratorFuelRecipe(input, value, pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, SoulConfiguratorFuelRecipe pRecipe) {
            pRecipe.input.toNetwork(pBuffer);
            pBuffer.writeInt(pRecipe.fuelValue);
        }
    }
}
