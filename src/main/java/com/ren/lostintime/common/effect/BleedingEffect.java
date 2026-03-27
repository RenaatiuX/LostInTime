package com.ren.lostintime.common.effect;

import com.ren.lostintime.common.init.ParticlesInit;
import com.ren.lostintime.datagen.server.loot.LITDamageTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BleedingEffect extends MobEffect {

    public BleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0x990000);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.isAlive()) {
            if (!pLivingEntity.level().isClientSide()) {
                float damage = 1.0F + (float) pAmplifier * 0.5F;
                DamageSource damageSource = LITDamageTypes.causeBleedingDamage(pLivingEntity.level());
                pLivingEntity.hurt(damageSource, damage);
            }

            if (pLivingEntity.level().isClientSide()) {
                int amountOfBlood = 1 + pAmplifier;

                for (int i = 0; i < amountOfBlood; i++) {
                    double x = pLivingEntity.getRandomX(0.6D);
                    double y = pLivingEntity.getY() + (pLivingEntity.getBbHeight() / 2.0D) + (pLivingEntity.getRandom().nextDouble() * 0.4D);
                    double z = pLivingEntity.getRandomZ(0.6D);

                    if (pLivingEntity.isInWater()) {
                        pLivingEntity.level().addParticle(ParticlesInit.BLEEDING_UNDERWATER.get(), x, y, z, 0, 0, 0);
                    } else {
                        pLivingEntity.level().addParticle(ParticlesInit.BLEEDING_STREAM.get(), x, y, z, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        int k;
        if (pAmplifier > 1) {
            k = 10;
        } else if (pAmplifier == 1) {
            k = 20;
        } else {
            k = 40;
        }
        return pDuration % k == 0;
    }
}
