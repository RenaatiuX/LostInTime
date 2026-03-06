package com.ren.lostintime.datagen.client;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.block.*;
import com.ren.lostintime.common.block.properties.TitanosarcolitesPart;
import com.ren.lostintime.common.init.BlockInit;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
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
        logBlock((RotatedPillarBlock) BlockInit.ARAUCARIOXYLON_LOG.get());
        blockItem(BlockInit.ARAUCARIOXYLON_LOG);
        logBlock((RotatedPillarBlock) BlockInit.STRIPPED_ARAUCARIOXYLON_LOG.get());
        blockItem(BlockInit.STRIPPED_ARAUCARIOXYLON_LOG);

        leavesBlock(BlockInit.ARAUCARIOXYLON_LEAVES);
        saplingBlock(BlockInit.ARAUCARIOXYLON_SAPLING);
        block(BlockInit.ARAUCARIOXYLON_PLANKS.get());

        leavesBlock(BlockInit.MANGO_LEAVES);
        saplingBlock(BlockInit.MANGO_SAPLING);
        stageBlock(BlockInit.MANGO_FRUIT_LEAVES.get(), LITFruitBlock.AGE, true);
        blockItem(BlockInit.MANGO_FRUIT_LEAVES);

        stairsBlock(((StairBlock) BlockInit.ARAUCARIOXYLON_STAIRS.get()), blockTexture(BlockInit.ARAUCARIOXYLON_PLANKS.get()));
        slabBlock(((SlabBlock) BlockInit.ARAUCARIOXYLON_SLAB.get()), blockTexture(BlockInit.ARAUCARIOXYLON_PLANKS.get()), blockTexture(BlockInit.ARAUCARIOXYLON_PLANKS.get()));

        buttonBlock(((ButtonBlock) BlockInit.ARAUCARIOXYLON_BUTTON.get()), blockTexture(BlockInit.ARAUCARIOXYLON_PLANKS.get()));
        pressurePlateBlock(((PressurePlateBlock) BlockInit.ARAUCARIOXYLON_PRESSURE_PLATE.get()), blockTexture(BlockInit.ARAUCARIOXYLON_PLANKS.get()));

        fenceBlock(((FenceBlock) BlockInit.ARAUCARIOXYLON_FENCE.get()), blockTexture(BlockInit.ARAUCARIOXYLON_PLANKS.get()));
        fenceGateBlock(((FenceGateBlock) BlockInit.ARAUCARIOXYLON_FENCE_GATE.get()), blockTexture(BlockInit.ARAUCARIOXYLON_PLANKS.get()));
        wallBlock(((WallBlock) BlockInit.ARAUCARIOXYLON_WALL.get()), blockTexture(BlockInit.ARAUCARIOXYLON_PLANKS.get()));

        doorBlockWithRenderType(((DoorBlock) BlockInit.ARAUCARIOXYLON_DOOR.get()), modLoc("block/araucarioxylon_door_bottom"), modLoc("block/araucarioxylon_door_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) BlockInit.ARAUCARIOXYLON_TRAPDOOR.get()), modLoc("block/araucarioxylon_trapdoor"), true, "cutout");

        createEggModel(BlockInit.DODO_EGG.get(), "dodo_egg", 1);
        createEggModel(BlockInit.SCUTOSAURUS_EGG.get(), "scutosaurus_egg", 2);

        createBabyRoeBlock(BlockInit.BOTHRIOLEPIS_ROE.get());
        createCrossRoeBlock(BlockInit.ENDOCERAS_EGG.get());
        createBabyRoeBlock(BlockInit.ANOMALOCARIS_ROE.get());
        createBabyRoeBlock(BlockInit.MASTODONSAURUS_EGG.get());
        customParentBlock(BlockInit.TITANOSARCOLITES, "titanosarcolites_model", "0");
        giantTitanosarcolites(BlockInit.GIANT_TITANOSARCOLITES.get());

        block(BlockInit.SPINEL_ORE.get());
        block(BlockInit.ZIRCON_ORE.get());
        block(BlockInit.QUATERNARY_FOSSIL_BLOCK.get());
        block(BlockInit.NEOGENE_FOSSIL_BLOCK.get());
        block(BlockInit.PALEOGENE_FOSSIL_BLOCK.get());
        block(BlockInit.CRETACEOUS_FOSSIL_BLOCK.get());
        block(BlockInit.DEEPSLATE_SPINEL_ORE.get());
        block(BlockInit.DEEPSLATE_ZIRCON_ORE.get());
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
        block(BlockInit.AMBER_BLOCK.get());
        block(BlockInit.SPINEL_BLOCK.get());
        block(BlockInit.ZIRCON_BLOCK.get());

        soulExtractorModels(BlockInit.SOUL_EXTRACTOR.get());
        soulExtractor(BlockInit.SOUL_EXTRACTOR.get());
        soulConfigurator(BlockInit.SOUL_CONFIGURATOR.get());

        transfigurator(BlockInit.TRANSFIGURATOR.get());

        identificationTableBlock(BlockInit.IDENTIFICATION_TABLE.get());

        spongeBlock(BlockInit.BARREL_SPONGE.get());
        spongeBlock(BlockInit.DEAD_BARREL_SPONGE.get());

        simplePlant(BlockInit.DEAD_GLASS_SPONGE);
        simplePlant(BlockInit.GLASS_SPONGE);
        simplePlant(BlockInit.CLADOPHLEBIS);
        simplePlant(BlockInit.CONIOPTERIS);
        simplePlant(BlockInit.COOKSONIA);
        simplePlant(BlockInit.GONDWANAGARICITES);

        hugeMushroomBlock(BlockInit.GONDWANAGARICITES_BLOCK.get(), modLoc("block/gondwanagaricites_block"));
        hugeMushroomBlock(BlockInit.GONDWANAGARICITES_STEM.get(), modLoc("block/gondwanagaricites_stem"));

        randomPlantVariants(BlockInit.RED_ALGAE, 3);

        doubleCoralPlant(BlockInit.DEAD_LARGE_PIPE_SPONGE);
        doubleCoralPlant(BlockInit.LARGE_PIPE_SPONGE);

        axisBlock((RotatedPillarBlock) BlockInit.ARAUCARIOXYLON_WOOD.get(),
                modLoc("block/araucarioxylon_log"),
                modLoc("block/araucarioxylon_log")
        );
        blockItem(BlockInit.ARAUCARIOXYLON_WOOD);

        axisBlock((RotatedPillarBlock) BlockInit.STRIPPED_ARAUCARIOXYLON_WOOD.get(),
                modLoc("block/stripped_araucarioxylon_log"),
                modLoc("block/stripped_araucarioxylon_log")
        );
        blockItem(BlockInit.STRIPPED_ARAUCARIOXYLON_WOOD);

        signBlock(((StandingSignBlock) BlockInit.ARAUCARIOXYLON_SIGN.get()), ((WallSignBlock) BlockInit.ARAUCARIOXYLON_WALL_SIGN.get()),
                blockTexture(BlockInit.ARAUCARIOXYLON_PLANKS.get()));

        hangingSignBlock(BlockInit.ARAUCARIOXYLON_HANGING_SIGN.get(), BlockInit.ARAUCARIOXYLON_WALL_HANGING_SIGN.get(), blockTexture(BlockInit.ARAUCARIOXYLON_PLANKS.get()));
    }

    public void hangingSignBlock(Block signBlock, Block wallSignBlock, ResourceLocation texture) {
        ModelFile sign = models().sign(blockName(signBlock), texture);
        hangingSignBlock(signBlock, wallSignBlock, sign);
    }

    public void hangingSignBlock(Block signBlock, Block wallSignBlock, ModelFile sign) {
        simpleBlock(signBlock, sign);
        simpleBlock(wallSignBlock, sign);
    }

    private void simplePlant(RegistryObject<Block> blockRO) {
        Block block = blockRO.get();
        String name = blockName(block);

        ModelFile model = models().cross(name, modLoc("block/" + name)).renderType("cutout");

        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    private void doubleCoralPlant(RegistryObject<Block> blockRO) {
        Block block = blockRO.get();
        String name = blockName(block);

        ModelFile lower = models()
                .cross(name + "_lower", modLoc("block/" + name + "_lower")).renderType("cutout");

        ModelFile upper = models()
                .cross(name + "_upper", modLoc("block/" + name + "_upper")).renderType("cutout");

        getVariantBuilder(block)
                .partialState().with(LITTallSpongeBlock.HALF, DoubleBlockHalf.LOWER)
                .modelForState().modelFile(lower).addModel()

                .partialState().with(LITTallSpongeBlock.HALF, DoubleBlockHalf.UPPER)
                .modelForState().modelFile(upper).addModel();

        simpleBlockItem(block, upper);
    }

    private void randomPlantVariants(RegistryObject<Block> blockRO, int variantCount) {
        Block block = blockRO.get();
        String name = blockName(block);

        ConfiguredModel[] models = new ConfiguredModel[variantCount];

        for (int i = 0; i < variantCount; i++) {
            ModelFile model = models()
                    .cross(name + "_" + i, modLoc("block/" + name + "_" + i))
                    .renderType("cutout");

            models[i] = new ConfiguredModel(model);
        }
        getVariantBuilder(block).partialState().setModels(models);
        simpleBlockItem(block, models[0].model);
    }

    private void hugeMushroomBlock(Block block, ResourceLocation texture) {
        String name = blockName(block);

        ModelFile outsideModel = models().withExistingParent(name, mcLoc("block/template_single_face"))
                .texture("texture", texture);

        ModelFile insideModel = new ModelFile.UncheckedModelFile(mcLoc("block/mushroom_block_inside"));

        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);

        builder.part().modelFile(outsideModel).rotationX(270).uvLock(true).addModel()
                .condition(HugeMushroomBlock.UP, true).end()
                .part().modelFile(insideModel).rotationX(270).uvLock(false).addModel()
                .condition(HugeMushroomBlock.UP, false).end();

        builder.part().modelFile(outsideModel).rotationX(90).uvLock(true).addModel()
                .condition(HugeMushroomBlock.DOWN, true).end()
                .part().modelFile(insideModel).rotationX(90).uvLock(false).addModel()
                .condition(HugeMushroomBlock.DOWN, false).end();

        builder.part().modelFile(outsideModel).addModel()
                .condition(HugeMushroomBlock.NORTH, true).end()
                .part().modelFile(insideModel).addModel()
                .condition(HugeMushroomBlock.NORTH, false).end();

        builder.part().modelFile(outsideModel).rotationY(90).uvLock(true).addModel()
                .condition(HugeMushroomBlock.EAST, true).end()
                .part().modelFile(insideModel).rotationY(90).uvLock(false).addModel()
                .condition(HugeMushroomBlock.EAST, false).end();

        builder.part().modelFile(outsideModel).rotationY(180).uvLock(true).addModel()
                .condition(HugeMushroomBlock.SOUTH, true).end()
                .part().modelFile(insideModel).rotationY(180).uvLock(false).addModel()
                .condition(HugeMushroomBlock.SOUTH, false).end();

        builder.part().modelFile(outsideModel).rotationY(270).uvLock(true).addModel()
                .condition(HugeMushroomBlock.WEST, true).end()
                .part().modelFile(insideModel).rotationY(270).uvLock(false).addModel()
                .condition(HugeMushroomBlock.WEST, false).end();

        simpleBlockItem(block, models().cubeAll(name + "_inventory", texture));
    }

    private void saplingBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlock(blockRegistryObject.get(),
                models().cross(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(),
                        blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    private void leavesBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().singleTexture(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(),
                        ResourceLocation.parse("minecraft:block/leaves"),
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

    private void createEggModel(Block block, String baseName, int maxModels) {
        getVariantBuilder(block).forAllStates(state -> {
            int hatchStage = state.getValue(BlockStateProperties.HATCH);
            int eggsCount = state.getValue(BlockStateProperties.EGGS);
            int visualEggs = Math.min(eggsCount, maxModels);

            String hatchSuffix = switch (hatchStage) {
                case 1 -> "_slightly_cracked";
                case 2 -> "_very_cracked";
                default -> "_not_cracked";
            };

            String parentName = maxModels > 1 ? baseName + visualEggs : baseName;
            String generatedModelName = parentName + hatchSuffix;

            ModelFile model = models().getBuilder(generatedModelName)
                    .parent(new ModelFile.UncheckedModelFile(modLoc("block/" + parentName)))
                    .texture("1", modLoc("block/" + baseName + hatchSuffix))
                    .texture("particle", modLoc("block/" + baseName + hatchSuffix));

            return ConfiguredModel.builder().modelFile(model).build();
        });
    }

    private void createBabyRoeBlock(Block block) {
        ModelFile modelFile = models().withExistingParent(blockName(block), modLoc("block/template_roe"))
                .texture("texture", resourceBlock(blockName(block)))
                .texture("particle", resourceBlock(blockName(block)))
                .renderType("cutout");

        simpleBlock(block, modelFile);
    }

    private void createCrossRoeBlock(Block block) {
        String name = blockName(block);
        ModelFile modelFile = models().cross(name, modLoc("block/" + name)).renderType("cutout");
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

    private void transfigurator(Block block) {
        String name = blockName(block);

        // Bottom Off
        ModelFile bottomOff = models().withExistingParent(name + "_bottom_off", modLoc("block/transfigurator_bottom"))
                .texture("0", modLoc("block/transfigurator_side"))
                .renderType("translucent");

        // Bottom On
        ModelFile bottomOn = models().withExistingParent(name + "_bottom_on", modLoc("block/transfigurator_bottom"))
                .texture("0", modLoc("block/transfigurator_side_on"))
                .renderType("translucent");

        // Top Off
        ModelFile topOff = models().withExistingParent(name + "_top_off", modLoc("block/transfigurator_top"))
                .texture("1", modLoc("block/transfigurator_side_top"))
                .renderType("translucent");

        // Top On
        ModelFile topOn = models().withExistingParent(name + "_top_on", modLoc("block/transfigurator_top"))
                .texture("1", modLoc("block/transfigurator_side_top_on"))
                .renderType("translucent");

        getVariantBuilder(block).forAllStates(state -> {
            boolean on = state.getValue(TransfiguratorBlock.ON);
            DoubleBlockHalf half = state.getValue(TransfiguratorBlock.HALF);
            Direction facing = state.getValue(TransfiguratorBlock.FACING);

            ModelFile model = half == DoubleBlockHalf.LOWER ? (on ? bottomOn : bottomOff) : (on ? topOn : topOff);

            int yRot = switch (facing) {
                case NORTH -> 0;
                case SOUTH -> 180;
                case WEST -> 270;
                case EAST -> 90;
                default -> 0;
            };

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(yRot)
                    .build();
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
            var onString = on ? "on" : "off";

            ModelFile model = switch (part) {
                case MAIN -> models().withExistingParent(name + "_main_" + onString,
                                modLoc("block/soul_configurator_lower"))
                        .texture("0", modLoc("block/soul_configurator_" + onString))
                        .texture("particle", modLoc("block/soul_configurator_" + onString))
                        .renderType("cutout");
                case TOP -> models().withExistingParent(name + "_top_" + onString,
                                modLoc("block/soul_configurator_upper"))
                        .texture("0", modLoc("block/soul_configurator_" + onString))
                        .texture("particle", modLoc("block/soul_configurator_" + onString))
                        .renderType("translucent");
                case SIDE -> models().withExistingParent(name + "_side_" + onString,
                                modLoc("block/soul_configurator_right"))
                        .texture("0", modLoc("block/soul_configurator_" + onString))
                        .texture("particle", modLoc("block/soul_configurator_" + onString))
                        .renderType("cutout");
            };

            int yRot = switch (facing) {
                case NORTH -> 180;
                case SOUTH -> 0;
                case WEST -> 90;
                case EAST -> 270;
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

    private void customParentBlock(RegistryObject<Block> blockRO, String customParentName, String textureKey) {
        Block block = blockRO.get();
        String name = blockName(block);
        ModelFile model = models().withExistingParent(name, modLoc("block/" + customParentName))
                .texture(textureKey, modLoc("block/" + name))
                .texture("particle", modLoc("block/" + name))
                .renderType("cutout");

        horizontalBlock(block, model);
        simpleBlockItem(block, model);
    }

    private void giantTitanosarcolites(Block block) {
        String name = blockName(block);

        ModelFile pincerLeft = models().withExistingParent(name + "_pincer_left",
                        modLoc("block/giant_titanosarcolites_big_3"))
                .texture("0", modLoc("block/titanosarcolites_big_top_lower"))
                .texture("1", modLoc("block/titanosarcolites_big_side_upper"))
                .renderType("cutout");
        ModelFile pincerRight = models().withExistingParent(name + "_pincer_right",
                        modLoc("block/giant_titanosarcolites_big_4"))
                .texture("0", modLoc("block/titanosarcolites_big_top_lower"))
                .texture("1", modLoc("block/titanosarcolites_big_side_upper"))
                .renderType("cutout");

        ModelFile baseLeft = models().withExistingParent(name + "_base_left",
                        modLoc("block/giant_titanosarcolites_big_2"))
                .texture("0", modLoc("block/titanosarcolites_big_top_upper"))
                .texture("1", modLoc("block/titanosarcolites_big_side_upper"))
                .texture("particle", modLoc("block/titanosarcolites_big_top_upper"))
                .renderType("cutout");

        ModelFile baseRight = models().withExistingParent(name + "_base_right",
                        modLoc("block/giant_titanosarcolites_big_2"))
                .texture("0", modLoc("block/titanosarcolites_big_top_upper"))
                .texture("1", modLoc("block/titanosarcolites_big_side_upper"))
                .texture("particle", modLoc("block/titanosarcolites_big_top_upper"))
                .renderType("cutout");

        getVariantBuilder(block).forAllStates(state -> {
            TitanosarcolitesPart part = state.getValue(GiantTitanosarcolitesBlock.PART);
            Direction facing = state.getValue(GiantTitanosarcolitesBlock.FACING);

            ModelFile model = switch (part) {
                case PINCER_LEFT -> pincerLeft;
                case PINCER_RIGHT -> pincerRight;
                case BASE_LEFT -> baseLeft;
                case BASE_RIGHT -> baseRight;
            };

            int yRot = (int) facing.toYRot();

            yRot += switch (part) {
                case PINCER_LEFT, PINCER_RIGHT, BASE_RIGHT -> 180;
                case BASE_LEFT -> 90;
            };

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(yRot)
                    .build();
        });

        simpleBlockItem(block, baseLeft);
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
        return ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "block/" + path);
    }
}
