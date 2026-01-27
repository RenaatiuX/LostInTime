package com.ren.lostintime.common.entity.goal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class MoveToEntityGoal<T extends Entity> extends Goal {


    protected final PathfinderMob mob;
    public final double speedModifier;
    public final double searchRange;

    public final Class<T> searchClass;

    /**
     * Controls task execution delay
     */
    protected int nextStartTick;
    protected int tryTicks;
    private int maxStayTicks;
    protected T target;
    private boolean reachedTarget;


    public MoveToEntityGoal(PathfinderMob mob, double speedModifier, double searchRange, Class<T> searchClass) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.searchRange = searchRange;
        this.searchClass = searchClass;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            return this.findEntity();
        }
    }

    @Override
    public void start() {
        this.moveMobToTarget();
        this.tryTicks = 0;
        this.maxStayTicks = this.mob.getRandom().nextInt(this.mob.getRandom().nextInt(1200) + 1200) + 1200;
    }

    @Override
    public boolean canContinueToUse() {
        return this.tryTicks >= -this.maxStayTicks && this.tryTicks <= 1200 && this.target != null && this.validEntity(this.target);
    }

    public void tick() {
        this.mob.getLookControl().setLookAt(this.target, 10.0F, (float)this.mob.getMaxHeadXRot());
        if (target.distanceToSqr(this.mob.position()) > acceptedDistance() * acceptedDistance()) {
            this.reachedTarget = false;
            ++this.tryTicks;
            if (this.shouldRecalculatePath()) {
                this.moveMobToTarget();
            }
        } else {
            this.reachedTarget = true;
            --this.tryTicks;
        }

    }

    public double acceptedDistance() {
        return 1.0D;
    }

    public boolean shouldRecalculatePath() {
        return this.tryTicks % 40 == 0;
    }

    protected boolean isReachedTarget() {
        return this.reachedTarget;
    }

    protected int nextStartTick(PathfinderMob pCreature) {
        return reducedTickDelay(200 + pCreature.getRandom().nextInt(200));
    }

    protected void moveMobToTarget() {
        this.mob.getNavigation().moveTo(this.target, this.speedModifier);
    }

    protected boolean findEntity() {
        List<T> entities = mob.level().getEntitiesOfClass(
                searchClass,
                mob.getBoundingBox().inflate(searchRange), this::validEntity
        );
        var nearest = findNearest(mob, entities);
        if (nearest != null){
            this.target = nearest;
            return true;
        }
        return false;
    }

    @Nullable
    private static <T extends Entity> T findNearest(Entity origin, List<T> entities) {
        double bestDistSq = Double.MAX_VALUE;
        T nearest = null;

        double ox = origin.getX();
        double oy = origin.getY();
        double oz = origin.getZ();

        for (T entity : entities) {
            double dx = entity.getX() - ox;
            double dy = entity.getY() - oy;
            double dz = entity.getZ() - oz;

            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                nearest = entity;
            }
        }

        return nearest;
    }

    protected boolean validEntity(T entity){
        return true;
    }
}
