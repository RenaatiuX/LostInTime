package com.ren.lostintime.common.block;

import com.ren.lostintime.common.blockentity.LITHangingSignBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class LITWallHangingSignBlock extends WallHangingSignBlock {

    public LITWallHangingSignBlock(Properties pProperties, WoodType pType) {
        super(pProperties, pType);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LITHangingSignBE(pPos, pState);
    }
}
