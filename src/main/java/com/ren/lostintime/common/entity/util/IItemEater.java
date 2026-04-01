package com.ren.lostintime.common.entity.util;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface IItemEater {

    boolean isFoodItem(ItemStack stack);

    default void consumeItem(Mob eater, ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();

        ItemStack visualStack = stack.copy();

        stack.shrink(1);
        if (stack.isEmpty()) {
            itemEntity.discard();
        }

        eater.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
        if (eater.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, visualStack),
                    eater.getX(), eater.getY() + (eater.getBbHeight() / 2.0), eater.getZ(),
                    10, 0.2D, 0.2D, 0.2D, 0.05D);
        }
        eater.heal(2.0F);
    }
}
