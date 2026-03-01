package com.ren.lostintime.common.entity.creatures;

import com.mojang.serialization.Dynamic;
import com.ren.lostintime.common.entity.LITTamableAnimal;
import com.ren.lostintime.common.entity.ai.DaeodonAi;
import com.ren.lostintime.common.entity.enums.DaeodonAggression;
import com.ren.lostintime.common.entity.util.SleepController;
import com.ren.lostintime.common.init.AttributeInit;
import com.ren.lostintime.common.init.MemoryModuleInit;
import com.ren.lostintime.common.util.BitUtils;
import com.ren.lostintime.datagen.server.LITTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
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

import java.util.Optional;

public class Daeodon extends LITTamableAnimal implements GeoEntity {

    private static final EntityDataAccessor<Float> DATA_HUNGER = SynchedEntityData.defineId(Daeodon.class, EntityDataSerializers.FLOAT);

    //ANIMATION
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("sleep");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation CHASE = RawAnimation.begin().thenLoop("chase");
    private static final RawAnimation SIT = RawAnimation.begin().thenLoop("sit");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation IMPOSING = RawAnimation.begin().thenPlay("imposing");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int temper;
    private DaeodonAggression currentAggression = DaeodonAggression.NONE;
    private int hungerTicks = 0;
    private int aggressionCooldown = 0;

    public Daeodon(EntityType<? extends LITTamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setMaxUpStep(1.0F);
        this.setHunger(getMaxHunger());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(AttributeInit.MAX_HUNGER.get(), 200D);
    }

    @Override
    public @Nullable SleepController<?> getSleepController() {
        return new SleepController<>(this, SleepController.SleepType.DIURNAL);
    }

    @Override
    protected @NotNull Brain.Provider<Daeodon> brainProvider() {
        return Brain.provider(DaeodonAi.MEMORY_MODULES, DaeodonAi.SENSOR_TYPES);
    }

    public float getHunger() {
        return this.entityData.get(DATA_HUNGER);
    }

    public void setHunger(float hunger) {
        this.entityData.set(DATA_HUNGER, Mth.clamp(hunger, 0.0F, getMaxHunger()));
    }

    public void addHunger(float hunger) {
        setHunger(getHunger() + hunger);
    }

    public float getMaxHunger() {
        return (float) this.getAttributeValue(AttributeInit.MAX_HUNGER.get());
    }

    public boolean isHungry() {
        return getHunger() < getMaxHunger() * 0.25F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HUNGER, 200.0F);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        return DaeodonAi.initBrain(this, brainProvider().makeBrain(pDynamic));
    }

    public boolean isInSittingPose() {
        return BitUtils.getBit(this.entityData.get(DATA_FLAGS_ID), 1);
    }

    public void setInSittingPose(boolean pSitting) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        this.entityData.set(DATA_FLAGS_ID, BitUtils.setBit(b0, 1, pSitting));
    }

    public boolean allowedToWander() {
        return BitUtils.getBit(this.entityData.get(DATA_FLAGS_ID), 2);
    }

    public void setAllowWandering(boolean wandering) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        this.entityData.set(DATA_FLAGS_ID, BitUtils.setBit(b0, 2, wandering));
    }

    public boolean shouldFollowOwner() {
        return BitUtils.getBit(this.entityData.get(DATA_FLAGS_ID), 3);
    }

    public void setShouldFollowOwner(boolean followOwner) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        this.entityData.set(DATA_FLAGS_ID, BitUtils.setBit(b0, 3, followOwner));
    }

    public void setOrderedToSit(boolean pOrdered) {
        this.getBrain().setMemory(MemoryModuleInit.SIT_ORDER.get(), Optional.ofNullable(pOrdered ? Unit.INSTANCE : null));
    }

    public boolean isOrderedToSit() {
        return this.getBrain().getMemory(MemoryModuleInit.SIT_ORDER.get()).isPresent();
    }

    public boolean isControlItem(ItemStack pStack) {
        return pStack.is(LITTags.Items.DAEODON_CONTROL_ITEMS);
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        boolean isOwner = this.isOwnedBy(pPlayer);

        if (this.level().isClientSide) {
            return InteractionResult.CONSUME;
        }

        if (this.isTame() && isOwner) {
            if (isControlItem(itemstack)) {
                if (!pPlayer.isShiftKeyDown()) {
                    if (!level().isClientSide) {
                        this.setOrderedToSit(!this.isOrderedToSit());
                    }
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget(null);

                    this.playSound(SoundEvents.WOLF_AMBIENT, 1.0F, 1.0F);
                }else {
                    if (!level().isClientSide) {
                        this.setAllowWandering(!this.allowedToWander());
                    }
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget(null);
                }
                return InteractionResult.SUCCESS;
            }

            if (itemstack.isEmpty() && !this.isOrderedToSit()) {
                pPlayer.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        } else if (!this.isTame()) {
            if (itemstack.isEmpty()) {
                pPlayer.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    protected void positionRider(Entity pPassenger, MoveFunction pCallback) {
        super.positionRider(pPassenger, pCallback);
        if (pPassenger instanceof Player player && !this.isTame()) {
            this.tameTick(player);
        }
    }

    private void tameTick(Player player) {
        if (this.random.nextInt(50) == 0) {
            this.temper += 5;

            if (this.temper >= 50 + this.random.nextInt(50)) {
                this.tame(player);
                this.level().broadcastEntityEvent(this, (byte) 7);
            } else {
                player.stopRiding();
                this.level().broadcastEntityEvent(this, (byte) 6);
                this.playSound(SoundEvents.HORSE_ANGRY, 1.0F, 1.0F);
                double pushBack = -0.5D;
                double launchHeight = 1.2D;

                Vec3 look = this.getLookAngle();
                Vec3 throwVector = new Vec3(look.x * pushBack, launchHeight, look.z * pushBack);

                player.setDeltaMovement(player.getDeltaMovement().add(throwVector));
                player.hurtMarked = true;
                player.hurt(this.damageSources().mobAttack(this), 2.0F);
            }
        }
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Temper", this.temper);
        pCompound.putFloat("Hunger", this.getHunger());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.temper = pCompound.getInt("Temper");
        this.setHunger(pCompound.getFloat("Hunger"));
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    //ANIMATION
    private <T extends Daeodon> PlayState predicate(final @NotNull AnimationState<T> event) {
        if (this.isSleeping()) {
            event.getController().setAnimation(SLEEP);
            event.getController().setAnimationSpeed(1.0D);
            return PlayState.CONTINUE;
        }
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
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

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean alwaysAccepts() {
        return super.alwaysAccepts();
    }

    @Override
    public boolean canAttack(LivingEntity pTarget) {
        return this.isSuitablePrey(pTarget) && super.canAttack(pTarget);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.restrictTo(this.blockPosition(), 15);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public boolean isSuitablePrey(LivingEntity entity) {
        if (!isWithinRestriction(entity.blockPosition()) && !isHungry())
            return false;
        double distSqr = distanceToSqr(entity);
        if (!isHungry() && this.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, entity) ? distSqr >= 100 : distSqr >= 25)
            return false;
        //prevent cannibalism
        return entity.getType() != this.getType() && entity.getHealth() < this.getHealth();
    }

    public Optional<? extends LivingEntity> findNearestValidAttackTarget() {
        return this.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty()).findClosest(this::isTargetable);
    }

    public boolean isTargetable(LivingEntity target) {
        return isSuitablePrey(target) && Sensor.isEntityAttackable(this, target);
    }
}
