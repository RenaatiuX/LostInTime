package com.ren.lostintime.common.entity.misc;

import com.ren.lostintime.common.entity.util.BoatType;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class LITBoat extends Boat {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(LITBoat.class, EntityDataSerializers.INT);

    public LITBoat(EntityType<? extends Boat> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public LITBoat(Level level, double pX, double pY, double pZ) {
        this(EntityInit.LIT_BOAT.get(), level);
        this.setPos(pX, pY, pZ);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
    }

    @Override
    public Item getDropItem() {
        return switch (getBoatVariant()) {
            case ARAUCARIOXYLON -> ItemInit.ARAUCARIOXYLON_BOAT.get();
        };
    }

    public void setBoatVariant(BoatType pVariant) {
        this.entityData.set(DATA_ID_TYPE, pVariant.ordinal());
    }

    public BoatType getBoatVariant() {
        return BoatType.byId(this.entityData.get(DATA_ID_TYPE));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, BoatType.ARAUCARIOXYLON.ordinal());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putString("boatType", this.getBoatVariant().getSerializedName());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("boatType", 8)) {
            this.setBoatVariant(BoatType.byName(pCompound.getString("boatType")));
        }
    }
}
