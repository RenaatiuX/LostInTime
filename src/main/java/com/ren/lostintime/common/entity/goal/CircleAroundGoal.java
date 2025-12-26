package com.ren.lostintime.common.entity.goal;

import com.ren.lostintime.common.entity.creatures.Dodo;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CircleAroundGoal extends Goal {
    private final Dodo dodo;
    private int timer = 0;

    public CircleAroundGoal(Dodo pDodo) {
        this.dodo = pDodo;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return dodo.peckState == Dodo.PeckState.CIRCLING;
    }

    @Override
    public boolean canContinueToUse() {
        return dodo.peckState == Dodo.PeckState.CIRCLING;
    }

    @Override
    public void start() {
        timer = 0;
    }

    @Override
    public void tick() {
        timer++;

        double angle = timer * 0.25;
        double radius = 2.0;

        Vec3 pos = new Vec3(
                dodo.getX() + Math.cos(angle) * radius,
                dodo.getY(),
                dodo.getZ() + Math.sin(angle) * radius
        );

        dodo.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.0);

        if (timer > 80) {
            dodo.pickRandomSoil();
            if (dodo.peckTarget != null) {
                dodo.startPecking(dodo.peckTarget);
            } else {
                dodo.peckState = Dodo.PeckState.NONE;
                dodo.hasFruitTarget = false;
            }
        }
    }
}
