package com.ren.lostintime.common.entity.goal;

import com.ren.lostintime.common.entity.creatures.Dodo;
import com.ren.lostintime.common.entity.util.IPeckerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CircleAroundGoal {
    private final Dodo dodo;
    private int timer = 0;

    public CircleAroundGoal(Dodo pDodo) {
        this.dodo = pDodo;
        //this.setFlags(EnumSet.of(Flag.MOVE));
    }

    /*@Override
    public boolean canUse() {
        if (dodo.getPeckTarget() == null){
            return false;
        }
        return dodo.getPeckState() == Dodo.PeckState.CIRCLING;
    }

    @Override
    public boolean canContinueToUse() {
        if (dodo.getPeckTarget() == null){
            return false;
        }
        return dodo.getPeckState() == Dodo.PeckState.CIRCLING;
    }

    @Override
    public void start() {
        timer = 0;
    }

    @Override
    public void tick() {
        timer++;

        BlockPos center = dodo.getPeckTarget();

        double angle = timer * 0.25;
        double radius = 2.0;

        Vec3 pos = new Vec3(
                center.getX() + 0.5 + Math.cos(angle) * radius,
                center.getY(),
                center.getZ() + 0.5 + Math.sin(angle) * radius
        );

        double x = Math.cos(angle) * radius;
        double z = Math.sin(angle) * radius;

        var circlePos = center.offset((int)Math.round(x), 0, (int)Math.round(z));

        dodo.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.0);

        if (timer > 80) {
            if (dodo.getPeckTarget() != null && dodo.isValidSoil(dodo.level().getBlockState(dodo.getPeckTarget()))) {
                dodo.startPecking();
            } else {
                dodo.setPeckState(IPeckerEntity.PeckState.NONE);
            }
        }

    }*/
}
