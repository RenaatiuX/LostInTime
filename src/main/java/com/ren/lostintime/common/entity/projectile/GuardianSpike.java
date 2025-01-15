package com.ren.lostintime.common.entity.projectile;

import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;

public class GuardianSpike extends AbstractArrow {

    private float rotation;
    public Vec2 groundedOffset;

    public GuardianSpike(EntityType<? extends GuardianSpike> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GuardianSpike(Level pLevel, LivingEntity pShooter) {
        super(EntityInit.GUARDIAN_SPIKE.get(), pShooter, pLevel);
    }

    public GuardianSpike(Level pLevel, double pX, double pY, double pZ) {
        super(EntityInit.GUARDIAN_SPIKE.get(), pX, pY, pZ, pLevel);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Entity entity = pResult.getEntity();
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), 4);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    @Override
    public ItemStack getPickupItem() {
        return new ItemStack(ItemInit.GUARDIAN_SPIKE.get());
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if(pResult.getDirection() == Direction.SOUTH) {
            groundedOffset = new Vec2(215f,180f);
        }
        if(pResult.getDirection() == Direction.NORTH) {
            groundedOffset = new Vec2(215f, 0f);
        }
        if(pResult.getDirection() == Direction.EAST) {
            groundedOffset = new Vec2(215f,-90f);
        }
        if(pResult.getDirection() == Direction.WEST) {
            groundedOffset = new Vec2(215f,90f);
        }
        if(pResult.getDirection() == Direction.DOWN) {
            groundedOffset = new Vec2(115f,180f);
        }
        if(pResult.getDirection() == Direction.UP) {
            groundedOffset = new Vec2(285f,180f);
        }
    }

    public boolean isGrounded() {
        return inGround;
    }
}
