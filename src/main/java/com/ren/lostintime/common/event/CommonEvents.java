package com.ren.lostintime.common.event;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.LITWaterAnimal;
import com.ren.lostintime.common.entity.creatures.*;
import com.ren.lostintime.common.entity.projectile.LITThrownEgg;
import com.ren.lostintime.common.entity.util.PlayerDiscoveredPrehistoric;
import com.ren.lostintime.common.entity.util.PlayerDiscoveredPrehistoricImpl;
import com.ren.lostintime.common.init.CapabilityInit;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.ItemInit;
import com.ren.lostintime.common.item.LITEggItem;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        event.put(EntityInit.DAEODON.get(), Daeodon.createAttributes().build());
        event.put(EntityInit.LEPTICTIDIUM.get(), Leptictidium.createAttributes().build());
        event.put(EntityInit.SCUTOSAURUS.get(), Scutosaurus.createAttributes().build());
        event.put(EntityInit.PLESIOSAURUS.get(), Plesiosaurus.createAttributes().build());
        event.put(EntityInit.MASTODONSAURUS.get(), Mastodonsaurus.createAttributes().build());
        event.put(EntityInit.HELICOPRION.get(), Helicoprion.createAttributes().build());
        event.put(EntityInit.DEINONYCHUS.get(), Deinonychus.createAttributes().build());
        event.put(EntityInit.PTERYGOTUS.get(),  Pterygotus.createAttributes().build());
        event.put(EntityInit.KALLIGRAMMATIDAE.get(), Kalligrammatidae.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnRules(SpawnPlacementRegisterEvent event){
        event.register(EntityInit.DODO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Dodo::checkLITAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(EntityInit.ANOMALOCARIS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LITWaterAnimal::checkWaterLITSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(EntityInit.ENDOCERAS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LITWaterAnimal::checkWaterLITSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(EntityInit.HYLONOMUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Hylonomus::checkHylonomusSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(EntityInit.DAEODON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Daeodon::checkLITAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
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
