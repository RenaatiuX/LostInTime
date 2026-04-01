package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ren.lostintime.common.entity.creatures.Helicoprion;
import com.ren.lostintime.common.entity.enums.GrowthStage;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.MobEffectInit;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.ItemEntity;
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
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, // Essential for spotting prey
            MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleType.NEAREST_VISIBLE_ADULT,
            MemoryModuleType.HURT_BY_ENTITY
    );

    public static final ImmutableList<SensorType<? extends Sensor<? super Helicoprion>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_ITEMS,
            SensorType.NEAREST_ADULT,
            SensorType.HURT_BY
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
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink()
        ));
    }

    private static void initIdleActivity(Brain<Helicoprion> pBrain) {
        pBrain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                new AnimalMakeLove(EntityInit.HELICOPRION.get(), 0.8F),
                BabyFollowAdult.create(UniformInt.of(5, 16), 1.15F),
                breachAttack(),
                StartAttacking.create(HelicoprionAi::findTarget),
                swimToFood(),
                new RunOne<>(ImmutableList.of(
                        Pair.of(RandomStroll.swim(0.8F), 1)))
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

    private static BehaviorControl<Helicoprion> swimToFood() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.present(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM),
                instance.absent(MemoryModuleType.ATTACK_TARGET)
        ).apply(instance, (itemAccessor, attackAccessor) -> (level, entity, time) -> {

            ItemEntity food = instance.get(itemAccessor);
            entity.getLookControl().setLookAt(food);
            entity.getNavigation().moveTo(food, 1.0D);
            return true;
        }));
    }

    private static Optional<? extends LivingEntity> findTarget(Helicoprion pHelicoprion) {
        Brain<?> brain = pHelicoprion.getBrain();
        if (pHelicoprion.getGrowthStage() == GrowthStage.BABY) {
            return Optional.empty();
        }

        if (!pHelicoprion.isHungry() && pHelicoprion.getLastHurtByMob() == null) {
            return Optional.empty();
        }
        return brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                .flatMap(entities -> entities.findClosest(entity -> {
                    if (entity instanceof Helicoprion || !entity.isAlive()) return false;

                    boolean isBleeding = pHelicoprion.canSmellBlood(entity);
                    double distance = pHelicoprion.distanceTo(entity);

                    if (isBleeding && distance <= pHelicoprion.getBloodScentRange()) {
                        return true;
                    }

                    return entity.isInWater() && (pHelicoprion.distanceTo(entity) <= 8.0D || entity.getHealth() <= entity.getMaxHealth() / 2.0F);
                }));
    }

    private static OneShot<Helicoprion> breachAttack() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES),
                instance.registered(MemoryModuleType.ATTACK_TARGET)
        ).apply(instance, (entitiesAccessor, targetAccessor) -> (level, entity, time) -> {

            if (entity.getGrowthStage() == GrowthStage.BABY || entity.isBreaching() || entity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_COOLING_DOWN)) {
                return false;
            }

            if (entity.isBreaching() || entity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_COOLING_DOWN)) {
                return false;
            }

            Optional<LivingEntity> flyingTarget = instance.get(entitiesAccessor).findClosest(target -> {

                if (target instanceof Helicoprion || !target.isAlive()) return false;
                if (target.isInWater() || target.onGround()) return false;

                double yDiff = target.getY() - entity.getY();
                if (yDiff < -2.0D || yDiff > 8.0D) {
                    return false;
                }

                double horizontalDist = Math.sqrt(entity.distanceToSqr(target.getX(), entity.getY(), target.getZ()));
                return !(horizontalDist > 10.0D) && !(horizontalDist < 2.0D);
            });

            if (flyingTarget.isPresent()) {
                LivingEntity prey = flyingTarget.get();

                entity.tickStalking();

                boolean isBleeding = prey.hasEffect(MobEffectInit.BLEEDING.get());
                if (!isBleeding && entity.getStalkingTicks() < entity.getRequiredStalkingTicks()) {
                    entity.getLookControl().setLookAt(prey);
                    entity.getNavigation().moveTo(prey.getX(), entity.getY(), prey.getZ(), 0.8D);
                    return true;
                }

                entity.getLookControl().setLookAt(prey);
                entity.executeBreachAttack(prey);

                entity.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, 100L);
                entity.resetStalking();
                return true;

            } else {
                if (entity.getStalkingTicks() > 0) {
                    entity.resetStalking();
                }
            }

            return false;
        }));
    }
}
