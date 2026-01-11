package com.ren.lostintime.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.ren.lostintime.common.worldgen.feature.config.FossilFormationConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class FossilFormationFeature extends Feature<FossilFormationConfig> {

    public FossilFormationFeature(Codec<FossilFormationConfig> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<FossilFormationConfig> context) {
        LevelAccessor level = context.level();
        BlockPos center = context.origin();
        FossilFormationConfig config = context.config();

        RandomSource random = context.random();

        int thickness = 1 + random.nextInt(2);
        int radius = 1 + random.nextInt(2);

        int placedBlocks = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x*x + z*z <= radius*radius) {
                    for (int y = 0; y < thickness; y++) {
                        BlockPos pos = center.offset(x, -y, z);
                        BlockState targetBlock;

                        if (pos.getY() < 0) {
                            if (config.deepslateReplaceable().test(level.getBlockState(pos), random)) {
                                targetBlock = config.deepslateFossilBlock();
                                level.setBlock(pos, targetBlock, 2);
                                placedBlocks++;
                            }
                        } else {
                            if (config.replaceable().test(level.getBlockState(pos), random)) {
                                targetBlock = config.fossilBlock();
                                level.setBlock(pos, targetBlock, 2);
                                placedBlocks++;
                            }
                        }
                        if (placedBlocks >= 8) return true;
                    }
                }
            }
        }
        return true;
    }
}
