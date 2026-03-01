package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ren.lostintime.common.entity.creatures.Endoceras;
import com.ren.lostintime.common.init.ActivitInit;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.MemoryModuleInit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Optional;

public class EndocerasAi {

    public static void initBrain(Brain<Endoceras> brain) {
        brain.addActivity(Activity.CORE, ImmutableList.of(
                Pair.of(0, new LookAtTargetSink(45, 90)),
                Pair.of(0, new MoveToTargetSink()),
                Pair.of(1, new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)),
                //updates the grab state to match the riding passenger, basically connects getGrabbedPrey and the memory module
                Pair.of(2, updateGrabPreyState())
        ));

        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(0, new PregnantAnimalLove(EntityInit.ENDOCERAS.get(), 1.1f)),
                Pair.of(1, StartAttacking.create(Endoceras::findNearestValidAttackTarget)),
                Pair.of(2, RandomStroll.swim(1.0f))
        ));

        brain.addActivityAndRemoveMemoriesWhenStopped(ActivitInit.GRAB_PREY.get(), ImmutableList.of(
                        Pair.of(0, SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.2F)),
                        Pair.of(1, StopAttackingIfTargetInvalid.create()),
                        Pair.of(2, makeGrabAttack())
                ), ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.HURT_BY, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.IS_PREGNANT, MemoryStatus.VALUE_ABSENT)),
                ImmutableSet.of(MemoryModuleType.ATTACK_TARGET));


        brain.addActivityAndRemoveMemoriesWhenStopped(ActivitInit.MATING.get(), ImmutableList.of(
                        Pair.of(1, new FindWaterEggLayingSpot<>(16, 1.0f)),
                        Pair.of(2, BrainAiUtils.layEggWhenPossible())
                ), ImmutableSet.of(Pair.of(MemoryModuleType.IS_PREGNANT, MemoryStatus.VALUE_PRESENT)),
                ImmutableSet.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.IS_PREGNANT));

        //avoiding player at all costs when having a grabbed prey, otherwise will just random swim
        brain.addActivityAndRemoveMemoriesWhenStopped(ActivitInit.HURT_GRABBED_PREY.get(), ImmutableList.of(
                Pair.of(0, hurtGrabbedAttack(20)),
                Pair.of(1, SetWalkTargetAwayFrom.entity(MemoryModuleType.NEAREST_VISIBLE_PLAYER, 1.0f, 6, true)),
                Pair.of(2, RandomStroll.swim(1.0f))
        ), ImmutableSet.of(Pair.of(MemoryModuleInit.GRABBED_PREY.get(), MemoryStatus.VALUE_PRESENT)), ImmutableSet.of(MemoryModuleInit.GRABBED_PREY.get()));


        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
    }

    public static OneShot<Endoceras> makeGrabAttack() {
        return BehaviorBuilder.create((context) -> {
            return context.group(context.absent(MemoryModuleInit.GRABBED_PREY.get()), context.present(MemoryModuleType.ATTACK_TARGET), context.absent(MemoryModuleType.ATTACK_COOLING_DOWN), context.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(context, (grabbedPrey, attackTarget, attackCooldown, visibleEntities) -> {
                return (level, entity, gameTime) -> {
                    LivingEntity target = context.get(attackTarget);
                    if (entity.isWithinMeleeAttackRange(target) && context.get(visibleEntities).contains(target)) {
                        if (entity.grabPrey(target)) {
                            grabbedPrey.set(target);
                            entity.swing(InteractionHand.MAIN_HAND);
                            return true;
                        }
                    }
                    return false;
                };
            });
        });
    }

    public static OneShot<Endoceras> hurtGrabbedAttack(int attackCooldown) {
        return BehaviorBuilder.create((context) -> {
            return context.group(context.present(MemoryModuleInit.GRABBED_PREY.get()), context.absent(MemoryModuleType.ATTACK_COOLING_DOWN)).apply(context, (grabbedPrey, cooldown) -> {
                return (level, entity, gameTime) -> {
                    LivingEntity prey = context.get(grabbedPrey);
                    if (prey.isAlive()) {
                        cooldown.setWithExpiry(true, attackCooldown);
                        entity.doHurtTarget(prey);
                        entity.swing(InteractionHand.MAIN_HAND);
                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }

    public static OneShot<Endoceras> updateGrabPreyState() {
        return BehaviorBuilder.create((context) -> {
            return context.group(context.registered(MemoryModuleInit.GRABBED_PREY.get())).apply(context, grabbedPrey ->
                    (level, entity, gameTime) -> {
                        LivingEntity currentPrey = entity.getGrabbedPrey();
                        Optional<LivingEntity> memoryPrey = context.tryGet(grabbedPrey);
                        if (memoryPrey.map(p -> p != currentPrey).orElse(currentPrey != null)) {
                            if (currentPrey == null) {
                                grabbedPrey.erase();
                            } else {
                                grabbedPrey.set(currentPrey);
                            }
                            return true;
                        }
                        return false;
                    });
        });
    }
}
