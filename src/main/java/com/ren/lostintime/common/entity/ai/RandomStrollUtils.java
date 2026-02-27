package com.ren.lostintime.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RandomStrollUtils {
    private static final int[][] SWIM_XY_DISTANCE_TIERS = new int[][]{{1, 1}, {3, 3}, {5, 5}, {6, 5}, {7, 7}, {10, 7}};

    /**
     * Creates a behavior that makes a mob stroll along the ocean floor.
     *
     * @param pSpeedModifier The speed multiplier for the mob's movement.
     * @return A {@link OneShot} behavior for ocean floor strolling.
     */
    public static OneShot<PathfinderMob> swimOceanFloor(float pSpeedModifier) {
        return RandomStroll.strollFlyOrSwim(pSpeedModifier, RandomStrollUtils::getOceanFloorTargetPos, Entity::isInWaterOrBubble);
    }
    
    /**
     * Calculates a position for a mob to flee to when panicked in water.
     * this also will avoid the entity if it got hit recently
     *
     * @param mob The mob that is panicking.
     * @return A {@link Vec3} representing the target position, or null if none found.
     */
    @Nullable
    public static Vec3 getPanicPosInWater(PathfinderMob mob){
        var hurtByEntity = mob.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY);
        if (hurtByEntity.isPresent()){
             Vec3 hurtPos = hurtByEntity.get().position();
             Vec3 fleeDir = mob.position().subtract(hurtPos).normalize();
             return getWeightedRandomPos(mob, fleeDir, 10, 0.8f);
        }
        return getTargetSwimPos(mob);
    }


    /**
     * Generates a random position biased towards a specific direction.
     *
     * @param mob       The mob for which the position is being calculated.
     * @param direction The target direction to bias towards.
     * @param radius    The distance from the mob's current position.
     * @param weight    The strength of the bias (0.0 to 1.0).
     * @return A {@link Vec3} representing the biased random position, or null if the position is not in water.
     */
    @Nullable
    public static Vec3 getWeightedRandomPos(PathfinderMob mob, Vec3 direction, int radius, float weight) {
        RandomSource random = mob.getRandom();

        // Generate a random direction
        double x = (random.nextDouble() * 2.0 - 1.0);
        double y = (random.nextDouble() * 2.0 - 1.0);
        double z = (random.nextDouble() * 2.0 - 1.0);
        Vec3 randomDir = new Vec3(x, y, z).normalize();

        // Bias towards the target direction
        Vec3 biasedDir = randomDir.lerp(direction, weight).normalize();

        // Scale by radius
        Vec3 targetPos = mob.position().add(biasedDir.scale(radius));

        // Check if valid (e.g. in water if mob is water creature)
        BlockPos blockPos = BlockPos.containing(targetPos);
        if (!mob.level().getFluidState(blockPos).is(FluidTags.WATER)) {
             // Try to find water nearby or return null/fallback
             return null;
        }

        return targetPos;
    }

    /**
     * Finds the nearest water position within a given range.
     *
     * @param mob    The mob searching for water.
     * @param range  The search radius.
     * @return A {@link Vec3} representing the center of the nearest water block, or null if none found.
     */
    @Nullable
    public static Vec3 findNearestWaterPos(PathfinderMob mob, int range) {
        BlockPos mobPos = mob.blockPosition();
        BlockPos nearestWater = null;
        double minDistanceSqr = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(mobPos.offset(-range, -range, -range), mobPos.offset(range, range, range))) {
            if (mob.level().getFluidState(pos).is(FluidTags.WATER)) {
                double distanceSqr = mobPos.distSqr(pos);
                if (distanceSqr < minDistanceSqr) {
                    minDistanceSqr = distanceSqr;
                    nearestWater = pos.immutable();
                }
            }
        }

        return nearestWater != null ? Vec3.atCenterOf(nearestWater) : null;
    }

    @Nullable
    public static Vec3 getOceanFloorTargetPos(PathfinderMob mob) {
        Vec3 currentBestPos = null;
        Vec3 nextPos = null;

        for (int[] tier : SWIM_XY_DISTANCE_TIERS) {
            if (currentBestPos == null) {
                // Initial attempt: find a random spot on the floor
                nextPos = findRandomOceanFloorPos(mob, tier[0], tier[1]);
            } else {
                // Extend the path in the same direction
                Vec3 direction = mob.position().vectorTo(currentBestPos).normalize();
                nextPos = mob.position().add(direction.multiply(tier[0], tier[1], tier[0]));

                // Snap nextPos to the floor if possible
                BlockPos nextBlockPos = BlockPos.containing(nextPos);
                nextPos = snapToOceanFloor(mob.level(), nextBlockPos, tier[1]);
            }

            // Validate: must be on floor (water at pos, solid below)
            if (nextPos == null || !isOceanFloor(mob.level(), BlockPos.containing(nextPos))) {
                return currentBestPos;
            }

            currentBestPos = nextPos;
        }

        return currentBestPos;
    }

    @Nullable
    public static Vec3 findRandomOceanFloorPos(PathfinderMob mob, int xzRange, int yRange) {
        BlockPos mobPos = mob.blockPosition();
        for (int i = 0; i < 10; i++) {
            int x = mob.getRandom().nextInt(2 * xzRange + 1) - xzRange;
            int z = mob.getRandom().nextInt(2 * xzRange + 1) - xzRange;
            int y = mob.getRandom().nextInt(2 * yRange + 1) - yRange;

            BlockPos targetPos = mobPos.offset(x, y, z);

            // Try to find floor near this random spot
            Vec3 floorPos = snapToOceanFloor(mob.level(), targetPos, yRange);
            if (floorPos != null) {
                return floorPos;
            }
        }
        return null;
    }

    @Nullable
    public static Vec3 snapToOceanFloor(Level level, BlockPos pos, int verticalSearch) {
        // Search down/up to find the floor
        for (int i = -verticalSearch; i <= verticalSearch; i++) {
            BlockPos checkPos = pos.offset(0, i, 0);
            if (isOceanFloor(level, checkPos)) {
                return Vec3.atBottomCenterOf(checkPos);
            }
        }
        return null;
    }

    @javax.annotation.Nullable
    public static Vec3 getTargetSwimPos(PathfinderMob mob) {
        Vec3 vec3 = null;
        Vec3 vec31 = null;

        for(int[] tier : SWIM_XY_DISTANCE_TIERS) {
            if (vec3 == null) {
                vec31 = BehaviorUtils.getRandomSwimmablePos(mob, tier[0], tier[1]);
            } else {
                vec31 = mob.position().add(mob.position().vectorTo(vec3).normalize().multiply((double)tier[0], (double)tier[1], (double)tier[0]));
            }

            if (vec31 == null || mob.level().getFluidState(BlockPos.containing(vec31)).isEmpty()) {
                return vec3;
            }

            vec3 = vec31;
        }

        return vec31;
    }

    private static boolean isOceanFloor(Level level, BlockPos pos) {
        return level.getFluidState(pos).is(FluidTags.WATER) && level.getBlockState(pos.below()).isSolid();
    }
}
