package com.ren.lostintime.datagen.client;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.block.DodoEggBlock;
import com.ren.lostintime.common.block.MangoFruitBlock;
import com.ren.lostintime.common.init.BlockInit;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;


public class LITBlockStateProvider extends BlockStateProvider {

    public LITBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, LostInTime.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        logBlock((RotatedPillarBlock) BlockInit.MANGO_LOG.get());

        leavesBlock(BlockInit.MANGO_LEAVES);
        blockItem(BlockInit.MANGO_LOG);
        saplingBlock(BlockInit.MANGO_SAPLING);
        stageBlock(BlockInit.MANGO_FRUIT_LEAVES.get(), MangoFruitBlock.AGE, true);
        blockItem(BlockInit.MANGO_FRUIT_LEAVES);

        createDodoEggModel(BlockInit.DODO_EGG.get(), DodoEggBlock.HATCH);
    }

    private void saplingBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlock(blockRegistryObject.get(),
                models().cross(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(),
                        blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    private void leavesBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().singleTexture(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(),
                        new ResourceLocation("minecraft:block/leaves"),
                        "all", blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    private void blockItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile(LostInTime.MODID +
                ":block/" + ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath()));
    }

    public void stageBlock(Block block, IntegerProperty ageProperty, boolean isLeafBlock, Property<?>... ignored) {
        getVariantBuilder(block)
                .forAllStatesExcept(state -> {
                    int ageSuffix = state.getValue(ageProperty);
                    String stageName = blockName(block) + "_stage" + ageSuffix;
                    if (isLeafBlock) {
                        return ConfiguredModel.builder()
                                .modelFile(models()
                                        .withExistingParent(stageName, mcLoc("block/cube_all"))
                                        .texture("all", resourceBlock(stageName))
                                        .renderType("cutout"))
                                .build();
                    } else {
                        return ConfiguredModel.builder()
                                .modelFile(models()
                                        .cross(stageName, resourceBlock(stageName))
                                        .renderType("cutout"))
                                .build();
                    }
                }, ignored);
    }

    private void createDodoEggModel(Block block, IntegerProperty hatchProperty) {
        Function<Integer, ResourceLocation> modelGenerator = (hatchStage) -> {
            String suffix = switch (hatchStage) {
                case 1 -> "_slightly_cracked";
                case 2 -> "_very_cracked";
                default -> "_not_cracked";
            };

            return models()
                    .getBuilder("dodo_egg" + suffix)
                    .parent(new ModelFile.UncheckedModelFile(modLoc("block/dodo_egg")))
                    .texture("1", modLoc("block/dodo_egg" + suffix))
                    .texture("particle", modLoc("block/dodo_egg" + suffix))
                    .getLocation();
        };

        getVariantBuilder(block).forAllStates(state -> {
            int hatchStage = state.getValue(hatchProperty);
            return ConfiguredModel.builder()
                    .modelFile(new ModelFile.ExistingModelFile(modelGenerator.apply(hatchStage), models().existingFileHelper))
                    .build();
        });
    }

    private String blockName(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block).getPath();
    }

    public ResourceLocation resourceBlock(String path) {
        return new ResourceLocation(LostInTime.MODID, "block/" + path);
    }
}
