package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.block.*;
import com.ren.lostintime.common.worldgen.fossil.FossilEra;
import com.ren.lostintime.common.worldgen.tree.trees.MangoTreeGrower;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockInit {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LostInTime.MODID);

    //Machines
    public static final RegistryObject<Block> IDENTIFICATION_TABLE = registerBlock("identification_table",
            () -> new IdentificationTableBlock(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE)));
    public static final RegistryObject<Block> SOUL_EXTRACTOR = registerBlock("soul_extractor",
            () -> new SoulExtractorBlock(BlockBehaviour.Properties.copy(Blocks.SMITHING_TABLE).noOcclusion()
                    .lightLevel(state -> state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) ==
                            DoubleBlockHalf.UPPER && state.getValue(SoulExtractorBlock.ON) ? 10 : 0)));
    public static final RegistryObject<Block> SOUL_CONFIGURATOR = registerBlock("soul_configurator",
            () -> new SoulConfiguratorBlock(BlockBehaviour.Properties.copy(Blocks.SMITHING_TABLE).noOcclusion().lightLevel(s -> s.getValue(SoulConfiguratorBlock.PART) == SoulConfiguratorBlock.Part.TOP && s.getValue(LITMachineBlock.ON) ? 10 : 0)));

    //SAPLINGS
    public static final RegistryObject<Block> MANGO_SAPLING = registerBlock("mango_sapling",
            () -> new SaplingBlock(new MangoTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));

    //LEAVES
    public static final RegistryObject<Block> MANGO_LEAVES = registerBlock("mango_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES)));
    public static final RegistryObject<Block> MANGO_FRUIT_LEAVES = registerBlock("mango_fruit_leaves",
            () -> new MangoFruitBlock(BlockBehaviour.Properties.copy(MANGO_LEAVES.get())));

    //LOGS
    public static final RegistryObject<Block> MANGO_LOG = registerBlock("mango_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG).strength(3F)));

    //EGGS
    public static final RegistryObject<Block> DODO_EGG = registerBlock("dodo_egg",
            () -> new SingleEggBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG).randomTicks(),
                    EntityInit.DODO, BlockTags.DIRT));
    public static final RegistryObject<Block> BOTHRIOLEPIS_ROE = BLOCKS.register("bothriolepis_roe",
            () -> new LITRoeBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG), EntityInit.DODO,
                    1, 3, 8000, 24000));
    public static final RegistryObject<Block> ANOMALOCARIS_EGG = BLOCKS.register("anomalocaris_egg",
            () -> new LITRoeBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG), EntityInit.DODO,
                    1, 2, 4000, 12000));

    //ORES
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
