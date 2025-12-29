package com.ren.lostintime;

import com.mojang.logging.LogUtils;
import com.ren.lostintime.common.config.Config;
import com.ren.lostintime.common.init.BlockInit;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.GroupInit;
import com.ren.lostintime.common.init.ItemInit;
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
    private static final Logger LOGGER = LogUtils.getLogger();

    public LostInTime(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(DataGatherer::gatherData);

        ItemInit.ITEMS.register(modEventBus);
        GroupInit.GROUP.register(modEventBus);
        EntityInit.ENTITIES.register(modEventBus);
        BlockInit.BLOCKS.register(modEventBus);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == GroupInit.LOST_IN_TIME_GROUP.get()) {
            event.accept(ItemInit.AMBER);
            event.accept(ItemInit.DEVONIAN_FOSSIL);
            event.accept(ItemInit.BOTHRIOLEPIS_FOSSIL);
            event.accept(ItemInit.QUATERNARY_FOSSIL);
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
            event.accept(ItemInit.ZIRCON);

            event.accept(ItemInit.GOLDEN_EYE);
            event.accept(ItemInit.GUARDIAN_SPIKE);

            event.accept(ItemInit.DODO_SPAWN_EGG);

            event.accept(BlockInit.MANGO_LEAVES);
            event.accept(BlockInit.MANGO_LOG);
            event.accept(BlockInit.MANGO_SAPLING);
            event.accept(BlockInit.DODO_EGG);
        }
    }
}
