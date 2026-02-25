package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableMap;
import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.util.IEggLayerAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.material.Fluids;

import java.util.Optional;

public class FindWaterEggLayingSpot<E extends Animal & IEggLayerAnimal> extends Behavior<E> {
    private final int range;
    private final float speedModifier;

    public FindWaterEggLayingSpot(int range, float speedModifier) {
        super(ImmutableMap.of(
                MemoryModuleType.IS_PREGNANT, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT
        ));
        this.range = range;
        this.speedModifier = speedModifier;
    }

    @Override
    protected void start(ServerLevel level, E entity, long gameTime) {
        findEggLayingSpot(level, entity).ifPresent(pos -> {
            entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, speedModifier, 0));
        });
    }

    private Optional<BlockPos> findEggLayingSpot(ServerLevel level, E entity) {
        BlockPos entityPos = entity.blockPosition();

        for (int i = 0; i < 30; i++) {
            BlockPos randomPos = entityPos.offset(
                    level.random.nextInt(range * 2 + 1) - range,
                    level.random.nextInt(range * 2 + 1) - range,
                    level.random.nextInt(range * 2 + 1) - range
            );

            if (entity.canLayEgg(level, entity, randomPos)) {
                return Optional.of(randomPos);
            }
        }

        return Optional.empty();
    }
}