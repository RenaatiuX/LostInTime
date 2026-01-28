package com.ren.lostintime;

import com.mojang.logging.LogUtils;
import com.ren.lostintime.common.config.Config;
import com.ren.lostintime.common.init.*;
import com.ren.lostintime.datagen.DataGatherer;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(LostInTime.MODID)
public class LostInTime {
    public static final String MODID = "lostintime";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LostInTime(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(DataGatherer::gatherData);

        ItemInit.ITEMS.register(modEventBus);
        GroupInit.GROUP.register(modEventBus);
        EntityInit.ENTITIES.register(modEventBus);
        BlockInit.BLOCKS.register(modEventBus);
        ParticlesInit.PARTICLE_TYPE.register(modEventBus);
        BlockEntityInit.BLOCK_ENTITY_TYPES.register(modEventBus);
        RecipeInit.RECIPE_SERIALIZER.register(modEventBus);
        RecipeInit.RECIPE_TYPES.register(modEventBus);
        MenuInit.MENUS.register(modEventBus);
        FeatureInit.FEATURES.register(modEventBus);
        VillagerInit.POI_TYPES.register(modEventBus);
        VillagerInit.VILLAGER_PROFESSIONS.register(modEventBus);

        ModLootConditions.LOOT_CONDITIONS.register(modEventBus);
        ModLootConditions.LOOT_POOL_ENTRIES.register(modEventBus);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == GroupInit.LOST_IN_TIME_GROUP.get()) {
            event.accept(ItemInit.AMBER);
            event.accept(ItemInit.ECTOPLASM);
            event.accept(ItemInit.SOUL_ASH);
            event.accept(ItemInit.SOUL_GRUME);
            event.accept(ItemInit.EMPTY_VITAL_PATTERN);
            event.accept(ItemInit.INFORMATION_DOME);
            event.accept(ItemInit.SOUL_POWDER);
            event.accept(ItemInit.PANEL);
            event.accept(ItemInit.REDSTONE_CHIP);

            event.accept(ItemInit.DEVONIAN_FOSSIL);
            event.accept(ItemInit.CAMBRIAN_FOSSIL);
            event.accept(ItemInit.ORDOVICIAN_FOSSIL);
            event.accept(ItemInit.SILURIAN_FOSSIL);
            event.accept(ItemInit.CARBONIFEROUS_FOSSIL);
            event.accept(ItemInit.PERMIAN_FOSSIL);
            event.accept(ItemInit.TRIASSIC_FOSSIL);
            event.accept(ItemInit.JURASSIC_FOSSIL);
            event.accept(ItemInit.CRETACEOUS_FOSSIL);
            event.accept(ItemInit.PALEOGENE_FOSSIL);
            event.accept(ItemInit.NEOGENE_FOSSIL);
            event.accept(ItemInit.QUATERNARY_FOSSIL);

            event.accept(ItemInit.BOTHRIOLEPIS_FOSSIL);
            event.accept(ItemInit.DODO_FOSSIL);
            event.accept(ItemInit.DODO_SKULL);
            event.accept(ItemInit.DODO_FOSSIL_MOUNT);
            event.accept(ItemInit.EMPTY_SKELETON_MOUNT);

            event.accept(ItemInit.RAW_DODO);
            event.accept(ItemInit.COOKED_DODO);
            event.accept(ItemInit.MANGO);
            event.accept(ItemInit.RAW_ANOMALOCARIS);
            event.accept(ItemInit.COOKED_ANOMALOCARIS);
            event.accept(ItemInit.RAW_BOTHRIOLEPIS);
            event.accept(ItemInit.COOKED_BOTHRIOLEPIS);
            event.accept(ItemInit.RAW_DAEODON);
            event.accept(ItemInit.COOKED_DAEODON);
            event.accept(ItemInit.RAW_ENDOCERAS);
            event.accept(ItemInit.COOKED_ENDOCERAS);

            event.accept(ItemInit.ASPECT_DIFFERENTIATION);
            event.accept(ItemInit.ASPECT_EMERGENCE);
            event.accept(ItemInit.ASPECT_INTEGRATION);
            event.accept(ItemInit.ASPECT_STRUCTURING);
            event.accept(ItemInit.ASPECT_TRANSIENCE);
            event.accept(ItemInit.ASPECT_ABUNDANCE);
            event.accept(ItemInit.ASPECT_CONTINUITY);
            event.accept(ItemInit.ASPECT_MAGNITUDE);
            event.accept(ItemInit.ASPECT_PROLIFERATION);
            event.accept(ItemInit.ASPECT_RECOVERY);
            event.accept(ItemInit.ASPECT_REFINEMENT);
            event.accept(ItemInit.ASPECT_RESILIENCE);

            event.accept(ItemInit.ZIRCON);

            event.accept(ItemInit.AMETHYST_CATALYST);
            event.accept(ItemInit.BLUE_ICE_CATALYST);
            event.accept(ItemInit.CALCITE_CATALYST);
            event.accept(ItemInit.COAL_CATALYST);
            event.accept(ItemInit.COPPER_CATALYST);
            event.accept(ItemInit.EMERALD_CATALYST);
            event.accept(ItemInit.GOLD_CATALYST);
            event.accept(ItemInit.IRON_CATALYST);
            event.accept(ItemInit.LAPIS_LAZULI_CATALYST);
            event.accept(ItemInit.QUARTZ_CATALYST);
            event.accept(ItemInit.REDSTONE_CATALYST);
            event.accept(ItemInit.ZIRCON_CATALYST);

            event.accept(ItemInit.ANOMALOCARIS_SOUL_CFC);
            event.accept(ItemInit.BOTHRIOLEPIS_SOUL_CFC);
            event.accept(ItemInit.DAEODON_SOUL_CFC);
            event.accept(ItemInit.DEINONYCHUS_SOUL_CFC);
            event.accept(ItemInit.DODO_SOUL_CFC);
            event.accept(ItemInit.EMPTY_SOUL_CFC);
            event.accept(ItemInit.ENDOCERAS_SOUL_CFC);
            event.accept(ItemInit.HYLONOMUS_SOUL_CFC);
            event.accept(ItemInit.LEPTICTIDIUM_SOUL_CFC);
            event.accept(ItemInit.MASTODONSAURUS_SOUL_CFC);
            event.accept(ItemInit.PLESIOSAURUS_SOUL_CFC);
            event.accept(ItemInit.PROTOTAXITES_VITAL_PATTERN);
            event.accept(ItemInit.PTERYGOTUS_SOUL_CFC);
            event.accept(ItemInit.SCUTOSAURUS_SOUL_CFC);

            event.accept(ItemInit.BARREL_SPONGE_VITAL_PATTERN);
            event.accept(ItemInit.GLASS_SPONGE_VITAL_PATTERN);
            event.accept(ItemInit.PIPE_SPONGE_VITAL_PATTERN);
            event.accept(ItemInit.TREE_SPONGE_VITAL_PATTERN);
            event.accept(ItemInit.VASE_SPONGE_VITAL_PATTERN);
            event.accept(ItemInit.WOOL_SPONGE_VITAL_PATTERN);

            event.accept(ItemInit.GOLDEN_EYE);
            event.accept(ItemInit.GUARDIAN_SPIKE);

            event.accept(ItemInit.DODO_SPAWN_EGG);

            event.accept(BlockInit.MANGO_LEAVES);
            event.accept(BlockInit.MANGO_LOG);
            event.accept(BlockInit.MANGO_SAPLING);
            event.accept(BlockInit.DODO_EGG);
            event.accept(BlockInit.DEAD_BARREL_SPONGE);
            event.accept(BlockInit.BARREL_SPONGE);
            event.accept(ItemInit.BOTHRIOLEPIS_ROE);

            event.accept(BlockInit.QUATERNARY_FOSSIL_BLOCK);
            event.accept(BlockInit.NEOGENE_FOSSIL_BLOCK);
            event.accept(BlockInit.PALEOGENE_FOSSIL_BLOCK);
            event.accept(BlockInit.CRETACEOUS_FOSSIL_BLOCK);
            event.accept(BlockInit.DEEPSLATE_JURASSIC_FOSSIL_BLOCK);
            event.accept(BlockInit.DEEPSLATE_TRIASSIC_FOSSIL_BLOCK);
            event.accept(BlockInit.DEEPSLATE_PERMIAN_FOSSIL_BLOCK);
            event.accept(BlockInit.DEEPSLATE_CARBONIFEROUS_FOSSIL_BLOCK);
            event.accept(BlockInit.DEEPSLATE_DEVONIAN_FOSSIL_BLOCK);
            event.accept(BlockInit.DEEPSLATE_SILURIAN_FOSSIL_BLOCK);
            event.accept(BlockInit.DEEPSLATE_ORDOVICIAN_FOSSIL_BLOCK);
            event.accept(BlockInit.DEEPSLATE_CAMBRIAN_FOSSIL_BLOCK);

            event.accept(BlockInit.SANDSTONE_BRICKS);
            event.accept(BlockInit.SMALL_SANDSTONE_BRICKS);

            event.accept(BlockInit.IDENTIFICATION_TABLE);
            event.accept(BlockInit.SOUL_EXTRACTOR);
            event.accept(BlockInit.SOUL_CONFIGURATOR);

            event.accept(BlockInit.DEAD_GLASS_SPONGE);
            event.accept(BlockInit.GLASS_SPONGE);

            event.accept(BlockInit.DEAD_LARGE_PIPE_SPONGE);
            event.accept(BlockInit.LARGE_PIPE_SPONGE);
        }
    }
}
