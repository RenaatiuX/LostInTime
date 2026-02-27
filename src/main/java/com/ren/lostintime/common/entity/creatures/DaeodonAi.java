package com.ren.lostintime.common.entity.creatures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ren.lostintime.common.init.MemoryModuleInit;
import com.ren.lostintime.common.init.SensorTypeInit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.List;

public class DaeodonAi {
    public static final List<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,

            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,

            MemoryModuleInit.IS_SLEEPING.get(),
            MemoryModuleInit.IS_SITTING.get(),
            MemoryModuleInit.SIT_ORDER.get(),
            MemoryModuleInit.WANDER_ORDER.get(),
            MemoryModuleInit.FOLLOW_ORDER.get()
    );

    public static final List<? extends SensorType<? extends Sensor<? super Daeodon>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.HURT_BY,
            SensorType.NEAREST_ADULT,
            SensorTypeInit.SLEEP_SENSOR.get()
    );

    public static Brain<Daeodon> initBrain(Daeodon daeodon, Brain<Daeodon> brain){
        brain.addActivity(Activity.CORE, ImmutableList.of(
                Pair.of(1, new MoveToTargetSink()),
                Pair.of(2, new LookAtTargetSink(45, 90))
        ));

        initIdleActivity(brain);
        initFightActivity(brain);




        return brain;
    }

    public static void initIdleActivity(Brain<Daeodon> brain) {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(0, StartAttacking.create(Daeodon::findNearestValidAttackTarget)),
                Pair.of(1, createIdleMovementBehaviors()),
                Pair.of(2, createIdleLookBehaviors())
        ));
    }

    public static void initFightActivity(Brain<Daeodon> brain){
        brain.addActivityAndRemoveMemoriesWhenStopped(Activity.FIGHT,
                ImmutableList.of(
                        Pair.of(0, SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.2F)),
                        Pair.of(1, StopAttackingIfTargetInvalid.create()),
                        Pair.of(2, MeleeAttack.create(20))),
                ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)),
                ImmutableSet.of(MemoryModuleType.ATTACK_TARGET)
        );

    }

    private static RunOne<Daeodon> createIdleLookBehaviors() {
        return new RunOne<>(ImmutableList.of(
                Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0F), 1),
                Pair.of(SetEntityLookTarget.create(EntityType.PIG, 8.0F), 1),
                Pair.of(new DoNothing(30, 60), 1)
        ));
    }

    private static RunOne<Daeodon> createIdleMovementBehaviors() {
        return new RunOne<>(ImmutableList.of(
                Pair.of(RandomStroll.stroll(0.6F), 2),
                Pair.of(new DoNothing(30, 60), 1)
        ));
    }



}
