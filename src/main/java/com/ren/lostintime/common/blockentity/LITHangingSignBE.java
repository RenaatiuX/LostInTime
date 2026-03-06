package com.ren.lostintime.common.blockentity;

import com.ren.lostintime.common.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LITHangingSignBE extends SignBlockEntity {

    public LITHangingSignBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.LIT_HANGING_SIGN.get(), pPos, pBlockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return BlockEntityInit.LIT_HANGING_SIGN.get();
    }
}
