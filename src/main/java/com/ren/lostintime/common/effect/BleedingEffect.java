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
            float damage = 1.0F + (float) pAmplifier * 0.5F;
            DamageSource damageSource = LITDamageTypes.causeBleedingDamage(pLivingEntity.level());
            pLivingEntity.hurt(damageSource, damage);

            if (!pLivingEntity.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) pLivingEntity.level();

                double x = pLivingEntity.getX();
                double y = pLivingEntity.getY() + (pLivingEntity.getBbHeight() / 2.0D);
                double z = pLivingEntity.getZ();

                serverLevel.sendParticles(ParticlesInit.BLEEDING_STREAM.get(), x, y, z,
                        5,
                        0.2D, 0.2D, 0.2D,
                        0.05D);

                serverLevel.sendParticles(ParticlesInit.BLEEDING_DROPLET.get(), x, y, z,
                        10,
                        0.3D, 0.3D, 0.3D,
                        0.01D);
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
