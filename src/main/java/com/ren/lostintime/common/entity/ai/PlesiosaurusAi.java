package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ren.lostintime.common.entity.creatures.Plesiosaurus;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class PlesiosaurusAi {

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.HURT_BY_ENTITY
    );

    //SENSORS
    public static final ImmutableList<SensorType<? extends Sensor<? super Plesiosaurus>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.HURT_BY
    );

    public static void makeBrain(Brain<Plesiosaurus> pBrain) {
        initCoreActivity(pBrain);
        initIdleActivity(pBrain);
        initFightActivity(pBrain);

        pBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        pBrain.setDefaultActivity(Activity.IDLE);
        pBrain.useDefaultActivity();
    }

    private static void initCoreActivity(Brain<Plesiosaurus> pBrain) {
        pBrain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink()
        ));
    }

    private static void initIdleActivity(Brain<Plesiosaurus> pBrain) {
        pBrain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                StartAttacking.create(PlesiosaurusAi::findTarget),
                seekAir(),
                stealFood(),
                new AnimalMakeLove(EntityInit.PLESIOSAURUS.get(), 1.0F),
                new RunOne<>(ImmutableList.of(
                        Pair.of(RandomStroll.swim(0.8F), 1),
                        Pair.of(new DoNothing(30, 60), 2)
                ))
        ));
    }

    public static void updateActivity(Plesiosaurus plesio) {
        Brain<Plesiosaurus> brain = plesio.getBrain();
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }

    private static void initFightActivity(Brain<Plesiosaurus> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                StopAttackingIfTargetInvalid.create(),
                SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.2F),
                MeleeAttack.create(20)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    private static Optional<? extends LivingEntity> findTarget(Plesiosaurus plesio) {
        Brain<?> brain = plesio.getBrain();

        Optional<LivingEntity> hurtBy = brain.getMemory(MemoryModuleType.HURT_BY_ENTITY);
        if (hurtBy.isPresent()) {
            return hurtBy;
        }

        UUID ownerUUID = plesio.bodyguardOwner;
        if (ownerUUID != null && plesio.level() instanceof ServerLevel serverLevel) {
            Entity entityOwner = serverLevel.getEntity(ownerUUID);

            if (entityOwner instanceof LivingEntity owner) {

                LivingEntity attacker = owner.getLastHurtByMob();
                if (attacker != null && attacker.isAlive() && attacker != plesio) {
                    return Optional.of(attacker);
                }

                LivingEntity targetsTarget = owner.getLastHurtMob();
                if (targetsTarget != null && targetsTarget.isAlive() && targetsTarget != plesio) {
                    return Optional.of(targetsTarget);
                }
            }
        }

        return Optional.empty();
    }

    private static OneShot<Plesiosaurus> seekAir() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET)
        ).apply(instance, (walkTargetAccessor) -> (level, entity, time) -> {

            if (entity.getAirSupply() < 1200 && entity.isInWater()) {
                BlockPos.MutableBlockPos surface = entity.blockPosition().mutable();
                while (level.getFluidState(surface).is(FluidTags.WATER) && surface.getY() < level.getMaxBuildHeight()) {
                    surface.move(0, 1, 0);
                }

                walkTargetAccessor.set(new WalkTarget(surface, 1.2F, 0));
                return true;
            }
            return false;
        }));
    }

    private static OneShot<Plesiosaurus> stealFood() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.present(MemoryModuleType.NEAREST_PLAYERS)
        ).apply(instance, (playersAccessor) -> (level, entity, time) -> {

            if (entity.stealCooldown > 0) return false;

            for (Player player : instance.get(playersAccessor)) {
                if (entity.distanceToSqr(player) <= 9.0D) {

                    ItemStack mainHand = player.getMainHandItem();
                    ItemStack offHand = player.getOffhandItem();
                    ItemStack foodStack = ItemStack.EMPTY;

                    if (mainHand.isEdible()) foodStack = mainHand;
                    else if (offHand.isEdible()) foodStack = offHand;

                    if (!foodStack.isEmpty()) {
                        if (player.getDeltaMovement().lengthSqr() < 0.001D) {
                            if (entity.hasLineOfSight(player)) {
                                foodStack.shrink(1);
                                entity.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                                entity.swing(InteractionHand.MAIN_HAND);
                                entity.stealCooldown = 600;
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }));
    }
}
