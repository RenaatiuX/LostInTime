package com.ren.lostintime.common.entity.creatures;

import com.ren.lostintime.common.entity.AbstractBaseFish;
import com.ren.lostintime.common.entity.AgeableWaterAnimal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class Anomalocaris extends AbstractBaseFish implements GeoEntity, NeutralMob {

    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(Anomalocaris.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(Anomalocaris.class,
            EntityDataSerializers.INT);

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable
    private UUID persistentAngerTarget;

    protected static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
    protected static final RawAnimation OUT_OF_WATER = RawAnimation.begin().thenLoop("move.out_of_water");
    protected static final RawAnimation GRABBED = RawAnimation.begin().thenPlay("misc.grabbed");
    public static final RawAnimation GRAB = RawAnimation.begin().thenLoop("misc.grab");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public Anomalocaris(EntityType<? extends WaterAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.8F)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 12);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_ATTACKING, false);
        this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IsAttacking", this.isAttacking());
        this.addPersistentAngerSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAttacking(pCompound.getBoolean("IsAttacking"));
        this.readPersistentAngerSaveData(this.level(), pCompound);
    }

    @Override
    public @Nullable AgeableWaterAnimal getBreedOffspring(ServerLevel pLevel, AgeableWaterAnimal pOtherParent) {
        return null;
    }

    @Override
    public @Nullable SoundEvent getFlopSound() {
        return SoundEvents.COD_FLOP;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        boolean flag = pEntity.hurt(this.damageSources().mobAttack(this),
                (float) ((int) this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (flag) {
            this.doEnchantDamageEffects(this, pEntity);
        }
        return flag;
    }

    public boolean isAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }

    public void setAttacking(boolean isAttacking) {
        this.entityData.set(IS_ATTACKING, isAttacking);
    }

    @Override
    public boolean canBeLeashed(Player pPlayer) {
        return true;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource pDamageSource) {
        return super.getHurtSound(pDamageSource);
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return super.getDeathSound();
    }

    @Override
    public boolean canAttack(LivingEntity pTarget) {
        return super.canAttack(pTarget) && !this.isBaby();
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int pRemainingPersistentAngerTime) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, pRemainingPersistentAngerTime);
    }

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID pPersistentAngerTarget) {
        this.persistentAngerTarget = pPersistentAngerTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public ItemStack getBucketItemStack() {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "moveController", 5, this::movePredicate));
    }

    protected <E extends Anomalocaris> PlayState movePredicate(final AnimationState<E> event) {
        if (this.isInWater()) {
            event.getController().setAnimation(SWIM);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
