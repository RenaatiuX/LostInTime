package com.ren.lostintime.common.entity.creatures;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.LITWaterAnimal;
import com.ren.lostintime.common.entity.ai.PlesiosaurusAi;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
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

import java.util.UUID;

public class Plesiosaurus extends LITWaterAnimal implements GeoEntity {

    private static final RawAnimation SWIM = RawAnimation.begin().thenPlay("swim");
    private static final RawAnimation BEACHED = RawAnimation.begin().thenPlay("beached");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int MAX_AIR_SUPPLY = 6000;
    private static final int MAX_MOISTURE = 2400;
    private int moistureLevel = MAX_MOISTURE;
    public int stealCooldown = 0;
    public int bodyguardTimer = 0;
    @Nullable
    public UUID bodyguardOwner = null;

    public Plesiosaurus(EntityType<? extends LITAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.navigation = new AmphibiousPathNavigation(this, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.8D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    //DOUBLE BREATHING SYSTEM
    @Override
    public boolean canBreatheUnderwater() {
        return false;
    }

    @Override
    public int getMaxAirSupply() {
        return MAX_AIR_SUPPLY;
    }

    @Override
    protected int increaseAirSupply(int pCurrentAir) {
        return this.getMaxAirSupply();
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!this.level().isClientSide) {
            this.handleMoisture();
        }
    }

    @Override
    protected void handleAirSupply(int pAirSupply) {

    }

    private void handleMoisture() {
        if (this.isInWaterOrRain()) {
            this.moistureLevel = MAX_MOISTURE;
        } else {
            this.moistureLevel--;
            if (this.moistureLevel <= 0) {
                this.hurt(this.damageSources().dryOut(), 1.0F);
                this.moistureLevel = 20;
            }
        }
    }

    @Override
    public boolean isInWaterOrRain() {
        BlockPos pos = this.blockPosition();
        return this.isInWater() || (this.level().isRaining() && this.level().canSeeSky(pos));
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new AmphibiousPathNavigation(this, pLevel);
    }

    @Override
    public boolean canFlop() {
        return false;
    }

    @Override
    public @Nullable SoundEvent getFlopSound() {
        return SoundEvents.GENERIC_SPLASH;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }

    //BRAIN
    @Override
    protected Brain.Provider<Plesiosaurus> brainProvider() {
        return Brain.provider(PlesiosaurusAi.MEMORY_TYPES, PlesiosaurusAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<Plesiosaurus> brain = this.brainProvider().makeBrain(pDynamic);
        PlesiosaurusAi.makeBrain(brain);
        return brain;
    }

    @Override
    public Brain<Plesiosaurus> getBrain() {
        return (Brain<Plesiosaurus>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("plesiosaurusBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();

        if (this.stealCooldown > 0) {
            this.stealCooldown--;
        }

        if (this.bodyguardTimer > 0) {
            this.bodyguardTimer--;
            if (this.bodyguardTimer == 0) {
                this.bodyguardOwner = null;
            }
        }

        PlesiosaurusAi.updateActivity(this);
        super.customServerAiStep();
    }

    //
    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.isFood(itemstack)) {
            if (!this.level().isClientSide) {
                this.bodyguardOwner = pPlayer.getUUID();
                this.bodyguardTimer = 4000;

                if (this.getAge() == 0 && !this.canFallInLove()) {
                    this.usePlayerItem(pPlayer, pHand, itemstack);
                    this.level().broadcastEntityEvent(this, (byte)7);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.COD);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return EntityInit.PLESIOSAURUS.get().create(pLevel);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "controller2", 0, this::attackPredicate));
    }

    private <T extends Plesiosaurus> PlayState predicate(final @NotNull AnimationState<T> event) {
        if (this.isInWaterOrRain()) {
            event.getController().setAnimation(SWIM);
            return PlayState.CONTINUE;
        } else {
            event.getController().setAnimation(BEACHED);
            return PlayState.CONTINUE;
        }
    }

    private <T extends Plesiosaurus> PlayState attackPredicate(final @NotNull AnimationState<T> event) {
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

    @Override
    public boolean canSleep() {
        return false;
    }
}
