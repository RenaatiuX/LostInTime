package com.ren.lostintime.common.entity.creatures;

import com.mojang.serialization.Dynamic;
import com.ren.lostintime.common.block.LITEggBlock;
import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.ai.ScutosaurusAi;
import com.ren.lostintime.common.entity.util.SleepController;
import com.ren.lostintime.common.init.BlockInit;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IPlantable;
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

public class Scutosaurus extends LITAnimal implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation IDLE2 = RawAnimation.begin().thenLoop("idle2");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("sleep");
    private static final RawAnimation EAT_FLOOR = RawAnimation.begin().thenPlay("eat_floor");
    private static final RawAnimation EAT = RawAnimation.begin().thenPlay("eat");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> DATA_EATING_FLOOR = SynchedEntityData.defineId(Scutosaurus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAND_FEEDING = SynchedEntityData.defineId(Scutosaurus.class, EntityDataSerializers.BOOLEAN);

    private int actionTicks = 0;
    private int hungerTicks = 0;
    private int eatAnimationTick = 0;

    public Scutosaurus(EntityType<? extends LITAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.2F)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_EATING_FLOOR, false);
        this.entityData.define(DATA_HAND_FEEDING, false);
    }

    //TIMER MANAGEMENT
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.actionTicks > 0) {
            this.actionTicks--;
            if (this.actionTicks == 0) {
                this.entityData.set(DATA_HAND_FEEDING, false);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getDirectEntity() instanceof Projectile projectile) {
            this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.2F);

            Vec3 delta = projectile.getDeltaMovement();
            projectile.setDeltaMovement(delta.multiply(-0.5D, -0.5D, -0.5D));
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        Item item = pStack.getItem();

        if (item == Items.WITHER_ROSE || item == Items.COCOA_BEANS) {
            return false;
        }

        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            return block instanceof IPlantable || block instanceof LeavesBlock;
        }

        return false;
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.isFood(itemstack)) {
            if (!this.level().isClientSide) {
                this.entityData.set(DATA_HAND_FEEDING, true);
                this.actionTicks = 10;
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public void setEatingFloor(boolean eating) {
        this.entityData.set(DATA_EATING_FLOOR, eating);
    }

    //SLEEP
    @Override
    public @Nullable SleepController<?> getSleepController() {
        return new SleepController<>(this, SleepController.SleepType.DIURNAL);
    }

    //BRAIN
    @Override
    protected Brain.Provider<Scutosaurus> brainProvider() {
        return Brain.provider(ScutosaurusAi.MEMORY_TYPES, ScutosaurusAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<Scutosaurus> brain = this.brainProvider().makeBrain(pDynamic);
        ScutosaurusAi.makeBrain(brain);
        return brain;
    }

    @Override
    public Brain<Scutosaurus> getBrain() {
        return (Brain<Scutosaurus>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("scutosaurusBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();

        ScutosaurusAi.updateActivity(this);

        if (this.isSleeping()) {
            this.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            this.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
            this.getBrain().eraseMemory(MemoryModuleType.PATH);
            this.getNavigation().stop();
        }
        else {
            if (this.getBrain().isActive(Activity.IDLE) && !this.isBaby()) {
                this.hungerTicks++;

                if (this.hungerTicks > 600 && this.eatAnimationTick == 0) {
                    BlockPos pos = this.blockPosition();
                    BlockState state = this.level().getBlockState(pos);
                    BlockState stateBelow = this.level().getBlockState(pos.below());

                    if (state.is(Blocks.GRASS) || state.is(Blocks.TALL_GRASS) || stateBelow.is(Blocks.GRASS_BLOCK)) {
                        this.eatAnimationTick = 40;
                        this.setEatingFloor(true);
                        this.getNavigation().stop();
                    }
                }
            }
        }
        super.customServerAiStep();
    }
    //


    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.eatAnimationTick > 0) {
            this.eatAnimationTick--;
            if (this.eatAnimationTick == 20) {
                BlockPos pos = this.blockPosition();
                if (this.level().getBlockState(pos).is(Blocks.GRASS) || this.level().getBlockState(pos).is(Blocks.TALL_GRASS)) {
                    this.level().destroyBlock(pos, false);
                } else if (this.level().getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)) {
                    this.level().levelEvent(2001, pos.below(), Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                    this.level().setBlock(pos.below(), Blocks.DIRT.defaultBlockState(), 2);
                }
                this.heal(2.0F);
                this.hungerTicks = 0;
            }

            if (this.eatAnimationTick == 0) {
                this.setEatingFloor(false);
            }
        }
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel pLevel, Animal pMate) {
        ServerPlayer serverplayer = this.getLoveCause();
        if (serverplayer == null && pMate.getLoveCause() != null) {
            serverplayer = pMate.getLoveCause();
        }

        if (serverplayer != null) {
            serverplayer.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, this, pMate, null);
        }

        this.setAge(6000);
        pMate.setAge(6000);
        this.resetLove();
        pMate.resetLove();

        int eggsCount = this.random.nextInt(2) + 1;
        BlockPos pos = this.blockPosition();

        if (pLevel.getBlockState(pos.below()).is(BlockTags.DIRT)) {
            BlockState eggState = BlockInit.SCUTOSAURUS_EGG.get().defaultBlockState()
                    .setValue(LITEggBlock.EGGS, eggsCount);

            pLevel.setBlock(pos, eggState, 3);
            pLevel.playSound(null, pos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + pLevel.random.nextFloat() * 0.2F);
        }
        else {
            this.spawnAtLocation(new ItemStack(BlockInit.SCUTOSAURUS_EGG.get().asItem(), eggsCount));
        }

        pLevel.broadcastEntityEvent(this, (byte)18);
        if (pLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            pLevel.addFreshEntity(new ExperienceOrb(pLevel, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "controller2", 0, this::attackPredicate));
    }

    private <T extends Scutosaurus> PlayState predicate(final @NotNull AnimationState<T> event) {
        if (this.isSleeping()) {
            event.getController().setAnimation(SLEEP);
            return PlayState.CONTINUE;
        }
        if (this.entityData.get(DATA_HAND_FEEDING)) {
            event.getController().setAnimation(EAT);
            return PlayState.CONTINUE;
        }
        if (this.entityData.get(DATA_EATING_FLOOR)) {
            event.getController().setAnimation(EAT_FLOOR);
            return PlayState.CONTINUE;
        }
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
            if (this.isSprinting()) {
                event.getController().setAnimation(RUN);
                event.getController().setAnimationSpeed(1.2D);
            } else {
                event.getController().setAnimation(WALK);
                event.getController().setAnimationSpeed(1.0D);
            }
            return PlayState.CONTINUE;
        }
        if (this.tickCount % 200 >= 180) {
            event.getController().setAnimation(IDLE2);
        } else {
            event.getController().setAnimation(IDLE);
        }
        event.getController().setAnimationSpeed(1.0D);
        return PlayState.CONTINUE;
    }

    private <T extends Scutosaurus> PlayState attackPredicate(final @NotNull AnimationState<T> event) {
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
}
