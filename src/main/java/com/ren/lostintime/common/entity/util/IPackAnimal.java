package com.ren.lostintime.common.entity.util;

import com.ren.lostintime.common.entity.creatures.Deinonychus;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public interface IPackAnimal {

    //radio to ask for help
    double getPackRadius();

    //perhaps having a leader
    boolean hasAlphaLeader();

    //alert the pack
    default void alertPack(LivingEntity victim, LivingEntity attacker) {
        if (victim.level().isClientSide) return;

        victim.level().getEntitiesOfClass(victim.getClass(), victim.getBoundingBox().inflate(getPackRadius()))
                .forEach(ally -> {
                    if (ally != victim && ally instanceof Mob mobAlly) {
                        if (mobAlly instanceof Deinonychus deinonychus) {
                            deinonychus.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, attacker);
                        }
                    }
                });
    }
}
