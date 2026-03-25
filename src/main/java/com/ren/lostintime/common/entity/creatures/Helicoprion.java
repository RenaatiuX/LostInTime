package com.ren.lostintime.common.entity.creatures;

import com.mojang.serialization.Dynamic;
import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.LITWaterAnimal;
import com.ren.lostintime.common.entity.ai.EndocerasAi;
import com.ren.lostintime.common.entity.ai.HelicoprionAi;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
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

public class Helicoprion extends LITWaterAnimal implements GeoEntity {

    private static final EntityDataAccessor<Boolean> DATA_IS_BREACHING = SynchedEntityData.defineId(Helicoprion.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_BREACH_PITCH = SynchedEntityData.defineId(Helicoprion.class, EntityDataSerializers.FLOAT);

    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("swim");
    private static final RawAnimation SWIM_FAST = RawAnimation.begin().thenLoop("swim_fast");
    private static final RawAnimation BEACHED = RawAnimation.begin().thenLoop("beached");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation ATTACK_SLOW = RawAnimation.begin().thenPlay("attack_slow");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public float breachPitch = 0;
    public float prevBreachPitch = 0;

    public Helicoprion(EntityType<? extends LITAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
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

    public boolean isBreaching() {
        return this.entityData.get(DATA_IS_BREACHING);
    }

    //PHYSICS OF JUMPING
    public void executeBreachAttack(Entity target) {
        this.entityData.set(DATA_IS_BREACHING, true);

        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();

        float yaw = (float)(Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
        this.setYRot(yaw);
        this.yBodyRot = yaw;

        //Very little horizontal movement
        double horizontalMultiplier = 0.15D;

        double verticalForce = 1.9D;

        Vec3 jumpVector = new Vec3(dx, 0, dz).normalize().scale(horizontalMultiplier);
        this.setDeltaMovement(jumpVector.x, verticalForce, jumpVector.z);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.isBreaching()) {
                if (this.isInWater() && this.getDeltaMovement().y < 0) {
                    this.entityData.set(DATA_IS_BREACHING, false);
                    this.entityData.set(DATA_BREACH_PITCH, 0.0F);
                } else {
                    float targetPitch = (float) (-this.getDeltaMovement().y * 85.0D);
                    targetPitch = Mth.clamp(targetPitch, -90.0F, 90.0F);
                    this.entityData.set(DATA_BREACH_PITCH, targetPitch);
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
        }

        if (this.isBreaching() && !this.level().isClientSide) {
            for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.5D))) {
                if (!(entity instanceof Helicoprion) && entity.isAlive()) {
                    entity.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                }
            }
        }
    }

    //BRAIN
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
        HelicoprionAi.updateActivity(this);

        super.customServerAiStep();
    }

    @Override
    public @Nullable SoundEvent getFlopSound() {
        return SoundEvents.COD_FLOP;
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

        //this.spawnAtLocation(ItemInit.HELICOPRION_EGG_CASE.get(), 1);

        pLevel.broadcastEntityEvent(this, (byte)18);
        if (pLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            pLevel.addFreshEntity(new ExperienceOrb(pLevel, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.COD) || pStack.is(Items.SALMON);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "action", 5, this::actionPredicate));
    }

    private <T extends Helicoprion> PlayState movementPredicate(final @NotNull software.bernie.geckolib.core.animation.AnimationState<T> event) {
        if (this.isInWaterOrRain()) {
            event.getController().setAnimation(SWIM);
            return PlayState.CONTINUE;
        } else {
            event.getController().setAnimation(BEACHED);
            return PlayState.CONTINUE;
        }
    }

    private <T extends Helicoprion> PlayState actionPredicate(final @NotNull AnimationState<T> event) {
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
