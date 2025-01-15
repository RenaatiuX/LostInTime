package com.ren.lostintime.common.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class FoodInit {

    //MEAT
    public static final FoodProperties RAW_DODO = new FoodProperties.Builder().nutrition(3)
            .saturationMod(0.3F).meat().build();
    public static final FoodProperties COOKED_DODO = new FoodProperties.Builder().nutrition(8)
            .saturationMod(0.6F).meat().build();

    //FRUIT
    public static final FoodProperties MANGO = new FoodProperties.Builder().nutrition(4)
            .saturationMod(0.3F)
            .effect(() ->new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 0.1F).build();
}
