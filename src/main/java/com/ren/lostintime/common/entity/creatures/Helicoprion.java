package com.ren.lostintime.common.entity.creatures;

import com.mojang.serialization.Dynamic;
import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.LITWaterAnimal;
import com.ren.lostintime.common.entity.ai.HelicoprionAi;
import com.ren.lostintime.common.entity.enums.GrowthStage;
import com.ren.lostintime.common.entity.util.IBloodSeeker;
import com.ren.lostintime.common.entity.util.IItemEater;
import com.ren.lostintime.common.entity.util.IStalker;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.MobEffectInit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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

public class Helicoprion extends LITWaterAnimal implements GeoEntity, IBloodSeeker, IStalker, IItemEater {

    // ==========================================
    // VARIABLES AND DATA RECORDS
    // ==========================================
    private static final EntityDataAccessor<Boolean> DATA_IS_BREACHING = SynchedEntityData.defineId(Helicoprion.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_BREACH_PITCH = SynchedEntityData.defineId(Helicoprion.class, EntityDataSerializers.FLOAT);

    // GeckoLib Animations
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("swim");
    private static final RawAnimation SWIM_FAST = RawAnimation.begin().thenLoop("swim_fast");
    private static final RawAnimation BEACHED = RawAnimation.begin().thenLoop("beached");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation ATTACK_SLOW = RawAnimation.begin().thenPlay("attack_slow");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Internal variables for visual and AI logic
    public float breachPitch = 0;
    public float prevBreachPitch = 0;
    public float swimPitch = 0;
    public float prevSwimPitch = 0;
    private int stalkingTicks = 0;

    // ==========================================
    // INITIALIZATION AND BASE ATTRIBUTES
    // ==========================================
    public Helicoprion(EntityType<? extends LITAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setCanPickUpLoot(true);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.2D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_BREACHING, false);
        this.entityData.define(DATA_BREACH_PITCH, 0.0F);
    }

    // ==========================================
    // NAVIGATION AND BASIC PHYSICS
    // ==========================================
    @Override
    public float getSpeed() {
        return this.isSprinting() ? super.getSpeed() * 1.3F : super.getSpeed();
    }

    @Override
    public int getMaxHeadYRot() {
        return 6;
    }

    @Override
    public int getMaxHeadXRot() {
        return 6;
    }

    // ==========================================
    // JUMP AND ATTACK SYSTEM
    // ==========================================
    public boolean isBreaching() {
        return this.entityData.get(DATA_IS_BREACHING);
    }

    // The jump begins by calculating the trajectory towards the prey.
    public void executeBreachAttack(Entity target) {
        this.entityData.set(DATA_IS_BREACHING, true);

        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();

        // Visually rotate the shark towards the target
        float yaw = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
        this.setYRot(yaw);
        this.yBodyRot = yaw;

        double horizontalMultiplier = 0.35D;
        double verticalForce = 1.2D;

        // Apply physical force
        Vec3 jumpVector = new Vec3(dx, 0, dz).normalize().scale(horizontalMultiplier);
        this.setDeltaMovement(jumpVector.x, verticalForce, jumpVector.z);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.isBreaching()) {
                this.getNavigation().stop();

                // Physics of jumping
                if (this.isInWater() && this.getDeltaMovement().y < 0) {
                    this.entityData.set(DATA_IS_BREACHING, false);
                    this.entityData.set(DATA_BREACH_PITCH, 0.0F);
                } else {
                    float targetPitch = (float) (-this.getDeltaMovement().y * 85.0D);
                    targetPitch = Mth.clamp(targetPitch, -90.0F, 90.0F);
                    this.entityData.set(DATA_BREACH_PITCH, targetPitch);
                }

                // Area damage (jaw hitbox) and prey trapping
                for (LivingEntity victim : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.8D))) {
                    if (!(victim instanceof Helicoprion) && victim.isAlive() && !this.hasPassenger(victim)) {
                        victim.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                        victim.addEffect(new MobEffectInstance(MobEffectInit.BLEEDING.get(), 200, 1));

                        if (this.getPassengers().isEmpty()) {
                            victim.startRiding(this, true);
                        }
                    }
                }
            }
        }
        else {
            this.prevBreachPitch = this.breachPitch;
            if (this.isBreaching()) {
                float serverPitch = this.entityData.get(DATA_BREACH_PITCH);
                this.breachPitch += (serverPitch - this.breachPitch) * 0.2F;
            } else {
                this.breachPitch *= 0.8F;
            }
            this.prevSwimPitch = this.swimPitch;
            this.swimPitch += (this.getXRot() - this.swimPitch) * 0.1F;

            float turnSpeed = (float) this.getMaxHeadYRot();
            this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, this.getYRot(), turnSpeed);
        }
    }

    @Override
    protected void positionRider(Entity pPassenger, MoveFunction pCallback) {
        if (this.hasPassenger(pPassenger)) {
            float offsetZ = 1.8F;
            float offsetY = 0.5F;
            Vec3 mouthPos = new Vec3(0, offsetY, offsetZ).yRot(-this.getYRot() * ((float) Math.PI / 180F));
            pCallback.accept(pPassenger, this.getX() + mouthPos.x, this.getY() + mouthPos.y, this.getZ() + mouthPos.z);
        } else {
            super.positionRider(pPassenger, pCallback);
        }
    }

    // ==========================================
    // BRAIN AND AI
    // ==========================================
    @Override
    protected @NotNull Brain.Provider<Helicoprion> brainProvider() {
        return Brain.provider(HelicoprionAi.MEMORY_TYPES, HelicoprionAi.SENSOR_TYPES);
    }

    @Override
    protected @NotNull Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<Helicoprion> brain = this.brainProvider().makeBrain(pDynamic);
        HelicoprionAi.makeBrain(brain);
        return brain;
    }

    @SuppressWarnings("unchecked")
    public Brain<Helicoprion> getBrain() {
        return (Brain<Helicoprion>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("helicoprionBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();

        this.level().getProfiler().push("helicoprionActivityUpdate");
        HelicoprionAi.updateActivity(this);
        this.level().getProfiler().pop();

        super.customServerAiStep();
    }

    // ==========================================
    // HUNTING MECHANICS, HUNGER AND FEEDING
    // ==========================================
    @Override
    public boolean killedEntity(ServerLevel pLevel, LivingEntity pEntity) {
        boolean flag = super.killedEntity(pLevel, pEntity);
        if (flag) {
            int recovery = (int)(this.getMaxHunger() * 0.20F);
            this.setHunger(this.getHunger() + recovery);
            this.heal(2.0F);
        }
        return flag;
    }

    @Override
    public boolean isFoodItem(ItemStack stack) {
        // Tag: stack.is(LITTags.FOOD_CARNIVORES)
        return stack.is(Items.COD) || stack.is(Items.SALMON);
    }

    @Override
    public boolean wantsToPickUp(ItemStack pStack) {
        return this.isFoodItem(pStack);
    }

    @Override
    protected void pickUpItem(ItemEntity pItemEntity) {
        if (this.wantsToPickUp(pItemEntity.getItem())) {
            int recovery = (int)(this.getMaxHunger() * 0.40F);
            this.setHunger(this.getHunger() + recovery);
            this.consumeItem(this, pItemEntity);
        }
    }

    @Override
    public float getBaseMaxHunger() {
        return 200;
    }

    @Override
    public int getHungerTickInterval() {
        return 800;
    }

    // ==========================================
    // STALKING INTERFACES (IStalker & IBloodSeeker)
    // ==========================================
    @Override
    public double getBloodScentRange() {
        return 48.0D;
    }

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
        return this.isInWater() && target.getY() >= this.getY();
    }

    // ==========================================
    // REPRODUCTION
    // ==========================================
    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.COD) || pStack.is(Items.SALMON);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return EntityInit.HELICOPRION.get().create(pLevel);
    }

    // ==========================================
    // GECKOLIB ANIMATIONS
    // ==========================================
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "action", 5, this::actionPredicate));
    }

    private <T extends Helicoprion> PlayState movementPredicate(final @NotNull software.bernie.geckolib.core.animation.AnimationState<T> event) {
        if (this.isInWaterOrBubble()) {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6 && this.getTarget() != null) {
                event.getController().setAnimation(SWIM_FAST);
            } else if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
                event.getController().setAnimation(SWIM);
            } else {
                event.getController().setAnimation(SWIM);
            }
            return PlayState.CONTINUE;
        } else {
            event.getController().setAnimation(BEACHED);
            return PlayState.CONTINUE;
        }
    }

    private <T extends Helicoprion> PlayState actionPredicate(final @NotNull AnimationState<T> event) {
        if (this.swinging && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            event.getController().forceAnimationReset();
            if (this.getHealth() < (this.getMaxHealth() * 0.3F)) {
                event.getController().setAnimation(ATTACK_SLOW);
            } else {
                event.getController().setAnimation(ATTACK);
            }
            this.swinging = false;
        }
        return PlayState.CONTINUE;
    }

    // ==========================================
    // SOUNDS
    // ==========================================
    @Override
    public @Nullable SoundEvent getFlopSound() {
        return SoundEvents.COD_FLOP;
    }

    // ==========================================
    // GROWTH
    // ==========================================
    @Override
    public boolean hasJuvenileStage() {
        return true;
    }
}
