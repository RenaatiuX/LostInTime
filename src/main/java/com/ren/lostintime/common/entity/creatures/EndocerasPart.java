package com.ren.lostintime.common.entity.creatures;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        return this.parent.interact(pPlayer, pHand);
    }

    @Override
    public InteractionResult interactAt(Player pPlayer, Vec3 pVec, InteractionHand pHand) {
        return this.parent.interactAt(pPlayer, pVec, pHand);
    }

    @Override
    public boolean mayInteract(Level pLevel, BlockPos pPos) {
        return this.parent.mayInteract(pLevel, pPos);
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
