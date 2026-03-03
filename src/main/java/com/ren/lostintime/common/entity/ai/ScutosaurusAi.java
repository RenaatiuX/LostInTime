package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ren.lostintime.common.entity.creatures.Scutosaurus;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
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

import java.util.Optional;

//TODO BRETZ review it please
public class ScutosaurusAi {

    //MEMOIRS
    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_ADULT, // So that babies will look for their parents
            MemoryModuleType.HURT_BY_ENTITY, // To find out who hit him
            MemoryModuleType.BREED_TARGET
    );

    //SENSORS
    public static final ImmutableList<SensorType<? extends Sensor<? super Scutosaurus>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_ADULT,
            SensorType.HURT_BY
    );

    public static void makeBrain(Brain<Scutosaurus> pBrain) {
        initCoreActivity(pBrain);
        initIdleActivity(pBrain);
        initFightActivity(pBrain);
        initPanicActivity(pBrain);

        pBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        pBrain.setDefaultActivity(Activity.IDLE);
        pBrain.useDefaultActivity();
    }

    private static void initCoreActivity(Brain<Scutosaurus> pBrain) {
        pBrain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new Swim(0.8F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink()
        ));
    }

    private static void initIdleActivity(Brain<Scutosaurus> pBrain) {
        pBrain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                StartAttacking.create(ScutosaurusAi::findTarget),
                herdCohesion(),
                new AnimalMakeLove(EntityInit.SCUTOSAURUS.get(), 1.0F),
                BabyFollowAdult.create(UniformInt.of(5, 16), 1.1F),
                SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60)),
                BabyFollowAdult.create(UniformInt.of(5, 16), 1.1F),
                new RunOne<>(ImmutableList.of(
                        Pair.of(RandomStroll.stroll(1.0F), 1),
                        Pair.of(new DoNothing(30, 60), 2)
                ))
        ));
    }

    private static void initFightActivity(Brain<Scutosaurus> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                StopAttackingIfTargetInvalid.create(),
                SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.3F),
                MeleeAttack.create(20)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    private static void initPanicActivity(Brain<Scutosaurus> pBrain) {
        pBrain.addActivity(Activity.PANIC, 10, ImmutableList.of(
                new AnimalPanic(1.5F)
        ));
    }

    public static void updateActivity(Scutosaurus scuto) {
        Brain<Scutosaurus> brain = scuto.getBrain();
        if (scuto.getHealth() < (scuto.getMaxHealth() * 0.25F)) {
            brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
            brain.setActiveActivityIfPossible(Activity.PANIC);
        } else {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
        }
    }

    private static Optional<? extends LivingEntity> findTarget(Scutosaurus scuto) {
        //if its a baby, its peaceful and wont fight
        //leave it with the adults or run away.
        if (scuto.isBaby()) {
            return Optional.empty();
        }

        Brain<?> brain = scuto.getBrain();

        Optional<LivingEntity> hurtBy = brain.getMemory(MemoryModuleType.HURT_BY_ENTITY);
        if (hurtBy.isPresent()) {
            return hurtBy;
        }

        return brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap(entities ->
                entities.findClosest(entity -> {
                    if (!(entity instanceof Player)) {
                        return false;
                    }

                    for (Scutosaurus other : scuto.level().getEntitiesOfClass(Scutosaurus.class, scuto.getBoundingBox().inflate(12.0D))) {
                        if (other.isBaby() && other.distanceTo(entity) < 5.0D) {
                            return true;
                        }
                    }
                    return false;
                })
        );
    }

    private static OneShot<Scutosaurus> herdCohesion() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.present(MemoryModuleType.NEAREST_VISIBLE_ADULT),
                instance.absent(MemoryModuleType.ATTACK_TARGET),
                instance.registered(MemoryModuleType.WALK_TARGET)
        ).apply(instance, (nearestAdultAccessor, attackTargetAccessor, walkTargetAccessor) -> (level, entity, time) -> {
            if (entity.isBaby()) {
                return false;
            }
            LivingEntity otherAdult = instance.get(nearestAdultAccessor);
            if (entity.distanceToSqr(otherAdult) > 256.0D) {
                walkTargetAccessor.set(new WalkTarget(otherAdult, 1.0F, 5));
                return true;
            }
            return false;
        }));
    }
}
