package com.ren.lostintime.common.entity.goal;

import com.ren.lostintime.common.entity.creatures.Dodo;
import com.ren.lostintime.common.entity.util.IPeckerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GoToPeckSpotGoal<T extends PathfinderMob & IPeckerEntity> extends Goal {

    private final T dodo;

    public GoToPeckSpotGoal(T pDodo) {
        this.dodo = pDodo;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (dodo.getPeckState() != IPeckerEntity.PeckState.MOVING)
            return false;
        var pos = dodo.getPeckTarget();

        if (pos == null){
            return false;
        }
        return true;
    }

    @Override
    public void tick() {
        // actually adding 0.5 to the position doesnt change anything cause it will just be converted back to a whole blocck position
        dodo.getNavigation().moveTo(
                dodo.getPeckTarget().getX(),
                dodo.getPeckTarget().getX(),
                dodo.getPeckTarget().getX(),
                1.0
        );
    }

    protected boolean hasReachedTarget(){
        return dodo.distanceToSqr(Vec3.atLowerCornerOf(dodo.getPeckTarget())) <= 1.2;
    }

    /*@Override
    public boolean canContinueToUse() {
        return dodo.getPeckState() == Dodo.PeckState.MOVING
                && dodo.getPeckTarget() != null
                && !hasReachedTarget();
    }

    @Override
    public void stop() {
        if (dodo.getPeckTarget() != null && hasReachedTarget()) {
            dodo.setPeckState(Dodo.PeckState.CIRCLING);
        } else {
            dodo.cancelPecking();
        }
    }*/

}
