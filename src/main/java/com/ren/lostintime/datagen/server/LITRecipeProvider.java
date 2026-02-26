package com.ren.lostintime.datagen.server;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.BlockInit;
import com.ren.lostintime.common.init.ItemInit;
import com.ren.lostintime.common.recipe.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class LITRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public LITRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter) {
       smeltingRecipes(pWriter);

        // Quaternary
        addFossilRecipe(pWriter, ItemInit.QUATERNARY_FOSSIL.get(), false, builder -> builder
                .addOutput(ItemInit.DODO_FOSSIL.get(), 25)
                .addOutput(ItemInit.DODO_SKULL.get(), 5)
                .addOutput(Items.BONE, 33)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(Items.SKELETON_SKULL, 1)
                .addOutput(Items.NAUTILUS_SHELL, 1)
                .addOutput(ItemInit.AMBER.get(), 0.25)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 1.25)
        );

        // Neogene
        addFossilRecipe(pWriter, ItemInit.NEOGENE_FOSSIL.get(), false, builder -> builder
                .addOutput(ItemInit.DAEODON_FOSSIL.get(), 20)
                .addOutput(ItemInit.DAEODON_SKULL.get(), 5)
                .addOutput(Items.BONE, 33)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(ItemInit.AMBER.get(), 0.25)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 1.25)
        );

        // Paleogene
        addFossilRecipe(pWriter, ItemInit.PALEOGENE_FOSSIL.get(), false, builder -> builder
                .addOutput(ItemInit.LEPTICTIDIUM_FOSSIL.get(), 25)
                .addOutput(Items.BONE, 33)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(ItemInit.AMBER.get(), 0.25)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 1.25)
        );

        // Cretaceous
        addFossilRecipe(pWriter, ItemInit.CRETACEOUS_FOSSIL.get(), false, builder -> builder
                .addOutput(ItemInit.DEINONYCHUS_FOSSIL.get(), 25)
                .addOutput(ItemInit.DEINONYCHUS_SKULL.get(), 5)
                .addOutput(Items.BONE, 33)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(ItemInit.AMBER.get(), 0.33)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 1.25)
        );

        // Jurassic
        addFossilRecipe(pWriter, ItemInit.JURASSIC_FOSSIL.get(), false, builder -> builder
                .addOutput(ItemInit.PLESIOSAURUS_FOSSIL.get(), 25)
                .addOutput(ItemInit.PLESIOSAURUS_SKULL.get(), 5)
                .addOutput(Items.BONE, 33)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(ItemInit.AMBER.get(), 0.4)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 1.25)
        );
        addFossilRecipe(pWriter, BlockInit.DEEPSLATE_JURASSIC_FOSSIL_BLOCK.get(), true, builder -> builder
                .addOutput(ItemInit.PLESIOSAURUS_FOSSIL.get(), 25)
                .addOutput(ItemInit.PLESIOSAURUS_SKULL.get(), 5)
                .addOutput(Items.BONE, 33)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(ItemInit.AMBER.get(), 0.4)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 1.25)
        );

        // Triassic
        addFossilRecipe(pWriter, ItemInit.TRIASSIC_FOSSIL.get(), false, builder -> builder
                .addOutput(ItemInit.MASTODONSAURUS_FOSSIL.get(), 25)
                .addOutput(ItemInit.MASTODONSAURUS_SKULL.get(), 5)
                .addOutput(Items.BONE, 33)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(ItemInit.AMBER.get(), 0.125)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 1.25)
        );
        addFossilRecipe(pWriter, BlockInit.DEEPSLATE_TRIASSIC_FOSSIL_BLOCK.get(), true, builder -> builder
                .addOutput(ItemInit.MASTODONSAURUS_FOSSIL.get(), 25)
                .addOutput(ItemInit.MASTODONSAURUS_SKULL.get(), 5)
                .addOutput(Items.BONE, 33)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(ItemInit.AMBER.get(), 0.125)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 1.25)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 1.25)
        );

        // Permian
        addFossilRecipe(pWriter, ItemInit.PERMIAN_FOSSIL.get(), false, builder -> builder
                .addOutput(ItemInit.SCUTOSAURUS_FOSSIL.get(), 25)
                .addOutput(ItemInit.SCUTOSAURUS_SKULL.get(), 5)
                .addOutput(Items.BONE, 33)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(ItemInit.AMBER.get(), 0.125)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 2.5)
        );
        addFossilRecipe(pWriter, BlockInit.DEEPSLATE_PERMIAN_FOSSIL_BLOCK.get(), true, builder -> builder
                .addOutput(ItemInit.SCUTOSAURUS_FOSSIL.get(), 25)
                .addOutput(ItemInit.SCUTOSAURUS_SKULL.get(), 5)
                .addOutput(Items.BONE, 33)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(ItemInit.AMBER.get(), 0.125)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 2.5)
        );

        // Carboniferous
        addFossilRecipe(pWriter, ItemInit.CARBONIFEROUS_FOSSIL.get(), false, builder -> builder
                .addOutput(ItemInit.HYLONOMUS_FOSSIL.get(), 25)
                .addOutput(Items.BONE, 15)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(ItemInit.AMBER.get(), 0.25)
                .addOutput(Items.COAL, 20)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 2.08)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 2.08)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 2.08)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 2.08)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 2.08)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 2.08)
        );
        addFossilRecipe(pWriter, BlockInit.DEEPSLATE_CARBONIFEROUS_FOSSIL_BLOCK.get(), true, builder -> builder
                .addOutput(ItemInit.HYLONOMUS_FOSSIL.get(), 25)
                .addOutput(Items.BONE, 15)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(ItemInit.AMBER.get(), 0.25)
                .addOutput(Items.COAL, 20)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 2.08)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 2.08)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 2.08)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 2.08)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 2.08)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 2.08)
        );

        // Devonian
        addFossilRecipe(pWriter, ItemInit.DEVONIAN_FOSSIL.get(), false, builder -> builder
                .addOutput(ItemInit.BOTHRIOLEPIS_FOSSIL.get(), 20)
                .addOutput(Items.BONE, 15)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 2.5)
        );
        addFossilRecipe(pWriter, BlockInit.DEEPSLATE_DEVONIAN_FOSSIL_BLOCK.get(), true, builder -> builder
                .addOutput(ItemInit.BOTHRIOLEPIS_FOSSIL.get(), 20)
                .addOutput(Items.BONE, 15)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 2.5)
        );

        // Silurian
        addFossilRecipe(pWriter, ItemInit.SILURIAN_FOSSIL.get(), false, builder -> builder
                .addOutput(ItemInit.PTERYGOTUS_FOSSIL.get(), 20)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(Items.CALCITE, 10)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 2.5)
        );
        addFossilRecipe(pWriter, BlockInit.DEEPSLATE_SILURIAN_FOSSIL_BLOCK.get(), true, builder -> builder
                .addOutput(ItemInit.PTERYGOTUS_FOSSIL.get(), 20)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(Items.CALCITE, 10)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 2.5)
        );

        // Ordovician
        addFossilRecipe(pWriter, ItemInit.ORDOVICIAN_FOSSIL.get(), false, builder -> builder
                .addOutput(ItemInit.ENDOCERAS_FOSSIL.get(), 20)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(Items.CALCITE, 10)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 2.5)
        );
        addFossilRecipe(pWriter, BlockInit.DEEPSLATE_ORDOVICIAN_FOSSIL_BLOCK.get(), true, builder -> builder
                .addOutput(ItemInit.ENDOCERAS_FOSSIL.get(), 20)
                .addOutput(Items.BONE_MEAL, 33)
                .addOutput(Items.CALCITE, 10)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 2.5)
        );

        // Cambrian
        addFossilRecipe(pWriter, ItemInit.CAMBRIAN_FOSSIL.get(), false, builder -> builder
                .addOutput(ItemInit.ANOMALOCARIS_FOSSIL.get(), 10)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 2.5)
        );
        addFossilRecipe(pWriter, BlockInit.DEEPSLATE_CAMBRIAN_FOSSIL_BLOCK.get(), true, builder -> builder
                .addOutput(ItemInit.ANOMALOCARIS_FOSSIL.get(), 10)
                .addOutput(BlockInit.DEAD_BARREL_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_GLASS_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_PIPE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_TREE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_VASE_SPONGE.get(), 2.5)
                .addOutput(BlockInit.DEAD_WOOL_SPONGE.get(), 2.5)
        );

        //I DON'T QUITE UNDERSTAND THE FIRST RECIPE
        soulExtract(ItemInit.ASPECT_EMERGENCE.get())
                .addInput(Ingredient.of(ItemInit.SOUL_GRUME.get()))
                .soulSource(Ingredient.of(ItemInit.ECTOPLASM.get()))
                .catalyst(Ingredient.of(ItemInit.CALCITE_CATALYST.get()))
                .chance(0.25f)
                .residueOnSuccess(3)
                .unlockedBy("has_soul_blob", has(ItemInit.SOUL_GRUME.get()))
                .save(pWriter);

        soulExtract(Items.SOUL_CAMPFIRE)
                .addInput(Ingredient.of(Items.CAMPFIRE))
                .soulSource(Ingredient.of(ItemTags.SOUL_FIRE_BASE_BLOCKS))
                .unlockedBy("has_campfire", has(Blocks.CAMPFIRE))
                .save(pWriter);
        soulExtract(Items.SOUL_LANTERN)
                .addInput(Ingredient.of(Items.LANTERN))
                .soulSource(Ingredient.of(ItemTags.SOUL_FIRE_BASE_BLOCKS))
                .unlockedBy("has_lantern", has(Blocks.LANTERN))
                .save(pWriter);
        soulExtract(Items.SOUL_TORCH)
                .addInput(Ingredient.of(Items.TORCH))
                .soulSource(Ingredient.of(ItemTags.SOUL_FIRE_BASE_BLOCKS))
                .unlockedBy("has_torch", has(Blocks.TORCH))
                .save(pWriter);

        soulExtract(Items.CRYING_OBSIDIAN)
                .addInput(Ingredient.of(Items.OBSIDIAN))
                .soulSource(Ingredient.of(ItemTags.SOUL_FIRE_BASE_BLOCKS))
                .unlockedBy("has_obsidian", has(Blocks.OBSIDIAN))
                .save(pWriter);

        soulExtract(new ItemStack(Items.ENDER_PEARL, 2))
                .addInput(Ingredient.of(Items.SLIME_BALL))
                .addInput(Ingredient.of(Items.EGG))
                .soulSource(Ingredient.of(ItemTags.SOUL_FIRE_BASE_BLOCKS))
                .unlockedBy("has_slime_ball", has(Items.SLIME_BALL))
                .unlockedBy("has_egg", has(Items.EGG))
                .save(pWriter);

        soulExtract(Items.ENDER_EYE)
                .addInput(Ingredient.of(Items.ENDER_PEARL))
                .soulSource(Ingredient.of(ItemInit.SOUL_ASH.get()))
                .residueOnSuccess(2)
                .unlockedBy("has_ender_perl", has(Items.ENDER_PEARL))
                .save(pWriter);

        soulExtract(ItemInit.SPINEL.get())
                .addInput(Ingredient.of(Items.ENDER_PEARL, Items.CHORUS_PLANT, Items.POPPED_CHORUS_FRUIT, Items.PURPUR_BLOCK, Items.SHULKER_SHELL))
                .soulSource(Ingredient.of(ItemTags.SOUL_FIRE_BASE_BLOCKS))
                .unlockedBy("has_chorus_block", has(Blocks.CHORUS_PLANT))
                .save(pWriter);

        SoulConfiguratorFuelRecipeBuilder.fuelRecipe(ItemInit.SOUL_POWDER.get(), 300).save(pWriter, Objects.requireNonNull(ResourceLocation.tryBuild(LostInTime.MODID, "soul_powder_fuel")));
        SoulConfiguratorRecipeBuilder.recipe(ItemInit.ANOMALOCARIS_SOUL_CFC.get(), ItemInit.EMPTY_SOUL_CFC.get())
                .aspect(ItemInit.ASPECT_EMERGENCE.get())
                .bindingMaterial(ItemInit.EMPTY_SOUL_CFC.get())
                .fossil(ItemInit.ANOMALOCARIS_FOSSIL.get())
                .unlockedBy("hasItem", has(ItemInit.ANOMALOCARIS_FOSSIL.get()))
                .save(pWriter);
        TransfiguratorRecipeBuilder.transfigurator(BlockInit.DODO_EGG.get(), TransfiguratorRecipe.Type.EGG)
                .addFailedResult(Items.ROTTEN_FLESH, 7)
                .addFailedResult(ItemInit.RAW_DODO.get(), 2)
                .input(ItemInit.DODO_SOUL_CFC.get())
                .unlockedBy("hasItem", has(ItemInit.DODO_SOUL_CFC.get())).save(pWriter);

    }

    private void addFossilRecipe(Consumer<FinishedRecipe> pWriter, ItemLike input, boolean isDeepslate, Consumer<IdentificationBuilder> builderConsumer) {
        IdentificationBuilder builder = identify(input);

        // Common outputs
        if (isDeepslate) {
            builder.addOutput(Items.COBBLED_DEEPSLATE, 30);
        } else {
            builder.addOutput(Items.COBBLESTONE, 15);
        }
        builder.addOutput(Items.GRAVEL, 33);
        builder.addOutput(Items.IRON_NUGGET, 1, 3, 10);
        builder.addOutput(Items.GOLD_NUGGET, 1, 2, 2.5);
        builder.addOutput(Items.DIRT, 25);

        // Specific outputs
        builderConsumer.accept(builder);

        builder.save(pWriter);
    }

    private static void cookingFood(String name, ItemLike ingredient, ItemLike result, float experience, Consumer<FinishedRecipe> consumer) {
        String namePrefix = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, name).toString();
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

    public void machineRecipes(Consumer<FinishedRecipe> pWriter) {
        /*
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockInit.SOUL_CONFIGURATOR.get())
                .define('I', ItemInit.INFORMATION_DOME.get())
                .define('S', ItemInit.PANE)

         */
    }

    public void smeltingRecipes(Consumer<FinishedRecipe> pWriter){
        cookingFood("cooked_dodo", ItemInit.RAW_DODO.get(), ItemInit.COOKED_DODO.get(), 0.35F, pWriter);
        cookingFood("cooked_endoceras", ItemInit.RAW_ENDOCERAS.get(), ItemInit.COOKED_ENDOCERAS.get(), 0.35F, pWriter);
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

    public SoulExtractorBuilder soulExtract(ItemStack resultStack) {
        return new SoulExtractorBuilder(resultStack);
    }

}
