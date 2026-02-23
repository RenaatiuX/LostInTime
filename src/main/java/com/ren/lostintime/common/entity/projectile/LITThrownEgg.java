package com.ren.lostintime.common.entity.projectile;

import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class LITThrownEgg extends ThrowableItemProjectile {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(LITThrownEgg.class, EntityDataSerializers.ITEM_STACK);
    private EntityType<?> entityType;

    public LITThrownEgg(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public LITThrownEgg(Level pLevel, LivingEntity pShooter, EntityType<?> entity, Item item) {
        super(EntityInit.LIT_THROWN_EGG.get(), pShooter, pLevel);
        this.entityType = entity;
        this.setItem(new ItemStack(item));
    }

    public LITThrownEgg(Level pLevel, double pX, double pY, double pZ) {
        super(EntityInit.LIT_THROWN_EGG.get(), pX, pY, pZ, pLevel);
    }

    public void setEntityTypeToSpawn(EntityType<?> type) {
        this.entityType = type;
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 3) {
            double d0 = 0.08D;
            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(),
                        this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * d0,
                        ((double)this.random.nextFloat() - 0.5D) * d0, ((double)this.random.nextFloat() - 0.5D) * d0);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        pResult.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level().isClientSide) {
            if (this.random.nextInt(8) == 0 && entityType != null) {
                Entity entity = entityType.create(this.level());
                if (entity != null) {
                    entity.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                    this.level().addFreshEntity(entity);
                }
            }
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    public void setItem(ItemStack pStack) {
        this.entityData.set(DATA_ITEM_STACK, pStack);
    }

    @Override
    protected Item getDefaultItem() {
        return this.entityData.get(DATA_ITEM_STACK).getItem();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("EntityType", EntityType.getKey(this.entityType).toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("EntityType")) {
            this.entityType = EntityType.byString(pCompound.getString("EntityType")).orElse(null);
        }
    }
}
