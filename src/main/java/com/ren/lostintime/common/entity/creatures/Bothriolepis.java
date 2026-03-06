package com.ren.lostintime.common.entity.creatures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.config.Config;
import com.ren.lostintime.common.entity.AbstractBaseFish;
import com.ren.lostintime.common.entity.ai.*;
import com.ren.lostintime.common.entity.util.IEggLayerAnimal;
import com.ren.lostintime.common.init.ActivitInit;
import com.ren.lostintime.common.init.BlockInit;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.ItemInit;
import com.ren.lostintime.datagen.server.LITTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Bothriolepis extends AbstractBaseFish implements GeoEntity, IEggLayerAnimal {

    private static final RawAnimation SWIM_FLOOR = RawAnimation.begin().thenLoop("swim_floor");
    private static final RawAnimation SWIM_WATER = RawAnimation.begin().thenLoop("swim_water");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation LAND_MOVE = RawAnimation.begin().thenLoop("land_move");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public Bothriolepis(EntityType<? extends AbstractBaseFish> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        //this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10,
        //        0.1F, 0.5F, false);
        this.moveControl = new MoveControl(this);
        this.lookControl = new LookControl(this);
        this.jumpControl = new NoJumpControl(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.setCanPickUpLoot(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractFish.createAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new AmphibiousPathNavigation(this, pLevel);
    }

    @Override
    public int getMaxAirSupply() {
        return 600;
    }

    @Override
    protected int increaseAirSupply(int pCurrentAir) {
        return this.getMaxAirSupply();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypeTags.IS_PROJECTILE)) {
            this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.2F);
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected Vec3i getPickupReach() {
        return new Vec3i(1, 1, 1);
    }

    @Override
    public @Nullable SoundEvent getFlopSound() {
        return null;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ItemInit.BOTHRIOLEPIS_BABY_BUCKET.get());
    }

    @Override
    public boolean canFlop() {
        return false;
    }

    @Override
    public boolean floatsUp() {
        return false;
    }

    public boolean isOnOceanFloor() {
        BlockPos pos = this.blockPosition();
        return this.level().getFluidState(pos).is(FluidTags.WATER) && this.level().getBlockState(pos.below()).isSolid();
    }

    @Override
    public boolean canSwim() {
        if (!isInWater())
            return false;
        return this.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING);
    }

    @Override
    public boolean wantsToPickUp(ItemStack pStack) {
        return pStack.is(LITTags.Items.BOTHRIOLEPIS_FOOD);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.ROTTEN_FLESH);
    }

    @Override
    protected Brain.Provider<Bothriolepis> brainProvider() {
        return Brain.provider(
                ImmutableList.of(MemoryModuleType.WALK_TARGET,
                        MemoryModuleType.LOOK_TARGET,
                        MemoryModuleType.PATH,
                        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                        MemoryModuleType.IS_PANICKING,
                        MemoryModuleType.HURT_BY,
                        MemoryModuleType.HURT_BY_ENTITY,
                        MemoryModuleType.BREED_TARGET,
                        MemoryModuleType.IS_PREGNANT,
                        MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
                        MemoryModuleType.NEAREST_LIVING_ENTITIES,
                        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                        MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
                        MemoryModuleType.IS_IN_WATER
                ),
                ImmutableList.of(
                        SensorType.NEAREST_PLAYERS,
                        SensorType.NEAREST_LIVING_ENTITIES,
                        SensorType.HURT_BY,
                        SensorType.NEAREST_ITEMS,
                        SensorType.IS_IN_WATER
                )
        );
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<Bothriolepis> brain = this.brainProvider().makeBrain(pDynamic);

        initCoreActivity(brain);
        initIdleActivity(brain);
        initPanicActivity(brain);
        initWalkToItemActivity(brain);
        initMatingActivity(brain);
        initFindWaterActivity(brain);

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    @Override
    protected void pickUpItem(ItemEntity pItemEntity) {
        ItemStack itemstack = pItemEntity.getItem();
        if (tryApplyBoneMealItems()) {
            if (!this.level().isClientSide) {
                this.level().levelEvent(1505, this.blockPosition(), 0);
                this.getBrain().setMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, Config.bothriolepisItemPickupCooldown);
            }
            this.onItemPickup(pItemEntity);
            this.take(pItemEntity, 1);
            itemstack.shrink(1);
            if (itemstack.isEmpty()) {
                pItemEntity.discard();
            }
        }
    }

    protected boolean tryApplyBoneMealItems() {
        if (this.level() instanceof ServerLevel serverLevel) {
            var fakePlayer = FakePlayerFactory.getMinecraft(serverLevel);
            var boneMealStack = new ItemStack(Items.BONE_MEAL);
            var pos = this.blockPosition();
            if (tryApplyBoneMealItems(pos, boneMealStack, serverLevel, fakePlayer))
                return true;
            return tryApplyBoneMealItems(pos.below(), boneMealStack, serverLevel, fakePlayer);
        }
        return false;
    }

    protected boolean tryApplyBoneMealItems(BlockPos pos, ItemStack stack, ServerLevel serverLevel, ServerPlayer serverPlayer) {
        if (!BoneMealItem.applyBonemeal(stack, serverLevel, pos, serverPlayer)) {
            return BoneMealItem.growWaterPlant(stack, serverLevel, pos, Direction.UP);
        }
        return true;
    }

    @Override
    public Brain<Bothriolepis> getBrain() {
        return (Brain<Bothriolepis>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("bothriolepisBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();
        this.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.PANIC, ActivitInit.FIND_WATER.get(), ActivitInit.MATING.get(), ActivitInit.CATCH_ITEM.get(), Activity.IDLE));
        super.customServerAiStep();
    }

    private void initCoreActivity(Brain<Bothriolepis> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new CountDownCooldownTicks(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)
        ));
    }

    private void initIdleActivity(Brain<Bothriolepis> brain) {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(1, new PregnantAnimalLove(EntityInit.BOTHRIOLEPIS.get(), 0.5F)),
                Pair.of(2, new RunOne<>(ImmutableList.of(Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 6.0F), 1), Pair.of(new DoNothing(30, 60), 1)))),
                Pair.of(3, BehaviorBuilder.triggerIf(Bothriolepis::shouldStroll, RandomStrollUtils.swimOceanFloor(1.7f)))
        ));

    }

    private void initFindWaterActivity(Brain<Bothriolepis> brain) {
        brain.addActivityWithConditions(ActivitInit.FIND_WATER.get(), ImmutableList.of(
                        Pair.of(0, BrainAiUtils.forceFindWater(10, 0.5f))
                ),
                ImmutableSet.of(
                        Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_ABSENT)
                )
        );

    }

    private void initMatingActivity(Brain<Bothriolepis> brain) {
        brain.addActivityAndRemoveMemoriesWhenStopped(ActivitInit.MATING.get(), ImmutableList.of(
                        Pair.of(1, new FindWaterEggLayingSpot<>(16, 1.9f)),
                        Pair.of(2, BrainAiUtils.layEggWhenPossible())
                ), ImmutableSet.of(Pair.of(MemoryModuleType.IS_PREGNANT, MemoryStatus.VALUE_PRESENT)),
                ImmutableSet.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.IS_PREGNANT));
    }

    private void initWalkToItemActivity(Brain<Bothriolepis> brain) {
        brain.addActivityWithConditions(ActivitInit.CATCH_ITEM.get(), ImmutableList.of(
                        Pair.of(0, GoToWantedItem.create(1.9F, true, 10))
                ),
                ImmutableSet.of(
                        Pair.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT),
                        Pair.of(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT))
        );
    }

    private void initPanicActivity(Brain<Bothriolepis> brain) {
        brain.addActivityAndRemoveMemoriesWhenStopped(Activity.PANIC, ImmutableList.of(
                        Pair.of(0, new WaterAnimalPanic<>(1.9F, RandomStrollUtils::getPanicPosInWater))
                ), ImmutableSet.of(
                        Pair.of(MemoryModuleType.HURT_BY, MemoryStatus.VALUE_PRESENT)
                ),
                //ensure panicking and walk target is erased so when the panicking is over it wont sped around anymore
                ImmutableSet.of(MemoryModuleType.IS_PANICKING, MemoryModuleType.WALK_TARGET)
        );

    }

    public boolean isCrepuscular() {
        long time = level().getDayTime() % 24000;
        boolean isDawnOrDusk = (time >= 23000 || time < 1000) || (time >= 12000 && time < 13000);
        boolean isRaining = level().isRaining();
        return isDawnOrDusk || isRaining;
    }

    private boolean shouldStroll() {
        return isCrepuscular() || level().random.nextInt(1000) == 0;
    }


    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return pSource.getDirectEntity() != null && pSource.getDirectEntity().getType().is(EntityTypeTags.ARROWS);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementPredicate));
    }

    private PlayState movementPredicate(AnimationState<Bothriolepis> event) {
        boolean isPanicking = this.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING);

        if (!this.isInWaterOrBubble()) {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
                event.getController().setAnimation(LAND_MOVE);
            } else {
                event.getController().setAnimation(IDLE);
            }
        } else {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
                if (isPanicking) {
                    event.getController().setAnimation(SWIM_WATER);
                } else {
                    event.getController().setAnimation(SWIM_FLOOR);
                }
            } else {
                event.getController().setAnimation(IDLE);
            }
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
        return BlockInit.BOTHRIOLEPIS_ROE.get().defaultBlockState();
    }

    @Override
    public boolean canBePickedUpWithBucket(Player player, InteractionHand hand) {
        return this.isBaby();
    }
}
