package com.ren.lostintime.common.entity.creatures;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Daeodon extends TamableAnimal implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int temper;

    public Daeodon(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        boolean isOwner = this.isOwnedBy(pPlayer);

        if (this.level().isClientSide) {
            return InteractionResult.CONSUME;
        }

        if (this.isTame() && isOwner) {
            if (itemstack.is(Items.BONE)) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.jumping = false;
                this.navigation.stop();
                this.setTarget(null);

                this.playSound(SoundEvents.WOLF_AMBIENT, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }

            if (itemstack.isEmpty() && !this.isOrderedToSit()) {
                pPlayer.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }

        else if (!this.isTame()) {
            if (itemstack.isEmpty()) {
                pPlayer.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    protected void positionRider(Entity pPassenger, MoveFunction pCallback) {
        super.positionRider(pPassenger, pCallback);
        if (pPassenger instanceof Player player && !this.isTame()) {
            this.tameTick(player);
        }
    }

    private void tameTick(Player player) {
        if (this.random.nextInt(50) == 0) {
            this.temper += 5;

            if (this.temper >= 50 + this.random.nextInt(50)) {
                this.tame(player);
                this.level().broadcastEntityEvent(this, (byte)7);
            } else {
                player.stopRiding();
                this.level().broadcastEntityEvent(this, (byte)6);
                this.playSound(SoundEvents.HORSE_ANGRY, 1.0F, 1.0F);
                double pushBack = -0.5D;
                double launchHeight = 1.2D;

                Vec3 look = this.getLookAngle();
                Vec3 throwVector = new Vec3(look.x * pushBack, launchHeight, look.z * pushBack);

                player.setDeltaMovement(player.getDeltaMovement().add(throwVector));
                player.hurtMarked = true;
                player.hurt(this.damageSources().mobAttack(this), 2.0F);
            }
        }
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Temper", this.temper);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.temper = pCompound.getInt("Temper");
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
        return null;
    }

    @Override
    public boolean alwaysAccepts() {
        return super.alwaysAccepts();
    }
}
