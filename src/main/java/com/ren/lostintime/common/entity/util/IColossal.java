package com.ren.lostintime.common.entity.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.LeavesBlock;

public interface IColossal {

    //Define how large the area is that is crushed when walking
    int getCrushRadius();

    default void crushBlocks(LivingEntity giant) {
        Level level = giant.level();
        if (level.isClientSide) return;

        BlockPos center = giant.blockPosition();
        int radius = getCrushRadius();

        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, 0, -radius), center.offset(radius, 2, radius))) {
            if (level.getBlockState(pos).getBlock() instanceof LeavesBlock ||
                    level.getBlockState(pos).getBlock() instanceof FenceBlock) {
                level.destroyBlock(pos, true);
            }
        }
    }
}
