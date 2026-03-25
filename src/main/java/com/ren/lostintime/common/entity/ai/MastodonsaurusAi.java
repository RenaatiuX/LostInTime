package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ren.lostintime.common.entity.creatures.Mastodonsaurus;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class MastodonsaurusAi {

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY
    );

    public static final ImmutableList<SensorType<? extends Sensor<? super Mastodonsaurus>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.HURT_BY
    );

    public static void makeBrain(Brain<Mastodonsaurus> pBrain) {
        initCoreActivity(pBrain);
        initIdleActivity(pBrain);
        initFightActivity(pBrain);

        pBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        pBrain.setDefaultActivity(Activity.IDLE);
        pBrain.useDefaultActivity();
    }

    private static void initCoreActivity(Brain<Mastodonsaurus> pBrain) {
        pBrain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink()
        ));
    }

    private static void initIdleActivity(Brain<Mastodonsaurus> pBrain) {
        pBrain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                babyPanic(),
                dragToWater(),
                StartAttacking.create(MastodonsaurusAi::findSheepTarget),
                seekWater(),
                seekLand(),
                new RunOne<>(ImmutableList.of(
                        Pair.of(RandomStroll.swim(0.8F), 2),
                        Pair.of(new DoNothing(30, 60), 1)
                ))
        ));
    }

    private static void initFightActivity(Brain<Mastodonsaurus> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                grabPrey(),
                StopAttackingIfTargetInvalid.create(),
                SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.2F),
                MeleeAttack.create(20)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    public static void updateActivity(Mastodonsaurus masto) {
        Brain<Mastodonsaurus> brain = masto.getBrain();
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }

    //SHEEPS For testing only
    private static Optional<? extends LivingEntity> findSheepTarget(Mastodonsaurus masto) {
        if (masto.isBaby() || !masto.getPassengers().isEmpty()) return Optional.empty();

        return masto.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap(entities ->
                entities.findClosest(entity -> entity instanceof Sheep)
        );
    }

    private static OneShot<Mastodonsaurus> grabPrey() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.present(MemoryModuleType.ATTACK_TARGET)
        ).apply(instance, (targetAccessor) -> (level, entity, time) -> {

            if (entity.getPassengers().isEmpty()) {
                LivingEntity target = instance.get(targetAccessor);
                if (entity.distanceTo(target) < 3.5F && entity.hasLineOfSight(target)) {
                    if (target.getBbWidth() < entity.getBbWidth()) {

                        target.startRiding(entity, true); // ¡A la boca!
                        entity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET); // Volvemos a IDLE
                        return true;
                    }
                }
            }
            return false;
        }));
    }

    private static OneShot<Mastodonsaurus> dragToWater() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET)
        ).apply(instance, (walkTargetAccessor) -> (level, entity, time) -> {

            if (!entity.isBaby() || !entity.getPassengers().isEmpty() && !entity.isInWater()) {
                if (!instance.tryGet(walkTargetAccessor).isPresent()) {
                    BlockPos waterPos = findNearestWater(level, entity.blockPosition(), 15);
                    if (waterPos != null) {
                        walkTargetAccessor.set(new WalkTarget(waterPos, 1.2F, 0));
                    }
                }
                return true;
            }
            return false;
        }));
    }

    private static BlockPos findNearestWater(Level level, BlockPos start, int range) {
        for (BlockPos pos : BlockPos.betweenClosed(start.offset(-range, -range, -range), start.offset(range, range, range))) {
            if (level.getFluidState(pos).is(FluidTags.WATER)) {
                return pos;
            }
        }
        return null;
    }

    private static OneShot<Mastodonsaurus> seekWater() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.absent(MemoryModuleType.ATTACK_TARGET)
        ).apply(instance, (walkTargetAccessor, attackTargetAccessor) -> (level, entity, time) -> {

            if (!entity.isBaby() || !entity.isInWater() && entity.landTimer > 600 && entity.getPassengers().isEmpty()) {

                BlockPos targetWater = null;
                RandomSource random = entity.getRandom();
                int range = 15;

                for (int i = 0; i < 15; i++) {
                    BlockPos pos = entity.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
                    while (level.isEmptyBlock(pos) && pos.getY() > level.getMinBuildHeight()) {
                        pos = pos.below();
                    }
                    if (level.getFluidState(pos).is(FluidTags.WATER)) {
                        targetWater = pos;
                        break;
                    }
                }

                if (targetWater != null) {
                    walkTargetAccessor.set(new WalkTarget(targetWater, 1.0F, 0));
                    return true;
                }
            }
            return false;
        }));
    }

    private static OneShot<Mastodonsaurus> seekLand() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.absent(MemoryModuleType.ATTACK_TARGET)
        ).apply(instance, (walkTargetAccessor, attackTargetAccessor) -> (level, entity, time) -> {

            if (!entity.isBaby() || entity.isInWater() && entity.swimTimer > 1200 && entity.getPassengers().isEmpty()) {

                Vec3 landPos = LandRandomPos.getPos(entity, 15, 7);

                if (landPos != null) {
                    BlockPos targetLand = BlockPos.containing(landPos);

                    if (!level.getFluidState(targetLand).is(FluidTags.WATER)) {
                        walkTargetAccessor.set(new WalkTarget(targetLand, 1.0F, 0));
                        return true;
                    }
                }
            }
            return false;
        }));
    }

    private static OneShot<Mastodonsaurus> babyPanic() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.present(MemoryModuleType.HURT_BY_ENTITY),
                instance.registered(MemoryModuleType.WALK_TARGET)
        ).apply(instance, (hurtByAccessor, walkTargetAccessor) -> (level, entity, time) -> {

            if (!entity.isBaby()) return false;

            LivingEntity attacker = instance.get(hurtByAccessor);

            if (entity.distanceToSqr(attacker) > 144.0D) {
                entity.getBrain().eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
                return true;
            }

            Vec3 fleePos = DefaultRandomPos.getPosAway(entity, 16, 7, attacker.position());
            if (fleePos != null) {
                walkTargetAccessor.set(new WalkTarget(fleePos, 1.5F, 0));
            }
            return true;
        }));
    }
}
