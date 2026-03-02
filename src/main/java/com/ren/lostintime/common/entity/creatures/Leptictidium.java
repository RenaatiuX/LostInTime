package com.ren.lostintime.common.entity.creatures;

import com.ren.lostintime.common.entity.LITTamableAnimal;
import com.ren.lostintime.common.entity.util.SleepController;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
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

import java.util.EnumSet;

public class Leptictidium extends LITTamableAnimal implements GeoEntity {

    private static final EntityDataAccessor<Boolean> DATA_TRUSTED = SynchedEntityData.defineId(Leptictidium.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_BEGGING = SynchedEntityData.defineId(Leptictidium.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DANCING = SynchedEntityData.defineId(Leptictidium.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SITTING = SynchedEntityData.defineId(Leptictidium.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("sleep");
    private static final RawAnimation SIT = RawAnimation.begin().thenLoop("sit");
    private static final RawAnimation BEG = RawAnimation.begin().thenLoop("beg");
    private static final RawAnimation DANCE = RawAnimation.begin().thenLoop("dance");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int panicTicks = 0;
    private BlockPos jukeboxPos;

    public Leptictidium(EntityType<? extends LITTamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LeptictidiumSitGoal(this));
        this.goalSelector.addGoal(2, new LeptictidiumFollowOwnerGoal(this, 1.2D, 10.0F, 2.0F, false)); // Sigue al dueño
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 12.0F, 1.2D, 1.5D,
                (entity) -> !this.isTrusted()));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LeptictidiumTemptGoal(this, 1.0D, Ingredient.of(Items.PUMPKIN_SEEDS), false));
        this.goalSelector.addGoal(7, new FollowParentGoal(this, 1.1D) {
            @Override
            public boolean canUse() {
                return super.canUse() && !Leptictidium.this.isTame();
            }
        });
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TRUSTED, false);
        this.entityData.define(DATA_BEGGING, false);
        this.entityData.define(DATA_DANCING, false);
        this.entityData.define(DATA_SITTING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Trusted", this.isTrusted());
        pCompound.putBoolean("Sitting", this.isOrderedToSit());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setTrusted(pCompound.getBoolean("Trusted"));
        this.setOrderedToSit(pCompound.getBoolean("Sitting"));
    }

    public boolean isTrusted() {
        return this.entityData.get(DATA_TRUSTED);
    }

    public void setTrusted(boolean trusted) {
        this.entityData.set(DATA_TRUSTED, trusted);
    }

    public boolean isBegging() {
        return this.entityData.get(DATA_BEGGING);
    }

    public void setBegging(boolean begging) {
        this.entityData.set(DATA_BEGGING, begging);
    }

    public boolean isOrderedToSit() {
        return this.entityData.get(DATA_SITTING);
    }

    public void setOrderedToSit(boolean pOrderedToSit) {
        this.entityData.set(DATA_SITTING, pOrderedToSit);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.setSprinting(this.getMoveControl().hasWanted() &&
                this.getMoveControl().getSpeedModifier() >= 1.5D);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (panicTicks >= 0) {
                panicTicks--;
            }
            if (panicTicks == 0 && this.getLastHurtByMob() != null) {
                this.setLastHurtByMob(null);
            }
            if (this.entityData.get(DATA_DANCING)) {
                if (this.jukeboxPos == null || !this.jukeboxPos.closerToCenterThan(this.position(), 3.46D) || !this.level().getBlockState(this.jukeboxPos).is(net.minecraft.world.level.block.Blocks.JUKEBOX)) {
                    this.entityData.set(DATA_DANCING, false);
                    this.jukeboxPos = null;
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean lastHurt = super.hurt(pSource, pAmount);
        if (lastHurt) {
            this.panicTicks = 100 + this.random.nextInt(100);
        }
        return lastHurt;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        Leptictidium baby = EntityInit.LEPTICTIDIUM.get().create(pLevel);
        if (baby != null) {
            baby.setTrusted(true);
        }
        return baby;
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        if (pStack.getItem().isEdible()) {
            return !pStack.is(Items.ROTTEN_FLESH) && !pStack.is(Items.FERMENTED_SPIDER_EYE);
        }
        return pStack.is(Items.PUMPKIN_SEEDS);
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.level().isClientSide) {
            boolean flag = this.isOwnedBy(pPlayer) || this.isTame() || (itemstack.is(Items.SPIDER_EYE) && !this.isTame() && this.isBaby());
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (this.isTame()) {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.heal((float) itemstack.getItem().getFoodProperties().getNutrition());
                    return InteractionResult.SUCCESS;
                }
                if (this.isOwnedBy(pPlayer) && !this.isFood(itemstack)) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.jumping = false;
                    this.navigation.stop();
                    return InteractionResult.SUCCESS;
                }
            } else if (itemstack.is(Items.SPIDER_EYE) && this.isBaby()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                if (this.random.nextInt(3) == 0) {
                    this.tame(pPlayer);
                    this.setOrderedToSit(true);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
                return InteractionResult.SUCCESS;
            } else if (this.isBaby() && this.isFood(itemstack)) {
                this.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(-this.getAge()), true);
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
            return super.mobInteract(pPlayer, pHand);
        }
    }

    @Override
    public @Nullable SleepController<?> getSleepController() {
        return new SleepController<>(this, SleepController.SleepType.DIURNAL);
    }

    //DANCE
    @Override
    public void setRecordPlayingNearby(BlockPos pJukebox, boolean pPartyParrot) {
        if (this.isTame()) {
            this.entityData.set(DATA_DANCING, pPartyParrot);
            this.jukeboxPos = pJukebox;
        } else {
            this.entityData.set(DATA_DANCING, false);
            this.jukeboxPos = null;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <T extends Leptictidium> PlayState predicate(final @NotNull AnimationState<T> event) {
        if (this.isSleeping()) {
            event.getController().setAnimation(SLEEP);
            return PlayState.CONTINUE;
        }
        if (this.entityData.get(DATA_DANCING)) {
            event.getController().setAnimation(DANCE);
            return PlayState.CONTINUE;
        }
        if (this.isOrderedToSit()) {
            event.getController().setAnimation(SIT);
            return PlayState.CONTINUE;
        }

        if (this.isBegging() && !(this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6)) {
            event.getController().setAnimation(BEG);
            return PlayState.CONTINUE;
        }

        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
            if (this.isSprinting()) {
                event.getController().setAnimation(RUN);
                event.getController().setAnimationSpeed(1.5D);
            } else {
                event.getController().setAnimation(WALK);
                event.getController().setAnimationSpeed(1.0D);
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

    class LeptictidiumTemptGoal extends TemptGoal {

        public LeptictidiumTemptGoal(Leptictidium pMob, double pSpeedModifier, Ingredient pItems, boolean pCanScare) {
            super(pMob, pSpeedModifier, pItems, pCanScare);
        }

        @Override
        public void tick() {
            super.tick();
            if (this.mob.distanceToSqr(this.player) < 6.25D && this.mob.getNavigation().isDone()) {
                Leptictidium.this.setBegging(true);
            } else {
                Leptictidium.this.setBegging(false);
            }
        }

        @Override
        public void stop() {
            super.stop();
            Leptictidium.this.setBegging(false);
        }
    }

    static class LeptictidiumSitGoal extends Goal {

        private final Leptictidium leptictidium;

        public LeptictidiumSitGoal(Leptictidium pTamable) {
            this.leptictidium = pTamable;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canContinueToUse() {
            return this.leptictidium.isOrderedToSit();
        }

        @Override
        public boolean canUse() {
            if (!this.leptictidium.isTame()) {
                return false;
            } else if (this.leptictidium.isInWaterOrBubble()) {
                return false;
            } else if (!this.leptictidium.onGround()) {
                return false;
            } else {
                LivingEntity livingentity = this.leptictidium.getOwner();
                if (livingentity == null) {
                    return true;
                } else {
                    return this.leptictidium.distanceToSqr(livingentity) < 144.0D && livingentity.getLastHurtByMob() != null ? false : this.leptictidium.isOrderedToSit();
                }
            }
        }

        @Override
        public void start() {
            this.leptictidium.getNavigation().stop();
        }
    }

    static class LeptictidiumFollowOwnerGoal extends Goal {

        private final Leptictidium tamable;
        private LivingEntity owner;
        private final LevelReader level;
        private final double speedModifier;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private final float stopDistance;
        private final float startDistance;
        private float oldWaterCost;
        private final boolean canFly;

        public LeptictidiumFollowOwnerGoal(Leptictidium pTamable, double pSpeedModifier, float pStartDistance, float pStopDistance, boolean pCanFly) {
            this.tamable = pTamable;
            this.level = pTamable.level();
            this.speedModifier = pSpeedModifier;
            this.navigation = pTamable.getNavigation();
            this.startDistance = pStartDistance;
            this.stopDistance = pStopDistance;
            this.canFly = pCanFly;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            if (!(pTamable.getNavigation() instanceof GroundPathNavigation) && !(pTamable.getNavigation() instanceof FlyingPathNavigation)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public boolean canUse() {
            LivingEntity livingentity = this.tamable.getOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.unableToMove()) {
                return false;
            } else if (this.tamable.distanceToSqr(livingentity) < (double) (this.startDistance * this.startDistance)) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.navigation.isDone()) {
                return false;
            } else if (this.unableToMove()) {
                return false;
            } else {
                return !(this.tamable.distanceToSqr(this.owner) <= (double) (this.stopDistance * this.stopDistance));
            }
        }

        private boolean unableToMove() {
            return this.tamable.isOrderedToSit() || this.tamable.isPassenger() || this.tamable.isLeashed();
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.tamable.getPathfindingMalus(BlockPathTypes.WATER);
            this.tamable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
            this.tamable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {
            this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float) this.tamable.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                if (this.tamable.distanceToSqr(this.owner) >= 144.0D) {
                    this.teleportToOwner();
                } else {
                    this.navigation.moveTo(this.owner, this.speedModifier);
                }

            }
        }

        private void teleportToOwner() {
            BlockPos blockpos = this.owner.blockPosition();

            for (int i = 0; i < 10; ++i) {
                int j = this.randomIntInclusive(-3, 3);
                int k = this.randomIntInclusive(-1, 1);
                int l = this.randomIntInclusive(-3, 3);
                boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
                if (flag) {
                    return;
                }
            }

        }

        private boolean maybeTeleportTo(int pX, int pY, int pZ) {
            if (Math.abs((double) pX - this.owner.getX()) < 2.0D && Math.abs((double) pZ - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.canTeleportTo(new BlockPos(pX, pY, pZ))) {
                return false;
            } else {
                this.tamable.moveTo((double) pX + 0.5D, (double) pY, (double) pZ + 0.5D, this.tamable.getYRot(), this.tamable.getXRot());
                this.navigation.stop();
                return true;
            }
        }

        private boolean canTeleportTo(BlockPos pPos) {
            BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pPos.mutable());
            if (blockpathtypes != BlockPathTypes.WALKABLE) {
                return false;
            } else {
                BlockState blockstate = this.level.getBlockState(pPos.below());
                if (!this.canFly && blockstate.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos blockpos = pPos.subtract(this.tamable.blockPosition());
                    return this.level.noCollision(this.tamable, this.tamable.getBoundingBox().move(blockpos));
                }
            }
        }

        private int randomIntInclusive(int pMin, int pMax) {
            return this.tamable.getRandom().nextInt(pMax - pMin + 1) + pMin;
        }
    }
}
