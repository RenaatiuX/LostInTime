package com.ren.lostintime.common.entity.creatures;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.LITWaterAnimal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Helicoprion extends LITWaterAnimal implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("swim");
    private static final RawAnimation FLOP = RawAnimation.begin().thenLoop("flop");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public Helicoprion(EntityType<? extends LITAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.2D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return Brain.provider(ImmutableList.of(), ImmutableList.of());
    }

    /*@Override
    protected @NotNull Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<Helicoprion> brain = this.brainProvider().makeBrain(pDynamic);
        return brain;
    }*/

    @SuppressWarnings("unchecked")
    public Brain<Helicoprion> getBrain() {
        return (Brain<Helicoprion>) super.getBrain();
    }

    @Override
    public @Nullable SoundEvent getFlopSound() {
        return null;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean canSleep() {
        return false;
    }
}
