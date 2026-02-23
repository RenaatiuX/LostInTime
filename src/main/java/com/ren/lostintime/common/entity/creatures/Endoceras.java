package com.ren.lostintime.common.entity.creatures;

import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.LITWaterAnimal;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Endoceras extends LITWaterAnimal implements GeoEntity {

    //ANIMATIONS
    protected static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
    protected static final RawAnimation BEACHED = RawAnimation.begin().thenLoop("misc.beached");
    protected static final RawAnimation GRAB = RawAnimation.begin().thenPlay("misc.grab");
    public static final RawAnimation EAT = RawAnimation.begin().thenPlayAndHold("misc.eat");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    //MULTIPART
    private final EndocerasPart[] parts;
    public final EndocerasPart headPart;
    public final EndocerasPart bodyPart1;
    public final EndocerasPart bodyPart2;
    public final EndocerasPart bodyPart3;
    public final EndocerasPart tailPart;
    private int shellDropTicker = 0;

    public Endoceras(EntityType<? extends LITAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.headPart = new EndocerasPart(this, "head", 1.8F, 1.8F);
        this.bodyPart1 = new EndocerasPart(this, "body", 2.0F, 2.0F);
        this.bodyPart2 = new EndocerasPart(this, "body", 2.0F, 2.0F);
        this.bodyPart3 = new EndocerasPart(this, "body", 2.0F, 2.0F);
        this.tailPart = new EndocerasPart(this, "tail", 1.5F, 1.5F);

        this.parts = new EndocerasPart[]{this.headPart, this.bodyPart1, this.bodyPart2, this.bodyPart3, this.tailPart};
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.7D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D);
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
            shellDropTicker++;

            if (shellDropTicker >= 3600) {
                if (this.horizontalCollision) {
                    if (this.random.nextFloat() < 0.2F) {
                        //this.spawnAtLocation(ItemInit.ENDOCERAS_SHELL_FRAGMENT.get());
                        shellDropTicker = 0;
                    }
                }
            }
        }

        float f = this.yBodyRot * ((float)Math.PI / 180F);
        float sin = Mth.sin(f);
        float cos = Mth.cos(f);

        this.movePart(this.bodyPart1, 0.0D, 0.0D, 0.0D);
        this.movePart(this.headPart, (double)(-sin * 2.0F), 0.0D, (double)(cos * 2.0F));
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

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        boolean flag = super.doHurtTarget(pEntity);
        if (flag && pEntity instanceof LivingEntity) {
            if (pEntity.getBbWidth() < 2.0f) {
                pEntity.startRiding(this);
                //this.triggerAnim("grab");
            }
        }
        return flag;
    }

    @Override
    public void positionRider(Entity passenger, MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            double d0 = this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset();
            callback.accept(passenger, this.getX(), d0, this.getZ());
        }
    }

    @Override
    public @Nullable SoundEvent getFlopSound() {
        return SoundEvents.COD_FLOP;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
