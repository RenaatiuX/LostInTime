package com.ren.lostintime.common.entity.util;

public interface ISleepingEntity {

    boolean isSleeping();
    void setSleeping(boolean sleeping);
    default boolean canSleep() {
        return true;
    }
}
