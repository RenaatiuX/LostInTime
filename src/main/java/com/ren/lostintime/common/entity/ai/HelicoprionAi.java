package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ren.lostintime.common.entity.creatures.Helicoprion;
import com.ren.lostintime.common.init.MobEffectInit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Optional;

public class HelicoprionAi {

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES // Essential for spotting prey
    );

    public static final ImmutableList<SensorType<? extends Sensor<? super Helicoprion>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_LIVING_ENTITIES
    );

    public static Brain<?> makeBrain(Brain<Helicoprion> pBrain) {
        initCoreActivity(pBrain);
        initIdleActivity(pBrain);
        initFightActivity(pBrain);
        pBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        pBrain.setDefaultActivity(Activity.IDLE);
        pBrain.useDefaultActivity();
        return pBrain;
    }

    private static void initCoreActivity(Brain<Helicoprion> pBrain) {
        pBrain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new Swim(0.8F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink()
        ));
    }

    private static void initIdleActivity(Brain<Helicoprion> pBrain) {
        pBrain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                StartAttacking.create(HelicoprionAi::findTarget),
                RandomStroll.swim(1.0F)
        ));
    }

    private static void initFightActivity(Brain<Helicoprion> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                StopAttackingIfTargetInvalid.create(
                        (target) -> !target.isInWater() || !target.isAlive()
                ),
                SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.5F),
                MeleeAttack.create(20)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    public static void updateActivity(Helicoprion pHelicoprion) {
        Brain<Helicoprion> brain = pHelicoprion.getBrain();
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }

    private static Optional<? extends LivingEntity> findTarget(Helicoprion pHelicoprion) {
        Brain<?> brain = pHelicoprion.getBrain();
        return brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                .flatMap(entities -> entities.findClosest(entity -> {
                    if (entity instanceof Helicoprion) {
                        return false;
                    }
                    if (!entity.isInWater()) {
                        return false;
                    }
                    if (entity.hasEffect(MobEffectInit.BLEEDING.get())) {
                        return true;
                    }
                    if (entity.getHealth() <= entity.getMaxHealth() / 2.0F) {
                        return true;
                    }
                    if (pHelicoprion.distanceTo(entity) <= 8.0D) {
                        return true;
                    }
                    return false;
                }));
    }

}
