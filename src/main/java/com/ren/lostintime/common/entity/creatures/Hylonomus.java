package com.ren.lostintime.common.entity.creatures;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class Hylonomus extends Animal implements GeoEntity {

    private static final EntityDataAccessor<Boolean> DATA_SLEEPING =
            SynchedEntityData.defineId(Hylonomus.class, EntityDataSerializers.BOOLEAN);

    //ANIMATION
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("sleep");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int panicTicks = 0;

    public Hylonomus(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.6D) {
            @Override
            public boolean canUse() {
                return super.canUse() && !Hylonomus.this.isSleeping();
            }
        });
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return super.canUse() && !Hylonomus.this.isSleeping();
            }
        });
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !Hylonomus.this.isSleeping();
            }
        });
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && !Hylonomus.this.isSleeping();
            }
        });
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SLEEPING, false);
    }

    public boolean isSleeping() {
        return this.entityData.get(DATA_SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        this.entityData.set(DATA_SLEEPING, sleeping);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    //MOVEMENT
    @Override
    public void customServerAiStep() {
        if (this.isSleeping()) {
            this.getNavigation().stop();
            this.setSprinting(false);
            return;
        }
        super.customServerAiStep();
        this.setSprinting(this.getMoveControl().hasWanted() &&
                this.getMoveControl().getSpeedModifier() >= 1.5D);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean hurt = super.hurt(pSource, pAmount);
        if (hurt) {
            int ticks = 100 + this.random.nextInt(100);
            this.panicTicks = ticks;
            List<? extends Hylonomus> hylonomuses = this.level().getEntitiesOfClass(Hylonomus.class,
                    this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D));
            for (Hylonomus hylonomus : hylonomuses) {
                hylonomus.panicTicks = ticks;
            }
        }
        return hurt;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (panicTicks >= 0) {
                panicTicks--;
            }
            if (panicTicks == 0 && this.getLastHurtByMob() != null) {
                this.setLastHurtByMob(null);
            }

            boolean shouldSleep = this.level().isNight() && !this.isInPanic();

            if (shouldSleep != this.isSleeping()) {
                this.setSleeping(shouldSleep);
                System.out.println("Hylonomus " + this.getId() + " sleeping = " + shouldSleep);
            }
        }
    }

    private boolean isInPanic() {
        return panicTicks > 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    //ANIMATION
    private <T extends Hylonomus> PlayState predicate(final @NotNull AnimationState<T> event) {
        if (this.isSleeping()) {
            event.getController().setAnimation(SLEEP);
            event.getController().setAnimationSpeed(1.0D);
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            if (this.isSprinting()) {
                event.getController().setAnimation(RUN);
                event.getController().setAnimationSpeed(2.3D);
            } else {
                event.getController().setAnimation(WALK);
                event.getController().setAnimationSpeed(1.5D);
            }
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(IDLE);
        event.getController().setAnimationSpeed(1.0D);
        return PlayState.CONTINUE;
    }
}
