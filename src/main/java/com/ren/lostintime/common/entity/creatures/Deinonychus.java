package com.ren.lostintime.common.entity.creatures;

import com.mojang.serialization.Dynamic;
import com.ren.lostintime.common.block.LITEggBlock;
import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.LITTamableAnimal;
import com.ren.lostintime.common.entity.ai.DeinonychusAi;
import com.ren.lostintime.common.entity.util.*;
import com.ren.lostintime.common.init.BlockInit;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
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

public class Deinonychus extends LITTamableAnimal implements GeoEntity, ISleepingEntity, IPackAnimal, IStalker, IBloodyEntity, IItemEater {

    private static final EntityDataAccessor<Boolean> IS_STALKING = SynchedEntityData.defineId(Deinonychus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_POUNCING = SynchedEntityData.defineId(Deinonychus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_THREATENING = SynchedEntityData.defineId(Deinonychus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> NEST_POS = SynchedEntityData.defineId(Deinonychus.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> BLOOD_TIMER = SynchedEntityData.defineId(Deinonychus.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_CHASING = SynchedEntityData.defineId(Deinonychus.class, EntityDataSerializers.BOOLEAN);

    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    public static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    public static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("sleep");
    public static final RawAnimation SIT = RawAnimation.begin().thenLoop("sit");
    public static final RawAnimation STALKING_IDLE = RawAnimation.begin().thenLoop("stalking_idle");
    public static final RawAnimation STALKING_WALK = RawAnimation.begin().thenLoop("stalking_walk");
    public static final RawAnimation POUNCE = RawAnimation.begin().thenLoop("pounce");
    public static final RawAnimation CHASE = RawAnimation.begin().thenLoop("chase");
    public static final RawAnimation SIT_THREATENING = RawAnimation.begin().thenLoop("sit_threatening");
    public static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    public static final RawAnimation EAT = RawAnimation.begin().thenPlay("eat");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int stalkingTicks = 0;
    private int huntCooldown = 0;
    public int intruderTime = 0;
    public boolean hasBeenSpotted = false;
    public boolean hasPounced = false;
    private int eatAnimationTick;

    public Deinonychus(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        GroundPathNavigation groundpathnavigation = (GroundPathNavigation) this.getNavigation();
        groundpathnavigation.setCanFloat(true);
        setPathfindingMalus(BlockPathTypes.WATER, 16.0F);
        setPathfindingMalus(BlockPathTypes.WATER_BORDER, 8.0F);
        setPathfindingMalus(BlockPathTypes.LAVA, -1.0F);
    }

    // ==========================================
    // ATTRIBUTES AND STATISTICS
    // ==========================================
    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    // ==========================================
    // NBT & SYNCHED DATA
    // ==========================================
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_STALKING, false);
        this.entityData.define(IS_POUNCING, false);
        this.entityData.define(IS_THREATENING, false);
        this.entityData.define(NEST_POS, Optional.empty());
        this.entityData.define(BLOOD_TIMER, 0);
        this.entityData.define(IS_CHASING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IsStalking", this.isStalking());
        getNestPos().ifPresent(pos -> pCompound.putLong("NestPos", pos.asLong()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setStalking(pCompound.getBoolean("IsStalking"));
        if (pCompound.contains("NestPos")) {
            setNestPos(BlockPos.of(pCompound.getLong("NestPos")));
        }
    }

    public boolean isStalking() {
        return this.entityData.get(IS_STALKING);
    }

    public void setStalking(boolean stalking) {
        this.entityData.set(IS_STALKING, stalking);
    }

    public boolean isPouncing() {
        return this.entityData.get(IS_POUNCING);
    }

    public void setPouncing(boolean pouncing) {
        this.entityData.set(IS_POUNCING, pouncing);
    }

    public void startHuntCooldown() {
        this.huntCooldown = 400;
    }

    public boolean canHunt() {
        return !this.isBaby() && this.getHunger() < 80.0F && this.huntCooldown <= 0;
    }

    public boolean isThreatening() {
        return this.entityData.get(IS_THREATENING);
    }

    public void setThreatening(boolean value) {
        this.entityData.set(IS_THREATENING, value);
    }

    public Optional<BlockPos> getNestPos() {
        return this.entityData.get(NEST_POS);
    }

    public void setNestPos(BlockPos pos) {
        this.entityData.set(NEST_POS, Optional.ofNullable(pos));
    }

    @Override
    public void setBloodTimer(int ticks) {
        this.entityData.set(BLOOD_TIMER, ticks);
    }

    @Override
    public int getBloodTimer() {
        return this.entityData.get(BLOOD_TIMER);
    }

    public boolean isEating() {
        return this.eatAnimationTick > 0;
    }

    public boolean isChasing() {
        return this.entityData.get(IS_CHASING);
    }

    // ==========================================
    // CAMOUFLAGE LOGIC
    // ==========================================
    @Override
    public double getVisibilityPercent(@Nullable Entity pLookingEntity) {
        double percent = super.getVisibilityPercent(pLookingEntity);
        if (this.isStalking()) {
            percent *= 0.2D;
        }
        if (this.isSleeping()) {
            percent *= 0.5D;
        }
        return percent;
    }

    // ==========================================
    // BRAIN
    // ==========================================
    @Override
    protected Brain.Provider<Deinonychus> brainProvider() {
        return Brain.provider(DeinonychusAi.MEMORY_TYPES, DeinonychusAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<Deinonychus> brain = this.brainProvider().makeBrain(pDynamic);
        DeinonychusAi.makeBrain(brain);
        return brain;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<Deinonychus> getBrain() {
        return (Brain<Deinonychus>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("deinonychusBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();

        DeinonychusAi.updateActivity(this);

        this.setSprinting(this.getMoveControl().hasWanted() && this.getMoveControl().getSpeedModifier() >= 1.2D);
        boolean hasTarget = this.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET);
        boolean isHurt = this.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY);
        if (hasTarget) {
            if (isHurt) {
                this.entityData.set(IS_CHASING, false);
            } else {
                LivingEntity target = this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
                if (this.hasPounced || this.hasBeenSpotted || !this.isReadyToStalk(target)) {
                    this.entityData.set(IS_CHASING, true);
                } else {
                    this.entityData.set(IS_CHASING, false);
                }
            }
        } else {
            this.entityData.set(IS_CHASING, false);
        }
        super.customServerAiStep();
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 10) {
            this.eatAnimationTick = 10;
        } else if (pId == 6) {
            super.handleEntityEvent(pId);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    // ==========================================
    // TICK & IA IMITATE SOUNDS
    // ==========================================
    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            Optional<BlockPos> nestOpt = this.getNestPos();
            if (nestOpt.isPresent()) {
                BlockPos nest = nestOpt.get();
                if (!this.level().getBlockState(nest).is(BlockInit.DEINONYCHUS_EGG.get())) {
                    this.setNestPos(null);
                    this.setThreatening(false);
                    this.intruderTime = 0;
                    this.setInSittingPose(false);
                    this.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                }
            }
            if (this.isSleeping() && this.isPregnant() && this.getGestationTicks() < 600) {
                this.setSleeping(false);
                this.sleepControllerOptional.ifPresent(c -> c.forceWakeUp(0));
            }
            if (this.isStalking() && !this.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
                this.setStalking(false);
                this.setStalkingTicks(0);
                this.hasBeenSpotted = false;
                this.hasPounced = false;
            }
            if (this.isAlive() && this.random.nextInt(50) == 0) {
                if (!this.isStalking() && !this.isSleeping()) {
                    Parrot.imitateNearbyMobs(this.level(), this);
                }
            }
            if (this.huntCooldown > 0) {
                this.huntCooldown--;
            }
            if (this.getBloodTimer() > 0) {
                this.setBloodTimer(this.getBloodTimer() - 1);
            }
        } else {
            this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.isPouncing() && this.onGround() && this.getDeltaMovement().y <= 0) {
                this.setPouncing(false);
            }
        }
    }

    @Override
    public void giveBirth() {
        if (this.level().isClientSide) return;

        BlockPos currentPos = this.blockPosition();
        BlockPos below = currentPos.below();

        if (this.level().getBlockState(below).is(BlockTags.DIRT) || this.level().getBlockState(below).is(Blocks.GRASS_BLOCK)) {
            int quantityEggs = this.random.nextInt(3) + 1;

            this.level().setBlock(currentPos, BlockInit.DEINONYCHUS_EGG.get().defaultBlockState()
                    .setValue(LITEggBlock.EGGS, quantityEggs), 3);

            this.setNestPos(currentPos);
            this.setPregnant(false);
            this.setPregnantProcess(0);
            this.setAge(this.getMatingCooldownTicks());

            this.playSound(SoundEvents.TURTLE_LAY_EGG, 1.0F, 1.0F);
            this.level().broadcastEntityEvent(this, (byte) 18);
        } else {
            // Retrasamos el parto para que la IA la lleve a la tierra
            this.setPregnantProcess(this.getPregnancyDuration() - 20);
        }
    }

    // ==========================================
    // DIET AND DOMESTICATION
    // ==========================================
    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.PORKCHOP);
    }

    public boolean isTameFood(ItemStack pStack) {
        return pStack.is(Items.COOKED_PORKCHOP);
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        if (!this.isTame() && this.isTameFood(itemstack)) {
            if (!this.level().isClientSide) {
                if (!pPlayer.getAbilities().instabuild) itemstack.shrink(1);

                if (this.random.nextInt(3) == 0) {
                    this.tame(pPlayer);
                    this.setInSittingPose(true);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (this.isTame() && this.isOwnedBy(pPlayer)) {
            if (!itemstack.getItem().isEdible() && !(itemstack.getItem() instanceof SpawnEggItem)) {
                if (!this.level().isClientSide) {
                    this.setInSittingPose(!this.isInSittingPose());
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        if (itemstack.getItem().isEdible() && itemstack.getItem().getFoodProperties() != null && itemstack.getItem().getFoodProperties().isMeat()) {
            if (this.getHunger() < this.getMaxHunger() || this.getHealth() < this.getMaxHealth()) {
                if (!this.level().isClientSide) {
                    if (!pPlayer.getAbilities().instabuild) itemstack.shrink(1);

                    this.setHunger(this.getHunger() + (this.getMaxHunger() * 0.25F));
                    this.heal(4.0F);
                    this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                    this.level().broadcastEntityEvent(this, (byte) 10);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else {
                if (!this.level().isClientSide) {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    // ==========================================
    // SLEEP
    // ==========================================
    @Override
    public SleepType getSleepType() {
        return SleepType.DIURNAL;
    }

    @Override
    public @Nullable SleepController<?> getSleepController() {
        return new SleepController<>(this);
    }

    // ==========================================
    // GECKOLIB
    // ==========================================
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 5, this::eatPredicate));
        controllers.add(new AnimationController<>(this, "attackController", 0, this::attackPredicate));
    }

    private <T extends Deinonychus> PlayState predicate(final AnimationState<T> event) {

        double velocity = this.getDeltaMovement().horizontalDistanceSqr();
        boolean isMoving = velocity > 1.0E-6;

        if (this.isPouncing() && !this.onGround()) {
            event.getController().setAnimation(POUNCE);
            return PlayState.CONTINUE;
        }

        if (isMoving) {
            if (this.isSprinting()) {
                if (this.isChasing()) {
                    event.getController().setAnimation(CHASE);
                } else {
                    event.getController().setAnimation(RUN);
                }
            } else if (this.isStalking()) {
                event.getController().setAnimation(STALKING_WALK);
                event.getController().setAnimationSpeed(0.8D);
            } else {
                event.getController().setAnimation(WALK);
                event.getController().setAnimationSpeed(2.0D);
            }
            return PlayState.CONTINUE;
        }

        if (this.isSleeping()) {
            event.getController().setAnimation(SLEEP);
            return PlayState.CONTINUE;
        }

        if (this.isStalking()) {
            event.getController().setAnimation(STALKING_IDLE);
            return PlayState.CONTINUE;
        }

        if (this.getNestPos().isPresent()) {
            BlockPos nest = this.getNestPos().get();
            if (this.level().getBlockState(nest).is(BlockInit.DEINONYCHUS_EGG.get())) {
                if (this.isThreatening()) {
                    event.getController().setAnimation(SIT_THREATENING);
                    return PlayState.CONTINUE;
                }
                if (this.distanceToSqr(Vec3.atCenterOf(nest)) < 1.5D) {
                    event.getController().setAnimation(SIT);
                    return PlayState.CONTINUE;
                }
            }
        }

        if (this.isInSittingPose()) {
            event.getController().setAnimation(SIT);
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    private <T extends Deinonychus> PlayState eatPredicate(final AnimationState<T> event) {
        //idk why i have to use this for it to work
        if (this.isEating()) {
            if (event.getController().getAnimationState().equals(AnimationController.State.STOPPED) ||
                    event.getController().getCurrentRawAnimation() != EAT) {
                event.getController().forceAnimationReset();
            }
            event.getController().setAnimation(EAT);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <E extends Deinonychus> PlayState attackPredicate(final AnimationState<E> event) {
        if (this.swinging && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(ATTACK);
            this.swinging = false;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // ==========================================
    // ALERT
    // ==========================================
    @Override
    public double getPackRadius() {
        return 24.0D;
    }

    @Override
    public boolean hasAlphaLeader() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean wasHurt = super.hurt(pSource, pAmount);

        if (wasHurt && pSource.getEntity() instanceof LivingEntity attacker) {
            if (!this.level().isClientSide) {
                this.alertPack(this, attacker);
            }
        }
        return wasHurt;
    }

    // ==========================================
    // STALK
    // ==========================================
    @Override
    public int getStalkingTicks() {
        return this.stalkingTicks;
    }

    @Override
    public void setStalkingTicks(int ticks) {
        this.stalkingTicks = ticks;
    }

    @Override
    public boolean isReadyToStalk(LivingEntity target) {
        if (this.isBaby()) return false;

        boolean isPrey = target.getType() == EntityType.PIG || target.getType() == EntityType.SHEEP;
        if (!isPrey) return false;

        double dist = this.distanceTo(target);
        return !this.isInSittingPose() && !this.isSleeping()
                && dist > 1.5F && dist < 16.0F
                && this.getSensing().hasLineOfSight(target);
    }

    // ==========================================
    // MOVE
    // ==========================================
    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isSleeping()) {
            this.setDeltaMovement(Vec3.ZERO);
            super.travel(Vec3.ZERO);
            return;
        }
        if (this.isInSittingPose()) {
            if (this.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
                this.setInSittingPose(false);
            } else {
                this.setDeltaMovement(Vec3.ZERO);
                super.travel(Vec3.ZERO);
                return;
            }
        }
        super.travel(pTravelVector);
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        boolean hurt = super.doHurtTarget(pEntity);
        if (hurt && pEntity instanceof LivingEntity target) {
            if (!this.onGround() && this.fallDistance > 0.1F) {
                target.hurt(this.damageSources().mobAttack(this), 2.0F);
                target.knockback(0.4F, this.getX() - target.getX(), this.getZ() - target.getZ());
            }
            this.setStalking(false);
            this.resetStalking();
        }
        return hurt;
    }

    @Override
    public void awardKillScore(Entity pKilled, int pScoreValue, DamageSource pSource) {
        super.awardKillScore(pKilled, pScoreValue, pSource);
        if (pKilled instanceof LivingEntity) {
            this.startHuntCooldown();
            this.heal(2.0F);
            this.setHunger(this.getHunger() + 20.0F);
            this.setBloodTimer(600);
            this.hasBeenSpotted = false;
            this.hasPounced = false;
        }
    }

    @Override
    public boolean isFoodItem(ItemStack stack) {
        return stack.isEdible() && stack.getFoodProperties(this) != null && stack.getFoodProperties(this).isMeat();
    }
}
