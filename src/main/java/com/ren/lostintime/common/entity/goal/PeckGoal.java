package com.ren.lostintime.common.entity.goal;

import com.ren.lostintime.common.entity.creatures.Dodo;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class PeckGoal {

    private final Dodo dodo;
    private final Level level;
    private int peckTime;

    public PeckGoal(Dodo pDodo) {
        this.dodo = pDodo;
        this.level = pDodo.level();
        //this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    /*@Override
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
    }*/
}
