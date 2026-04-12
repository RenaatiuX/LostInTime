package com.ren.lostintime.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public abstract class LITBottomWalkingWaterAnimal extends LITWaterAnimal {

    public LITBottomWalkingWaterAnimal(EntityType<? extends LITAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean canFlop() {
        return false;
    }

    @Override
    public boolean canSwim() {
        return false;
    }

    @Override
    public void aiStep() {
        if (this.isInWater()) {
            if (!this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
            }
            BlockPos pos = this.blockPosition();
            BlockState block = this.level().getBlockState(pos.above());
            if (this.getStepHeight() >= 1 && block.getFluidState().is(Fluids.EMPTY)) {
                this.setMaxUpStep(0);
            } else if (this.isInWater() && block.getFluidState().is(Fluids.WATER)) {
                this.setMaxUpStep(1);
            }
        }
        super.aiStep();
    }

    @Override
    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        return pLevel.getFluidState(pPos.above()).is(FluidTags.WATER) ? 0F : super.getWalkTargetValue(pPos, pLevel);
    }
}
