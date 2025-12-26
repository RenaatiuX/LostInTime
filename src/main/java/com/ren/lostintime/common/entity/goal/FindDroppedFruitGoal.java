package com.ren.lostintime.common.entity.goal;

import com.ren.lostintime.common.entity.creatures.Dodo;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class FindDroppedFruitGoal extends Goal {

    private final Dodo dodo;
    private ItemEntity targetItem;

    public FindDroppedFruitGoal(Dodo pDodo) {
        this.dodo = pDodo;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (dodo.peckCooldown > 0 || dodo.peckState != Dodo.PeckState.NONE)
            return false;

        List<ItemEntity> items = dodo.level().getEntitiesOfClass(
                ItemEntity.class,
                dodo.getBoundingBox().inflate(6),
                e -> dodo.isFruit(e.getItem())
        );

        if (items.isEmpty()) return false;

        targetItem = items.stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(dodo)))
                .orElse(null);

        return targetItem != null;
    }

    @Override
    public void start() {
        dodo.peckTarget = targetItem.blockPosition().below();
        dodo.hasFruitTarget = true;
        dodo.peckState = Dodo.PeckState.MOVING;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}
