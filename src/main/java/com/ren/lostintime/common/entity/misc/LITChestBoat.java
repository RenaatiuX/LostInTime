package com.ren.lostintime.common.entity.misc;

import com.ren.lostintime.common.entity.enums.BoatType;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.ItemInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import static com.ren.lostintime.common.entity.enums.BoatType.ARAUCARIOXYLON;

public class LITChestBoat extends ChestBoat {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);

    public LITChestBoat(EntityType<? extends Boat> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public LITChestBoat(Level pLevel, double pX, double pY, double pZ) {
        this(EntityInit.LIT_CHEST_BOAT.get(), pLevel);
        this.setPos(pX, pY, pZ);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
    }

    @Override
    public Item getDropItem() {
        switch (getBoatVariant()) {
            case ARAUCARIOXYLON -> {
                return  ItemInit.ARAUCARIOXYLON_CHEST_BOAT.get();
            }
        }
        return super.getDropItem();
    }

    public void setChestBoatVariant(BoatType pVariant) {
        this.entityData.set(DATA_ID_TYPE, pVariant.ordinal());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE, ARAUCARIOXYLON.ordinal());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("boatType", this.getBoatVariant().getSerializedName());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("boatType", 8)) {
            this.setChestBoatVariant(BoatType.byName(pCompound.getString("boatType")));
        }
    }

    public BoatType getBoatVariant() {
        return BoatType.byId(this.entityData.get(DATA_ID_TYPE));
    }
}
