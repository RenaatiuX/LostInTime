package com.ren.lostintime.common.block;

import com.ren.lostintime.common.blockentity.LITSignBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class LITStandingSignBlock extends StandingSignBlock {

    public LITStandingSignBlock(Properties pProperties, WoodType pType) {
        super(pProperties, pType);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LITSignBE(pPos, pState);
    }
}
