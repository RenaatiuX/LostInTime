package com.ren.lostintime.common.event;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.renderer.TransfiguratorBERenderer;
import com.ren.lostintime.common.entity.LITWaterAnimal;
import com.ren.lostintime.common.entity.creatures.*;
import com.ren.lostintime.common.entity.projectile.LITThrownEgg;
import com.ren.lostintime.common.init.BlockEntityInit;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.ItemInit;
import com.ren.lostintime.common.item.LITEggItem;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = LostInTime.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonEvents {

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            dispenser();
        });
    }
    @SubscribeEvent
    public static void registerAttr(EntityAttributeCreationEvent event) {
        event.put(EntityInit.DODO.get(), Dodo.createAttributes().build());
        event.put(EntityInit.ANOMALOCARIS.get(), Anomalocaris.createAttributes().build());
        event.put(EntityInit.BOTHRIOLEPIS.get(), Bothriolepis.createAttributes().build());
        event.put(EntityInit.HYLONOMUS.get(), Hylonomus.createAttributes().build());
        event.put(EntityInit.ENDOCERAS.get(), Endoceras.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnRules(SpawnPlacementRegisterEvent event){
        event.register(EntityInit.DODO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Dodo::checkLITAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(EntityInit.ANOMALOCARIS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LITWaterAnimal::checkWaterLITSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(EntityInit.ENDOCERAS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LITWaterAnimal::checkWaterLITSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    private static void dispenser() {
        DispenseItemBehavior eggBehavior = new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level level, Position pos, ItemStack stack) {
                if (stack.getItem() instanceof LITEggItem eggItem) {
                    LITThrownEgg entity = new LITThrownEgg(level, pos.x(), pos.y(), pos.z());
                    entity.setEntityTypeToSpawn(eggItem.getEntityType());
                    entity.setItem(stack);
                    return entity;
                }
                return null;
            }
        };
        DispenserBlock.registerBehavior(ItemInit.HYLONOMUS_EGG.get(), eggBehavior);
    }
}
