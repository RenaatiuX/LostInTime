package com.ren.lostintime.common.entity.creatures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.serialization.Dynamic;
import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.LITWaterAnimal;
import com.ren.lostintime.common.entity.util.IEggLayerAnimal;
import com.ren.lostintime.common.init.*;
import com.ren.lostintime.datagen.server.LITTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
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

import java.util.Arrays;
import java.util.Optional;

public class Endoceras extends LITWaterAnimal implements GeoEntity, IEggLayerAnimal {

    //ANIMATIONS
    protected static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.endoceras.swim");
    protected static final RawAnimation BEACHED = RawAnimation.begin().thenLoop("animation.endoceras.beached");
    protected static final RawAnimation GRAB = RawAnimation.begin().thenPlayAndHold("animation.endoceras.grab");
    public static final RawAnimation EAT = RawAnimation.begin().thenPlayAndHold("animation.endoceras.eat");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    //MULTIPART
    private final EndocerasPart[] parts;
    public final EndocerasPart headPart;
    private int shellDropTicker = 0;

    protected static final ImmutableList<SensorType<? extends Sensor<? super Endoceras>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.HURT_BY
    );

    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.AVOID_TARGET,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleType.IS_PREGNANT,
            MemoryModuleInit.GRABBED_PREY.get()
    );

    public Endoceras(EntityType<? extends LITAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.headPart = new EndocerasPart(this, "head", 1.0F, 1.0F);

        this.parts = new EndocerasPart[]{this.headPart,
                new EndocerasPart(this, "body", 1.0F, 1.0F),
                new EndocerasPart(this, "body", 1.0F, 1.0F),
                new EndocerasPart(this, "body", 1.0F, 1.0F),
                new EndocerasPart(this, "body", 1.0F, 1.0F),
                new EndocerasPart(this, "body", 1.0F, 1.0F),
                new EndocerasPart(this, "body", 1.0F, 1.0F)
        };
    }

    @Override
    protected void registerGoals() {
        // Brain system handles goals
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.7D)
                .add(Attributes.ATTACK_DAMAGE, 0.5D);
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return super.shouldRender(pX, pY, pZ);
    }

    //MULTIPART
    @Override
    public void setId(int pId) {
        super.setId(pId);
        for (int i = 0; i < this.parts.length; ++i) {
            this.parts[i].setId(pId + i + 1);
        }
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public @Nullable EndocerasPart[] getParts() {
        return parts;
    }
    //

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypeTags.IS_PROJECTILE) || pSource.is(DamageTypeTags.IS_EXPLOSION)) {
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.isAlive()) {
            if (shellDropTicker > 0)
                shellDropTicker--;

            if (shellDropTicker >= 3600) {
                if (this.horizontalCollision) {
                    if (this.random.nextFloat() < 0.2F) {
                        this.spawnAtLocation(ItemInit.ENDOCERAS_SHELL_FRAGMENT.get());
                        shellDropTicker = 3400;
                    }
                }
            }
        }

        float yRotRad = Mth.DEG_TO_RAD * this.yBodyRot;
        float sinY = Mth.sin(yRotRad);
        float cosY = Mth.cos(yRotRad);

        double dx = -sinY;
        double dz = cosY;
        double spacing = 1.1D;

        this.movePart(this.headPart, -dx * 1.1D, 0, -dz * 1.1D);
        for (int i = 1; i < this.parts.length; i++) {
            var part = this.parts[i];
            this.movePart(part, dx * spacing * (i - 1), 0, dz * spacing * (i - 1));
        }
    }

    private void movePart(EndocerasPart part, double offsetX, double offsetY, double offsetZ) {
        part.setPos(this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ);
        part.xo = part.getX();
        part.yo = part.getY();
        part.zo = part.getZ();
        part.xOld = part.getX();
        part.yOld = part.getY();
        part.zOld = part.getZ();
    }

    public boolean grabPrey(Entity prey) {
        if (this.hasPassenger(prey))
            return false;
        return prey.startRiding(this);
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

    public boolean hasGrabbedPrey() {
        return getGrabbedPrey() != null;
    }

    @Override
    public void positionRider(Entity passenger, MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            double d0 = this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset() - 0.4d;
            double forwardOffset = 1.0;
            double yaw = Math.toRadians(this.yBodyRot);
            double dx = -Math.sin(yaw);
            double dz = Math.cos(yaw);
            passenger.setYBodyRot(this.yBodyRot);
            callback.accept(passenger, this.getX() + dx * -forwardOffset, d0, this.getZ() + dz * -forwardOffset);
        }
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(LITTags.Items.ENDOCERAS_BREEDABLE_FOOD);
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(10);
    }

    @Override
    public @Nullable SoundEvent getFlopSound() {
        return SoundEvents.COD_FLOP;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return EntityInit.ENDOCERAS.get().create(pLevel);
    }

    @Override
    protected @NotNull Brain.Provider<Endoceras> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected @NotNull Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<Endoceras> brain = this.brainProvider().makeBrain(pDynamic);
        EndocerasAi.initBrain(brain);
        return brain;
    }

    public Optional<? extends LivingEntity> findNearestValidAttackTarget() {
        //1% chance to actually hunt a target so this doesnt go rampage
        if (this.hasGrabbedPrey() || this.level().random.nextInt(100) != 0)
            return Optional.empty();
        return this.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty()).findClosest(this::isTargetable);
    }

    public boolean isTargetable(LivingEntity target) {
        return isSuitablePrey(target) && Sensor.isEntityAttackable(this, target);
    }

    @Override
    protected void customServerAiStep() {
        this.tickBrain();
        super.customServerAiStep();
    }

    protected void tickBrain() {
        Brain<Endoceras> brain = this.getBrain();
        brain.tick((ServerLevel) this.level(), this);
        brain.setActiveActivityToFirstValid(ImmutableList.of(ActivitInit.HURT_GRABBED_PREY.get(),ActivitInit.MATING.get(), ActivitInit.GRAB_PREY.get(), Activity.IDLE));
    }

    public boolean isSuitablePrey(LivingEntity entity) {
        //prevent cannibalism
        return entity.getType() != this.getType() && entity.getBbWidth() <= 0.7F && entity.getBbHeight() <= 0.7F && entity.isInWater();
    }

    @Override
    public boolean canAttack(LivingEntity pTarget) {
        return this.isSuitablePrey(pTarget) && super.canAttack(pTarget);
    }

    @SuppressWarnings("unchecked")
    public Brain<Endoceras> getBrain() {
        return (Brain<Endoceras>) super.getBrain();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::movePredicate));
    }

    private PlayState movePredicate(AnimationState<Endoceras> state) {
        if (!this.isInWater()) {
            state.getController().transitionLength(10);
            state.getController().setAnimation(BEACHED);
        } else if (this.hasGrabbedPrey()) {
            state.getController().transitionLength(0);
            state.getController().setAnimation(GRAB);
        } else {
            state.getController().transitionLength(10);
            state.getController().setAnimation(SWIM);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean canLayEgg(ServerLevel level, Animal entity, BlockPos pos) {
        return level.getFluidState(pos).is(Fluids.WATER) &&
                level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);

    }

    @Override
    public BlockState getEggState(ServerLevel level, Animal entity, BlockPos pos) {
        return BlockInit.ENDOCERAS_EGG.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true);
    }
}
