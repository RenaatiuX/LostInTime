package com.ren.lostintime.common.entity.util;

import net.minecraft.world.entity.Mob;

public class SleepController<T extends Mob & ISleepingEntity> {

    public enum SleepType {
        DIURNAL, // Active during day, sleeps at night
        NOCTURNAL // Active during night, sleeps at day
    }

    private final T entity;
    private final SleepType sleepType;
    private int forceWakeTicks = 0;
    private boolean initialized = false;

    public SleepController(T entity, SleepType sleepType) {
        this.entity = entity;
        this.sleepType = sleepType;
    }

    public void tick() {
        if (!initialized) {
            if (entity.isSleeping()) {
                setSleeping(true);
            }
            initialized = true;
        }

        if (forceWakeTicks > 0) {
            forceWakeTicks--;
            if (entity.isSleeping()) {
                setSleeping(false);
            }
            return;
        }

        if (shouldSleep() && entity.canSleep()) {
            if (!entity.isSleeping()) {
                setSleeping(true);
            }
        } else {
            if (entity.isSleeping()) {
                setSleeping(false);
            }
        }
    }

    private void setSleeping(boolean sleeping) {
        entity.setSleeping(sleeping);
        if (sleeping)
            entity.getNavigation().stop();
    }


    /**
     * Determines if the entity should currently be in a sleeping state based on the world time and force-wake status.
     *
     * @return true if the entity's sleep conditions are met, false otherwise.
     */
    public boolean shouldSleep() {
        if (forceWakeTicks > 0) return false;
        //if we are ridden by an entity we dont sleep
        if (!this.entity.getPassengers().isEmpty()) return false;
        //if we are riding an entity we also dont sleep
        if (this.entity.getVehicle() != null) return false;

        boolean isDay = entity.level().isDay();

        if (sleepType == SleepType.DIURNAL) {
            return !isDay;
        } else {
            return isDay;
        }
    }

    /**
     * Forces the entity to wake up and remain awake for a specified duration.
     *
     * @param ticks The number of game ticks the entity should stay awake.
     */
    public void forceWakeUp(int ticks) {
        this.forceWakeTicks = ticks;
        if (entity.isSleeping()) {
            setSleeping(false);
        }
    }

    public SleepType getSleepType() {
        return sleepType;
    }
}
