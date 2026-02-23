package com.ren.lostintime.common.entity.creatures;

import com.ren.lostintime.common.entity.AbstractBaseFish;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
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
import java.util.List;

public class Bothriolepis extends AbstractBaseFish implements GeoEntity {

    private static final RawAnimation SWIM_FLOOR = RawAnimation.begin().thenLoop("swim_floor");
    private static final RawAnimation SWIM_WATER = RawAnimation.begin().thenLoop("swim_water");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation LAND_MOVE = RawAnimation.begin().thenLoop("land_move");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int panicTicks = 0;

    public Bothriolepis(EntityType<? extends AbstractBaseFish> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MoveToWaterGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(3, new IdleOnFloorGoal(this));
        this.goalSelector.addGoal(4, new BenthicWanderGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 8.0);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.isInWater()) {
            this.setSprinting(this.getMoveControl().hasWanted() &&
                    this.getMoveControl().getSpeedModifier() >= 1.5D);
        }
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
        }
    }

    @Override
    public boolean canFlop() {
        return false;
    }

    @Override
    public boolean floatsUp() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity direct = source.getDirectEntity();

        if (direct instanceof Projectile projectile) {
            Vec3 bounce = projectile.getDeltaMovement().scale(-0.6D);
            projectile.setDeltaMovement(bounce);
            this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);
            return false;
        }

        boolean result = super.hurt(source, amount);

        if (result) {
            int ticks = 100 + this.random.nextInt(100);
            this.panicTicks = ticks;
        }
        return result;
    }

    public boolean isOutOfWater() {
        return !this.isInWater() && !this.isInWaterOrBubble();
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
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
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

    //AIR
    @Override
    public int getMaxAirSupply() {
        return 600;
    }

    @Override
    protected void handleAirSupply(int pAirSupply) {
        if (this.isAlive() && this.isOutOfWater()) {
            this.setAirSupply(pAirSupply - 1);
            if (this.getAirSupply() <= -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().dryOut(), 2.0F);
            }
        } else {
            this.setAirSupply(this.getMaxAirSupply());
        }
    }

    public boolean isCrepuscularOrRaining() {
        long time = this.level().getDayTime() % 24000;
        boolean isDawnOrDusk = (time > 22000 || time < 2000) || (time > 11000 && time < 13000);
        return isDawnOrDusk || this.level().isRaining();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movePredicate));
    }

    protected <E extends Bothriolepis> PlayState movePredicate(AnimationState<E> event) {
        if (!this.isInWater()) {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 1E-6) {
                return event.setAndContinue(LAND_MOVE);
            } else {
                return event.setAndContinue(IDLE);
            }
        }
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1E-6) {
            if (this.isSprinting()) {
                return event.setAndContinue(SWIM_WATER);
            } else {
                return event.setAndContinue(SWIM_FLOOR);
            }
        }
        return event.setAndContinue(IDLE);
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    static class MoveToWaterGoal extends Goal {

        private final Bothriolepis fish;
        private BlockPos targetWater;

        public MoveToWaterGoal(Bothriolepis fish) {
            this.fish = fish;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!fish.isOutOfWater()) return false;

            targetWater = findNearbyWater();
            return targetWater != null;
        }

        @Override
        public boolean canContinueToUse() {
            return fish.isOutOfWater() && targetWater != null;
        }

        @Override
        public void stop() {
            if (fish.isInWater()) {
                Vec3 look = fish.getLookAngle();
                fish.setDeltaMovement(look.x * 0.4D, -0.2D, look.z * 0.4D);
            }
        }

        @Override
        public void tick() {
            if (targetWater != null) {
                double dx = targetWater.getX() + 0.5D - fish.getX();
                double dz = targetWater.getZ() + 0.5D - fish.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);

                fish.getLookControl().setLookAt(targetWater.getX() + 0.5D, targetWater.getY() + 0.5D,
                        targetWater.getZ() + 0.5D, 10.0F, (float)fish.getMaxHeadXRot());

                if (distance > 0.5D) {
                    float targetYaw = (float) (net.minecraft.util.Mth.atan2(dz, dx) * (180F / Math.PI)) - 90.0F;
                    fish.setYRot(net.minecraft.util.Mth.approachDegrees(fish.getYRot(), targetYaw, 5.0F));
                    fish.yBodyRot = fish.getYRot();

                    if (fish.onGround()) {
                        Vec3 currentMovement = fish.getDeltaMovement();
                        Vec3 targetMovement = new Vec3(dx, 0, dz).normalize().scale(0.02D);

                        fish.setDeltaMovement(
                                currentMovement.x * 0.5D + targetMovement.x,
                                currentMovement.y,
                                currentMovement.z * 0.5D + targetMovement.z
                        );
                    }
                }
            }
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

    static class EatFloorItemsGoal extends Goal {
        private final Bothriolepis fish;
        private ItemEntity itemEntity;

        public EatFloorItemsGoal(Bothriolepis fish) {
            this.fish = fish;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!fish.isInWater()) return false;

            List<ItemEntity> items = fish.level().getEntitiesOfClass(ItemEntity.class, fish.getBoundingBox()
                            .inflate(8.0D),
                    item -> item.getItem().isEdible() || item.getItem().is(Items.SPIDER_EYE) ||
                            item.getItem().is(Items.ROTTEN_FLESH)
            );

            if (items.isEmpty()) return false;

            this.itemEntity = items.get(0);
            return true;
        }

        @Override
        public void tick() {
            if (this.itemEntity != null && this.itemEntity.isAlive()) {
                fish.getNavigation().moveTo(this.itemEntity, 1.0D);

                if (fish.distanceToSqr(this.itemEntity) < 2.5D) {
                    ItemStack stack = this.itemEntity.getItem();

                    if (stack.is(Items.COOKIE)) {
                        fish.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
                    } else {
                        fish.heal(2.0F);
                        BlockPos pos = fish.blockPosition().below();
                        if (fish.level() instanceof ServerLevel serverLevel) {

                            BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), fish.level(), pos);
                            serverLevel.levelEvent(2005, pos, 0);
                        }
                    }

                    this.itemEntity.discard();
                    this.itemEntity = null;
                }
            }
        }
    }

    static class BenthicWanderGoal extends Goal {

        private final Bothriolepis fish;
        private double wantedX, wantedY, wantedZ;

        public BenthicWanderGoal(Bothriolepis fish) {
            this.fish = fish;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!fish.isInWater() || fish.panicTicks > 0) return false;

            boolean isFloating = !fish.level().getBlockState(fish.blockPosition().below()).isSolidRender(fish.level(), fish.blockPosition().below());
            int chance = isFloating ? 5 : (fish.isCrepuscularOrRaining() ? 30 : 80);
            if (fish.getRandom().nextInt(chance) != 0) {
                return false;
            }

            Vec3 target = this.getBenthicPos();
            if (target == null) {
                return false;
            } else {
                this.wantedX = target.x;
                this.wantedY = target.y;
                this.wantedZ = target.z;
                return true;
            }
        }

        @Override
        public void start() {
            this.fish.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, 0.3D);
        }

        @Override
        public boolean canContinueToUse() {
            return !this.fish.getNavigation().isDone() && fish.panicTicks == 0;
        }

        @Nullable
        private Vec3 getBenthicPos() {
            RandomSource random = this.fish.getRandom();
            for (int i = 0; i < 10; i++) {
                int dx = random.nextInt(16) - 8;
                int dz = random.nextInt(16) - 8;
                int dy = random.nextInt(8) - 4;

                BlockPos pos = this.fish.blockPosition().offset(dx, dy, dz);

                if (!this.fish.level().getFluidState(pos).is(Fluids.WATER)) continue;

                while (this.fish.level().getFluidState(pos.below()).is(Fluids.WATER) && pos.getY() > this.fish.level().getMinBuildHeight()) {
                    pos = pos.below();
                }

                if (this.fish.level().getBlockState(pos.below()).isSolidRender(this.fish.level(), pos.below())) {
                    return Vec3.atBottomCenterOf(pos);
                }
            }
            return null;
        }
    }

    static class IdleOnFloorGoal extends Goal {

        private final Bothriolepis fish;
        private int idleTime;

        public IdleOnFloorGoal(Bothriolepis fish) {
            this.fish = fish;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (!fish.isInWater() || fish.panicTicks > 0) return false;

            BlockPos pos = fish.blockPosition();
            if (!fish.level().getBlockState(pos.below()).isSolidRender(fish.level(), pos.below())) {
                return false;
            }

            int chance = fish.isCrepuscularOrRaining() ? 150 : 50;
            return fish.getRandom().nextInt(chance) == 0;
        }

        @Override
        public void start() {
            int maxTicks = fish.isCrepuscularOrRaining() ? 1200 : 2400;
            this.idleTime = (int) (maxTicks * 0.7) + fish.getRandom().nextInt((int) (maxTicks * 0.3));
            fish.getNavigation().stop();
        }

        @Override
        public boolean canContinueToUse() {
            return this.idleTime > 0 && fish.panicTicks == 0 && fish.isInWater();
        }

        @Override
        public void tick() {
            this.idleTime--;
            fish.getNavigation().stop();
            fish.setDeltaMovement(0.0D, fish.getDeltaMovement().y < 0 ? fish.getDeltaMovement().y : -0.01D, 0.0D);
            fish.setYHeadRot(fish.yBodyRot);
            fish.setXRot(0.0F);
        }
    }
}
