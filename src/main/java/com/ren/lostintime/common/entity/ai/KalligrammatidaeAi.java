package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ren.lostintime.common.entity.creatures.Kalligrammatidae;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.Vec3;

public class KalligrammatidaeAi {

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH
    );

    public static Brain<?> makeBrain(Brain<Kalligrammatidae> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<Kalligrammatidae> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink()
        ));
    }

    private static void initIdleActivity(Brain<Kalligrammatidae> brain) {
        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                stayLandedLogic(),
                takeoffLogic(),
                checkLandingArrivalLogic(),
                startLandingProcessLogic(),
                continuousFlightLogic()
        ));
    }

    private static BehaviorControl<Kalligrammatidae> stayLandedLogic() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET)
        ).apply(instance, (walkTarget) -> (level, entity, time) -> {
            if (!entity.isLanded()) return false;
            walkTarget.erase();
            entity.setDeltaMovement(Vec3.ZERO);
            return true;
        }));
    }

    private static BehaviorControl<Kalligrammatidae> takeoffLogic() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET)
        ).apply(instance, (walkTarget) -> (level, entity, time) -> {
            if (!entity.isLanded()) return false;
            if (entity.getRandom().nextFloat() < 0.01F) {
                entity.setLanded(false);
                entity.wantsToLand = false;
                return true;
            }
            return false;
        }));
    }

    private static BehaviorControl<Kalligrammatidae> startLandingProcessLogic() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.absent(MemoryModuleType.WALK_TARGET)
        ).apply(instance, (walkTarget) -> (level, entity, time) -> {
            if (entity.isLanded() || entity.wantsToLand) return false;

            if (entity.getRandom().nextFloat() < 0.02F) {
                BlockPos currentPos = entity.blockPosition();
                for (BlockPos p : BlockPos.betweenClosed(currentPos.offset(-5, -3, -5), currentPos.offset(5, 3, 5))) {
                    for (Direction dir : Direction.values()) {
                        if (level.getBlockState(p).isFaceSturdy(level, p, dir)) {
                            BlockPos airSpace = p.relative(dir);
                            if (level.getBlockState(airSpace).isAir()) {

                                entity.wantsToLand = true;
                                walkTarget.set(new WalkTarget(airSpace, 0.6F, 0));
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }));
    }

    private static BehaviorControl<Kalligrammatidae> checkLandingArrivalLogic() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.present(MemoryModuleType.WALK_TARGET)
        ).apply(instance, (walkTarget) -> (level, entity, time) -> {
            if (entity.isLanded() || !entity.wantsToLand) return false;

            Vec3 target = instance.get(walkTarget).getTarget().currentPosition();
            double distanceSqr = entity.position().distanceToSqr(target);

            if (distanceSqr < 2.0D || entity.horizontalCollision || entity.verticalCollision) {
                BlockPos pos = entity.blockPosition();

                for (Direction dir : Direction.values()) {
                    BlockPos adjacent = pos.relative(dir);
                    if (level.getBlockState(adjacent).isFaceSturdy(level, adjacent, dir.getOpposite())) {

                        entity.setLanded(true);
                        entity.wantsToLand = false;
                        entity.setAttachFace(dir.getOpposite());
                        entity.setDeltaMovement(0, 0, 0);
                        walkTarget.erase();
                        return true;
                    }
                }
                entity.wantsToLand = false;
                walkTarget.erase();
            }
            return false;
        }));
    }

    private static BehaviorControl<Kalligrammatidae> continuousFlightLogic() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.absent(MemoryModuleType.WALK_TARGET)
        ).apply(instance, (walkTarget) -> (level, entity, time) -> {
            if (entity.isLanded() || entity.wantsToLand) return false;

            BlockPos currentPos = entity.blockPosition();

            int distToGround = 0;
            for (int i = 1; i <= 10; i++) {
                if (!level.getBlockState(currentPos.below(i)).isAir()) {
                    distToGround = i;
                    break;
                }
            }

            double dy;
            if (distToGround < 2) {
                dy = 0.5D + entity.getRandom().nextDouble() * 0.5D;
            } else if (distToGround > 6) {
                dy = -0.2D - entity.getRandom().nextDouble() * 0.3D;
            } else {
                dy = (entity.getRandom().nextDouble() - 0.5D) * 0.6D;
            }

            double dx = (entity.getRandom().nextDouble() - 0.5D) * 8.0D;
            double dz = (entity.getRandom().nextDouble() - 0.5D) * 8.0D;

            Vec3 targetVec = entity.position().add(dx, dy, dz);
            BlockPos targetPos = BlockPos.containing(targetVec);

            if (level.getBlockState(targetPos).isAir() && level.getFluidState(targetPos.below()).isEmpty()) {
                walkTarget.set(new WalkTarget(targetVec, 0.8F, 1));
                return true;
            }
            return false;
        }));
    }
}
