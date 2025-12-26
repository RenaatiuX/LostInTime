package com.ren.lostintime.common.entity.goal;

import com.ren.lostintime.common.entity.creatures.Dodo;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GoToPeckSpotGoal extends Goal {

    private final Dodo dodo;

    public GoToPeckSpotGoal(Dodo pDodo) {
        this.dodo = pDodo;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return dodo.peckState == Dodo.PeckState.MOVING && dodo.peckTarget != null;
    }

    @Override
    public void tick() {
        dodo.getNavigation().moveTo(
                dodo.peckTarget.getX() + 0.5,
                dodo.peckTarget.getY(),
                dodo.peckTarget.getZ() + 0.5,
                1.0
        );
    }

    @Override
    public boolean canContinueToUse() {
        return dodo.peckState == Dodo.PeckState.MOVING
                && dodo.peckTarget != null
                && dodo.distanceToSqr(Vec3.atCenterOf(dodo.peckTarget)) > 1.2;
    }

    @Override
    public void stop() {
        if (dodo.peckTarget != null &&
                dodo.distanceToSqr(Vec3.atCenterOf(dodo.peckTarget)) <= 1.2) {
            dodo.peckState = Dodo.PeckState.CIRCLING;
        } else {
            dodo.peckState = Dodo.PeckState.NONE;
            dodo.peckTarget = null;
            dodo.hasFruitTarget = false;
        }
    }

}
