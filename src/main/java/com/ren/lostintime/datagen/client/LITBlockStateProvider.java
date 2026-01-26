package com.ren.lostintime.datagen.client;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.block.*;
import com.ren.lostintime.common.init.BlockInit;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
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
        blockItem(BlockInit.MANGO_LOG);

        leavesBlock(BlockInit.MANGO_LEAVES);
        saplingBlock(BlockInit.MANGO_SAPLING);
        stageBlock(BlockInit.MANGO_FRUIT_LEAVES.get(), MangoFruitBlock.AGE, true);
        blockItem(BlockInit.MANGO_FRUIT_LEAVES);

        createDodoEggModel(BlockInit.DODO_EGG.get(), SingleEggBlock.HATCH);
        createBabyRoeBlock(BlockInit.BOTHRIOLEPIS_ROE.get());

        block(BlockInit.QUATERNARY_FOSSIL_BLOCK.get());
        block(BlockInit.NEOGENE_FOSSIL_BLOCK.get());
        block(BlockInit.PALEOGENE_FOSSIL_BLOCK.get());
        block(BlockInit.CRETACEOUS_FOSSIL_BLOCK.get());
        block(BlockInit.DEEPSLATE_JURASSIC_FOSSIL_BLOCK.get());
        block(BlockInit.DEEPSLATE_TRIASSIC_FOSSIL_BLOCK.get());
        block(BlockInit.DEEPSLATE_PERMIAN_FOSSIL_BLOCK.get());
        block(BlockInit.DEEPSLATE_CARBONIFEROUS_FOSSIL_BLOCK.get());
        block(BlockInit.DEEPSLATE_DEVONIAN_FOSSIL_BLOCK.get());
        block(BlockInit.DEEPSLATE_SILURIAN_FOSSIL_BLOCK.get());
        block(BlockInit.DEEPSLATE_ORDOVICIAN_FOSSIL_BLOCK.get());
        block(BlockInit.DEEPSLATE_CAMBRIAN_FOSSIL_BLOCK.get());

        block(BlockInit.SANDSTONE_BRICKS.get());
        block(BlockInit.SMALL_SANDSTONE_BRICKS.get());

        soulExtractorModels(BlockInit.SOUL_EXTRACTOR.get());
        soulExtractor(BlockInit.SOUL_EXTRACTOR.get());
        soulConfigurator(BlockInit.SOUL_CONFIGURATOR.get());

        identificationTableBlock(BlockInit.IDENTIFICATION_TABLE.get());

        spongeBlock(BlockInit.BARREL_SPONGE.get());
        spongeBlock(BlockInit.DEAD_BARREL_SPONGE.get());

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

    private void createBabyRoeBlock(Block block) {
        ModelFile modelFile = models().withExistingParent(blockName(block), modLoc("block/template_roe"))
                .texture("texture", resourceBlock(blockName(block)))
                .texture("particle", resourceBlock(blockName(block)))
                .renderType("cutout");

        simpleBlock(block, modelFile);
    }

    private void soulExtractorModels(Block block) {
        String name = blockName(block);

        // LOWER OFF
        models().withExistingParent(name + "_lower_off",
                        modLoc("block/soul_extractor_lower"))
                .texture("2", modLoc("block/soul_extractor_lower_side_off"))
                .texture("3", modLoc("block/soul_extractor_lower_top"))
                .texture("4", modLoc("block/soul_extractor_bottom"))
                .texture("particle", modLoc("block/soul_extractor_lower_side_off"))
                .renderType("cutout");

        // LOWER ON
        models().withExistingParent(name + "_lower_on",
                        modLoc("block/soul_extractor_lower"))
                .texture("2", modLoc("block/soul_extractor_lower_side_on"))
                .texture("3", modLoc("block/soul_extractor_lower_top"))
                .texture("4", modLoc("block/soul_extractor_bottom"))
                .texture("particle", modLoc("block/soul_extractor_lower_side_on"))
                .renderType("cutout");

        // UPPER OFF
        models().withExistingParent(name + "_upper_off",
                        modLoc("block/soul_extractor_upper"))
                .texture("3", modLoc("block/soul_extractor_side_upper_off"))
                .texture("0", modLoc("block/soul_extractor_upper_top"))
                .texture("2", modLoc("block/soul_extractor_lower_bottom"))
                .texture("particle", modLoc("block/soul_extractor_upper_top"))
                .renderType("cutout");

        // UPPER ON
        models().withExistingParent(name + "_upper_on",
                        modLoc("block/soul_extractor_upper"))
                .texture("3", modLoc("block/soul_extractor_side_upper_on"))
                .texture("0", modLoc("block/soul_extractor_upper_top"))
                .texture("2", modLoc("block/soul_extractor_lower_bottom"))
                .texture("particle", modLoc("block/soul_extractor_upper_top"))
                .renderType("cutout");
    }


    private void soulExtractor(Block block) {
        String name = blockName(block);

        ModelFile lowerOff = models().getExistingFile(modLoc("block/" + name + "_lower_off"));
        ModelFile lowerOn = models().getExistingFile(modLoc("block/" + name + "_lower_on"));
        ModelFile upperOff = models().getExistingFile(modLoc("block/" + name + "_upper_off"));
        ModelFile upperOn = models().getExistingFile(modLoc("block/" + name + "_upper_on"));

        getVariantBuilder(block).forAllStates(state -> {
            boolean on = state.getValue(SoulExtractorBlock.ON);
            DoubleBlockHalf half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);

            ModelFile model = half == DoubleBlockHalf.LOWER ? (on ? lowerOn : lowerOff) : (on ? upperOn : upperOff);

            return ConfiguredModel.builder().modelFile(model).build();
        });
    }

    private void identificationTableBlock(Block block) {
        var name = blockName(block);
        var model = models().orientableWithBottom("block/" + name, modLoc("block/identification_table_side"),
                modLoc("block/identification_table_side_front"), mcLoc("block/oak_planks"), modLoc
                        ("block/identification_table_top"));

        horizontalBlock(block, model);
        simpleBlockItem(block, model);
    }


    private void soulConfigurator(Block block) {
        String name = blockName(block);
        getVariantBuilder(block).forAllStates(state -> {
            SoulConfiguratorBlock.Part part = state.getValue(SoulConfiguratorBlock.PART);
            Direction facing = state.getValue(SoulConfiguratorBlock.FACING);
            boolean on = state.getValue(LITMachineBlock.ON);
            var onString =  on ? "on" : "off";

            ModelFile model = switch (part) {
                case MAIN -> models().withExistingParent(name + "_main_" + onString,
                                modLoc("block/soul_configurator_lower"))
                        .texture("0", modLoc("block/soul_configurator_" + onString))
                        .texture("particle", modLoc("block/soul_configurator_" + onString))
                        .renderType("cutout");
                case TOP  ->  models().withExistingParent(name + "_top_" + onString,
                                modLoc("block/soul_configurator_upper"))
                        .texture("0", modLoc("block/soul_configurator_" + onString))
                        .texture("particle", modLoc("block/soul_configurator_" + onString))
                        .renderType("translucent");
                case SIDE ->  models().withExistingParent(name + "_side_" + onString,
                                modLoc("block/soul_configurator_right"))
                        .texture("0", modLoc("block/soul_configurator_" + onString))
                        .texture("particle", modLoc("block/soul_configurator_" + onString))
                        .renderType("cutout");
            };

            int yRot = switch (facing) {
                case NORTH -> 180;
                case SOUTH -> 0;
                case WEST  -> 90;
                case EAST  -> 270;
                default -> 0;
            };

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(yRot)
                    .build();
        });
    }

    private void spongeBlock(Block block) {
        String name = blockName(block);
        ModelFile model = models().cubeBottomTop(name, modLoc("block/" + name + "_side"), modLoc("block/" + name +
                "_bottom"), modLoc("block/" + name + "_top"));
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    protected void block(Block block, ModelFile model) {
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    protected void block(Block... blocks) {
        for (Block b : blocks) {
            simpleBlock(b);
            simpleBlockItem(b, cubeAll(b));
        }
    }

    private String blockName(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block).getPath();
    }

    public ResourceLocation resourceBlock(String path) {
        return new ResourceLocation(LostInTime.MODID, "block/" + path);
    }
}
