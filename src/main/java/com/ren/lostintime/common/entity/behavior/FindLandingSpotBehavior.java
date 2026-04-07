package com.ren.lostintime.common.entity.behavior;

import com.ren.lostintime.common.entity.creatures.Kalligrammatidae;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class FindLandingSpotBehavior {

    public static BehaviorControl<Kalligrammatidae> create() {
        return BehaviorBuilder.create(instance -> instance.group(
                instance.absent(MemoryModuleType.WALK_TARGET)
        ).apply(instance, (walkTarget) -> (level, entity, time) -> {

            if (entity.isLanded()) return false;

            if (entity.getRandom().nextFloat() < 0.25F) {
                BlockPos currentPos = entity.blockPosition();

                for (BlockPos targetPos : BlockPos.betweenClosed(currentPos.offset(-3, -2, -3), currentPos.offset(3, 2, 3))) {
                    for (Direction dir : Direction.values()) {
                        if (level.getBlockState(targetPos).isFaceSturdy(level, targetPos, dir)) {
                            BlockPos airPos = targetPos.relative(dir);

                            if (level.getBlockState(airPos).isAir()) {
                                walkTarget.set(new WalkTarget(airPos, 0.6F, 0));

                                if (entity.distanceToSqr(airPos.getX() + 0.5, airPos.getY(), airPos.getZ() + 0.5) < 1.0D) {
                                    entity.setLanded(true);
                                    entity.setAttachFace(dir);
                                    entity.setDeltaMovement(0, 0, 0);
                                }
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }));
    }

}
