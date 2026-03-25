package com.ren.lostintime.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class InfectionEffect extends MobEffect {

    public InfectionEffect() {
        super(MobEffectCategory.HARMFUL, 0x4B6E29);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.tickCount % 80 == 0) {
            pLivingEntity.hurt(pLivingEntity.damageSources().magic(), 1.0F);
        }

        if (pLivingEntity instanceof Player player) {
            player.causeFoodExhaustion(0.02F * (pAmplifier + 1));
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

}
