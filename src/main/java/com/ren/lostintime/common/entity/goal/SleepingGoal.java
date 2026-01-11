package com.ren.lostintime.common.entity.goal;

import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.util.ISleepingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SleepingGoal<T extends LITAnimal & ISleepingEntity> extends Goal {

    private T entity;

    public SleepingGoal(T entity) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return isNight() && entity.canSleep() && !entity.isInWater() && !entity.isPassenger()
                && entity.getDeltaMovement().horizontalDistanceSqr() < 1.0E-6;
    }

    @Override
    public boolean canContinueToUse() {
        return isNight();
    }

    @Override
    public void start() {
        entity.setSleeping(true);
        entity.getNavigation().stop();
        entity.getMoveControl().setWantedPosition(entity.getX(), entity.getY(), entity.getZ(), 0.0D);
        entity.setDeltaMovement(0, 0, 0);
    }

    @Override
    public void stop() {
        entity.setSleeping(false);
    }

    protected boolean isNight() {
        long time = entity.level().getDayTime() % 24000L;
        return time >= 13000L && time <= 23000L;
    }
}
