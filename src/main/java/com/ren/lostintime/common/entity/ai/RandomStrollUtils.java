package com.ren.lostintime.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RandomStrollUtils {
    private static final int[][] SWIM_XY_DISTANCE_TIERS = new int[][]{{1, 1}, {3, 3}, {5, 5}, {6, 5}, {7, 7}, {10, 7}};

    public static OneShot<PathfinderMob> swimOceanFloor(float pSpeedModifier) {
        return RandomStroll.strollFlyOrSwim(pSpeedModifier, RandomStrollUtils::getOceanFloorTargetPos, Entity::isInWaterOrBubble);
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
