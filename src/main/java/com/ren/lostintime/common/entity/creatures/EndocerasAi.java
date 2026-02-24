package com.ren.lostintime.common.entity.creatures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Optional;

public class EndocerasAi {

    public static void initBrain(Brain<Endoceras> brain) {
        brain.addActivity(Activity.CORE, ImmutableList.of(
                Pair.of(0, new LookAtTargetSink(45, 90)),
                Pair.of(0, new MoveToTargetSink()),
                Pair.of(1, new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS))
        ));

        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(0, StartAttacking.create(endoceras ->  findNearestValidAttackTarget(brain, endoceras))),
                Pair.of(1, RandomStroll.swim(1.0f))
                ));

        brain.addActivity(Activity.FIGHT, ImmutableList.of(
                Pair.of(0, StopAttackingIfTargetInvalid.create()),
                Pair.of(0, SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.2F)),
                Pair.of(1, MeleeAttack.create(20))
        ));

        brain.addActivity(Activity.AVOID, ImmutableList.of(
                Pair.of(0, SetWalkTargetAwayFrom.entity(MemoryModuleType.NEAREST_VISIBLE_PLAYER, 1.5F, 6, true))
        ));

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
    }


    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Brain<Endoceras> brain, Endoceras endoceras) {
        return brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty()).findClosest(endoceras::isTargetable);
    }
}
