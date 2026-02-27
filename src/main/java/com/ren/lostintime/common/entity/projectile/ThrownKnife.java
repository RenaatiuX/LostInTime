package com.ren.lostintime.common.entity.projectile;

import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.item.KnifeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class ThrownKnife extends AbstractArrow {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(ThrownKnife.class,
            EntityDataSerializers.ITEM_STACK);

    public ThrownKnife(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ThrownKnife(Level pLevel, LivingEntity shooter, ItemStack pStack) {
        super(EntityInit.THROWN_KNIFE.get(), shooter, pLevel);
        this.setKnifeItem(pStack.copy());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (!this.getKnifeItem().isEmpty()) {
            pCompound.put("KnifeItem", this.getKnifeItem().save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("KnifeItem", 10)) {
            this.setKnifeItem(ItemStack.of(pCompound.getCompound("KnifeItem")));
        }
    }

    public void setKnifeItem(ItemStack stack) {
        this.entityData.set(DATA_ITEM_STACK, stack);
    }

    public ItemStack getKnifeItem() {
        return this.entityData.get(DATA_ITEM_STACK);
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.getKnifeItem().copy();
    }

    public boolean isKnifeInGround() {
        return this.inGround;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity target = pResult.getEntity();
        Entity shooter = this.getOwner();

        float damage = 2.0F;
        if (this.getKnifeItem().getItem() instanceof KnifeItem knife) {
            damage = knife.getDamage() + 1.0F;
        }

        target.hurt(this.damageSources().thrown(this, shooter), damage);

        if (this.getKnifeItem().isDamageableItem()) {
            this.getKnifeItem().hurtAndBreak(1, shooter instanceof LivingEntity ? (LivingEntity) shooter : null, (entity) -> {
            });
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.1D, -0.1D, -0.1D));
        this.setYRot(this.getYRot() + 180.0F);
        this.yRotO += 180.0F;
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
