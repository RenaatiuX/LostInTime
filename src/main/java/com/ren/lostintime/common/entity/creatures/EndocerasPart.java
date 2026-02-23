package com.ren.lostintime.common.entity.creatures;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;

public class EndocerasPart extends PartEntity<Endoceras> {

    public final Endoceras parent;
    public final String name;
    private final EntityDimensions dimensions;

    public EndocerasPart(Endoceras parent, String pName, float pWidth, float pHeight) {
        super(parent);
        this.parent = parent;
        this.name = pName;
        this.dimensions = EntityDimensions.scalable(pWidth, pHeight);
        this.refreshDimensions();
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return this.parent.getPickResult();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return this.isInvulnerableTo(pSource) ? false : this.parent.hurt(pSource, pAmount);
    }

    @Override
    public boolean is(Entity pEntity) {
        return this == pEntity || this.parent == pEntity;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return this.dimensions;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}
