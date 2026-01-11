package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.recipe.IdentificationRecipe;
import com.ren.lostintime.common.recipe.SoulExtractorRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeInit {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES,
            LostInTime.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS, LostInTime.MODID);

    public static final RegistryObject<RecipeType<IdentificationRecipe>> IDENTIFICATION_RECIPE = register("identification_recipe",
            IdentificationRecipe.SERIALIZER);
    public static final RegistryObject<RecipeType<SoulExtractorRecipe>> SOUL_EXTRACTOR_RECIPE = register("soul_extractor_recipe",
            SoulExtractorRecipe.SERIALIZER);

    public static <T extends Recipe<?>> RegistryObject<RecipeType<T>> register(String name, RecipeSerializer<T> serializer) {
        RegistryObject<RecipeType<T>> type = RECIPE_TYPES.register(name, () -> type(name));
        RECIPE_SERIALIZER.register(name, () -> serializer);
        return type;
    }

    protected static <T extends Recipe<?>> RecipeType<T> type(String name) {
        return new RecipeType<T>() {
            @Override
            public String toString() {
                return new ResourceLocation(LostInTime.MODID, name).toString();
            }
        };
    }
}
