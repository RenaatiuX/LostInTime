package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ren.lostintime.common.entity.creatures.Deinonychus;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class DeinonychusAi {

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

    public static final ImmutableList<SensorType<? extends Sensor<? super Deinonychus>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.HURT_BY,
            SensorType.NEAREST_ADULT
    );

    public static void makeBrain(Brain<Deinonychus> pBrain) {
        initCoreActivity(pBrain);
        initIdleActivity(pBrain);
        initFightActivity(pBrain);

        pBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        pBrain.setDefaultActivity(Activity.IDLE);
        pBrain.useDefaultActivity();
    }

    private static void initCoreActivity(Brain<Deinonychus> pBrain) {
        pBrain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new Swim(0.8F),
                new AnimalPanic(2.0F)
        ));
    }

    private static void initIdleActivity(Brain<Deinonychus> pBrain) {
        pBrain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                StartAttacking.create(DeinonychusAi::findTarget),
                eatDroppedMeat(),
                goToSoilAndLayEgg(),
                protectNest(),
                followOwner(1.0F, 10.0F, 3.0F),
                BabyFollowAdult.create(UniformInt.of(5, 16), 1.15F),
                new AnimalMakeLove(EntityInit.DEINONYCHUS.get(), 1.0F),
                new RunOne<>(ImmutableList.of(
                        Pair.of(RandomStroll.stroll(1.0F), 2),
                        Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0F), 1),
                        Pair.of(new DoNothing(30, 60), 1)
                ))
        ));
    }

    private static void initFightActivity(Brain<Deinonychus> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                StopAttackingIfTargetInvalid.create(),
                stalkingAndPouncing(),
                new AnimalPanic(1.5F),
                BehaviorBuilder.triggerIf(entity -> !entity.isStalking(),
                        (OneShot<? super Deinonychus>) SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.2F)),
                MeleeAttack.create(20)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    public static void updateActivity(Deinonychus deinonychus) {
        Brain<Deinonychus> brain = deinonychus.getBrain();

        if (deinonychus.isBaby()) {
            brain.setActiveActivityIfPossible(Activity.IDLE);
            return;
        }

        if (brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
            deinonychus.setInSittingPose(false); // for safety, if a fight breaks out, dont sit down
            brain.setActiveActivityIfPossible(Activity.FIGHT);
            return;
        }

        // if it is sitting (tamed) it does nothing
        if (deinonychus.isInSittingPose()) {
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            brain.setActiveActivityIfPossible(Activity.IDLE);
            return;
        }

        if (deinonychus.isSleeping()) {
            brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
            brain.setActiveActivityIfPossible(Activity.IDLE);
            return;
        }

        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }

    private static Optional<? extends LivingEntity> findTarget(Deinonychus deinonychus) {
        if (deinonychus.isSleeping()) return Optional.empty();

        Brain<?> brain = deinonychus.getBrain();

        //defend urself if attacked
        Optional<LivingEntity> hurtBy = brain.getMemory(MemoryModuleType.HURT_BY_ENTITY);
        if (hurtBy.isPresent()) {
            return hurtBy;
        }

        if (deinonychus.isThreatening() && brain.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)) {
            Optional<LivingEntity> intruder = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                    .flatMap(entities -> entities.findClosest(e ->
                            deinonychus.getNestPos().isPresent() &&
                                    e.distanceToSqr(Vec3.atCenterOf(deinonychus.getNestPos().get())) < 64.0D &&
                                    !deinonychus.isOwnedBy(e) &&
                                    !(e instanceof Deinonychus)
                    ));
            if (intruder.isPresent()) return intruder;
        }

        //defend the owner
        if (!deinonychus.isBaby() && deinonychus.isTame()) {
            LivingEntity owner = deinonychus.getOwner();
            if (owner != null) {
                LivingEntity attacker = owner.getLastHurtByMob();
                if (attacker != null && attacker.isAlive() && !deinonychus.isOwnedBy(attacker)) {
                    return Optional.of(attacker);
                }
                //if the owner attacks something
                LivingEntity target = owner.getLastHurtMob();
                if (target != null && target.isAlive() && !deinonychus.isOwnedBy(target)) {
                    //we prevent the dino from attacking us if we accidentally get stuck to another one of our dinos
                    return Optional.of(target);
                }
            }
        }

        //Hunger
        //it only looks for food if its not domesticated or if its hungr
        if (!deinonychus.isTame() && deinonychus.canHunt()) {
            return brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                    .flatMap(entities -> entities.findClosest(e ->
                            (e.getType() == EntityType.PIG || e.getType() == EntityType.SHEEP) &&
                                    !(e instanceof Deinonychus)
                    ));
        }

        return Optional.empty();
    }

    private static BehaviorControl<Deinonychus> followOwner(float speed, float startDist, float stopDist) {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.registered(MemoryModuleType.LOOK_TARGET)
        ).apply(instance, (walkTarget, lookTarget) -> (level, entity, time) -> {

            if (entity.isInSittingPose() || entity.isSleeping()) return false;

            LivingEntity owner = entity.getOwner();
            if (owner != null && entity.distanceToSqr(owner) > (startDist * startDist)) {
                if (entity.distanceToSqr(owner) > 400.0D) { // 20 bloques
                    entity.moveTo(owner.getX(), owner.getY(), owner.getZ());
                    return true;
                }

                walkTarget.set(new WalkTarget(new EntityTracker(owner, false), speed, (int) stopDist));
                lookTarget.set(new EntityTracker(owner, true));
                return true;
            }
            return false;
        }));
    }

    private static BehaviorControl<Deinonychus> stalkingAndPouncing() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.present(MemoryModuleType.ATTACK_TARGET),
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.registered(MemoryModuleType.LOOK_TARGET)
        ).apply(instance, (attackTarget, walkTarget, lookTarget) -> (level, entity, time) -> {

            LivingEntity target = instance.get(attackTarget);
            double distanceSqr = entity.distanceToSqr(target);

            //We always look at the prey
            lookTarget.set(new EntityTracker(target, true));

            // ==========================================
            // STALKING AND JUMP
            // ==========================================
            //can only enter here if you havent been spotted, havent jumped, and are within stealth range
            if (entity.isReadyToStalk(target) && !entity.hasBeenSpotted && !entity.hasPounced) {

                if (!isTargetLookingAtMe(target, entity)) {
                    entity.setStalking(true);

                    //3-6 blocks
                    if (distanceSqr <= 36.0D && distanceSqr > 9.0D) {
                        if (entity.onGround()) {
                            Vec3 direction = (new Vec3(target.getX() - entity.getX(), 0.0D, target.getZ() - entity.getZ())).normalize().scale(0.8D).add(0.0D, 0.4D, 0.0D);
                            entity.setDeltaMovement(direction.x, 0.6D, direction.z);

                            entity.setPouncing(true);
                            entity.setStalking(false);
                            entity.hasPounced = true;
                            walkTarget.erase();
                            return true;
                        }
                    }

                    //if it still, cant jump, it slowly aprroaches
                    entity.tickStalking();
                    if (entity.getStalkingTicks() <= entity.getRequiredStalkingTicks()) {
                        walkTarget.erase(); // Quieto (Idle)
                    } else {
                        walkTarget.set(new WalkTarget(new EntityTracker(target, false), 0.7F, 2)); // Caminando agachado
                    }
                    return true;
                } else {
                    //if he sees us, we break the stealth and run
                    entity.hasBeenSpotted = true;
                    entity.setStalking(false);
                }
            }

            // ==========================================
            // ATTACK
            // ==========================================
            //if they discover us, or we are too far/close to stalk, we go straight for the attack
            entity.setStalking(false);
            lookTarget.set(new EntityTracker(target, true));
            walkTarget.set(new WalkTarget(new EntityTracker(target, false), 1.2F, 0));

            return true;
        }));
    }

    //useful for determining if the prey is looking at the hunter
    private static boolean isTargetLookingAtMe(LivingEntity target, Deinonychus entity) {
        Vec3 viewVector = target.getViewVector(1.0F).normalize();
        Vec3 toEntityVector = new Vec3(entity.getX() - target.getX(), entity.getEyeY() - target.getEyeY(), entity.getZ() - target.getZ()).normalize();
        double dotProduct = viewVector.dot(toEntityVector);
        return dotProduct > 0.5D; //if it is greater than 0.5, the target has the dinosaur in its field of vision
    }

    private static BehaviorControl<Deinonychus> protectNest() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.ATTACK_TARGET),
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.registered(MemoryModuleType.LOOK_TARGET),
                instance.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
        ).apply(instance, (attackTarget, walkTarget, lookTarget, visibleEntities) -> (level, entity, time) -> {

            if (entity.getNestPos().isEmpty()) return false;

            if (instance.tryGet(attackTarget).isPresent()) {
                entity.setThreatening(false);
                entity.setInSittingPose(false);
                return false;
            }

            BlockPos nest = entity.getNestPos().get();
            double distToNest = entity.distanceToSqr(nest.getX(), nest.getY(), nest.getZ());

            LivingEntity intruder = instance.get(visibleEntities).findClosest(target ->
                    target.distanceToSqr(nest.getX(), nest.getY(), nest.getZ()) < 64.0D &&
                            entity.getSensing().hasLineOfSight(target) &&
                            !entity.isOwnedBy(target) &&
                            !(target instanceof Deinonychus)
            ).orElse(null);

            if (intruder != null) {
                lookTarget.set(new EntityTracker(intruder, true));
                entity.intruderTime++;

                if (entity.intruderTime > 20) {
                    entity.setThreatening(true);
                }

                if (entity.intruderTime >= 100) {
                    entity.setInSittingPose(false);
                    entity.setThreatening(false);
                    entity.intruderTime = 0;
                    attackTarget.set(intruder);
                    return true;
                }

                // If its near the nest, let it continue sitting and incubating
                if (distToNest < 2.0D) {
                    entity.setInSittingPose(true);
                    walkTarget.erase();
                }
                return true;
            } else {
                //Quick reset if the intruder leaves
                if (entity.intruderTime > 0) entity.intruderTime = Math.max(0, entity.intruderTime - 10);
                else entity.setThreatening(false);
            }

            //logic of returning to the nest (Only if there is no intruder)
            if (distToNest > 2.25D) {
                entity.setInSittingPose(false);
                entity.setThreatening(false);
                walkTarget.set(new WalkTarget(nest, 1.0F, 0));
                return true;
            } else {
                entity.setInSittingPose(true);
                walkTarget.erase();
                return true;
            }
        }));
    }

    public static BehaviorControl<Deinonychus> goToSoilAndLayEgg() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.registered(MemoryModuleType.LOOK_TARGET)
        ).apply(instance, (walkTarget, lookTarget) -> (level, entity, time) -> {

            if (!entity.isPregnant() || entity.getGestationTicks() > 600) return false;

            //if we are already standing on ground/grass, we erase the destination so that it stays still.
            BlockPos posBelow = entity.blockPosition().below();
            if (level.getBlockState(posBelow).is(BlockTags.DIRT) || level.getBlockState(posBelow).is(Blocks.GRASS_BLOCK)) {
                walkTarget.erase();
                return true;
            }

            //search for a dirt/grass block within a 10-block radius
            BlockPos targetPos = null;
            Iterable<BlockPos> checkPos = BlockPos.betweenClosed(entity.blockPosition().offset(-10, -3, -10), entity.blockPosition().offset(10, 2, 10));
            for (BlockPos p : checkPos) {
                if (level.getBlockState(p).is(BlockTags.DIRT) || level.getBlockState(p).is(Blocks.GRASS_BLOCK)) {
                    if (level.isEmptyBlock(p.above())) {
                        targetPos = p.above();
                        break;
                    }
                }
            }

            if (targetPos != null) {
                walkTarget.set(new WalkTarget(targetPos, 1.2F, 0));
                lookTarget.set(new BlockPosTracker(targetPos));
                return true;
            }

            return false;
        }));
    }

    public static BehaviorControl<Deinonychus> eatDroppedMeat() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.registered(MemoryModuleType.LOOK_TARGET)
        ).apply(instance, (walkTarget, lookTarget) -> (level, entity, time) -> {

            //He only looks for food if he lacks life or is hungry
            if (entity.getHunger() >= entity.getMaxHunger() && entity.getHealth() >= entity.getMaxHealth()) {
                return false;
            }

            //We are looking for meat on the ground (within an 8-block radius)
            List<ItemEntity> droppedMeat = level.getEntitiesOfClass(
                    ItemEntity.class,
                    entity.getBoundingBox().inflate(8.0D, 4.0D, 8.0D),
                    item -> entity.isFoodItem(item.getItem())
            );

            if (droppedMeat.isEmpty()) return false;

            ItemEntity targetItem = droppedMeat.get(0);

            //If its close enough to bite (1.5 blocks)
            if (entity.distanceToSqr(targetItem) < 2.25D) {
                entity.consumeItem(entity, targetItem);

                entity.setHunger(entity.getHunger() + 10.0F);
                entity.level().broadcastEntityEvent(entity, (byte) 10);

                walkTarget.erase();
                return true;
            }

            //If its far away, walk towards the item.
            lookTarget.set(new EntityTracker(targetItem, true));
            walkTarget.set(new WalkTarget(new EntityTracker(targetItem, false), 1.0F, 0));

            return true;
        }));
    }
}