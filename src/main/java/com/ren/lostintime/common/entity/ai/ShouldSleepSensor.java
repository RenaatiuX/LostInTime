package com.ren.lostintime.common.entity.ai;

import com.google.common.collect.ImmutableSet;
import com.ren.lostintime.common.entity.util.ISleepingEntity;
import com.ren.lostintime.common.entity.util.SleepType;
import com.ren.lostintime.common.init.MemoryModuleInit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.Set;

public class ShouldSleepSensor<E extends LivingEntity & ISleepingEntity> extends Sensor<E> {



    @Override
    protected void doTick(ServerLevel pLevel, E pEntity) {
        if (pEntity.canSleep() && shouldSleep(pEntity)){
            pEntity.getBrain().setMemory(MemoryModuleInit.SHOULD_SLEEP.get(), Unit.INSTANCE);
        }else
            pEntity.getBrain().eraseMemory(MemoryModuleInit.SHOULD_SLEEP.get());
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleInit.SHOULD_SLEEP.get());
    }

    public boolean shouldSleep(E entity) {
        if (!entity.getPassengers().isEmpty()) return false;
        //if we are riding an entity we also dont sleep
        if (entity.getVehicle() != null) return false;

        boolean isDay = entity.level().isDay();

        if (entity.getSleepType() == SleepType.DIURNAL) {
            return !isDay;
        } else {
            return isDay;
        }
    }
}
