package com.ren.lostintime.common.entity.creatures;

import com.mojang.serialization.Dynamic;
import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.LITWaterAnimal;
import com.ren.lostintime.common.entity.ai.MastodonsaurusAi;
import com.ren.lostintime.common.entity.ai.ScutosaurusAi;
import com.ren.lostintime.common.entity.util.ISleepingEntity;
import com.ren.lostintime.common.entity.util.SleepController;
import com.ren.lostintime.common.entity.util.SleepType;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Mastodonsaurus extends LITWaterAnimal implements GeoEntity, ISleepingEntity {

    private static final EntityDataAccessor<Integer> DATA_GROWTH_STAGE = SynchedEntityData.defineId(Mastodonsaurus.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DEATH_ROLLING = SynchedEntityData.defineId(Mastodonsaurus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_REGURGITATING = SynchedEntityData.defineId(Mastodonsaurus.class, EntityDataSerializers.BOOLEAN);

    //ANIMATIONS
    protected static final RawAnimation SWIM = RawAnimation.begin().thenLoop("swim");
    protected static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("sleep");
    protected static final RawAnimation DEATHROLL = RawAnimation.begin().thenLoop("deathroll");
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation PANTING = RawAnimation.begin().thenLoop("panting");
    protected static final RawAnimation REGURGITATE = RawAnimation.begin().thenLoop("regurgitate");
    //BABY ANIM
    protected static final RawAnimation BABY_SWIM_FAST = RawAnimation.begin().thenLoop("swim_fast");
    protected static final RawAnimation BABY_OUT_OF_WATER = RawAnimation.begin().thenLoop("out_of_water");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public int digestionTimer = 0;
    public boolean isDigesting = false;
    public float healthAtDigestionStart = 0;
    public boolean wasStarvingBeforeEating = false;
    public int regurgitationAnimTimer = 0;
    public int pendingVomitType = 0;

    public int deathRollTimer = 0;
    private boolean isLandNavigator;
    public int swimTimer = 0;
    public int landTimer = 0;

    public Mastodonsaurus(EntityType<? extends LITAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.switchNavigator(false);
    }

    //NAV
    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
            this.navigation = new AmphibiousPathNavigation(this, level());
            this.isLandNavigator = false;
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.8D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D);
    }

    @Override
    public boolean onClimbable() {
        return this.isInWater() && this.horizontalCollision;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DEATH_ROLLING, false);
        this.entityData.define(DATA_GROWTH_STAGE, 0);
        this.entityData.define(DATA_REGURGITATING, false);
    }

    public boolean isDeathRolling() {
        return this.entityData.get(DATA_DEATH_ROLLING);
    }

    public boolean isRegurgitating() {
        return this.entityData.get(DATA_REGURGITATING);
    }

    //BRAIN
    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("mastodonsaurusBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();

        MastodonsaurusAi.updateActivity(this);
        super.customServerAiStep();

        //CHANGE OF NAVIGATION
        boolean onGround = !this.isInWater();
        if (onGround && !this.isLandNavigator) {
            this.switchNavigator(true);
        } else if (!onGround && this.isLandNavigator) {
            this.switchNavigator(false);
        }

        //AMPHIBIAN TIMERS
        if (this.isInWater()) {
            this.swimTimer++;
            this.landTimer = 0;
        } else {
            this.landTimer++;
            this.swimTimer = 0;
        }

        if (!this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof LivingEntity prey) {
            if (this.isInWater()) {
                if (!this.entityData.get(DATA_DEATH_ROLLING)) {
                    this.entityData.set(DATA_DEATH_ROLLING, true);
                }

                this.deathRollTimer++;

                if (this.deathRollTimer % 10 == 0) {
                    prey.hurt(this.damageSources().mobAttack(this), 5.0F);
                }

                if (!prey.isAlive()) {
                    prey.stopRiding();
                    this.entityData.set(DATA_DEATH_ROLLING, false);
                    this.deathRollTimer = 0;
                    // DIGESTION
                    if (!this.isDigesting) {
                        this.isDigesting = true;
                        this.digestionTimer = 60; // 3 minutes of digestion
                        this.healthAtDigestionStart = this.getHealth();
                        this.wasStarvingBeforeEating = this.getHealth() < (this.getMaxHealth() * 0.5F);
                    }
                }
            }
            else {
                this.entityData.set(DATA_DEATH_ROLLING, false);
                this.deathRollTimer++;
                if (this.deathRollTimer % 20 == 0) {
                    prey.hurt(this.damageSources().mobAttack(this), 2.0F);
                }
            }
        }
        else {
            if (this.entityData.get(DATA_DEATH_ROLLING)) {
                this.entityData.set(DATA_DEATH_ROLLING, false);
                this.deathRollTimer = 0;
            }
        }

        if (this.isDigesting) {
            this.digestionTimer--;

            if (this.digestionTimer <= 0) {
                this.isDigesting = false;
                float healthLost = this.healthAtDigestionStart - this.getHealth();
                boolean tookHeavyDamage = healthLost > (this.getMaxHealth() * 0.33F);

                int roll = this.random.nextInt(100) + 1;
                int vomitType = 0;

                if (tookHeavyDamage) {
                    if (roll <= 33) vomitType = 2;
                } else if (this.wasStarvingBeforeEating) {
                    if (roll <= 50) vomitType = 3;
                } else {
                    if (roll <= 20) vomitType = 1;
                }

                if (vomitType > 0) {
                    this.entityData.set(DATA_REGURGITATING, true);
                    this.regurgitationAnimTimer = 40;
                    this.pendingVomitType = vomitType;
                }
            }
        }

        if (this.isRegurgitating()) {
            this.regurgitationAnimTimer--;
            if (this.regurgitationAnimTimer == 20) {
                this.regurgitateLoot(this.pendingVomitType);
            }

            if (this.regurgitationAnimTimer <= 0) {
                this.entityData.set(DATA_REGURGITATING, false);
                this.pendingVomitType = 0;
            }
        }
    }

    private void regurgitateLoot(int vomitType) {
        this.playSound(SoundEvents.LLAMA_SPIT, 1.0F, 0.8F);

        ItemStack regurgitatedItem = ItemStack.EMPTY;

        switch (vomitType) {
            case 1: // Normal
                regurgitatedItem = new ItemStack(Items.SLIME_BALL);
                break;
            case 2: // Por Daño
                regurgitatedItem = new ItemStack(Items.BONE_MEAL);
                break;
            case 3: // Hambriento
                regurgitatedItem = new ItemStack(Items.GOLD_NUGGET);
                break;
        }

        if (!regurgitatedItem.isEmpty()) {
            ItemEntity itementity = this.spawnAtLocation(regurgitatedItem, 0.5F);
            if (itementity != null) {
                itementity.setDeltaMovement(itementity.getDeltaMovement().add(
                        this.getLookAngle().scale(0.3D)
                ));
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getGrowthStage() != 2 && this.isInWater() && this.horizontalCollision) {
            float f = this.getYRot() * ((float)Math.PI / 180F);

            this.setDeltaMovement(this.getDeltaMovement().add(
                    (double)(-Mth.sin(f) * 0.2F),
                    0.15D,
                    (double)(Mth.cos(f) * 0.2F)
            ));
        }
    }

    @Override
    protected Brain.Provider<Mastodonsaurus> brainProvider() {
        return Brain.provider(MastodonsaurusAi.MEMORY_TYPES, MastodonsaurusAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<Mastodonsaurus> brain = this.brainProvider().makeBrain(pDynamic);
        MastodonsaurusAi.makeBrain(brain);
        return brain;
    }

    @Override
    public Brain<Mastodonsaurus> getBrain() {
        return (Brain<Mastodonsaurus>) super.getBrain();
    }

    //RIDER
    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        return null;
    }

    @Override
    protected void positionRider(Entity pPassenger, MoveFunction pCallback) {
        if (this.hasPassenger(pPassenger)) {
            pPassenger.setYBodyRot(this.yBodyRot);

            final float radius = 2.0F;
            final float angle = (float) Math.PI / 180F * this.yBodyRot;

            final double extraX = radius * -Mth.sin(angle);
            final double extraZ = radius * Mth.cos(angle);

            pCallback.accept(pPassenger, this.getX() + extraX, this.getY() + 0.2F, this.getZ() + extraZ);
        }
    }

    @Override
    public boolean canFlop() {
        return this.getGrowthStage() == 2;
    }

    @Override
    public @Nullable SoundEvent getFlopSound() {
        return SoundEvents.GENERIC_SPLASH;
    }

    //GROWTH SYSTEM
    public int getGrowthStage() {
        return this.entityData.get(DATA_GROWTH_STAGE);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        EntityDimensions base = super.getDimensions(pPose);
        return switch (getGrowthStage()) {
            case 2 -> base.scale(0.35F);
            case 1 -> base.scale(0.70F);
            default -> base;
        };
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!this.level().isClientSide) {
            int age = this.getAge();
            int currentStage = (age >= 0) ? 0 : (age >= -12000 ? 1 : 2);
            if (this.entityData.get(DATA_GROWTH_STAGE) != currentStage) {
                this.entityData.set(DATA_GROWTH_STAGE, currentStage);
                this.refreshDimensions();
            }
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return EntityInit.MASTODONSAURUS.get().create(pLevel);
    }

    //DIET
    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.SALMON);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "action", 5, this::actionPredicate));
    }

    private <T extends Mastodonsaurus> PlayState movementPredicate(final AnimationState<T> event) {
        if (this.isDeathRolling()) return PlayState.STOP;

        if (this.getGrowthStage() == 2) {
            if (!this.isInWater()) {
                event.getController().setAnimation(BABY_OUT_OF_WATER);
            } else if (this.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY)) {
                event.getController().setAnimation(BABY_SWIM_FAST);
            } else {
                event.getController().setAnimation(SWIM);
            }
            return PlayState.CONTINUE;
        }

        if (this.isRegurgitating()) {
            event.getController().setAnimation(REGURGITATE);
            return PlayState.CONTINUE;
        }
        if (this.isInWater()) {
            event.getController().setAnimation(SWIM);
        } else if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
            event.getController().setAnimation(WALK);
        } else {
            event.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoEntity> PlayState actionPredicate(AnimationState<T> event) {
        if (this.isDeathRolling()) {
            event.getController().setAnimation(DEATHROLL);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public @Nullable SleepController<?> getSleepController() {
        return new SleepController<>(this);
    }

    @Override
    public SleepType getSleepType() {
        return SleepType.DIURNAL;
    }
}
