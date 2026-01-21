package com.ren.lostintime.common.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class SoulConfiguratorRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final Ingredient precedentMatter;
    private final Ingredient aspect;
    private final Ingredient coherenceMedium;
    private final Ingredient soulPowder;
    private final ItemStack result;
    private final float chance;

    public SoulConfiguratorRecipe(ResourceLocation id, Ingredient precedentMatter, Ingredient aspect, Ingredient coherenceMedium,
                                  Ingredient soulPowder, ItemStack result, float chance) {
        this.id = id;
        this.precedentMatter = precedentMatter;
        this.aspect = aspect;
        this.coherenceMedium = coherenceMedium;
        this.soulPowder = soulPowder;
        this.result = result;
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
            return ItemStack.EMPTY;
        }
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }
}
