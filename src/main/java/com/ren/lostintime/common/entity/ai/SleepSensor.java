package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableSet;
import com.ren.lostintime.common.init.MemoryModuleInit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class SleepSensor extends Sensor<LivingEntity> {

    @Override
    protected void doTick(ServerLevel level, LivingEntity entity) {
        if (entity.isSleeping()) {
            entity.getBrain().setMemory(MemoryModuleInit.IS_SLEEPING.get(), Unit.INSTANCE);
        } else {
            entity.getBrain().eraseMemory(MemoryModuleInit.IS_SLEEPING.get());
        }
    }

    @Override
    public @NotNull Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleInit.IS_SLEEPING.get());
    }
}
