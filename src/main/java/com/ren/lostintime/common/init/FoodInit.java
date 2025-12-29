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
    public static final FoodProperties RAW_ANOMALOCARIS = new FoodProperties.Builder().nutrition(2)
            .saturationMod(0.3F).build();
    public static final FoodProperties COOKED_ANOMALOCARIS = new FoodProperties.Builder().nutrition(8)
            .saturationMod(0.6F).build();
    public static final FoodProperties RAW_BOTHRIOLEPIS = new FoodProperties.Builder().nutrition(1)
            .saturationMod(0.2F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 1200, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F).build();
    public static final FoodProperties COOKED_BOTHRIOLEPIS = new FoodProperties.Builder().nutrition(6)
            .saturationMod(0.6F).build();
    public static final FoodProperties RAW_DAEODON = new FoodProperties.Builder().nutrition(4)
            .saturationMod(0.3F).meat().build();
    public static final FoodProperties COOKED_DAEODON = new FoodProperties.Builder().nutrition(12)
            .saturationMod(0.9F).meat().build();
    public static final FoodProperties RAW_ENDOCERAS = new FoodProperties.Builder().nutrition(3)
            .saturationMod(0.3F).build();
    public static final FoodProperties COOKED_ENDOCERAS = new FoodProperties.Builder().nutrition(8)
            .saturationMod(0.6F).build();

    //FRUIT
    public static final FoodProperties MANGO = new FoodProperties.Builder().nutrition(4)
            .saturationMod(0.3F)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 0.1F).build();
}
