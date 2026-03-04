package com.ren.lostintime.common.entity.util;

public interface ISleepingEntity {

    /**
     * Checks if the entity is currently sleeping.
     * @return true if the entity is sleeping, false otherwise.
     */
    boolean isSleeping();

    /**
     * Sets the sleeping state of the entity.
     * @param sleeping the new sleeping state.
     */
    void setSleeping(boolean sleeping);

    /**
     * Determines if the entity is currently capable of falling asleep.
     * this allows for additional conditions besides {@link SleepType}
     * @return true if the entity can sleep, false otherwise.
     */
    default boolean canSleep() {
        return true;
    }

    /**
     * Gets the sleep type configuration for this entity.
     *
     * @return the {@link SleepType} defining when this entity sleeps.
     */
    SleepType getSleepType();
}
