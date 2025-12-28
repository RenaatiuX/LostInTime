package com.ren.lostintime.common.entity.goal;

import com.ren.lostintime.common.entity.creatures.Dodo;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PeckGoal extends Goal {

    private final Dodo dodo;
    private int peckTime;

    public PeckGoal(Dodo pDodo) {
        this.dodo = pDodo;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return dodo.peckState == Dodo.PeckState.PECKING;
    }

    @Override
    public boolean canContinueToUse() {
        return dodo.peckState == Dodo.PeckState.PECKING && peckTime > 0;
    }

    @Override
    public void start() {
        // because it is just executed every second tick
        peckTime = reducedTickDelay(40);
        dodo.startPecking();
    }

    @Override
    public void stop() {
        dodo.finishPecking();
    }

    @Override
    public void tick() {

        peckTime--;
    }
}
