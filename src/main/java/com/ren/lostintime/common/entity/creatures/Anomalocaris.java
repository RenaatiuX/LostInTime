package com.ren.lostintime.common.entity.creatures;

import com.ren.lostintime.common.entity.AbstractBaseFish;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class Anomalocaris extends AbstractBaseFish implements GeoEntity {

    //ANIMATIONS
    protected static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
    protected static final RawAnimation OUT_OF_WATER = RawAnimation.begin().thenLoop("move.out_of_water");
    protected static final RawAnimation GRABBED = RawAnimation.begin().thenPlay("misc.grabbed");
    public static final RawAnimation GRAB = RawAnimation.begin().thenPlayAndHold("misc.grab");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    //DATA PARAMETERS
    private static final EntityDataAccessor<Float> DATA_HUNGER = SynchedEntityData.defineId(Anomalocaris.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<ItemStack> DATA_HELD_ITEM = SynchedEntityData.defineId(Anomalocaris.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Optional<UUID>> DATA_CURIO_PLAYER = SynchedEntityData.defineId(Anomalocaris.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> DATA_CURIO_START = SynchedEntityData.defineId(Anomalocaris.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_LAST_FED = SynchedEntityData.defineId(Anomalocaris.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_GRABBING = SynchedEntityData.defineId(Anomalocaris.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_EGG = SynchedEntityData.defineId(Anomalocaris.class, EntityDataSerializers.BOOLEAN);

    private static final float MAX_HUNGER = 100.0F;
    private static final float HUNGER_THRESHOLD = 99.0F;
    private static final float PREY_HEIGHT_MAX = 0.4F;
    private static final float HUNGER_DECREASE_PER_TICK = 1.0F / 400.0F;
    private static final float HUNGER_GAIN_PER_PREY = 10.0F;

    private int hurtTimeCoolDown = 0;
    private int grabDamageTicks = 0;

    private TargetingConditions grabTargets() {
        return TargetingConditions.forCombat().range(12.0D).selector(this::isSuitablePrey);
    }

    public Anomalocaris(EntityType<? extends AbstractBaseFish> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(3, new CuriositySwimGoal(this));
        this.goalSelector.addGoal(5, new AnomalocarisGrabAttackGoal(this));
        this.goalSelector.addGoal(6, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, this::isSuitablePrey));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.8F)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 12);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return EntityInit.ANOMALOCARIS.get().create(pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HUNGER, MAX_HUNGER);
        this.entityData.define(DATA_HELD_ITEM, ItemStack.EMPTY);
        this.entityData.define(DATA_CURIO_PLAYER, Optional.empty());
        this.entityData.define(DATA_CURIO_START, 0);
        this.entityData.define(DATA_LAST_FED, 0);
        this.entityData.define(DATA_HAS_EGG, false);
        this.entityData.define(DATA_GRABBING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Hunger", this.getHunger());
        tag.put("HeldItem", this.getHeldItem().save(new CompoundTag()));
        if (this.getCurioPlayer() != null && !this.getCurioPlayer().equals(Util.NIL_UUID)) {
            tag.putUUID("CurioPlayer", this.getCurioPlayer());
        }
        tag.putInt("CurioStart", this.getCurioStart());
        tag.putInt("LastFed", this.getLastFed());
        tag.putBoolean("HasEgg", this.hasEgg());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setHunger(tag.getFloat("Hunger"));
        this.setHeldItem(ItemStack.of(tag.getCompound("HeldItem")));
        if (tag.hasUUID("CurioPlayer")) {
            this.setCurioPlayer(tag.getUUID("CurioPlayer"));
        } else {
            this.setCurioPlayer(Util.NIL_UUID);
        }
        this.setCurioStart(tag.getInt("CurioStart"));
        this.setLastFed(tag.getInt("LastFed"));
        this.setHasEgg(tag.getBoolean("HasEgg"));
    }

    //GETTER AND SETTER
    public float getHunger() {
        return this.entityData.get(DATA_HUNGER);
    }

    public void setHunger(float hunger) {
        this.entityData.set(DATA_HUNGER, Mth.clamp(hunger, 0.0F, MAX_HUNGER));
    }

    public ItemStack getHeldItem() {
        return this.entityData.get(DATA_HELD_ITEM);
    }

    public void setHeldItem(ItemStack stack) {
        this.entityData.set(DATA_HELD_ITEM, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
    }

    public boolean hasHeldItem() {
        return !this.getHeldItem().isEmpty();
    }

    public UUID getCurioPlayer() {
        return this.entityData.get(DATA_CURIO_PLAYER).orElse(null);
    }

    public void setCurioPlayer(UUID pUuid) {
        this.entityData.set(DATA_CURIO_PLAYER, Optional.ofNullable(pUuid));
    }

    public int getCurioStart() {
        return this.entityData.get(DATA_CURIO_START);
    }

    public void setCurioStart(int tick) {
        this.entityData.set(DATA_CURIO_START, tick);
    }

    public int getLastFed() {
        return this.entityData.get(DATA_LAST_FED);
    }

    public void setLastFed(int tick) {
        this.entityData.set(DATA_LAST_FED, tick);
    }

    public boolean hasRecentlyFed(long now) {
        return now - this.getLastFed() < 24000L;
    }

    public int getCurioTicksRemaining(long now) {
        return (int) (now - this.getCurioStart());
    }

    public int getCurioDuration() {
        return this.hasRecentlyFed(this.level().getGameTime()) ? 7200 : 4800;
    }

    public boolean hasEgg() {
        return this.entityData.get(DATA_HAS_EGG);
    }

    public void setHasEgg(boolean hasEgg) {
        this.entityData.set(DATA_HAS_EGG, hasEgg);
    }

    public boolean isGrabbing() {
        return this.entityData.get(DATA_GRABBING);
    }

    public void setGrabbing(boolean grabbing) {
        this.entityData.set(DATA_GRABBING, grabbing);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            long time = this.level().getGameTime();
            float hunger = this.getHunger() - HUNGER_DECREASE_PER_TICK;
            this.setHunger(Math.max(hunger, 0.0F));
            if (this.hasHeldItem() && !this.getCurioPlayer().equals(Util.NIL_UUID)) {
                Player curioPlayer = ((ServerLevel) this.level()).getPlayerByUUID(this.getCurioPlayer());
                if (curioPlayer != null && this.distanceToSqr(curioPlayer) < 16.0 && this.getCurioTicksRemaining(time) >= this.getCurioDuration()) {
                    this.dropHeldItemToPlayer(curioPlayer);
                }
            }
            if (this.hurtTimeCoolDown > 0) {
                this.hurtTimeCoolDown--;
            }
            if (this.isGrabbedState()) {
                LivingEntity prey = this.getGrabbedPrey();
                if (prey != null && ++this.grabDamageTicks % 20 == 0) {
                    prey.hurt(this.damageSources().mobAttack(this), 0.2F);
                    if (prey.getHealth() <= 0) {
                        this.eatPrey(prey);
                    }
                }
            }
            if (this.isGrabbing() && !this.hasGrabbedPrey() && !this.hasHeldItem()) {
                this.setGrabbing(false);
            }
        }
    }

    public boolean isGrabbedState() {
        return this.hasHeldItem() || this.hasGrabbedPrey();
    }

    public LivingEntity getGrabbedPrey() {
        Entity passenger = this.getFirstPassenger();
        if (passenger instanceof LivingEntity prey) {
            if (!prey.isRemoved() && prey.isAlive()) {
                return prey;
            }
        }
        return null;
    }

    private void eatPrey(LivingEntity prey) {
        prey.stopRiding();
        prey.kill();
        this.setHunger(Math.min(this.getHunger() + HUNGER_GAIN_PER_PREY, MAX_HUNGER));
        this.heal(3.0F);
        this.setGrabbing(false);
        this.grabDamageTicks = 0;
    }

    public void grabPrey(LivingEntity prey) {
        prey.startRiding(this, true);
        this.setGrabbing(true);
    }

    @Override
    protected void positionRider(Entity pPassenger, MoveFunction pCallback) {
        if (pPassenger instanceof LivingEntity) {
            double yOffset = 0.0;
            double xOffset = 0.2;
            double zOffset = -0.5;
            Vec3 riderPos = this.position().add(xOffset, yOffset, zOffset);
            pPassenger.setPos(riderPos.x, riderPos.y, riderPos.z);
        } else {
            super.positionRider(pPassenger);
        }
    }

    public boolean hasGrabbedPrey() {
        return getGrabbedPrey() != null;
    }

    private void dropHeldItemToPlayer(Player player) {
        player.getInventory().placeItemBackInInventory(this.getHeldItem());
        this.setHeldItem(ItemStack.EMPTY);
        this.setCurioPlayer(Util.NIL_UUID);
        this.setCurioStart(0);
    }

    //BREED


    @Override
    public @Nullable SoundEvent getFlopSound() {
        return SoundEvents.COD_FLOP;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.COD);
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        if (pEntity instanceof LivingEntity prey && this.isSuitablePrey(prey)) {
            this.grabPrey(prey);
            return true;
        }
        return false;
    }

    @Override
    public boolean hurt(DamageSource pSource, float source) {
        boolean hurt = super.hurt(pSource, source);
        if (hurt) {
            LivingEntity prey = this.getGrabbedPrey();
            if (prey != null) {
                prey.stopRiding();
            }
            if (this.hasHeldItem()) {
                this.spawnAtLocation(this.getHeldItem(), 0.5F);
                this.setHeldItem(ItemStack.EMPTY);
            }
            this.hurtTimeCoolDown = 100;
            this.setNoActionTime(100);
        }
        return hurt;
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
        return !hasGrabbedPrey() && !hasHeldItem() && !this.isBaby() && this.getHunger() <= HUNGER_THRESHOLD && isSuitablePrey(pTarget);
    }

    private boolean isSuitablePrey(LivingEntity target) {
        return target.getBbHeight() < PREY_HEIGHT_MAX && target.isAlive() && !(target instanceof Player);
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (this.level().isClientSide) {
            return InteractionResult.CONSUME;
        }
        ItemStack stack = pPlayer.getItemInHand(pHand);
        long time = this.level().getGameTime();
        if (!this.hasHeldItem() && !stack.isEmpty() && this.distanceToSqr(pPlayer) < 5.0) {
            ItemStack given = stack.split(1);
            this.setHeldItem(given);
            this.setGrabbing(true);
            this.setCurioPlayer(pPlayer.getUUID());
            this.setCurioStart((int) time);
            return InteractionResult.SUCCESS;
        }
        if (this.hasHeldItem() && this.getCurioPlayer().equals(pPlayer.getUUID()) && this.distanceToSqr(pPlayer) < 5.0) {
            this.dropHeldItemToPlayer(pPlayer);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public ItemStack getBucketItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) {
        return pDimensions.height * 0.65F;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers
                .add(new AnimationController<>(this, "move", 10, this::movePredicate))
                .add(new AnimationController<>(this, "grab", 10, this::grabPredicate));
    }

    protected <E extends Anomalocaris> PlayState movePredicate(final AnimationState<E> event) {
        if (!this.isInWater()) {
            event.getController().setAnimation(OUT_OF_WATER);
        } else {
            event.getController().setAnimation(SWIM);
        }
        return PlayState.CONTINUE;
    }

    private <E extends Anomalocaris> PlayState grabPredicate(AnimationState<E> event) {
        if (this.hasGrabbedPrey()) {
            event.getController().setAnimation(GRABBED);
            return PlayState.CONTINUE;
        }
        if (this.hasHeldItem()) {
            event.getController().setAnimation(GRABBED);
            return PlayState.CONTINUE;
        }
        if (this.isGrabbing()) {
            event.getController().setAnimation(GRAB);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    static class AnomalocarisGrabAttackGoal extends MeleeAttackGoal {

        private final Anomalocaris anomalocaris;
        private LivingEntity target;

        public AnomalocarisGrabAttackGoal(Anomalocaris anomalocaris) {
            super(anomalocaris, 1.0D, true);
            this.anomalocaris = anomalocaris;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            this.target = this.anomalocaris.getTarget();
            return this.target != null && anomalocaris.canAttack(this.target) && this.anomalocaris.getHunger() <= HUNGER_THRESHOLD && !anomalocaris.isBaby() && super.canUse();
        }

        @Override
        public void tick() {
            this.anomalocaris.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            if (this.anomalocaris.distanceToSqr(this.target) > 1.2D) {
                this.anomalocaris.getNavigation().moveTo(this.target, 1.4D);
            } else {
                this.anomalocaris.doHurtTarget(this.target);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !this.anomalocaris.hasGrabbedPrey()
                    && this.anomalocaris.canAttack(this.target)
                    && !this.anomalocaris.hasHeldItem()
                    && this.target.isAlive()
                    && !anomalocaris.isBaby()
                    && super.canContinueToUse();
        }
    }

    static class CuriositySwimGoal extends Goal {

        private final Anomalocaris anomalocaris;
        private Player targetPlayer;
        private int circleTicks;
        private float circleAngle = 0.0F;

        public CuriositySwimGoal(Anomalocaris anomalocaris) {
            this.anomalocaris = anomalocaris;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            UUID playerUuid = this.anomalocaris.getCurioPlayer();
            if (playerUuid.equals(Util.NIL_UUID) || !this.anomalocaris.hasHeldItem()) {
                return false;
            }
            this.targetPlayer = this.anomalocaris.level().getPlayerByUUID(playerUuid);
            return this.targetPlayer != null && this.anomalocaris.distanceToSqr(this.targetPlayer) < 16.0 &&
                    this.anomalocaris.level().getGameTime() - this.anomalocaris.getCurioStart() < this.anomalocaris.getCurioDuration();
        }

        @Override
        public void tick() {
            this.anomalocaris.getLookControl().setLookAt(this.targetPlayer, 30.0F, 30.0F);
            Vec3 vecToPlayer = this.targetPlayer.position().subtract(this.anomalocaris.position()).multiply(1, 0, 1);
            if (vecToPlayer.lengthSqr() < 1.0E-4) {
                return;
            }
            Vec3 up = new Vec3(0, 1, 0);
            Vec3 right = vecToPlayer.normalize().cross(up).normalize();
            float radius = 3.0F + Mth.sin(this.circleTicks * 0.1F);
            Vec3 circleOffset = right.scale(radius * Math.sin(this.circleAngle)).add(0, Mth.sin(this.circleTicks * 0.2F) * 0.5, 0);
            this.anomalocaris.setDeltaMovement(this.anomalocaris.getDeltaMovement().lerp(circleOffset.normalize().scale(0.5), 0.1));
            this.circleAngle += 0.2F;
            this.circleTicks++;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }
    }
}
