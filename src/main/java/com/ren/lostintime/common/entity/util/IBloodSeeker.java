package com.ren.lostintime.common.entity.util;

import com.ren.lostintime.common.init.MobEffectInit;
import net.minecraft.world.entity.LivingEntity;

public interface IBloodSeeker {

    double getBloodScentRange();

    default boolean canSmellBlood(LivingEntity target) {
        return target.hasEffect(MobEffectInit.BLEEDING.get());
    }

}
