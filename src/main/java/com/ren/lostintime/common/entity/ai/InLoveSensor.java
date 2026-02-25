package com.ren.lostintime.common.entity.ai;

import com.ren.lostintime.common.init.MemoryModuleInit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.animal.Animal;

import java.util.Set;

public class InLoveSensor extends Sensor<Animal> {

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryModuleInit.IN_LOVE.get());
    }

    @Override
    protected void doTick(ServerLevel pLevel, Animal pEntity) {
        if (pEntity.isInLove()) {
            pEntity.getBrain().setMemory(MemoryModuleInit.IN_LOVE.get(), Unit.INSTANCE);
        } else {
            pEntity.getBrain().eraseMemory(MemoryModuleInit.IN_LOVE.get());
        }
    }
}