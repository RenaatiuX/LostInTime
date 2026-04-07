package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ren.lostintime.common.entity.creatures.Pterygotus;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.MemoryModuleInit;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
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
            MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.IS_PANICKING, MemoryModuleType.NEAREST_VISIBLE_ADULT,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleInit.AQUATIC_PHASE_TIMER.get(),
            MemoryModuleInit.IS_WATER_PHASE.get(),
            MemoryModuleInit.IS_TRAVELING_TO_WATER.get()
    );

    public static final ImmutableList<SensorType<? extends Sensor<? super Pterygotus>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_PLAYERS, SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.NEAREST_ADULT
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
                new AnimalPanic(2.0F)
        ));
    }

    private static void initIdleActivity(Brain<Pterygotus> pBrain) {
        pBrain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                StartAttacking.create(PterygotusAi::findTarget),
                babyMountAdult(),
                BabyFollowAdult.create(UniformInt.of(5, 16), 1.2F),
                managePhaseCycle(),
                eatDroppedFood(),
                new AnimalMakeLove(EntityInit.PTERYGOTUS.get(), 1.0F),
                new RunOne<>(ImmutableList.of(
                        Pair.of(RandomStroll.swim(0.8F), 4), // más peso
                        Pair.of(RandomStroll.stroll(0.8F), 2),
                        Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0F), 1)
                ))
        ));
    }

    private static void initFightActivity(Brain<Pterygotus> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                StopAttackingIfTargetInvalid.create(),
                MeleeAttack.create(20)
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
        return pterygotus.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY);
    }

    // ==========================================
    // BEHAVIORS
    // ==========================================
    public static BehaviorControl<Pterygotus> babyMountAdult() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.present(MemoryModuleType.NEAREST_VISIBLE_ADULT)
        ).apply(instance, (walkTarget, nearestAdult) -> (level, entity, time) -> {
            if (!entity.isBaby() || entity.isPassenger()) return false;
            Pterygotus adult = (Pterygotus) instance.get(nearestAdult);
            if (adult.getPassengers().size() >= 3) return false;

            if (entity.distanceToSqr(adult) < 4.0D) {
                entity.startRiding(adult);
                walkTarget.erase();
                return true;
            }
            walkTarget.set(new WalkTarget(new EntityTracker(adult, false), 1.1F, 1));
            return true;
        }));
    }

    public static BehaviorControl<Pterygotus> eatDroppedFood() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.registered(MemoryModuleType.LOOK_TARGET)
        ).apply(instance, (walkTarget, lookTarget) -> (level, entity, time) -> {
            if (entity.getHunger() >= entity.getMaxHunger() && entity.getHealth() >= entity.getMaxHealth())
                return false;
            List<ItemEntity> droppedFood = level.getEntitiesOfClass(ItemEntity.class, entity.getBoundingBox().inflate(8.0D), item -> entity.isFoodItem(item.getItem()));
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

    public static BehaviorControl<Pterygotus> managePhaseCycle() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.registered(MemoryModuleInit.AQUATIC_PHASE_TIMER.get()),
                instance.registered(MemoryModuleInit.IS_WATER_PHASE.get()),
                instance.registered(MemoryModuleInit.IS_TRAVELING_TO_WATER.get())
        ).apply(instance, (walkTarget, phaseTimer, isWaterPhase, isTraveling) -> (level, entity, time) -> {

            int waterDuration = entity.getWaterPhaseDuration();
            int landDuration = entity.getLandPhaseDuration();

            Optional<Integer> timerOpt = entity.getBrain().getMemory(MemoryModuleInit.AQUATIC_PHASE_TIMER.get());
            Optional<Boolean> phaseOpt = entity.getBrain().getMemory(MemoryModuleInit.IS_WATER_PHASE.get());
            Optional<Boolean> travelingOpt = entity.getBrain().getMemory(MemoryModuleInit.IS_TRAVELING_TO_WATER.get());
            boolean currentlyInWater = entity.isConsideredInWater();
            boolean traveling = travelingOpt.orElse(false);

            if (timerOpt.isEmpty() || phaseOpt.isEmpty()) {
                isWaterPhase.set(currentlyInWater);
                isTraveling.set(false);
                phaseTimer.set(currentlyInWater ? waterDuration : landDuration);
                return false;
            }

            int remaining = timerOpt.get();
            boolean waterPhase = phaseOpt.get();

            if (traveling) {
                if (currentlyInWater && !waterPhase) {
                    isWaterPhase.set(true);
                    isTraveling.set(false);
                    phaseTimer.set(waterDuration);
                    System.out.println("[Pterygotus] Llego al agua! Iniciando fase AGUA");
                } else if (!currentlyInWater && waterPhase) {
                    isWaterPhase.set(false);
                    isTraveling.set(false);
                    phaseTimer.set(landDuration);
                    System.out.println("[Pterygotus] Llego a tierra! Iniciando fase TIERRA");
                }
                return false;
            }

            if (currentlyInWater != waterPhase) {
                isWaterPhase.set(currentlyInWater);
                phaseTimer.set(currentlyInWater ? waterDuration : landDuration);
                System.out.println("[Pterygotus] Cambio externo detectado, ajustando fase");
                return false;
            }

            if (remaining > 0) {
                if (remaining % 100 == 0) {
                    System.out.println("[Pterygotus] Timer: " + remaining + " | Fase: "
                            + (waterPhase ? "AGUA" : "TIERRA"));
                }

                if (!waterPhase && remaining == 200) {
                    BlockPos waterPos = BlockPos.findClosestMatch(
                            entity.blockPosition(), 16, 8,
                            p -> level.getFluidState(p).is(FluidTags.WATER)
                    ).orElse(null);
                    if (waterPos != null) {
                        walkTarget.set(new WalkTarget(waterPos, 0.8F, 0));
                        isTraveling.set(true);
                        phaseTimer.set(0); // fuerza el cambio cuando llegue
                        System.out.println("[Pterygotus] Saliendo anticipado hacia agua!");
                        return true;
                    }
                }

                phaseTimer.set(remaining - 1);
                return false;
            }

            System.out.println("[Pterygotus] TIMER A 0! Buscando " + (waterPhase ? "TIERRA" : "AGUA"));

            if (waterPhase) {
                BlockPos landPos = null;
                for (int i = 0; i < 10; i++) {
                    BlockPos p = entity.blockPosition().offset(
                            level.random.nextInt(16) - 8,
                            level.random.nextInt(6) - 3,
                            level.random.nextInt(16) - 8
                    );
                    if (level.getBlockState(p).isSolidRender(level, p)
                            && level.getFluidState(p.above()).isEmpty()) {
                        landPos = p.above();
                        break;
                    }
                }
                if (landPos != null) {
                    walkTarget.set(new WalkTarget(landPos, 0.8F, 0));
                    isTraveling.set(true);
                    System.out.println("[Pterygotus] Viajando a tierra: " + landPos);
                    return true;
                }
                System.out.println("[Pterygotus] No encontro tierra, reintentando en 5s");
                phaseTimer.set(100);

            } else {
                BlockPos waterPos = BlockPos.findClosestMatch(
                        entity.blockPosition(), 16, 8,
                        p -> level.getFluidState(p).is(FluidTags.WATER)
                ).orElse(null);

                if (waterPos != null) {
                    walkTarget.set(new WalkTarget(waterPos, 0.8F, 0));
                    isTraveling.set(true);
                    System.out.println("[Pterygotus] Viajando a agua: " + waterPos);
                    return true;
                }
                System.out.println("[Pterygotus] No encontro agua, reintentando en 5s");
                phaseTimer.set(100);
            }

            return false;
        }));
    }
}