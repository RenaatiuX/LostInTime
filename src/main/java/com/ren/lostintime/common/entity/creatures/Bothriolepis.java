package com.ren.lostintime.common.entity.creatures;

import com.ren.lostintime.common.entity.AbstractBaseFish;
import com.ren.lostintime.common.entity.AgeableWaterAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
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

public class Bothriolepis extends AbstractBaseFish implements GeoEntity {

    private static final EntityDataAccessor<Boolean> DATA_IS_ON_FLOOR = SynchedEntityData.defineId(Bothriolepis.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> IDLE_TIME = SynchedEntityData.defineId(Bothriolepis.class, EntityDataSerializers.INT);

    private static final RawAnimation SWIM_FLOOR = RawAnimation.begin().thenLoop("swim_floor");
    private static final RawAnimation SWIM_WATER = RawAnimation.begin().thenLoop("swim_water");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation LAND_MOVE = RawAnimation.begin().thenLoop("land_move");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int idleTimer = 0;
    private int outOfWaterTicks = 0;
    private int panicTicks = 0;

    public Bothriolepis(EntityType<? extends WaterAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setMaxUpStep(1.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MoveToWaterGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.6D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_ON_FLOOR, true);
        this.entityData.define(IDLE_TIME, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.12)
                .add(Attributes.FOLLOW_RANGE, 8.0);
    }

    @Override
    public boolean isAggressive() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isInWater() && this.isOnSeafloor()) {
            idleTimer++;
        } else {
            idleTimer = 0;
        }
        if (this.isInWater() && !this.isOnSeafloor()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.02, 0));
        }
        if (this.isOutOfWater()) {
            outOfWaterTicks++;
            if (outOfWaterTicks > 20 * 30) {
                this.hurt(this.damageSources().dryOut(), 1.0F);
            }
        } else {
            outOfWaterTicks = 0;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.isInWater()) return;
        if (this.isOnSeafloor()) {
            if (this.shouldIdleOnFloor()) {
                if (this.random.nextInt(40) == 0) {
                    this.moveOnFloor();
                }
            } else {
                this.liftOffFloor();
            }
        }
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isInWater() && !this.isOnSeafloor()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 0, 0.8));
        }
        super.travel(pTravelVector);
    }

    public boolean isOnSeafloor() {
        BlockPos pos = this.blockPosition();
        BlockPos below = pos.below();
        boolean solidBelow = level().getBlockState(below).isSolid();
        boolean inWater = this.isInWater();
        boolean closeToFloor = this.getY() - below.getY() <= 1.05;

        return inWater && solidBelow && closeToFloor;
    }

    public void moveOnFloor() {
        if (!this.isInWater() || !this.isOnSeafloor()) return;
        this.setDeltaMovement(this.getRandom().triangle(0, 0.02),
                0, this.getRandom().triangle(0, 0.02));
    }

    public boolean shouldIdleOnFloor() {
        int max = this.isCrepuscularActive() ? 20 * 60 : 20 * 120;
        return idleTimer < max;
    }

    public void liftOffFloor() {
        if (this.isOnSeafloor()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.2, 0));
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.getDirectEntity() instanceof Projectile) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity direct = source.getDirectEntity();
        boolean result = super.hurt(source, amount);
        if (result) {
            this.liftOffFloor();
        }
        if (direct instanceof AbstractArrow projectile) {
            if (this.random.nextFloat() < 0.6f) {
                Vec3 dir = this.getLookAngle().scale(1.2);
                projectile.setDeltaMovement(dir);
                projectile.setOwner(this);
                this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);
                return false;
            }
        }
        return result;
    }

    public boolean isCrepuscularActive() {
        long time = this.level().getDayTime() % 24000;

        boolean isDawn = time >= 22000 || time <= 2000;
        boolean isDusk = time >= 12000 && time <= 14000;
        boolean isRaining = this.level().isRaining();

        return isDawn || isDusk || isRaining;
    }

    public boolean isOutOfWater() {
        return !this.isInWater() && !this.isInWaterOrBubble();
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
        return stack.isEdible();
    }

    @Override
    public ItemStack getBucketItemStack() {
        return null;
    }

    @Override
    protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (!level().isClientSide()) {
            if (stack.isEdible()) {
                if (stack.is(Items.COOKIE)) {
                    this.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
                }
                this.heal(2.0F);
                if (!pPlayer.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", this::movePredicate));
    }

    protected <E extends Bothriolepis> PlayState movePredicate(final AnimationState<E> event) {
        if (!this.isInWater()) {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 1e-6) {
                event.setAnimation(LAND_MOVE);
            } else {
                event.setAnimation(IDLE);
            }
        } else {
            if (this.isOnSeafloor()) {
                if (this.getDeltaMovement().horizontalDistanceSqr() < 1e-5 && this.idleTimer > 100) {
                    event.setAnimation(IDLE);
                } else {
                    event.setAnimation(SWIM_FLOOR);
                }
            } else {
                event.setAnimation(SWIM_WATER);
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    static class MoveToWaterGoal extends Goal {

        private final Bothriolepis fish;
        private BlockPos targetWater;

        public MoveToWaterGoal(Bothriolepis fish) {
            this.fish = fish;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!fish.isOutOfWater()) return false;

            targetWater = findNearbyWater();
            return targetWater != null;
        }

        @Override
        public void start() {
            if (targetWater != null) {
                fish.getNavigation().moveTo(
                        targetWater.getX() + 0.5,
                        targetWater.getY() + 0.5,
                        targetWater.getZ() + 0.5,
                        1.2D
                );
            }
        }

        @Override
        public boolean canContinueToUse() {
            return fish.isOutOfWater() && targetWater != null;
        }

        private BlockPos findNearbyWater() {
            BlockPos origin = fish.blockPosition();
            int radius = 12;

            for (BlockPos pos : BlockPos.betweenClosed(
                    origin.offset(-radius, -3, -radius),
                    origin.offset(radius, 3, radius))) {

                if (fish.level().getFluidState(pos).is(Fluids.WATER)) {
                    return pos.immutable();
                }
            }
            return null;
        }
    }
}
