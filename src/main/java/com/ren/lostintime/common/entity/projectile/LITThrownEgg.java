package com.ren.lostintime.common.entity.projectile;

import com.ren.lostintime.common.entity.creatures.Hylonomus;
import com.ren.lostintime.common.entity.enums.HylonomusVariant;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class LITThrownEgg extends ThrowableItemProjectile {

    private EntityType<?> entityType;

    public LITThrownEgg(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public LITThrownEgg(Level pLevel, LivingEntity pShooter, EntityType<?> entity, ItemStack itemStack) {
        super(EntityInit.LIT_THROWN_EGG.get(), pShooter, pLevel);
        this.entityType = entity;
        this.setItem(itemStack.copy());
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

            if (entityType != null) {
                Entity entity = entityType.create(this.level());
                if (entity != null) {
                    entity.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);

                    if (entity instanceof AgeableMob ageable) {
                        ageable.setAge(-24000);
                    }

                    if (entity instanceof Hylonomus hylonomus) {
                        ItemStack thrownStack = this.getItem();
                        if (thrownStack.hasTag() && thrownStack.getTag().contains("Variant")) {
                            int variantId = thrownStack.getTag().getInt("Variant");
                            hylonomus.setVariant(HylonomusVariant.byId(variantId));
                        } else {
                            hylonomus.setVariant(HylonomusVariant.STRIPPED);
                        }
                    }

                    this.level().addFreshEntity(entity);
                }
            }
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.EGG;
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
