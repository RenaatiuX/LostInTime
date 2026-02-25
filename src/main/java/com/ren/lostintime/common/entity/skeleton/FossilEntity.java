package com.ren.lostintime.common.entity.skeleton;

import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class FossilEntity extends Entity {

    private static final EntityDataAccessor<String> SKELETON_TYPE =
            SynchedEntityData.defineId(FossilEntity.class, EntityDataSerializers.STRING);

    public FossilEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SKELETON_TYPE, "dodo");
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("SkeletonType")) {
            this.setSkeletonType(pCompound.getString("SkeletonType"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putString("SkeletonType", this.getSkeletonType());
    }

    public String getSkeletonType() {
        return this.entityData.get(SKELETON_TYPE);
    }

    public void setSkeletonType(String type) {
        this.entityData.set(SKELETON_TYPE, type);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        }
        if (!this.level().isClientSide && !this.isRemoved()) {
            if (pSource.getEntity() instanceof Player player && !player.isCreative()) {
                dropFossilItem();
            }
            this.remove(RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
            return true;
        }
        return false;
    }

    private void dropFossilItem() {
        Item dropItem = ItemInit.DODO_FOSSIL_MOUNT.get();
        this.spawnAtLocation(dropItem);
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean isPickable() {
        return true;
    }
}
