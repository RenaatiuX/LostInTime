package com.ren.lostintime.common.entity.goal;

import com.ren.lostintime.common.entity.creatures.Dodo;
import com.ren.lostintime.common.entity.util.IPeckerEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

/**
 * this goal should search for item entities and check if they are a fruit, when they are, it will notify the dod to move to a pecking position,
 * after reaching the pecking position it will consume one of the items, if the item is despawning, it will cancel everything
 *
 * @param <T>
 */
public class FindDroppedFruitGoal<T extends LivingEntity & IPeckerEntity> extends Goal {

    private final T dodo;
    private ItemEntity targetItem;


    /**
     * @param entity
     */
    public FindDroppedFruitGoal(T entity) {
        this.dodo = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!dodo.canPeck() || dodo.getPeckState() != IPeckerEntity.PeckState.NONE)
            return false;

        List<ItemEntity> items = dodo.level().getEntitiesOfClass(
                ItemEntity.class,
                dodo.getBoundingBox().inflate(6),
                dodo::isFruit);

        if (items.isEmpty()) return false;


        targetItem = items.stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(dodo)))
                .orElse(null);

        return true;
    }

    @Override
    public void start() {
        dodo.setPeckTarget(targetItem.blockPosition().below());
        dodo.setPeckState(IPeckerEntity.PeckState.MOVING);
    }


    // we reset the target item once we have finished or cancelled the pecking
    @Override
    public void stop() {
        super.stop();
        //ensures that the dodo will cancel packing if something happens to the target item entity
        if (targetItem == null || !targetItem.isAlive()) {
            dodo.cancelPecking();
        }
        targetItem = null;
    }

    @Override
    public void tick() {
        super.tick();
        if (dodo.getPeckState() == IPeckerEntity.PeckState.PECKING) {
            targetItem.getItem().shrink(1);
            targetItem = null;//resetting this goal as we did everything necessary for this goal
        }
    }

    @Override
    public boolean canContinueToUse() {
        //we can continue to use this goal as long as our target item is valid, so we stay active and then also consume the item
        return targetItem != null && targetItem.isAlive() && dodo.getPeckState() != IPeckerEntity.PeckState.NONE;
    }
}
