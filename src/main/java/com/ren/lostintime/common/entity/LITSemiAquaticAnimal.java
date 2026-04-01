package com.ren.lostintime.common.entity;

import com.ren.lostintime.common.entity.ai.LITSemiAquaticMoveControl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public abstract class LITSemiAquaticAnimal extends LITAnimal {

    protected int timeInWater = 0;
    protected int timeOnLand = 0;

    public LITSemiAquaticAnimal(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.setMaxUpStep(1.0F);
        this.moveControl = new LITSemiAquaticMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new AmphibiousPathNavigation(this, pLevel);
    }

    // ==========================================
    // SEMI-AQUATIC METHODS
    // ==========================================
    protected abstract void handleSemiAquaticNeeds();
    public abstract int getWaterPhaseDuration();
    public abstract int getLandPhaseDuration();

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (this.isConsideredInWater()) {
                this.timeInWater++;
                this.timeOnLand = 0;
            } else {
                this.timeOnLand++;
                this.timeInWater = 0;
            }
            this.handleSemiAquaticNeeds();
        }
    }

    // ==========================================
    // PHYSICS AND AQUATIC MOVEMENT
    // ==========================================
    @Override
    public void travel(@NotNull Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public @NotNull Vec3 getFluidFallingAdjustedMovement(double gravity, boolean isFalling, @NotNull Vec3 deltaMovement) {
        return deltaMovement.scale(0.90D);
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return true;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }

    public boolean isConsideredInWater() {
        return this.isInWaterOrBubble() || this.isUnderWater()
                || (this.timeInWater > 0 && this.timeOnLand < 10);
    }

    // ==========================================
    // NBT
    // ==========================================
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

    @Override
    public int getMaxHeadXRot() {
        return 25;
    }

    @Override
    public int getMaxHeadYRot() {
        return 10;
    }
}
