package com.ren.lostintime.common.entity.util;

import net.minecraft.world.entity.LivingEntity;

public interface IStalker {

    int getStalkingTicks();

    void setStalkingTicks(int ticks);

    default int getRequiredStalkingTicks() {
        return 60;
    }

    boolean isReadyToStalk(LivingEntity target);

    default void tickStalking() {
        setStalkingTicks(getStalkingTicks() + 1);
    }

    default void resetStalking() {
        setStalkingTicks(0);
    }

}
