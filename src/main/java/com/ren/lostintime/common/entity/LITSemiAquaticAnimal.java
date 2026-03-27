package com.ren.lostintime.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidType;

public abstract class LITSemiAquaticAnimal extends LITAnimal {

    protected int timeInWater = 0;
    protected int timeOnLand = 0;

    public LITSemiAquaticAnimal(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (this.isInWater()) {
                this.timeInWater++;
                this.timeOnLand = 0;
            } else {
                this.timeOnLand++;
                this.timeInWater = 0;
            }

            this.handleSemiAquaticNeeds();
        }
    }

    protected abstract void handleSemiAquaticNeeds();

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("TimeInWater", this.timeInWater);
        pCompound.putInt("TimeOnLand", this.timeOnLand);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.timeInWater = pCompound.getInt("TimeInWater");
        this.timeOnLand = pCompound.getInt("TimeOnLand");
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    public int getTimeInWater() {
        return this.timeInWater;
    }

    public void setTimeInWater(int time) {
        this.timeInWater = time;
    }

    public int getTimeOnLand() {
        return this.timeOnLand;
    }

    public void setTimeOnLand(int time) {
        this.timeOnLand = time;
    }
}
