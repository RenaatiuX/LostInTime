package com.ren.lostintime.common.entity.enums;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.IntFunction;

public enum BoatType implements StringRepresentable {

    ARAUCARIOXYLON(Blocks.OAK_LOG,"araucarioxylon");

    private final String name;
    private final Block planks;
    public static final EnumCodec<BoatType> CODEC = StringRepresentable.fromEnum(BoatType::values);
    private static final IntFunction<BoatType> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    BoatType(Block pPlanks, String pName) {
        this.name = pName;
        this.planks = pPlanks;
    }

    public String getName() {
        return name;
    }

    public Block getPlanks() {
        return planks;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static BoatType byId(int pId) {
        return BY_ID.apply(pId);
    }

    public static BoatType byName(String pName) {
        return CODEC.byName(pName, ARAUCARIOXYLON);
    }
}
