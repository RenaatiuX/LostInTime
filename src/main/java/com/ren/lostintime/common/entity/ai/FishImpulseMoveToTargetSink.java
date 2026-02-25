package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableMap;
import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.MemoryModuleInit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class FishImpulseMoveToTargetSink extends Behavior<Mob> {

    public FishImpulseMoveToTargetSink() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleInit.VISIBLE_BLOCKS.get(), MemoryStatus.REGISTERED
        ));
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, Mob pEntity, long pGameTime) {
        return pEntity.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET);
    }

    @Override
    protected void tick(ServerLevel level, Mob entity, long gameTime) {
        long startTime = System.nanoTime();

        Optional<WalkTarget> walkTargetOpt = entity.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
        if (walkTargetOpt.isEmpty()) return;

        WalkTarget walkTarget = walkTargetOpt.get();
        Vec3 targetPos = walkTarget.getTarget().currentPosition();

        double distToTargetSqr = entity.position().distanceToSqr(targetPos);
        if (distToTargetSqr < walkTarget.getCloseEnoughDist() * walkTarget.getCloseEnoughDist()) {
            entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.5));
            return;
        }
        if(entity.hasImpulse || entity.getDeltaMovement().lengthSqr() > 0.02) return;
        Vec3 entityPos = entity.position();
        Vec3 toTarget = targetPos.subtract(entityPos).normalize();

        Vec3 avoidance = Vec3.ZERO;
        Optional<List<BlockPos>> visibleBlocksOpt = entity.getBrain().getMemory(MemoryModuleInit.VISIBLE_BLOCKS.get());

        if (visibleBlocksOpt.isPresent()) {
            List<BlockPos> visibleBlocks = visibleBlocksOpt.get();
            if (visibleBlocks.size() > 500) {
                LostInTime.LOGGER.warn("FishImpulseMoveToTargetSink: Large visibleBlocks list size: {}", visibleBlocks.size());
            }

            for (BlockPos pos : visibleBlocks) {
                Vec3 blockCenter = Vec3.atCenterOf(pos);
                Vec3 fromBlock = entityPos.subtract(blockCenter);
                double distSqr = fromBlock.lengthSqr();
                if (distSqr < 25.0 && distSqr > 0.0001) {
                    avoidance = avoidance.add(fromBlock.scale(2.0 / distSqr));
                }
            }
        }

        Vec3 desiredVelocity = toTarget.add(avoidance).normalize();
        float speed = walkTarget.getSpeedModifier() * 0.05F;

        Vec3 currentDelta = entity.getDeltaMovement();
        Vec3 newDelta = currentDelta.add(desiredVelocity.scale(speed));

        entity.setDeltaMovement(newDelta);
        entity.hasImpulse = true;

        double d0 = newDelta.x;
        double d2 = newDelta.z;
        double d1 = newDelta.y;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        float targetYRot = (float)(Math.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
        float targetXRot = (float)(-(Math.atan2(d1, d3) * (double)(180F / (float)Math.PI)));

        entity.setYRot(Mth.rotLerp(entity.getYRot(), targetYRot, 10.0F));
        entity.setXRot(Mth.rotLerp(entity.getXRot(), targetXRot, 10.0F));
        entity.yBodyRot = entity.getYRot();
        entity.yHeadRot = entity.getYRot();

        long duration = System.nanoTime() - startTime;
        if (duration > 1_000_000) { // Log if takes longer than 1ms
            LostInTime.LOGGER.warn("FishImpulseMoveToTargetSink tick took {} ns", duration);
        }
    }
}
