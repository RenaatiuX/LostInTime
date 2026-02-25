package com.ren.lostintime.common.entity.ai;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.MemoryModuleInit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VisibleBlockSensor extends Sensor<LivingEntity> {

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryModuleInit.VISIBLE_BLOCKS.get());
    }

    @Override
    protected void doTick(ServerLevel level, LivingEntity entity) {
        long start = System.nanoTime();

        List<BlockPos> visibleBlocks = new ArrayList<>();
        AABB box = entity.getBoundingBox().inflate(16);
        BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
        Vec3 eyePos = entity.getEyePosition();

        int count = 0;
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            count++;
            BlockState state = level.getBlockState(pos);
            if (!state.getCollisionShape(level, pos).isEmpty()) {
                if (hasLineOfSight(level, eyePos, pos, entity)) {
                    visibleBlocks.add(pos.immutable());
                }
            }
        }

        entity.getBrain().setMemory(MemoryModuleInit.VISIBLE_BLOCKS.get(), visibleBlocks);

        long end = System.nanoTime();
        if (end - start > 5_000_000) { // Log if takes longer than 5ms
            LostInTime.LOGGER.warn("VisibleBlockSensor scanned {} blocks and took {} ns", count, (end - start));
        }
    }

    private boolean hasLineOfSight(ServerLevel level, Vec3 eyePos, BlockPos targetPos, LivingEntity entity) {
        Vec3 targetCenter = Vec3.atCenterOf(targetPos);
        BlockHitResult result = level.clip(new ClipContext(eyePos, targetCenter, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        return result.getType() == HitResult.Type.BLOCK && result.getBlockPos().equals(targetPos);
    }
}