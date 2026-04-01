package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.block.*;
import com.ren.lostintime.common.util.LITWoodTypes;
import com.ren.lostintime.common.worldgen.LITConfiguredFeatures;
import com.ren.lostintime.common.worldgen.fossil.FossilEra;
import com.ren.lostintime.common.worldgen.tree.trees.AraucarioxylonTreeGrower;
import com.ren.lostintime.common.worldgen.tree.trees.MangoTreeGrower;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockInit {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LostInTime.MODID);

    //Machines
    public static final RegistryObject<Block> IDENTIFICATION_TABLE = registerBlock("identification_table",
            () -> new IdentificationTableBlock(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SOUL_EXTRACTOR = registerBlock("soul_extractor",
            () -> new SoulExtractorBlock(BlockBehaviour.Properties.copy(Blocks.SMITHING_TABLE).requiresCorrectToolForDrops().noOcclusion()
                    .lightLevel(state -> state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) ==
                            DoubleBlockHalf.UPPER && state.getValue(SoulExtractorBlock.ON) ? 10 : 0)));
    public static final RegistryObject<Block> SOUL_CONFIGURATOR = registerBlock("soul_configurator",
            () -> new SoulConfiguratorBlock(BlockBehaviour.Properties.copy(Blocks.SMITHING_TABLE).requiresCorrectToolForDrops().noOcclusion().lightLevel(s -> s.getValue(SoulConfiguratorBlock.PART) == SoulConfiguratorBlock.Part.TOP && s.getValue(LITMachineBlock.ON) ? 10 : 0)));
    public static final RegistryObject<Block> TRANSFIGURATOR = registerBlock("transfigurator",
            () -> new TransfiguratorBlock(BlockBehaviour.Properties.copy(Blocks.SMITHING_TABLE).requiresCorrectToolForDrops().noOcclusion()
                    .lightLevel(state -> state.getValue(TransfiguratorBlock.HALF) == DoubleBlockHalf.UPPER && state.getValue(TransfiguratorBlock.ON) ? 10 : 0)));

    //Environment
    public static final RegistryObject<Block> DEAD_BARREL_SPONGE = registerBlock("dead_barrel_sponge",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEAD_TUBE_CORAL_BLOCK)));
    public static final RegistryObject<Block> BARREL_SPONGE = registerBlock("barrel_sponge",
            () -> new LITSpongeBlock(DEAD_BARREL_SPONGE, true, BlockBehaviour.Properties.copy(Blocks.TUBE_CORAL_BLOCK)));
    public static final RegistryObject<Block> DEAD_GLASS_SPONGE = registerBlock("dead_glass_sponge",
            () -> new BaseCoralPlantBlock(BlockBehaviour.Properties.copy(Blocks.DEAD_TUBE_CORAL)));
    public static final RegistryObject<Block> GLASS_SPONGE = registerBlock("glass_sponge",
            () -> new CoralPlantBlock(DEAD_GLASS_SPONGE.get(), BlockBehaviour.Properties.copy(Blocks.TUBE_CORAL)));
    public static final RegistryObject<Block> DEAD_PIPE_SPONGE = registerBlock("dead_pipe_sponge",
            () -> new BaseCoralPlantBlock(BlockBehaviour.Properties.copy(Blocks.DEAD_TUBE_CORAL)));
    public static final RegistryObject<Block> PIPE_SPONGE = registerBlock("pipe_sponge",
            () -> new CoralPlantBlock(DEAD_PIPE_SPONGE.get(), BlockBehaviour.Properties.copy(Blocks.TUBE_CORAL)));
    public static final RegistryObject<Block> DEAD_TREE_SPONGE = registerBlock("dead_tree_sponge",
            () -> new BaseCoralPlantBlock(BlockBehaviour.Properties.copy(Blocks.DEAD_TUBE_CORAL)));
    public static final RegistryObject<Block> TREE_SPONGE = registerBlock("tree_sponge",
            () -> new CoralPlantBlock(DEAD_TREE_SPONGE.get(), BlockBehaviour.Properties.copy(Blocks.TUBE_CORAL)));
    public static final RegistryObject<Block> DEAD_VASE_SPONGE = registerBlock("dead_vase_sponge",
            () -> new BaseCoralPlantBlock(BlockBehaviour.Properties.copy(Blocks.DEAD_TUBE_CORAL)));
    public static final RegistryObject<Block> VASE_SPONGE = registerBlock("vase_sponge",
            () -> new CoralPlantBlock(DEAD_VASE_SPONGE.get(), BlockBehaviour.Properties.copy(Blocks.TUBE_CORAL)));
    public static final RegistryObject<Block> DEAD_WOOL_SPONGE = registerBlock("dead_wool_sponge",
            () -> new BaseCoralPlantBlock(BlockBehaviour.Properties.copy(Blocks.DEAD_TUBE_CORAL)));
    public static final RegistryObject<Block> WOOL_SPONGE = registerBlock("wool_sponge",
            () -> new CoralPlantBlock(DEAD_WOOL_SPONGE.get(), BlockBehaviour.Properties.copy(Blocks.TUBE_CORAL)));
    public static final RegistryObject<Block> DEAD_LARGE_PIPE_SPONGE = registerBlock("dead_large_pipe_sponge",
            () -> new LITDeadTallSpongeBlock(BlockBehaviour.Properties.copy(Blocks.DEAD_TUBE_CORAL)));
    public static final RegistryObject<Block> LARGE_PIPE_SPONGE = registerBlock("large_pipe_sponge",
            () -> new LITTallSpongeBlock(DEAD_LARGE_PIPE_SPONGE, BlockBehaviour.Properties.copy(Blocks.TUBE_CORAL)));
    public static final RegistryObject<Block> CLADOPHLEBIS = registerBlock("cladophlebis",
            () -> new LITFernBlock(BlockBehaviour.Properties.copy(Blocks.FERN)));
    public static final RegistryObject<Block> CONIOPTERIS = registerBlock("coniopteris",
            () -> new LITFernBlock(BlockBehaviour.Properties.copy(Blocks.FERN)));
    public static final RegistryObject<Block> COOKSONIA = registerBlock("cooksonia",
            () -> new CooksoniaBlock(BlockBehaviour.Properties.copy(Blocks.FERN)));
    public static final RegistryObject<Block> TITANOSARCOLITES = registerBlock("titanosarcolites",
            () -> new TitanosarcolitesBlock(BlockBehaviour.Properties.copy(Blocks.TUBE_CORAL_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> GIANT_TITANOSARCOLITES = registerBlock("giant_titanosarcolites",
            () -> new GiantTitanosarcolitesBlock(BlockBehaviour.Properties.copy(Blocks.TUBE_CORAL_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> RED_ALGAE = registerBlock("red_algae",
            () -> new RedAlgaeBlock(BlockBehaviour.Properties.copy(Blocks.SEAGRASS)));

    //MUSHROOMS
    public static final RegistryObject<Block> GONDWANAGARICITES_BLOCK = registerBlock("gondwanagaricites_block",
            () -> new HugeMushroomBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM_BLOCK)));

    public static final RegistryObject<Block> GONDWANAGARICITES_STEM = registerBlock("gondwanagaricites_stem",
            () -> new HugeMushroomBlock(BlockBehaviour.Properties.copy(Blocks.MUSHROOM_STEM)));

    public static final RegistryObject<Block> GONDWANAGARICITES = registerBlock("gondwanagaricites",
            () -> new MushroomBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM), LITConfiguredFeatures.GONDWANAGARICITES_KEY));

    //SAPLINGS
    public static final RegistryObject<Block> MANGO_SAPLING = registerBlock("mango_sapling",
            () -> new SaplingBlock(new MangoTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));
    public static final RegistryObject<Block> ARAUCARIOXYLON_SAPLING = registerBlock("araucarioxylon_sapling",
            () -> new SaplingBlock(new AraucarioxylonTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));

    //LEAVES
    public static final RegistryObject<Block> MANGO_LEAVES = registerBlock("mango_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
    public static final RegistryObject<Block> MANGO_FRUIT_LEAVES = registerBlock("mango_fruit_leaves",
            () -> new LITFruitBlock(BlockBehaviour.Properties.copy(MANGO_LEAVES.get())));
    public static final RegistryObject<Block> ARAUCARIOXYLON_LEAVES = registerBlock("araucarioxylon_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));

    //LOGS
    public static final RegistryObject<Block> MANGO_LOG = registerBlock("mango_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3F)));
    public static final RegistryObject<Block> ARAUCARIOXYLON_LOG = registerBlock("araucarioxylon_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3F)));

    //STRIPPED LOGS
    public static final RegistryObject<Block> STRIPPED_ARAUCARIOXYLON_LOG = registerBlock("stripped_araucarioxylon_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3F)));

    //STRIPPED WOODS
    public static final RegistryObject<Block> STRIPPED_ARAUCARIOXYLON_WOOD = registerBlock("stripped_araucarioxylon_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3F)));

    //WOOD
    public static final RegistryObject<Block> ARAUCARIOXYLON_WOOD = registerBlock("araucarioxylon_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));

    //PLANKS
    public static final RegistryObject<Block> ARAUCARIOXYLON_PLANKS = registerBlock("araucarioxylon_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

    //DOOR
    public static final RegistryObject<Block> ARAUCARIOXYLON_DOOR = registerBlock("araucarioxylon_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), BlockSetType.OAK));

    //STAIRS
    public static final RegistryObject<Block> ARAUCARIOXYLON_STAIRS = registerBlock("araucarioxylon_stairs",
            () -> new StairBlock(() -> ARAUCARIOXYLON_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

    //SLABS
    public static final RegistryObject<Block> ARAUCARIOXYLON_SLAB = registerBlock("araucarioxylon_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

    //BUTTONS
    public static final RegistryObject<Block> ARAUCARIOXYLON_BUTTON = registerBlock("araucarioxylon_button",
            () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON), BlockSetType.OAK, 30, true));

    //PRESSURE PLATE
    public static final RegistryObject<Block> ARAUCARIOXYLON_PRESSURE_PLATE = registerBlock("araucarioxylon_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS),
                    BlockSetType.OAK));

    //FENCES
    public static final RegistryObject<Block> ARAUCARIOXYLON_FENCE = registerBlock("araucarioxylon_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> ARAUCARIOXYLON_FENCE_GATE = registerBlock("araucarioxylon_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), WoodType.OAK));

    //WALLS
    public static final RegistryObject<Block> ARAUCARIOXYLON_WALL = registerBlock("araucarioxylon_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

    //TRAPDOORS
    public static final RegistryObject<Block> ARAUCARIOXYLON_TRAPDOOR = registerBlock("araucarioxylon_trapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), BlockSetType.OAK));

    //SIGNS
    public static final RegistryObject<Block> ARAUCARIOXYLON_SIGN = BLOCKS.register("araucarioxylon_sign",
            () -> new LITStandingSignBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_SIGN), LITWoodTypes.ARAUCARIOXYLON));
    public static final RegistryObject<Block> ARAUCARIOXYLON_WALL_SIGN = BLOCKS.register("araucarioxylon_wall_sign",
            () -> new LITWallSignBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_WALL_SIGN), LITWoodTypes.ARAUCARIOXYLON));
    public static final RegistryObject<Block> ARAUCARIOXYLON_HANGING_SIGN = BLOCKS.register("araucarioxylon_hanging_sign",
            () -> new LITHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_HANGING_SIGN), LITWoodTypes.ARAUCARIOXYLON));
    public static final RegistryObject<Block> ARAUCARIOXYLON_WALL_HANGING_SIGN = BLOCKS.register("araucarioxylon_wall_hanging_sign",
            () -> new LITWallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_WALL_HANGING_SIGN), LITWoodTypes.ARAUCARIOXYLON));

    //EGGS
    public static final RegistryObject<Block> DODO_EGG = registerBlock("dodo_egg",
            () -> new LITEggBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG).randomTicks(),
                    EntityInit.DODO, BlockTags.DIRT, 1));
    public static final RegistryObject<Block> SCUTOSAURUS_EGG = registerBlock("scutosaurus_egg",
            () -> new LITEggBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG).randomTicks(),
                    EntityInit.SCUTOSAURUS, BlockTags.DIRT, 2));
    public static final RegistryObject<Block> DEINONYCHUS_EGG = registerBlock("deinonychus_egg",
            () -> new LITEggBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG).randomTicks(),
                    EntityInit.DEINONYCHUS, BlockTags.DIRT, 3));
    public static final RegistryObject<Block> ENDOCERAS_EGG = BLOCKS.register("endoceras_eggs",
            () -> new LITRoeBlock(BlockBehaviour.Properties.copy(Blocks.FROGSPAWN), EntityInit.ENDOCERAS,
                    ItemInit.ENDOCERAS_EGGS_BUCKET, 1, 2, 10000, 32000, true));
    public static final RegistryObject<Block> BOTHRIOLEPIS_ROE = BLOCKS.register("bothriolepis_roe",
            () -> new LITRoeBlock(BlockBehaviour.Properties.copy(Blocks.FROGSPAWN), EntityInit.BOTHRIOLEPIS,
                    ItemInit.BOTHRIOLEPIS_ROE_BUCKET, 1, 3, 8000, 24000));
    public static final RegistryObject<Block> ANOMALOCARIS_ROE = BLOCKS.register("anomalocaris_roe",
            () -> new LITRoeBlock(BlockBehaviour.Properties.copy(Blocks.FROGSPAWN), EntityInit.ANOMALOCARIS,
                    null, 1, 2, 4000, 12000));
    public static final RegistryObject<Block> MASTODONSAURUS_EGG = BLOCKS.register("mastodonsaurus_egg",
            () -> new LITRoeBlock(BlockBehaviour.Properties.copy(Blocks.FROGSPAWN), EntityInit.MASTODONSAURUS,
                    null, 1, 1, 10000, 32000));

    //ORES
    public static final RegistryObject<Block> SPINEL_ORE = registerBlock("spinel_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                    .strength(3.0F, 3.0F), UniformInt.of(2, 4)));
    public static final RegistryObject<Block> ZIRCON_ORE = registerBlock("zircon_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                    .strength(3.0F, 3.0F), UniformInt.of(2, 4)));
    public static final RegistryObject<Block> QUATERNARY_FOSSIL_BLOCK = registerBlock("quaternary_fossil_block",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                    .strength(3.0F, 3.0F), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> NEOGENE_FOSSIL_BLOCK = registerBlock("neogene_fossil_block",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                    .strength(3.0F, 3.0F), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> PALEOGENE_FOSSIL_BLOCK = registerBlock("paleogene_fossil_block",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                    .strength(3.0F, 3.0F), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> CRETACEOUS_FOSSIL_BLOCK = registerBlock("cretaceous_fossil_block",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                    .strength(3.0F, 3.0F), UniformInt.of(1, 3)));

    public static final RegistryObject<Block> DEEPSLATE_SPINEL_ORE = registerBlock("deepslate_spinel_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(SPINEL_ORE.get()).mapColor(MapColor.DEEPSLATE)
                    .strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> DEEPSLATE_ZIRCON_ORE = registerBlock("deepslate_zircon_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(ZIRCON_ORE.get()).mapColor(MapColor.DEEPSLATE)
                    .strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> DEEPSLATE_JURASSIC_FOSSIL_BLOCK = registerBlock("deepslate_jurassic_fossil_block",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(QUATERNARY_FOSSIL_BLOCK.get()).mapColor(MapColor.DEEPSLATE)
                    .strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> DEEPSLATE_TRIASSIC_FOSSIL_BLOCK = registerBlock("deepslate_triassic_fossil_block",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(QUATERNARY_FOSSIL_BLOCK.get()).mapColor(MapColor.DEEPSLATE)
                    .strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> DEEPSLATE_PERMIAN_FOSSIL_BLOCK = registerBlock("deepslate_permian_fossil_block",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(QUATERNARY_FOSSIL_BLOCK.get()).mapColor(MapColor.DEEPSLATE)
                    .strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> DEEPSLATE_CARBONIFEROUS_FOSSIL_BLOCK = registerBlock("deepslate_carboniferous_fossil_block",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(QUATERNARY_FOSSIL_BLOCK.get()).mapColor(MapColor.DEEPSLATE)
                    .strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> DEEPSLATE_DEVONIAN_FOSSIL_BLOCK = registerBlock("deepslate_devonian_fossil_block",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(QUATERNARY_FOSSIL_BLOCK.get()).mapColor(MapColor.DEEPSLATE)
                    .strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> DEEPSLATE_SILURIAN_FOSSIL_BLOCK = registerBlock("deepslate_silurian_fossil_block",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(QUATERNARY_FOSSIL_BLOCK.get()).mapColor(MapColor.DEEPSLATE)
                    .strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> DEEPSLATE_ORDOVICIAN_FOSSIL_BLOCK = registerBlock("deepslate_ordovician_fossil_block",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(QUATERNARY_FOSSIL_BLOCK.get()).mapColor(MapColor.DEEPSLATE)
                    .strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(1, 3)));
    public static final RegistryObject<Block> DEEPSLATE_CAMBRIAN_FOSSIL_BLOCK = registerBlock("deepslate_cambrian_fossil_block",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(QUATERNARY_FOSSIL_BLOCK.get()).mapColor(MapColor.DEEPSLATE)
                    .strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(1, 3)));

    //MISC
    public static final RegistryObject<Block> SANDSTONE_BRICKS = registerBlock("sandstone_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SANDSTONE)));
    public static final RegistryObject<Block> SMALL_SANDSTONE_BRICKS = registerBlock("small_sandstone_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SANDSTONE)));
    public static final RegistryObject<Block> AMBER_BLOCK = registerBlock("amber_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.5F, 3.0F)
                    .sound(SoundType.GLASS).requiresCorrectToolForDrops().noOcclusion()));
    public static final RegistryObject<Block> SPINEL_BLOCK = registerBlock("spinel_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(5.0F, 6.0F)
                    .sound(SoundType.AMETHYST).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ZIRCON_BLOCK = registerBlock("zircon_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(5.0F, 1200.0F)
                    .sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> registeredBlock = BLOCKS.register(name, block);
        registerBlockItem(name, registeredBlock);
        return registeredBlock;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, Supplier<T> block) {
        return ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static Block getFossilBlock(FossilEra era) {
        return switch (era) {
            case CRETACEOUS -> CRETACEOUS_FOSSIL_BLOCK.get();
            case PALEOGENE -> PALEOGENE_FOSSIL_BLOCK.get();
            case NEOGENE -> NEOGENE_FOSSIL_BLOCK.get();
            case QUATERNARY -> QUATERNARY_FOSSIL_BLOCK.get();
            default -> throw new IllegalArgumentException("This era does not generate in stone: " + era);
        };
    }

    public static Block getDeepslateFossilBlock(FossilEra era) {
        return switch (era) {
            case CAMBRIAN -> DEEPSLATE_CAMBRIAN_FOSSIL_BLOCK.get();
            case ORDOVICIAN -> DEEPSLATE_ORDOVICIAN_FOSSIL_BLOCK.get();
            case SILURIAN -> DEEPSLATE_SILURIAN_FOSSIL_BLOCK.get();
            case DEVONIAN -> DEEPSLATE_DEVONIAN_FOSSIL_BLOCK.get();
            case CARBONIFEROUS -> DEEPSLATE_CARBONIFEROUS_FOSSIL_BLOCK.get();
            case PERMIAN -> DEEPSLATE_PERMIAN_FOSSIL_BLOCK.get();
            case TRIASSIC -> DEEPSLATE_TRIASSIC_FOSSIL_BLOCK.get();
            case JURASSIC -> DEEPSLATE_JURASSIC_FOSSIL_BLOCK.get();
            default -> throw new IllegalArgumentException("This era does not generate in deepslate: " + era);
        };
    }


}
