package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ren.lostintime.common.entity.creatures.Pterygotus;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.schedule.Activity;

import java.util.List;
import java.util.Optional;

public class PterygotusAi {

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.IS_PANICKING,
            MemoryModuleType.NEAREST_VISIBLE_ADULT,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleType.NEAREST_REPELLENT
    );

    public static final ImmutableList<SensorType<? extends Sensor<? super Pterygotus>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.HURT_BY,
            SensorType.NEAREST_ADULT
    );

    public static void makeBrain(Brain<Pterygotus> pBrain) {
        initCoreActivity(pBrain);
        initIdleActivity(pBrain);
        initFightActivity(pBrain);

        pBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        pBrain.setDefaultActivity(Activity.IDLE);
        pBrain.useDefaultActivity();
    }

    private static void initCoreActivity(Brain<Pterygotus> pBrain) {
        pBrain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new Swim(0.8F),
                new AnimalPanic(2.0F) //vital for babies to escape when they fall
        ));
    }

    private static void initIdleActivity(Brain<Pterygotus> pBrain) {
        pBrain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                StartAttacking.create(PterygotusAi::findTarget),

                //babies have the highest priority they must climb on the adult
                babyMountAdult(),
                //if the adult is full, the previous behavior fails and they switch to this(follow)
                BabyFollowAdult.create(UniformInt.of(5, 16), 1.2F),

                //eating thrown away food
                eatDroppedFood(),

                //brred
                new AnimalMakeLove(EntityInit.PTERYGOTUS.get(), 1.0F),

                new RunOne<>(ImmutableList.of(
                        Pair.of(RandomStroll.stroll(1.0F), 2),
                        Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0F), 1),
                        Pair.of(new DoNothing(30, 60), 1)
                ))
        ));
    }

    private static void initFightActivity(Brain<Pterygotus> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                StopAttackingIfTargetInvalid.create(),
                MeleeAttack.create(20) //attack
        ), MemoryModuleType.ATTACK_TARGET);
    }

    public static void updateActivity(Pterygotus pterygotus) {
        Brain<Pterygotus> brain = pterygotus.getBrain();

        if (brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
            brain.setActiveActivityIfPossible(Activity.FIGHT);
            return;
        }

        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }

    private static Optional<? extends LivingEntity> findTarget(Pterygotus pterygotus) {
        Brain<?> brain = pterygotus.getBrain();

        //since it is neutral, it only attacks if it is attacked first.
        Optional<LivingEntity> hurtBy = brain.getMemory(MemoryModuleType.HURT_BY_ENTITY);
        if (hurtBy.isPresent()) {
            return hurtBy;
        }

        return Optional.empty();
    }

    // ==========================================
    //BEHAVIOR OF THE BREEDS
    // ==========================================
    public static BehaviorControl<Pterygotus> babyMountAdult() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.registered(MemoryModuleType.LOOK_TARGET),
                instance.present(MemoryModuleType.NEAREST_VISIBLE_ADULT)
        ).apply(instance, (walkTarget, lookTarget, nearestAdult) -> (level, entity, time) -> {

            //If its not a baby, or if its already riding something, ignore it
            if (!entity.isBaby() || entity.isPassenger()) return false;

            //we obtain the closest adult memory
            Pterygotus adult = (Pterygotus) instance.get(nearestAdult);

            //ff the adult is already full (3 babies), the current baby will ignore this adult.
            if (adult.getPassengers().size() >= 3) return false;

            //If its less than 2 blocks away, jump onto its back
            if (entity.distanceToSqr(adult) < 4.0D) {
                entity.startRiding(adult);
                walkTarget.erase();
                return true;
            }

            lookTarget.set(new EntityTracker(adult, true));
            walkTarget.set(new WalkTarget(new EntityTracker(adult, false), 1.1F, 1));

            return true;
        }));
    }

    // ==========================================
    // FEEDING
    // ==========================================
    public static BehaviorControl<Pterygotus> eatDroppedFood() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.registered(MemoryModuleType.LOOK_TARGET)
        ).apply(instance, (walkTarget, lookTarget) -> (level, entity, time) -> {

            if (entity.getHunger() >= entity.getMaxHunger() && entity.getHealth() >= entity.getMaxHealth()) {
                return false;
            }

            List<ItemEntity> droppedFood = level.getEntitiesOfClass(
                    ItemEntity.class,
                    entity.getBoundingBox().inflate(8.0D, 4.0D, 8.0D),
                    item -> entity.isFoodItem(item.getItem())
            );

            if (droppedFood.isEmpty()) return false;

            ItemEntity targetItem = droppedFood.get(0);

            if (entity.distanceToSqr(targetItem) < 2.25D) {
                entity.consumeItem(entity, targetItem);
                entity.setHunger(entity.getHunger() + 10.0F);
                entity.level().broadcastEntityEvent(entity, (byte) 10);
                walkTarget.erase();
                return true;
            }

            lookTarget.set(new EntityTracker(targetItem, true));
            walkTarget.set(new WalkTarget(new EntityTracker(targetItem, false), 1.0F, 0));

            return true;
        }));
    }
}
