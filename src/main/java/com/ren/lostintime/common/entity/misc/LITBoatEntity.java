package com.ren.lostintime.common.entity.misc;

import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class LITBoatEntity extends Boat {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(LITBoatEntity.class, EntityDataSerializers.INT);

    public LITBoatEntity(EntityType<? extends Boat> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public LITBoatEntity(Level level, double pX, double pY, double pZ) {
        this(EntityInit.LIT_BOAT.get(), level);
        this.setPos(pX, pY, pZ);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
    }

    @Override
    public Item getDropItem() {
        return super.getDropItem();
    }


}
