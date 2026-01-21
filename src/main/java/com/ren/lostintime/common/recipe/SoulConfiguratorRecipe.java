package com.ren.lostintime.common.recipe;

import com.google.gson.JsonObject;
import com.ren.lostintime.common.init.RecipeInit;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SoulConfiguratorRecipe implements Recipe<SimpleContainer> {

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation id;
    private final Ingredient precedentMatter;
    private final Ingredient aspect;
    private final Ingredient coherenceMedium;
    private final Ingredient soulPowder;
    private final ItemStack resultA;
    private final ItemStack resultB;
    private final float chance;

    public SoulConfiguratorRecipe(ResourceLocation id, Ingredient precedentMatter, Ingredient aspect, Ingredient coherenceMedium,
                                  Ingredient soulPowder, ItemStack resultA, ItemStack resultB, float chance) {
        this.id = id;
        this.precedentMatter = precedentMatter;
        this.aspect = aspect;
        this.coherenceMedium = coherenceMedium;
        this.soulPowder = soulPowder;
        this.resultA = resultA;
        this.resultB = resultB;
        this.chance = chance;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) return false;

        return precedentMatter.test(pContainer.getItem(0)) && aspect.test(pContainer.getItem(1)) &&
                coherenceMedium.test(pContainer.getItem(2)) && soulPowder.test(pContainer.getItem(3));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        if (Math.random() < chance) {
            return resultA.copy();
        } else {
            return resultB.copy();
        }
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return resultA.copy();
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
            Ingredient soulPowder = Ingredient.fromJson(pSerializedRecipe.get("soul_powder"));

            ItemStack resultA = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("result_a"));

            ItemStack resultB = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("result_b"));

            float chance = pSerializedRecipe.get("chance").getAsFloat();
            return new SoulConfiguratorRecipe(pRecipeId, precedent, aspect, coherence, soulPowder, resultA, resultB, chance);
        }

        @Override
        public @Nullable SoulConfiguratorRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient precedent = Ingredient.fromNetwork(pBuffer);
            Ingredient aspect = Ingredient.fromNetwork(pBuffer);
            Ingredient coherence = Ingredient.fromNetwork(pBuffer);
            Ingredient soulPowder = Ingredient.fromNetwork(pBuffer);
            ItemStack resultA = pBuffer.readItem();
            ItemStack resultB = pBuffer.readItem();
            float chance = pBuffer.readFloat();
            return new SoulConfiguratorRecipe(pRecipeId, precedent, aspect, coherence, soulPowder, resultA, resultB, chance);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, SoulConfiguratorRecipe pRecipe) {
            pRecipe.precedentMatter.toNetwork(pBuffer);
            pRecipe.aspect.toNetwork(pBuffer);
            pRecipe.coherenceMedium.toNetwork(pBuffer);
            pRecipe.soulPowder.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.resultA);
            pBuffer.writeItem(pRecipe.resultB);
            pBuffer.writeFloat(pRecipe.chance);
        }
    }
}
