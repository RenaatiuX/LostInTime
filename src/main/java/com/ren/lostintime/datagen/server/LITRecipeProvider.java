package com.ren.lostintime.datagen.server;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.BlockInit;
import com.ren.lostintime.common.init.ItemInit;
import com.ren.lostintime.common.recipe.IdentificationBuilder;
import com.ren.lostintime.common.recipe.SoulConfiguratorFuelRecipeBuilder;
import com.ren.lostintime.common.recipe.SoulConfiguratorRecipeBuilder;
import com.ren.lostintime.common.recipe.SoulExtractorBuilder;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class LITRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public LITRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter) {
        cookingFood("cooked_dodo", ItemInit.RAW_DODO.get(), ItemInit.COOKED_DODO.get(),0.35F, pWriter);

        identify(ItemInit.QUATERNARY_FOSSIL.get()).addOutput(ItemInit.DODO_FOSSIL.get(), 25)
                .addOutput(ItemInit.DODO_SKULL.get(), 5).addOutput(Items.BONE, 33)
                .addOutput(Items.BONE_MEAL, 33).addOutput(Items.NAUTILUS_SHELL, 1)
                .addOutput(ItemInit.AMBER.get(), 0.25D).addOutput(Items.COBBLESTONE, 15)
                .addOutput(Items.COBBLED_DEEPSLATE, 30).addOutput(Items.GRAVEL, 33)
                .addOutput(Items.IRON_NUGGET, 1, 3, 10).addOutput(Items.GOLD_NUGGET, 1, 3, 0.25)
                .addOutput(Items.DIRT, 25).addOutput(Items.SKELETON_SKULL, 1).save(pWriter);

        soulExtract(ItemInit.ASPECT_EMERGENCE.get())
                .addInput(Ingredient.of(ItemInit.SOUL_GRUME.get()))
                .soulSource(Ingredient.of(ItemInit.ECTOPLASM.get()))
                .catalyst(Ingredient.of(ItemInit.CALCITE_CATALYST.get()))
                .chance(0.25f)
                .residueOnSuccess(3)
                .unlockedBy("has_soul_blob", has(ItemInit.SOUL_GRUME.get()))
                .save(pWriter);

        SoulConfiguratorFuelRecipeBuilder.fuelRecipe(ItemInit.SOUL_POWDER.get(), 300).save(pWriter, new ResourceLocation(LostInTime.MODID, "soul_powder_fuel"));
        SoulConfiguratorRecipeBuilder.recipe(ItemInit.ANOMALOCARIS_SOUL_CFC.get(), ItemInit.EMPTY_SOUL_CFC.get())
                .aspect(ItemInit.ASPECT_EMERGENCE.get())
                .bindingMaterial(ItemInit.EMPTY_SOUL_CFC.get())
                .fossil(ItemInit.ANOMALOCARIS_FOSSIL.get())
                .unlockedBy("hasItem", has(ItemInit.ANOMALOCARIS_FOSSIL.get()))
                .save(pWriter);

    }

    private static void cookingFood(String name, ItemLike ingredient, ItemLike result, float experience, Consumer<FinishedRecipe> consumer) {
        String namePrefix = new ResourceLocation(LostInTime.MODID, name).toString();
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), RecipeCategory.FOOD, result, experience, 200)
                .unlockedBy(name, InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
                .save(consumer);
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(ingredient), RecipeCategory.FOOD, result, experience, 600)
                .unlockedBy(name, InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
                .save(consumer, namePrefix + "_from_campfire_cooking");
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(ingredient), RecipeCategory.FOOD, result, experience, 100)
                .unlockedBy(name, InventoryChangeTrigger.TriggerInstance.hasItems(ingredient))
                .save(consumer, namePrefix + "_from_smoking");
    }

    public void machineRecipes(Consumer<FinishedRecipe> pWriter){
        /*
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockInit.SOUL_CONFIGURATOR.get())
                .define('I', ItemInit.INFORMATION_DOME.get())
                .define('S', ItemInit.PANE)

         */
    }

    private IdentificationBuilder identify(ItemLike item) {
        return new IdentificationBuilder(LostInTime.MODID, item);
    }

    private IdentificationBuilder identify(TagKey<Item> tagKey) {
        return new IdentificationBuilder(LostInTime.MODID, tagKey);
    }

    public SoulExtractorBuilder soulExtract(ItemLike result) {
        return new SoulExtractorBuilder(new ItemStack(result));
    }

}
